package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ItemCartLineBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.CartLine
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.toCop

class CartLineAdapter(
    private val onPlus: (CartLine) -> Unit,
    private val onMinus: (CartLine) -> Unit
) : ListAdapter<CartLine, CartLineAdapter.ViewHolder>(Diff) {

    class ViewHolder(val binding: ItemCartLineBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemCartLineBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val line = getItem(position)
        val b = holder.binding
        b.tvEmoji.text = line.item.emoji
        b.tvName.text = line.item.name
        b.tvUnitPrice.text = b.root.context.getString(R.string.cart_unit_price, line.item.price.toCop())
        b.tvLineTotal.text = line.total.toCop()
        b.tvQty.text = line.quantity.toString()
        b.btnPlus.setOnClickListener { onPlus(line) }
        b.btnMinus.setOnClickListener { onMinus(line) }
    }

    private object Diff : DiffUtil.ItemCallback<CartLine>() {
        override fun areItemsTheSame(a: CartLine, b: CartLine) = a.item.id == b.item.id
        override fun areContentsTheSame(a: CartLine, b: CartLine) = a == b
    }
}
