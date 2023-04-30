package com.kodego.app.inventory.app.orgino.restoup.ui.customer_checkout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.compose.runtime.*
import com.kodego.app.inventory.app.orgino.restoup.R
import com.kodego.app.inventory.app.orgino.restoup.databinding.FragmentCustomerCheckoutBinding
import com.kodego.app.inventory.app.orgino.restoup.db


class CustomerCheckoutFragment : Fragment() {

    private var _binding: FragmentCustomerCheckoutBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db.loadTableData(db.currentUser)
        db.loadMenuItems(db.currentUser)
        db.loadOrderData(db.currentUser)
        _binding = FragmentCustomerCheckoutBinding.inflate(layoutInflater)
        var checkOutSpinner1 = arrayOf(db.loadOrderData(db.currentUser))

        var checkOutSpinner2 = arrayOf(db.restaurantTableDataList)
        var checkOutSpinner3 = arrayOf(db.restaurantOrderList)



        var spinnerAdapter = ArrayAdapter(requireContext(), androidx.transition.R.layout.support_simple_spinner_dropdown_item,checkOutSpinner3)

        binding.spinnerTableDropDown.adapter = spinnerAdapter


        binding.spinnerTableDropDown.onItemSelectedListener = object :

            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        return binding.root
    }


}



















