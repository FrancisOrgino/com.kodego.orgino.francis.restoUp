package com.kodego.app.inventory.app.orgino.restoup.ui.take_an_order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kodego.app.inventory.app.orgino.restoup.databinding.FragmentTakeAnOrderBinding

class TakeAnOrderFragment : Fragment() {

    private var _binding: FragmentTakeAnOrderBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val takeAnOrderViewModel =
            ViewModelProvider(this).get(TakeAnOrderViewModel::class.java)

        _binding = FragmentTakeAnOrderBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGallery
        takeAnOrderViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}