package com.dicoding.appcapstone.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.dicoding.appcapstone.R
import com.dicoding.appcapstone.ui.article.ArticleFragment
import com.dicoding.appcapstone.ui.auth.LoginActivity
import com.dicoding.appcapstone.ui.home.HomeFragment
import com.dicoding.appcapstone.ui.scan.ScanFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class NavigationActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        val topAppBar: Toolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(topAppBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        auth = FirebaseAuth.getInstance()
        bottomNavigation = findViewById(R.id.bottom_navigation)

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.articleFragment -> {
                    // Start ArticleFragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, ArticleFragment())
                        .commit()
                    true
                }
                R.id.homeFragment -> {
                    // Start HomeFragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, HomeFragment())
                        .commit()
                    true
                }
                R.id.scanFragment-> {
                    // Start ScanActivity
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, ScanFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }

        // Display HomeFragment by default
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HomeFragment())
                .commit()
            bottomNavigation.selectedItemId = R.id.homeFragment
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // Handle menu item selection
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                // Perform your logout action here
                auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // No user is signed in
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val sharedPreferences = getSharedPreferences("ArticleData", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }

    override fun onStop() {
        super.onStop()
        val sharedPreferences = getSharedPreferences("ArticleData", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }
}
