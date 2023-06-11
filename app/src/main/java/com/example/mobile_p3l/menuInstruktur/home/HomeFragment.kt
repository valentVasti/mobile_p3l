package com.example.mobile_p3l.menuInstruktur.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mobile_p3l.databinding.FragmentHomeInstrukturBinding

class HomeFragment() : Fragment() {

    private var _binding: FragmentHomeInstrukturBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeInstrukturBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        textView.setText("Home Fragment Instruktur")
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}