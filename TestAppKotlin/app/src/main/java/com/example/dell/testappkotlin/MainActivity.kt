package com.example.dell.testappkotlin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.view.View
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var act: Int =1
    val TAG="Movie.TAG"
    private val REQUEST_CODE_PERMISSION = 2
    internal var mPermission = Manifest.permission.ACCESS_COARSE_LOCATION
    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d(TAG, "onCreate0")
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate1")
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        Log.d(TAG, "onCreate2")

        nav_view.setNavigationItemSelectedListener(this)

        Log.d(TAG, "onCreate3")
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this, mPermission) != PackageManager.PERMISSION_GRANTED)
            run { ActivityCompat.requestPermissions(this, arrayOf<String>(mPermission), REQUEST_CODE_PERMISSION) }


        Log.d(TAG, "onCreate4")
        val bundle = intent.extras
        if(bundle!=null&&bundle.getInt("Activity Key", 1)==2)
        {
            callBeaconFragment()
        }
        else if(savedInstanceState!=null)
            updateFromBundle(savedInstanceState)
        else
            callMovieFragment()
        Log.d(TAG, "onCreate5")
    }


    fun updateFromBundle(bundle: Bundle)
    {
        Log.d(TAG, "updateFromBundle")
        Log.d(TAG, bundle.getInt("Activity Key").toString())
        when(bundle.getInt("Activity Key")){
            1->callMovieFragment()
            2->callBeaconFragment()
            //More cases to be added
            else -> callMovieFragment()
        }
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }*/

    fun callMovieFragment()
    {
        act=1;
        Log.d(TAG, "callMovieFragment")
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val movieFragment = PopularMovies()
        fragmentTransaction.replace(R.id.fragment_container, movieFragment, "MovieFragment").commit()
        nav_view.menu.getItem(0).isChecked = true
    }

    fun callBeaconFragment()
    {
        act=2;
        Log.d(TAG, "callBeaconFragment")
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val beaconFragment = BeaconFragment()
        fragmentTransaction.replace(R.id.fragment_container, beaconFragment, "BeaconFragment").commit()
        nav_view.menu.getItem(1).isChecked = true
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }*/

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.popular_movies) {
            callMovieFragment()
        } else if (id == R.id.nav_beacon) {
            callBeaconFragment()
        } else if (id == R.id.nav_geofence) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("Activity Key", act)
        super.onSaveInstanceState(outState)
    }
}
