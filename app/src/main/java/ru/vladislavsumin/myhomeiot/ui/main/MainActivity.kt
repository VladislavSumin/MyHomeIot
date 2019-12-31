package ru.vladislavsumin.myhomeiot.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import moxy.presenter.InjectPresenter
import ru.vladislavsumin.myhomeiot.ui.lamp.manage.ManageGyverLampActivity
import ru.vladislavsumin.myhomeiot.ui.lamp.control.GyverLampControlActivity
import ru.vladislavsumin.myhomeiot.R
import ru.vladislavsumin.myhomeiot.app.AppConfig
import ru.vladislavsumin.myhomeiot.database.entity.GyverLampEntity
import ru.vladislavsumin.myhomeiot.ui.core.ToolbarActivity
import ru.vladislavsumin.myhomeiot.ui.utils.setClickableLinkListener

class MainActivity : ToolbarActivity(), MainActivityView {
    companion object {
        private const val LAYOUT = R.layout.activity_main

        fun getLaunchIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    @InjectPresenter
    lateinit var mPresenter: MainActivityPresenter

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupNavigation()
        setupUi()
        setupUx()
    }

    override fun getLayoutResId(): Int = LAYOUT

    private fun setupNavigation() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun setupUi() {
        val appVersion = getString(R.string.app_version).format(AppConfig.VERSION_NAME)
        activity_main_app_version_name.text = appVersion
    }

    private fun setupUx() {
        activity_main_privacy_policy.setClickableLinkListener {
            mPresenter.onClickReadPrivacyPolicy()
        }
    }

    override fun showPrivacyPolicyScreen(privacyPolicyUri: Uri) {
        val browserIntent = Intent(Intent.ACTION_VIEW, privacyPolicyUri)
        startActivity(browserIntent)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}