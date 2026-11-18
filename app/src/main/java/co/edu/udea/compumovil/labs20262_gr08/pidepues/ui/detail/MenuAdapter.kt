package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ItemMenuBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.MenuItem
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.toCop

/** Un plato del menú junto con la cantidad que hay en el carrito. */
data class MenuRow(val item: MenuItem, val quantity: Int)

class MenuAdapter(
    private val onAdd: (MenuItem) -> Unit,
    private val onPlus: (MenuItem) -> Unit,
    private val onMinus: (MenuItem) -> Unit
) : ListAdapter<MenuRow, MenuAdapter.ViewHolder>(Diff) {

    class ViewHolder(val binding: ItemMenuBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val row = getItem(position)
        val item = row.item
        val b = holder.binding

        b.tvEmoji.text = item.emoji
        b.tvName.text = item.name
        b.tvDescription.text = item.description
        b.tvPrice.text = item.price.toCop()

        // Cantidad 0: botón "+". Cantidad > 0: selector  −  1  +
        b.btnAdd.isVisible = row.quantity == 0
        b.stepper.isVisible = row.quantity > 0
        b.tvQty.text = row.quantity.toString()

        b.btnAdd.setOnClickListener { onAdd(item) }
        b.btnPlus.setOnClickListener { onPlus(item) }
        b.btnMinus.setOnClickListener { onMinus(item) }
    }

    private object Diff : DiffUtil.ItemCallback<MenuRow>() {
        override fun areItemsTheSame(a: MenuRow, b: MenuRow) = a.item.id == b.item.id
        override fun areContentsTheSame(a: MenuRow, b: MenuRow) = a == b
    }
}
