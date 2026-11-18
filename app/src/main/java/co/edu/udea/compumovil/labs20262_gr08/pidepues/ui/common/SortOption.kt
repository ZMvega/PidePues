package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common


import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.SortOption

/** Textos y emojis de cada forma de ordenar (Kotlin puro en el modelo, recursos aquí). */

val SortOption.emoji: String
    get() = when (this) {
        SortOption.RECOMMENDED -> "🎯"
        SortOption.NEAREST -> "📍"
        SortOption.BEST_RATED -> "⭐"
        SortOption.FASTEST -> "⚡"
    }

val SortOption.titleRes: Int
    get() = when (this) {
        SortOption.RECOMMENDED -> R.string.sort_recommended_title
        SortOption.NEAREST -> R.string.sort_nearest_title
        SortOption.BEST_RATED -> R.string.sort_best_title
        SortOption.FASTEST -> R.string.sort_fastest_title
    }

val SortOption.subtitleRes: Int
    get() = when (this) {
        SortOption.RECOMMENDED -> R.string.sort_recommended_subtitle
        SortOption.NEAREST -> R.string.sort_nearest_subtitle
        SortOption.BEST_RATED -> R.string.sort_best_subtitle
        SortOption.FASTEST -> R.string.sort_fastest_subtitle
    }

val SortOption.infoRes: Int
    get() = when (this) {
        SortOption.RECOMMENDED -> R.string.sort_info_recommended
        SortOption.NEAREST -> R.string.sort_info_nearest
        SortOption.BEST_RATED -> R.string.sort_info_best
        SortOption.FASTEST -> R.string.sort_info_fastest
    }

val SortOption.listTitleRes: Int
    get() = when (this) {
        SortOption.RECOMMENDED -> R.string.list_title_recommended
        SortOption.NEAREST -> R.string.list_title_nearest
        SortOption.BEST_RATED -> R.string.list_title_best
        SortOption.FASTEST -> R.string.list_title_fastest
    }
