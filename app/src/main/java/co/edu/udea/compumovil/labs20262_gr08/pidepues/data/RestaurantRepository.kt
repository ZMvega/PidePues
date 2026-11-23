package co.edu.udea.compumovil.labs20262_gr08.pidepues.data

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Category
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.MenuCategory
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.MenuItem
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.OrderItem
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.OrderStatus
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Restaurant
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.RestaurantOrder
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.RestaurantProfile
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Review

/**
 * Restaurantes y menús desde Firestore.
 *   restaurants/{id}            -> datos del restaurante
 *   restaurants/{id}/menu/{id}  -> platos del menú
 *   orders/{id}                 -> pedidos en curso y finalizados
 */
object RestaurantRepository {

    const val RESTAURANTS = "restaurants"
    const val MENU = "menu"
    const val ORDERS = "orders"
    const val REVIEWS = "reviews"

    private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    fun fetchRestaurants(onResult: (Result<List<Restaurant>>) -> Unit) {
        db.collection(RESTAURANTS).get()
            .addOnSuccessListener { snapshot ->
                onResult(Result.success(snapshot.documents.mapNotNull { it.toRestaurantOrNull() }))
            }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun fetchMenu(restaurantId: Int, onResult: (Result<List<MenuItem>>) -> Unit) {
        db.collection(RESTAURANTS).document(restaurantId.toString()).collection(MENU).get()
            .addOnSuccessListener { snapshot ->
                val items = snapshot.documents
                    .sortedBy { it.getLong("order") ?: Long.MAX_VALUE }
                    .mapNotNull { it.toMenuItemOrNull(restaurantId.toString()) }
                onResult(Result.success(items))
            }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun findOrCreateOwnedRestaurant(
        ownerUid: String,
        restaurantName: String,
        onResult: (Result<String>) -> Unit
    ) {
        val restaurants = db.collection(RESTAURANTS)
        restaurants.whereEqualTo("ownerUid", ownerUid).limit(1).get()
            .addOnSuccessListener { ownedRestaurants ->
                val ownedRestaurant = ownedRestaurants.documents.firstOrNull()
                if (ownedRestaurant != null) {
                    onResult(Result.success(ownedRestaurant.id))
                    return@addOnSuccessListener
                }
                val restaurantDocument = restaurants.document(ownerUid)
                restaurantDocument.get()
                    .addOnSuccessListener { existing ->
                        val existingOwner = existing.getString("ownerUid")
                        if (existing.exists() && existingOwner != null && existingOwner != ownerUid) {
                            onResult(Result.failure(SecurityException("El restaurante pertenece a otra cuenta")))
                            return@addOnSuccessListener
                        }
                        val name = existing.getString("name")
                            ?.takeIf { it.isNotBlank() }
                            ?: restaurantName
                        restaurantDocument.set(
                            mapOf("ownerUid" to ownerUid, "name" to name),
                            SetOptions.merge()
                        ).addOnSuccessListener { onResult(Result.success(ownerUid)) }
                            .addOnFailureListener { onResult(Result.failure(it)) }
                    }
                    .addOnFailureListener { onResult(Result.failure(it)) }
            }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun listenMenu(
        restaurantDocumentId: String,
        onResult: (Result<List<MenuItem>>) -> Unit
    ): ListenerRegistration = db.collection(RESTAURANTS)
        .document(restaurantDocumentId)
        .collection(MENU)
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                onResult(Result.failure(error))
                return@addSnapshotListener
            }
            if (snapshot == null) {
                onResult(Result.failure(IllegalStateException("No se recibió el menú del restaurante")))
                return@addSnapshotListener
            }
            val items = snapshot.documents
                .sortedBy { it.getLong("order") ?: Long.MAX_VALUE }
                .mapNotNull { it.toMenuItemOrNull(restaurantDocumentId) }
            onResult(Result.success(items))
        }

    fun setMenuItemAvailability(
        restaurantDocumentId: String,
        itemId: String,
        isAvailable: Boolean,
        onResult: (Result<Unit>) -> Unit
    ) {
        db.collection(RESTAURANTS).document(restaurantDocumentId)
            .collection(MENU).document(itemId)
            .update("isAvailable", isAvailable)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun saveMenuItem(
        restaurantDocumentId: String,
        item: MenuItem,
        onResult: (Result<Unit>) -> Unit
    ) {
        val menu = db.collection(RESTAURANTS).document(restaurantDocumentId).collection(MENU)
        val isNew = item.id.isBlank()
        val itemDocument = if (isNew) menu.document() else menu.document(item.id)
        val data = mutableMapOf<String, Any>(
            "name" to item.name.trim(),
            "description" to item.description.trim(),
            "price" to item.price,
            "emoji" to item.emoji,
            "menuCategory" to item.category.key,
            "isAvailable" to item.isAvailable
        )
        if (isNew) data["order"] = System.currentTimeMillis()
        item.imageUri?.let { data["imageUri"] = it }
        itemDocument.set(data, SetOptions.merge())
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    private fun DocumentSnapshot.toRestaurantOrNull(): Restaurant? {
        val restaurantId = id.toIntOrNull() ?: return null
        val point = getGeoPoint("location") ?: return null
        val categoryName = getString("category") ?: return null
        val category = runCatching { Category.valueOf(categoryName) }.getOrNull() ?: return null
        return Restaurant(
            id = restaurantId,
            name = getString("name") ?: return null,
            cuisine = getString("cuisine").orEmpty(),
            dishTag = getString("dishTag").orEmpty(),
            category = category,
            rating = getDouble("rating")?.toFloat() ?: 0f,
            deliveryMinutes = getLong("deliveryMinutes")?.toInt() ?: 0,
            deliveryFee = getLong("deliveryFee")?.toInt() ?: 0,
            latitude = point.latitude,
            longitude = point.longitude,
            emoji = getString("emoji").orEmpty(),
            isOpen = getBoolean("isOpen") ?: true,
            keywords = getString("keywords").orEmpty(),
            reviewCount = getLong("reviewCount")?.toInt() ?: 0,
            description = getString("description").orEmpty(),
            address = getString("address") ?: getString("direccion") ?: "El Poblado, Medellín",
            minOrder = getLong("minOrder")?.toInt() ?: 15000
        )
    }

    private fun DocumentSnapshot.toMenuItemOrNull(restaurantDocumentId: String): MenuItem? {
        val name = getString("name") ?: return null
        return MenuItem(
            id = id,
            restaurantId = restaurantDocumentId.toIntOrNull() ?: 0,
            restaurantDocumentId = restaurantDocumentId,
            name = name,
            description = getString("description").orEmpty(),
            price = getLong("price")?.toInt() ?: 0,
            emoji = getString("emoji").orEmpty(),
            category = MenuCategory.fromKey(getString("menuCategory") ?: getString("category")),
            isAvailable = getBoolean("isAvailable") ?: getBoolean("available") ?: true,
            imageUri = getString("imageUri")
        )
    }

    fun listenRestaurantOrders(
        restaurantId: String,
        onResult: (Result<List<RestaurantOrder>>) -> Unit
    ): ListenerRegistration = db.collection(ORDERS)
        .whereEqualTo("restaurantId", restaurantId)
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                onResult(Result.failure(error))
                return@addSnapshotListener
            }
            if (snapshot == null) {
                onResult(Result.success(emptyList()))
                return@addSnapshotListener
            }
            val orders = snapshot.documents.mapNotNull { it.toRestaurantOrderOrNull() }
                .sortedByDescending { it.createdAtMillis }
            onResult(Result.success(orders))
        }

    fun updateOrderStatus(
        orderId: String,
        newStatus: OrderStatus,
        onResult: (Result<Unit>) -> Unit
    ) {
        val updates = mapOf(
            "status" to newStatus.label,
            "estado" to newStatus.label,
            "statusKey" to newStatus.key,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        db.collection(ORDERS).document(orderId)
            .update(updates)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener {
                db.collection(ORDERS).document(orderId)
                    .set(updates, SetOptions.merge())
                    .addOnSuccessListener { onResult(Result.success(Unit)) }
                    .addOnFailureListener { error -> onResult(Result.failure(error)) }
            }
    }

    fun listenRestaurantReviews(
        restaurantId: String,
        onResult: (Result<List<Review>>) -> Unit
    ): ListenerRegistration = db.collection(RESTAURANTS)
        .document(restaurantId)
        .collection(REVIEWS)
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                onResult(Result.failure(error))
                return@addSnapshotListener
            }
            if (snapshot == null) {
                onResult(Result.success(emptyList()))
                return@addSnapshotListener
            }
            val reviews = snapshot.documents.mapNotNull { it.toReviewOrNull() }
                .sortedByDescending { it.createdAtMillis }
            onResult(Result.success(reviews))
        }

