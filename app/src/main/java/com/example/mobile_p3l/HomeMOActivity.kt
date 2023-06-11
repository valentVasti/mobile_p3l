package com.example.mobile_p3l

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mobile_p3l.databinding.ActivityHomeMoactivityBinding
import com.example.mobile_p3l.menuMO.dashboard.DashboardFragment
import com.example.mobile_p3l.menuMO.home.HomeFragment
import com.example.mobile_p3l.menuMO.presensiInstruktur.presensiInstrukturFragment

class HomeMOActivity: AppCompatActivity() {

    private lateinit var binding: ActivityHomeMoactivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeMoactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firstFragment = HomeFragment()
        val secondFragment = DashboardFragment()
        val thirdFragment = presensiInstrukturFragment()


        val bundle = intent.extras
        val id_user = bundle?.getString("id_user")
        val index = bundle?.getInt("index", 0)


        val fragmentBundle = Bundle()
        fragmentBundle.putString("id_user", id_user)

        if(index == 1){
            setCurrentFragment(firstFragment)
        }else if(index == 2){
            setCurrentFragment(secondFragment)
        }else if(index == 3){
            setCurrentFragment(thirdFragment)
        }else{
            val navView: BottomNavigationView = binding.navView

            navView.setOnNavigationItemSelectedListener {
                if(it.itemId == R.id.navigation_home_mo) {
//                setLoading(true)
                    setCurrentFragment(firstFragment)
//                setLoading(false)
                }else if(it.itemId == R.id.navigation_dashboard_mo){
                    setCurrentFragment(secondFragment)
                }else if(it.itemId == R.id.navigation_presensi_instruktur_mo) {
                    setCurrentFragment(thirdFragment)
                }
                true
            }
        }

    }

    private fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_activity_home_moactivity,fragment)
            commit()
        }
    }
}