package com.kodego.app.inventory.app.orgino.restoup.ui.order_queue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kodego.app.inventory.app.orgino.restoup.databinding.FragmentOrderQueueBinding

class OrderQueueFragment : Fragment() {

    private var _binding: FragmentOrderQueueBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val orderQueueViewModel =
            ViewModelProvider(this).get(OrderQueueViewModel::class.java)

        _binding = FragmentOrderQueueBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        orderQueueViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}