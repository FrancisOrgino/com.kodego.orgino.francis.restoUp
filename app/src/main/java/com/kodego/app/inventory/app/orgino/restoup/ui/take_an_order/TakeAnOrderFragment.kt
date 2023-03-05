package com.kodego.app.inventory.app.orgino.restoup.ui.take_an_order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.compose.AppTheme
import com.kodego.app.inventory.app.orgino.restoup.Data.MenuItem
import com.kodego.app.inventory.app.orgino.restoup.Data.Order
import com.kodego.app.inventory.app.orgino.restoup.Data.OrderStatus
import com.kodego.app.inventory.app.orgino.restoup.Data.UserTypes
import com.kodego.app.inventory.app.orgino.restoup.R
import com.kodego.app.inventory.app.orgino.restoup.db
import java.lang.Math.abs

class TakeAnOrderFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    TakeAnOrder()
                }
            }
        }
    }
}

@Composable
fun TakeAnOrder() {
    var _assignedRestaurant by remember {
        mutableStateOf( db.currentUser.assignedRestaurant )
    }
    if (!_assignedRestaurant.isNullOrEmpty()) {
        db.loadOrderData(db.currentUser)
    }
    if(db.currentUser.userType==UserTypes.ADMIN){
        db.loadRestaurantList(db.currentUser.adminUID.toString())
    }

    val orderList by remember { mutableStateOf ( db.restaurantOrderList ) }
    Surface(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        LazyColumn {
            item {
                CreateOrderDialog()
            }
            items (orderList) {order ->
                OrderCard(order)
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrderDialog() {
    db.loadTableData(db.currentUser)
    db.loadMenuItems(db.currentUser)
    var openCreateOrderDialog = remember { mutableStateOf(false) }
    var adminUser = remember { mutableStateOf(db.currentUser.assignedRestaurant.isNullOrEmpty()) }
    val dialogTitle = "Create A New Order"
    var orderNotes by remember { mutableStateOf(TextFieldValue("")) }
    var selectedTable by remember { mutableStateOf(TextFieldValue("")) }
    lateinit var orderItem:MenuItem
    var orderItemList by remember { mutableStateOf(mutableMapOf<MenuItem, Int>()) }
    AppTheme {
        if(adminUser.value){
            Dialog(
                onDismissRequest = {adminUser.value = false}) {
                //RestaurantPicker
                Text (text = "Currently managing this restaurant:", modifier = Modifier.padding(10.dp, 10.dp, 0.dp))
                var restaurantPickerExpanded by remember { mutableStateOf(false) }
                var selectedRestaurantText by remember { mutableStateOf("")}
                var restaurantOptions by remember {mutableStateOf(db.ownedRestaurantsList)}
                ExposedDropdownMenuBox(
                    expanded = restaurantPickerExpanded,
                    onExpandedChange = {
                        restaurantPickerExpanded = !restaurantPickerExpanded
                    }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedRestaurantText,
                        onValueChange = {  },
                        shape = ShapeDefaults.Small,
                        label = { Text(text = "Choose a Restaurant") },
                        modifier = Modifier
                            .menuAnchor()
                            .padding(10.dp, 0.dp, 10.dp, 10.dp),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = restaurantPickerExpanded
                            )
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = restaurantPickerExpanded,
                        onDismissRequest = {
                            restaurantPickerExpanded = false
                        }
                    ) {
                        restaurantOptions.forEach { selectionOption ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedRestaurantText = selectionOption
                                    db.currentUser.assignedRestaurant = selectionOption
                                    db.loadTableData(db.currentUser)
                                    db.loadMenuItems(db.currentUser)
                                    db.loadOrderData(db.currentUser)
                                    restaurantPickerExpanded = false
                                    adminUser.value = false
                                },
                                text = { Text(text = selectionOption) }
                            )
                        }
                    }
                }
            }
        }

        FilledTonalButton(
            enabled = !db.currentUser.assignedRestaurant.isNullOrEmpty(),
            onClick = { openCreateOrderDialog.value = true }
        ){
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Create new order" )
            Text (
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
                    .wrapContentWidth(),
                text = "Create New Order")
        }
        if (openCreateOrderDialog.value) {
            Dialog(
                onDismissRequest = { openCreateOrderDialog.value = false }
            ) {
                Surface(
                    shape = ShapeDefaults.ExtraSmall,
                ) {
                    LazyColumn(
                        //modifier
                    ) {
                        item {
                            //Create Order Header
                            Row(
                                Modifier
                                    .height(IntrinsicSize.Min)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(onClick = { openCreateOrderDialog.value = false }) {
                                    Icon(
                                        Icons.Outlined.ArrowBack,
                                        contentDescription = "Cancel $dialogTitle."
                                    )
                                }
                                Text(
                                    text = dialogTitle,
                                    modifier = Modifier
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                        .align(Alignment.CenterVertically)
                                )
                                TextButton(
                                    enabled = orderItemList.isNotEmpty() && selectedTable.text.isNotEmpty(),
                                    onClick = {
                                        val _newOrder = Order(
                                            selectedTable.text,
                                            "",
                                            orderItemList,
                                            db.currentUser.assignedRestaurant.toString(),
                                            db.currentUser.uid.toString(),
                                            db.currentUser.userType,
                                            db.currentUser.adminUID.toString(),
                                            orderNotes.text
                                        )
                                        db.addOrder(_newOrder, db.currentUser)
                                        openCreateOrderDialog.value = false
                                    })
                                {
                                    Icon(
                                        Icons.Outlined.Add,
                                        contentDescription = "Save $dialogTitle Data."
                                    )
                                    Text(
                                        text = "Save",
                                        modifier = Modifier.wrapContentSize(Alignment.Center)
                                    )
                                }
                            }

                            Text (text = "Restaurant: ${db.currentUser.assignedRestaurant}", modifier = Modifier.padding(10.dp, 10.dp, 0.dp))

                            //TablePicker
                            Text (text = "Currently Taking Orders for this Table:", modifier = Modifier.padding(10.dp, 10.dp, 0.dp))
                            var tableOptions by remember{ mutableStateOf(db.restaurantTableDataList) }
                            var tablePickerExpanded by remember { mutableStateOf(false) }
                            var selectedTableText by remember { mutableStateOf("")}
                            ExposedDropdownMenuBox(
                                expanded = tablePickerExpanded,
                                onExpandedChange = {
                                    tablePickerExpanded = !tablePickerExpanded
                                }
                            ) {
                                OutlinedTextField(
                                    readOnly = true,
                                    value = selectedTableText,
                                    onValueChange = {  },
                                    shape = ShapeDefaults.Small,
                                    label = { Text(text = "Choose a Table") },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .padding(10.dp, 0.dp, 10.dp, 10.dp),
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = tablePickerExpanded
                                        )
                                    },
                                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                                )
                                ExposedDropdownMenu(
                                    expanded = tablePickerExpanded,
                                    onDismissRequest = {
                                        tablePickerExpanded = false
                                    }
                                ) {
                                    tableOptions.forEach { selectionOption ->
                                        DropdownMenuItem(
                                            onClick = {
                                                selectedTableText = selectionOption.tableName
                                                selectedTable = TextFieldValue(selectionOption.tableName)
                                                tablePickerExpanded = false
                                            },
                                            text = { Text(text = selectionOption.tableName) }
                                        )
                                    }
                                }
                            }

                            //MenuItemPicker
                            db.loadMenuItems(db.currentUser)
                            var menuOfferings by remember{mutableStateOf(db.restaurantMenuList)}
                            var showMenuItemPickerDialog = remember{ mutableStateOf(false) }
                            var showOrderItemQuantityDialog = remember{ mutableStateOf(false) }
                            var orderItemAmount by remember { mutableStateOf(TextFieldValue("")) }

                            if (showMenuItemPickerDialog.value) {
                                Dialog(
                                    onDismissRequest = {
                                        showMenuItemPickerDialog.value = false},
                                    properties =  DialogProperties (usePlatformDefaultWidth = true)
                                ) {
                                    Surface(
                                        Modifier
                                            .verticalScroll(rememberScrollState(0), true)
                                            .fillMaxWidth(),
                                        shape = ShapeDefaults.Medium
                                    ) {
                                        Column (
                                            Modifier.fillMaxWidth()
                                        ){
                                            Row(
                                                Modifier
                                                    .height(IntrinsicSize.Min)
                                                    .fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                IconButton(onClick = { showMenuItemPickerDialog.value = false }) {
                                                    Icon(
                                                        Icons.Outlined.ArrowBack,
                                                        contentDescription = "Cancel choosing order item."
                                                    )
                                                }
                                                Text(
                                                    text = "Choose Order Item(s)",
                                                    modifier = Modifier
                                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                                        .align(Alignment.CenterVertically)
                                                )
                                                TextButton(onClick = { showMenuItemPickerDialog.value = false }) {
                                                    Text(text = "Done")
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(15.dp))
                                            LazyVerticalGrid(
                                                modifier = Modifier.aspectRatio(1F/1.5F, true),
                                                columns = GridCells.Fixed(3),
                                            ){
                                                items(menuOfferings) {menuOffering ->
                                                    Card(
                                                        onClick = {
                                                            orderItem = menuOffering
                                                            orderItemAmount = TextFieldValue("")
                                                            orderItemList[orderItem] = 0
                                                            showOrderItemQuantityDialog.value = true },
                                                        elevation = CardDefaults.cardElevation(2.dp),
                                                        modifier = Modifier
                                                            .padding(5.dp)
                                                            .wrapContentSize(Alignment.Center)
                                                    ) {
                                                        menuOffering.itemImages?.let {
                                                            AsyncImage(
                                                                model = ImageRequest.Builder(LocalContext.current)
                                                                    .data(it[0].toString())
                                                                    .crossfade(true)
                                                                    .build(),
                                                                placeholder = painterResource(id = R.drawable.logo),
                                                                contentDescription = null,
                                                                contentScale = ContentScale.Crop,
                                                                modifier = Modifier
                                                                    .aspectRatio(1f)
                                                                    .padding(
                                                                        0.dp,
                                                                        3.dp,
                                                                        0.dp,
                                                                        3.dp
                                                                    )
                                                                    .clip(ShapeDefaults.ExtraSmall))
                                                        }
                                                        Text(
                                                            text = menuOffering.itemName,
                                                            textAlign = TextAlign.Center,
                                                            modifier = Modifier
                                                                .fillMaxWidth())
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                            FilledTonalButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp, 10.dp, 10.dp)
                                    .width(258.dp),
                                enabled = true,
                                onClick =  { showMenuItemPickerDialog.value = true },
                                colors = ButtonDefaults.filledTonalButtonColors()) {
                                Text(text = "Select Order Item(s)")
                            }

                            //Order List
                            Column(
                            ){
                                Spacer(modifier = Modifier.width(15.dp))
                                for (item in orderItemList){
                                    OrderRow(item.key, item.value)
                                }
                            }

                            //OrderItemQuantityDialog
                            if (showOrderItemQuantityDialog.value) {
                                Dialog(
                                    onDismissRequest = {
                                        showOrderItemQuantityDialog.value = false},
                                    properties =  DialogProperties (usePlatformDefaultWidth = true)
                                ) {
                                    Surface(
                                        Modifier
                                            .verticalScroll(rememberScrollState(0), true)
                                            .fillMaxWidth(),
                                        shape = ShapeDefaults.Medium
                                    ) {
                                        Column (
                                            Modifier.fillMaxWidth()
                                        ){
                                            Row(
                                                Modifier
                                                    .height(IntrinsicSize.Min),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                IconButton(onClick = { showOrderItemQuantityDialog.value = false }) {
                                                    Icon(
                                                        Icons.Outlined.ArrowBack,
                                                        contentDescription = null
                                                    )
                                                }
                                                Text(
                                                    text = "Choose Order Quantity",
                                                    modifier = Modifier
                                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                                        .align(Alignment.CenterVertically)
                                                )
                                                TextButton(
                                                    enabled = orderItemList.isNotEmpty(),
                                                    onClick = {
                                                        orderItemList[orderItem] = orderItemAmount.text.toInt()
                                                        showOrderItemQuantityDialog.value = false
                                                    }) {
                                                    Icon(
                                                        Icons.Outlined.Add,
                                                        contentDescription = "Add order quantity data."
                                                    )
                                                    Text(
                                                        text = "Add",
                                                        modifier = Modifier.wrapContentSize(Alignment.Center)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(15.dp))
                                            Row (
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .height(IntrinsicSize.Min),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Card(
                                                    modifier = Modifier.padding(2.dp)
                                                ) {
                                                    orderItem.itemImages?.let {
                                                        AsyncImage(
                                                            model = ImageRequest.Builder(LocalContext.current)
                                                                .data(it[0].toString())
                                                                .crossfade(true)
                                                                .build(),
                                                            placeholder = painterResource(id = R.drawable.logo),
                                                            contentDescription = null,
                                                            contentScale = ContentScale.Crop,
                                                            alignment = Alignment.Center,
                                                            modifier = Modifier
                                                                .size(88.dp)
                                                                .padding(0.dp, 3.dp, 0.dp, 3.dp)
                                                                .clip(ShapeDefaults.ExtraSmall))
                                                    }

                                                    Text(
                                                        modifier = Modifier
                                                            .padding(0.dp, 2.dp)
                                                            .wrapContentSize(),
                                                        text = orderItem.itemName)
                                                }

                                                val orderItemDialogContext = LocalContext.current
                                                OutlinedTextField(
                                                    value = orderItemAmount,
                                                    onValueChange = {
                                                        try{
                                                            if (it.text.isNotEmpty()){
                                                                it.text.toInt()
                                                            }
                                                            orderItemAmount = it
                                                        } catch (e:java.lang.NumberFormatException){
                                                            Toast.makeText(orderItemDialogContext, "Please enter valid order quantity.", Toast.LENGTH_LONG).show()
                                                        }
                                                    },
                                                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                                    modifier = Modifier
                                                        .padding(8.dp, 0.dp, 0.dp, 0.dp)
                                                        .fillMaxHeight()
                                                        .wrapContentWidth(Alignment.Start),
                                                    label = { Text(text = "Order Quantity") }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(15.dp))
                            Text (text = "Order Notes", modifier =  Modifier.padding(10.dp, 0.dp))
                            OutlinedTextField(
                                value = orderNotes,
                                onValueChange = {
                                    orderNotes = it
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp, 0.dp, 10.dp, 10.dp),
                                label = { Text(text = "Any Order Notes") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderRow(menu:MenuItem, quantity:Int){
    Surface(
        modifier = Modifier
            .padding(5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = menu.itemName,
                textAlign = TextAlign.Start)
            Text(
                text = quantity.toString(),
                textAlign = TextAlign.End
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCard(order:Order) {
    if (!db.currentUser.assignedRestaurant.isNullOrEmpty()){
        AppTheme() {
            Surface(
                color = colorResource(id = R.color.md_theme_light_primaryContainer),
                shape = ShapeDefaults.ExtraSmall,
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxWidth(),
                onClick = { /*TODO*/ }) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(5.dp)
                ) {
                    Surface(
                        color = colorResource(id = R.color.md_theme_dark_secondaryContainer),
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val fs = if (order.table.length<=2) {70.sp} else {
                                abs(70-((order.table.length-2)*15)).sp
                            }
                            Text(
                                color = colorResource(id = R.color.md_theme_dark_onSecondaryContainer),
                                fontSize = fs,
                                fontWeight = FontWeight.Bold,
                                text = order.table,
                            )
                        }
                    }

                    val orderItemNames = order.orderItems.keys.map { it.itemName }
                    Spacer(modifier = Modifier.width(5.dp))
                    Column(){
                        Text(text = "ID: ${order.orderID}")
                        Text(text = "Status: ${order.orderStatus.toString()}")
                        Text(text = orderItemNames.toString())
                    }

                    val orderValue = order.orderItems.map { it.key.itemPrice * it.value }.sum()
                    val vfs = if (orderValue.toString().length<=2) {70.sp} else {
                        abs(70-((orderValue.toString().length-2)*15)).sp
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(text = "Value:")
                        Text(
                            fontSize = vfs,
                            text = orderValue.toString())
                    }
                }
            }
        }
    }
}




