package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ItemCategoryBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Category

/**explorar por categoria */
class CategoryAdapter(
    private val onClick: (Category) -> Unit
) : ListAdapter<Category, CategoryAdapter.ViewHolder>(Diff) {

    class ViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = getItem(position)
        holder.binding.tvEmoji.text = category.emoji
        holder.binding.tvLabel.text = category.label
        holder.binding.root.setOnClickListener { onClick(category) }
    }

    private object Diff : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(a: Category, b: Category) = a == b
        override fun areContentsTheSame(a: Category, b: Category) = a == b
    }
}
