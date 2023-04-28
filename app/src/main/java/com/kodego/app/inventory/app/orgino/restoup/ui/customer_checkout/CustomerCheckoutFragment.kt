package com.kodego.app.inventory.app.orgino.restoup.ui.customer_checkout

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.TextFieldValue
import com.kodego.app.inventory.app.orgino.restoup.AddItem
import com.kodego.app.inventory.app.orgino.restoup.Data.MenuItem
import com.kodego.app.inventory.app.orgino.restoup.Data.UserTypes
import com.kodego.app.inventory.app.orgino.restoup.R
import com.kodego.app.inventory.app.orgino.restoup.databinding.FragmentCustomerCheckoutBinding
import com.kodego.app.inventory.app.orgino.restoup.db
import com.kodego.app.inventory.app.orgino.restoup.ui.take_an_order.CreateOrderDialog
import com.kodego.app.inventory.app.orgino.restoup.ui.theme.AppTheme


class CustomerCheckoutFragment : Fragment(R.layout.fragment_customer_checkout) {

    private var _binding: FragmentCustomerCheckoutBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


//        _binding = FragmentCustomerCheckoutBinding.inflate(inflater, container, false)
//        return binding.root

        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    checkOutOrder()
                }
            }
        }

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
    override fun checkOutDialog(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        db.loadTableData(db.currentUser)
        db.loadMenuItems(db.currentUser)
        db.loadOrderData(db.currentUser)
//    var openCheckOutOrderDialog = remember { mutableStateOf(false) }
//    var adminUser by remember { mutableStateOf(db.currentUser.assignedRestaurant.isNullOrEmpty()) }
//    var scope = rememberCoroutineScope()
//    val dialogTitle = "Check Out Order?"
//    var selectedTable by remember { mutableStateOf(db.restaurantTableDataList) } }
    val tableDropDown = binding.spinnerTableDropDown
    val arrayOfSelectedTable = arrayOf(db.restaurantTableDataList)
    val selectedTableAdapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item)
    selectedTableAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tableDropDown.adapter = adapter
        tableDropDown.onItemClickListener = object : AdapterView.OnItemSelectedListener
    }
}









