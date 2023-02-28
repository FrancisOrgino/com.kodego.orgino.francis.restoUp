package com.kodego.app.inventory.app.orgino.restoup

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.google.firebase.FirebaseOptions
import com.google.firebase.ktx.Firebase
import com.kodego.app.inventory.app.orgino.restoup.Data.UserTypes
import com.kodego.app.inventory.app.orgino.restoup.databinding.ActivityMainInterfaceBinding

class MainInterface : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainInterfaceBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainInterfaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMainInterface.toolbar)

        binding.appBarMainInterface.fab.setOnClickListener { view ->
            Snackbar.make(view, "Chat Feature Coming Soon!", Snackbar.LENGTH_LONG)
                .setAction("OK", null).show()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        //Programmatically hide menu items based on user
        when (db.currentUser.userType) {
            UserTypes.ADMIN -> {
                /* Do Nothing */
            }
            UserTypes.CASHIER -> {
                navView.menu.removeItem(R.id.nav_home)
                navView.menu.removeItem(R.id.nav_take_an_order)
                navView.menu.removeItem(R.id.nav_order_queue)
            }
            UserTypes.WAITER -> {
                navView.menu.removeItem(R.id.nav_home)
                navView.menu.removeItem(R.id.nav_customer_checkout)
                navView.menu.removeItem(R.id.nav_order_queue)
            }
            UserTypes.KITCHENSTAFF -> {
                navView.menu.removeItem(R.id.nav_home)
                navView.menu.removeItem(R.id.nav_take_an_order)
                navView.menu.removeItem(R.id.nav_order_queue)
            }
            UserTypes.CUSTOMER -> {
                /*do nothing*/
            }
        }

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_interface)
        //Programmatically set Navigation's Start Destination
        val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)
        when (db.currentUser.userType) {
            UserTypes.ADMIN -> navGraph.setStartDestination(R.id.nav_home)
            UserTypes.CASHIER -> navGraph.setStartDestination(R.id.nav_customer_checkout)
            UserTypes.WAITER -> navGraph.setStartDestination(R.id.nav_take_an_order)
            UserTypes.KITCHENSTAFF -> navGraph.setStartDestination(R.id.nav_order_queue)
            UserTypes.CUSTOMER -> TODO()
        }
        navController.graph = navGraph

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_take_an_order, R.id.nav_order_queue, R.id.nav_customer_checkout
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_interface, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_MyProfile -> {
                false
            }
            R.id.action_SignOut -> {
                try {
                    auth.signOut()
                    db.clear()
                    startActivity(Intent(this, LoginOptionsActivity::class.java))
                    finish()
                    true
                } catch (e:java.lang.Exception) {
                    Toast.makeText(applicationContext, "Sign out error", Toast.LENGTH_SHORT).show()
                    false
                }
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main_interface)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
        db.loadRestaurantList(db.currentUser.adminUID!!)
    }
}

