package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ItemRestaurantBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Restaurant
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.RestaurantWithDistance
import java.text.NumberFormat
import java.util.Locale

/** tarjeta de restaurante */
class RestaurantAdapter(
    private val onClick: (Restaurant) -> Unit
) : ListAdapter<RestaurantWithDistance, RestaurantAdapter.ViewHolder>(Diff) {

    class ViewHolder(val binding: ItemRestaurantBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val r = item.restaurant
        val b = holder.binding
        val ctx = b.root.context

        // Mientras no haya fotos reales: color pastel + emoji del plato
        b.imageArea.setBackgroundColor(ContextCompat.getColor(ctx, PALETTE[r.id % PALETTE.size]))
        b.tvEmoji.text = r.emoji
        b.tvRating.text = String.format(Locale.US, "%.1f", r.rating)
        b.tvTag.text = r.dishTag
        b.tvName.text = r.name
        b.tvCuisine.text = r.cuisine
        b.tvTime.text = ctx.getString(R.string.delivery_minutes, r.deliveryMinutes)
        b.tvDistance.text = ctx.getString(R.string.delivery_distance, String.format(Locale.US, "%.1f", item.distanceKm))
        b.tvFee.text = if (r.deliveryFee == 0) {
            ctx.getString(R.string.delivery_free)
        } else {
            ctx.getString(R.string.delivery_fee, MONEY.format(r.deliveryFee))
        }
        b.closedOverlay.isVisible = !r.isOpen
        b.tvClosed.isVisible = !r.isOpen
        b.root.setOnClickListener { onClick(r) }
    }

    private object Diff : DiffUtil.ItemCallback<RestaurantWithDistance>() {
        override fun areItemsTheSame(a: RestaurantWithDistance, b: RestaurantWithDistance) =
            a.restaurant.id == b.restaurant.id

        override fun areContentsTheSame(a: RestaurantWithDistance, b: RestaurantWithDistance) = a == b
    }

    private companion object {
        val PALETTE = intArrayOf(
            R.color.food_1, R.color.food_2, R.color.food_3,
            R.color.food_4, R.color.food_5, R.color.food_6
        )
        // 3000 -> "3.000" (formato COP)
        val MONEY: NumberFormat = NumberFormat.getInstance(Locale.forLanguageTag("es-CO"))
    }
}