    fun replyToReview(
        restaurantId: String,
        reviewId: String,
        replyText: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        val updates = mapOf(
            "reply" to replyText.trim(),
            "respuesta" to replyText.trim(),
            "repliedAt" to FieldValue.serverTimestamp()
        )
        db.collection(RESTAURANTS).document(restaurantId)
            .collection(REVIEWS).document(reviewId)
            .set(updates, SetOptions.merge())
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun listenRestaurantProfile(
        restaurantId: String,
        onResult: (Result<RestaurantProfile>) -> Unit
    ): ListenerRegistration = db.collection(RESTAURANTS)
        .document(restaurantId)
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                onResult(Result.failure(error))
                return@addSnapshotListener
            }
            if (snapshot == null || !snapshot.exists()) {
                onResult(Result.success(RestaurantProfile(uid = restaurantId)))
                return@addSnapshotListener
            }
            val profile = snapshot.toRestaurantProfile(restaurantId)
            onResult(Result.success(profile))
        }

    private fun DocumentSnapshot.toRestaurantProfile(restaurantId: String): RestaurantProfile {
        return RestaurantProfile(
            uid = restaurantId,
            name = getString("name").orEmpty(),
            address = getString("address") ?: getString("direccion").orEmpty(),
            phone = getString("phone") ?: getString("telefono").orEmpty(),
            email = getString("email") ?: getString("correo").orEmpty(),
            logoUrl = getString("logoUrl") ?: getString("imageUrl") ?: getString("photoUrl"),
            bankName = getString("bankName") ?: getString("banco").orEmpty(),
            accountType = getString("accountType") ?: getString("tipoCuenta") ?: RestaurantProfile.ACCOUNT_TYPE_SAVINGS,
            accountNumber = getString("accountNumber") ?: getString("numeroCuenta").orEmpty(),
            accountHolder = getString("accountHolder") ?: getString("titular").orEmpty(),
            notifyNewOrders = getBoolean("notifyNewOrders") ?: true,
            notifyNewReviews = getBoolean("notifyNewReviews") ?: true,
            notifyAlertSound = getBoolean("notifyAlertSound") ?: true,
            notifyDailySummary = getBoolean("notifyDailySummary") ?: false
        )
    }

    fun saveRestaurantProfile(
        profile: RestaurantProfile,
        onResult: (Result<Unit>) -> Unit
    ) {
        val data = mutableMapOf<String, Any>(
            "name" to profile.name.trim(),
            "address" to profile.address.trim(),
            "phone" to profile.phone.trim(),
            "email" to profile.email.trim(),
            "bankName" to profile.bankName.trim(),
            "accountType" to profile.accountType,
            "accountNumber" to profile.accountNumber.trim(),
            "accountHolder" to profile.accountHolder.trim(),
            "notifyNewOrders" to profile.notifyNewOrders,
            "notifyNewReviews" to profile.notifyNewReviews,
            "notifyAlertSound" to profile.notifyAlertSound,
            "notifyDailySummary" to profile.notifyDailySummary,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        profile.logoUrl?.let { data["logoUrl"] = it }

        db.collection(RESTAURANTS).document(profile.uid)
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                if (profile.name.isNotBlank() || profile.phone.isNotBlank()) {
                    val userUpdates = mutableMapOf<String, Any>()
                    if (profile.name.isNotBlank()) userUpdates["fullName"] = profile.name.trim()
                    if (profile.phone.isNotBlank()) userUpdates["phone"] = profile.phone.trim()
                    db.collection("users").document(profile.uid)
                        .set(userUpdates, SetOptions.merge())
                }
                onResult(Result.success(Unit))
            }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun inviteCourier(
        restaurantId: String,
        phone: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        val invitation = mapOf(
            "phone" to phone.trim(),
            "createdAt" to FieldValue.serverTimestamp(),
            "createdAtMillis" to System.currentTimeMillis(),
            "status" to "pending",
            "restaurantId" to restaurantId
        )
        db.collection(RESTAURANTS).document(restaurantId)
            .collection("courier_invitations")
            .add(invitation)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    private fun DocumentSnapshot.toReviewOrNull(): Review? {
        val reviewId = id
        val restaurantId = getString("restaurantId") ?: ""
        val customerName = getString("customerName")
            ?: getString("userName")
            ?: getString("clientName")
            ?: getString("name")
            ?: "Cliente"
        val ratingRaw = getDouble("rating")
            ?: getLong("rating")?.toDouble()
            ?: getDouble("stars")
            ?: getLong("stars")?.toDouble()
            ?: 5.0
        val rating = ratingRaw.toFloat().coerceIn(1f, 5f)
        val comment = getString("comment")
            ?: getString("review")
            ?: getString("text")
            ?: getString("comentario")
            ?: ""
        val reply = getString("reply")
            ?: getString("respuesta")
            ?: getString("replyText")
            ?: ""

        val timestamp = getTimestamp("createdAt")
            ?: getTimestamp("timestamp")
            ?: getTimestamp("date")
        val createdAtMillis = timestamp?.toDate()?.time
            ?: getLong("createdAt")
            ?: getLong("timestamp")
            ?: System.currentTimeMillis()

        return Review(
            id = reviewId,
            restaurantId = restaurantId,
            customerName = customerName,
            rating = rating,
            comment = comment,
            createdAtMillis = createdAtMillis,
            reply = reply
        )
    }

    private fun DocumentSnapshot.toRestaurantOrderOrNull(): RestaurantOrder? {
        val orderId = this.id
        val restaurantId = getString("restaurantId")
            ?: getString("restaurantDocumentId")
            ?: getLong("restaurantId")?.toString()
            ?: ""

        val orderNumber = getString("orderNumber")
            ?: getString("code")
            ?: getString("orderCode")
            ?: getLong("orderNumber")?.toString()
            ?: orderId.takeLast(4).uppercase()

        val customerName = getString("customerName")
            ?: getString("clientName")
            ?: getString("userName")
            ?: (get("customer") as? Map<*, *>)?.get("name") as? String
            ?: "Cliente"

        val customerPhone = getString("customerPhone")
            ?: getString("phone")
            ?: getString("telefono")
            ?: (get("customer") as? Map<*, *>)?.get("phone") as? String
            ?: ""

        val customerAddress = getString("customerAddress")
            ?: getString("address")
            ?: getString("direccion")
            ?: getString("deliveryAddress")
            ?: (get("customer") as? Map<*, *>)?.get("address") as? String
            ?: ""

        val customerNote = getString("note")
            ?: getString("notes")
            ?: getString("nota")
            ?: getString("customerNote")
            ?: getString("instructions")
            ?: getString("observaciones")
            ?: ""

        val paymentMethod = getString("paymentMethod")
            ?: getString("metodoPago")
            ?: getString("pago")
            ?: (get("payment") as? Map<*, *>)?.get("method") as? String
            ?: "Efectivo"

        val courierName = getString("courierName")
            ?: getString("driverName")
            ?: getString("repartidorNombre")
            ?: (get("courier") as? Map<*, *>)?.get("name") as? String
            ?: (get("repartidor") as? Map<*, *>)?.get("nombre") as? String
            ?: ""

        val courierPhone = getString("courierPhone")
            ?: getString("driverPhone")
            ?: getString("repartidorTelefono")
            ?: (get("courier") as? Map<*, *>)?.get("phone") as? String
            ?: (get("repartidor") as? Map<*, *>)?.get("telefono") as? String
            ?: ""

        val rawItems = (get("items") ?: get("products") ?: get("lines")) as? List<*>
        val items = rawItems?.mapNotNull { itemAny ->
            when (itemAny) {
                is Map<*, *> -> {
                    val name = itemAny["name"] as? String
                        ?: itemAny["productName"] as? String
                        ?: itemAny["title"] as? String
                        ?: ""
                    val quantity = (itemAny["quantity"] as? Number)?.toInt()
                        ?: (itemAny["cant"] as? Number)?.toInt()
                        ?: 1
                    val price = (itemAny["price"] as? Number)?.toInt()
                        ?: (itemAny["unitPrice"] as? Number)?.toInt()
                        ?: 0
                    val total = (itemAny["total"] as? Number)?.toInt() ?: (price * quantity)
                    if (name.isNotBlank()) OrderItem(name, quantity, price, total) else null
                }
                else -> null
            }
        } ?: emptyList()

        val calculatedSubtotal = items.sumOf { it.total }
        val subtotalCop = (getLong("subtotal")
            ?: getLong("subtotalCop")
            ?: getDouble("subtotal")?.toInt()
            ?: calculatedSubtotal).toInt()

        val deliveryFeeCop = (getLong("deliveryFee")
            ?: getLong("deliveryFeeCop")
            ?: getLong("domicilio")
            ?: getLong("costoEnvio")
            ?: getDouble("deliveryFee")?.toInt()
            ?: getDouble("domicilio")?.toInt()
            ?: 0).toInt()

        val totalCop = (getLong("total")
            ?: getLong("totalCop")
            ?: getLong("totalPrice")
            ?: getDouble("total")?.toInt()
            ?: (subtotalCop + deliveryFeeCop)).toInt()

        val statusRaw = getString("status")
            ?: getString("estado")
            ?: getString("statusKey")
        val status = OrderStatus.fromKey(statusRaw)

        val timestamp = getTimestamp("createdAt")
            ?: getTimestamp("timestamp")
            ?: getTimestamp("date")
        val createdAtMillis = timestamp?.toDate()?.time
            ?: getLong("createdAt")
            ?: getLong("timestamp")
            ?: System.currentTimeMillis()

        return RestaurantOrder(
            id = orderId,
            orderNumber = orderNumber,
            restaurantId = restaurantId,
            customerName = customerName,
            customerPhone = customerPhone,
            customerAddress = customerAddress,
            items = items,
            totalCop = totalCop,
            subtotalCop = subtotalCop,
            deliveryFeeCop = deliveryFeeCop,
            status = status,
            createdAtMillis = createdAtMillis,
            customerNote = customerNote,
            paymentMethod = paymentMethod,
            courierName = courierName,
            courierPhone = courierPhone
        )
    }
}
