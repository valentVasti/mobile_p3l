package com.example.mobile_p3l.menuMember.bookingKelas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mobile_p3l.databinding.FragmentBookingKelasBinding

class BookingKelasFragment : Fragment() {

    private var _binding: FragmentBookingKelasBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingKelasBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textHistory
//        textView.setText("History Fragment")
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}