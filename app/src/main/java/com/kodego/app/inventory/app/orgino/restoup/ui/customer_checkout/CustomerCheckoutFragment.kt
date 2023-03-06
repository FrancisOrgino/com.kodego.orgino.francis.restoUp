package com.kodego.app.inventory.app.orgino.restoup.ui.customer_checkout

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kodego.app.inventory.app.orgino.restoup.R
import com.kodego.app.inventory.app.orgino.restoup.databinding.FragmentCustomerCheckoutBinding
import com.kodego.app.inventory.app.orgino.restoup.databinding.FragmentOrderQueueBinding
import com.kodego.app.inventory.app.orgino.restoup.ui.order_queue.OrderQueueViewModel

class CustomerCheckoutFragment : Fragment() {

    private var _binding: FragmentCustomerCheckoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val orderQueueViewModel =
            ViewModelProvider(this).get(CustomerCheckoutViewModel::class.java)

        _binding = FragmentCustomerCheckoutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textCustomerCheckout
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