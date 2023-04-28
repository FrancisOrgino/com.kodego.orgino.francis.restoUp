package com.kodego.app.inventory.app.orgino.restoup.ui.order_queue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.kodego.app.inventory.app.orgino.restoup.Data.UserTypes
import com.kodego.app.inventory.app.orgino.restoup.db
import com.kodego.app.inventory.app.orgino.restoup.ui.take_an_order.OrderCard
import com.kodego.app.inventory.app.orgino.restoup.ui.take_an_order.OrderDetailsDialog
import com.kodego.app.inventory.app.orgino.restoup.ui.theme.AppTheme

class OrderQueueFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    OrderQueue()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderQueue(){
    var _assignedRestaurant by remember {
        mutableStateOf( db.currentUser.assignedRestaurant )
    }
    if (!_assignedRestaurant.isNullOrEmpty()) {
        db.loadOrderData(db.currentUser)
    }
    if(db.currentUser.userType== UserTypes.ADMIN){
        db.loadRestaurantList(db.currentUser.adminUID.toString())
    }

    //Order List
    val orderList by remember { mutableStateOf ( db.restaurantOrderList ) }
    Surface(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        LazyColumn(
        ) {
            items (orderList.value) {order ->
                OrderCard(order)
            }
        }
    }
}

