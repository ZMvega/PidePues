package co.edu.udea.compumovil.labs20262_gr08.pidepues.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.DeliveryAddress

/** Direcciones guardadas del usuario: users/{uid}/addresses/{id}. */
object AddressRepository {

    private const val USERS = "users"
    private const val ADDRESSES = "addresses"

    private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    fun listen(uid: String, onChange: (List<DeliveryAddress>) -> Unit): ListenerRegistration =
        db.collection(USERS).document(uid).collection(ADDRESSES)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    onChange(
                        snapshot.documents.mapNotNull { doc ->
                            val text = doc.getString("text") ?: return@mapNotNull null
                            DeliveryAddress(
                                id = doc.id,
                                label = doc.getString("label").orEmpty(),
                                emoji = doc.getString("emoji") ?: "📍",
                                text = text
                            )
                        }
                    )
                }
            }

    fun add(uid: String, label: String, emoji: String, text: String, onResult: (Result<String>) -> Unit) {
        val data = hashMapOf<String, Any>(
            "label" to label,
            "emoji" to emoji,
            "text" to text,
            "createdAt" to FieldValue.serverTimestamp()
        )
        db.collection(USERS).document(uid).collection(ADDRESSES).add(data)
            .addOnSuccessListener { ref -> onResult(Result.success(ref.id)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }
}
