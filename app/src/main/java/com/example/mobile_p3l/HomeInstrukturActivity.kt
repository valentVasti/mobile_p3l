package com.example.mobile_p3l

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mobile_p3l.databinding.ActivityHomeInstrukturBinding
import com.example.mobile_p3l.menuInstruktur.home.HomeFragment
import com.example.mobile_p3l.menuInstruktur.jadwalPresensiKelas.JadwalPresensiKelasFragment
import com.example.mobile_p3l.menuInstruktur.izinInstruktur.izinInstrukturFragment
import com.example.mobile_p3l.menuInstruktur.profile.ProfileInstrukturFragment

class HomeInstrukturActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeInstrukturBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeInstrukturBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firstFragment = HomeFragment()
        val secondFragment = JadwalPresensiKelasFragment()
        val thirdFragment = izinInstrukturFragment()
        val fourthFragment = ProfileInstrukturFragment()

        val bundle = intent.extras
        val id_user = bundle?.getString("id_user")

        val fragmentBundle = Bundle()
        fragmentBundle.putString("id_user", id_user)

        val navView: BottomNavigationView = binding.navView

        navView.setOnNavigationItemSelectedListener {
            if(it.itemId == R.id.navigation_home_instruktur) {
                setCurrentFragment(firstFragment)
            }else if(it.itemId == R.id.navigation_presensi_kelas){
                secondFragment.arguments = fragmentBundle
                setCurrentFragment(secondFragment)
            }else if(it.itemId == R.id.navigation_izin_instruktur) {
                thirdFragment.arguments = fragmentBundle
                setCurrentFragment(thirdFragment)
            }else if(it.itemId == R.id.navigation_profile_instruktur){
                fourthFragment.arguments = fragmentBundle
                setCurrentFragment(fourthFragment)
            }
            true
        }

//        val navController = findNavController(R.id.nav_host_fragment_activity_home_instruktur)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
////        val appBarConfiguration = AppBarConfiguration(
////            setOf(
////                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_izin_instruktur
////            )
////        )
////        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
    }

    private fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_activity_home_instruktur,fragment)
            commit()
        }
    }
}