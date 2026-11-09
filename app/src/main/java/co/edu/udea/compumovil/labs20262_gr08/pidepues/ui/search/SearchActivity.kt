package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.search

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivitySearchBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.filter.FilterActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.home.HomeActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.orders.OrdersActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.profile.ProfileActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val recentSearchesList = mutableListOf("Bandeja Paisa", "Sushi Rolls", "Smash Burger")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        renderRecentSearches()
        setupListeners()
        setupNavigation()
        showKeyboardAutomatically()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun showKeyboardAutomatically() {
        binding.etSearch.requestFocus()
        binding.etSearch.post {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun renderRecentSearches() {
        binding.containerRecentSearches.removeAllViews()

        if (recentSearchesList.isEmpty()) {
            binding.sectionRecentSearches.visibility = View.GONE
            return
        }

        binding.sectionRecentSearches.visibility = View.VISIBLE
        recentSearchesList.forEach { query ->
            val itemView = layoutInflater.inflate(R.layout.item_recent_search, binding.containerRecentSearches, false)
            val tvQuery = itemView.findViewById<TextView>(R.id.tvSearchQuery)
            val btnDelete = itemView.findViewById<View>(R.id.btnDeleteQuery)

            tvQuery.text = query
            itemView.setOnClickListener {
                binding.etSearch.setText(query)
                binding.etSearch.setSelection(query.length)
            }

            btnDelete.setOnClickListener {
                recentSearchesList.remove(query)
                renderRecentSearches()
            }

            binding.containerRecentSearches.addView(itemView)
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnClearHistory.setOnClickListener {
            recentSearchesList.clear()
            renderRecentSearches()
        }

        binding.btnClearSearch.setOnClickListener {
            binding.etSearch.text?.clear()
        }

        // Navegación segura a la pantalla completa de Filtros
        binding.btnFilter.setOnClickListener {
            startActivity(Intent(this, FilterActivity::class.java))
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim() ?: ""
                if (query.isNotEmpty()) {
                    binding.btnClearSearch.visibility = View.VISIBLE
                    binding.sectionResults.visibility = View.VISIBLE
                    binding.tvResultsTitle.text = "Resultados para \"$query\""

                    binding.cardResult1.tvRestaurantName.text = "Burger Bros"
                    binding.cardResult1.tvCuisineType.text = "Hamburguesas Artesanales"
                    binding.cardResult1.tvTag.text = "Smash Burger"
                    binding.cardResult1.tvRating.text = "⭐ 4.6"
                    binding.cardResult1.tvDeliveryTime.text = "🕒 30 min"
                    binding.cardResult1.tvDistance.text = "📍 0.7 km"
                    binding.cardResult1.tvDeliveryFee.text = "$4.000 envío"
                } else {
                    binding.btnClearSearch.visibility = View.GONE
                    binding.sectionResults.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_search
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                    finish()
                    true
                }
                R.id.nav_search -> true
                R.id.nav_orders -> {
                    startActivity(Intent(this, OrdersActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}