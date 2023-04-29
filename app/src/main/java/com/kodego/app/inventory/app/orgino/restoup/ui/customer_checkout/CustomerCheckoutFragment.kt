package com.kodego.app.inventory.app.orgino.restoup.ui.customer_checkout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.compose.runtime.*
import com.kodego.app.inventory.app.orgino.restoup.Data.Table
import com.kodego.app.inventory.app.orgino.restoup.Data.UserTypes
import com.kodego.app.inventory.app.orgino.restoup.R
import com.kodego.app.inventory.app.orgino.restoup.databinding.FragmentCustomerCheckoutBinding
import com.kodego.app.inventory.app.orgino.restoup.db


class CustomerCheckoutFragment : Fragment(R.layout.fragment_customer_checkout) {

    private var _binding: FragmentCustomerCheckoutBinding? = null
    private val binding get() = _binding!!
    lateinit var messag : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCustomerCheckoutBinding.inflate(inflater, container, false)
        return binding.root

    }

    @Composable
    private fun checkOutOrder() {

        var _assignedRestaurant by remember {
            mutableStateOf(db.currentUser.assignedRestaurant)
        }

        val orderList by remember { mutableStateOf(db.restaurantOrderList) }
        if (!_assignedRestaurant.isNullOrEmpty()) {
            db.loadOrderData(db.currentUser)
        }
        if (db.currentUser.userType == UserTypes.ADMIN) {
            db.loadRestaurantList(db.currentUser.adminUID.toString())
        }

    }
    @Composable
    private fun checkOutDialog(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db.loadTableData(db.currentUser)
        db.loadMenuItems(db.currentUser)
        db.loadOrderData(db.currentUser)
        val tableOptionsCheckOut = arrayOf(db.restaurantTableDataList)
        val message1 = "Please Select Table to Check Out"

        binding.spinnerTableDropDown.adapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,tableOptionsCheckOut)

        binding.spinnerTableDropDown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                TODO("Not yet implemented")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }



        }


}


















