package com.example.mobile_p3l

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mobile_p3l.databinding.ActivityHomeMemberBinding
import com.example.mobile_p3l.menuMember.historyMember.HistoryMemberFragment
import com.example.mobile_p3l.menuMember.home.HomeFragment
import com.example.mobile_p3l.menuMember.bookingKelas.BookingKelasFragment
import com.example.mobile_p3l.menuMember.profile.ProfileMemberFragment

class HomeMemberActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeMemberBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firstFragment = HomeFragment()
        val secondFragment = HistoryMemberFragment()
        val thirdFragment = BookingKelasFragment()
        val fourthFragment = ProfileMemberFragment()

        val bundle = intent.extras
        val id_user = bundle?.getString("id_user")

        val fragmentBundle = Bundle()
        fragmentBundle.putString("id_user", id_user)

        val navView: BottomNavigationView = binding.navView

        navView.setOnNavigationItemSelectedListener {
            if(it.itemId == R.id.navigation_home_member) {
                setCurrentFragment(firstFragment)
            }else if(it.itemId == R.id.navigation_dashboard_member){
                secondFragment.arguments = fragmentBundle
                setCurrentFragment(secondFragment)
            }else if(it.itemId == R.id.navigation_booking_kelas_member) {
                thirdFragment.arguments = fragmentBundle
                setCurrentFragment(thirdFragment)
            }else if(it.itemId == R.id.navigation_profile_member){
                fourthFragment.arguments = fragmentBundle
                setCurrentFragment(fourthFragment)
            }
            true
        }

//        val navController = findNavController(R.id.nav_host_fragment_activity_home2)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
////        val appBarConfiguration = AppBarConfiguration(
////            setOf(
////                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_profile
////            )
////        )
////        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
    }

    private fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_activity_home_member,fragment)
            commit()
        }
    }
}