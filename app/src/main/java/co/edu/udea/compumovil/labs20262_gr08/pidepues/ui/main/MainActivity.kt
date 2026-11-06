package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityMainBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.cart.CartFragment
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.filters.FiltersFragment
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.home.HomeFragment
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.profile.ProfileFragment
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.search.SearchFragment

/**
 * Contenedor principal: barra inferior (Inicio, Buscar, Carrito, Perfil).
 * Los filtros se abren como una pantalla encima (overlayContainer).
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: RestaurantsViewModel by viewModels()
    private var currentTab = R.id.nav_home

    /**estando en otra pestaña, "atrás" vuelve a Inicio antes de salir de la app. */
    private val backToHome = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            binding.bottomNav.selectedItemId = R.id.nav_home
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, backToHome)
        supportFragmentManager.addOnBackStackChangedListener { refreshChrome() }
        binding.bottomNav.setOnItemSelectedListener { item ->
            showTab(item.itemId)
            true
        }

        if (savedInstanceState == null) {
            showTab(R.id.nav_home)
            //si hay permiso, reemplaza la ubicación por defecto por la del dispositivo
            viewModel.loadDeviceLocation(applicationContext)
        } else {
            currentTab = binding.bottomNav.selectedItemId
        }
        refreshChrome()
    }

    private fun showTab(itemId: Int) {
        currentTab = itemId
        val fragment: Fragment = when (itemId) {
            R.id.nav_search -> SearchFragment()
            R.id.nav_cart -> CartFragment()
            R.id.nav_profile -> ProfileFragment()
            else -> HomeFragment()
        }
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        refreshChrome()
    }

    /**cambia de pestaña desde un fragment (ej. tocar la barra de búsqueda de Inicio). */
    fun selectTab(itemId: Int) {
        binding.bottomNav.selectedItemId = itemId
    }

    fun openFilters() {
        if (supportFragmentManager.backStackEntryCount > 0) return
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.overlayContainer, FiltersFragment())
            .addToBackStack(FILTERS_TAG)
            .commit()
    }

    /** Muestra/oculta la capa de filtros, ajusta el botón atrás y el color de los iconos de estado. */
    private fun refreshChrome() {
        val filtersOpen = supportFragmentManager.backStackEntryCount > 0
        binding.overlayContainer.isVisible = filtersOpen
        backToHome.isEnabled = !filtersOpen && currentTab != R.id.nav_home

        //colores
        val lightBackground = filtersOpen || currentTab != R.id.nav_home
        WindowCompat.getInsetsController(window, binding.root).isAppearanceLightStatusBars = lightBackground
    }

    companion object {
        private const val FILTERS_TAG = "filters"

        /**abre la app principal limpiando la pila, después de login / permiso de ubicación */
        fun newIntent(context: Context): Intent =
            Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }
}