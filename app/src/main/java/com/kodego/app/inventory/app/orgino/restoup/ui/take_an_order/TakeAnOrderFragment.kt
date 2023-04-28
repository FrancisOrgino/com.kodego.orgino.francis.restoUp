package com.kodego.app.inventory.app.orgino.restoup.ui.take_an_order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ErrorResult
import coil.request.ImageRequest
import com.kodego.app.inventory.app.orgino.restoup.Data.*
import com.kodego.app.inventory.app.orgino.restoup.ui.theme.AppTheme
import com.kodego.app.inventory.app.orgino.restoup.R
import com.kodego.app.inventory.app.orgino.restoup.db
import kotlinx.coroutines.launch
import java.lang.Math.abs
import java.lang.NumberFormatException

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
            items (orderList.value) {order ->
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
    var adminUser by remember { mutableStateOf(db.currentUser.assignedRestaurant.isNullOrEmpty()) }
    var scope = rememberCoroutineScope()
    val dialogTitle = "Create A New Order"
    var orderNotes by remember { mutableStateOf(TextFieldValue("")) }
    var selectedTable by remember { mutableStateOf(TextFieldValue("")) }
    lateinit var orderItem:MenuItem
    var orderItemList by remember { mutableStateOf(mutableMapOf<MenuItem, Int>()) }
    AppTheme {
        if(adminUser){
            Dialog(
                onDismissRequest = {adminUser = false}) {
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
                        restaurantOptions.value.forEach { selectionOption ->
                            DropdownMenuItem(
                                onClick = {
                                    scope.launch {
                                        selectedRestaurantText = selectionOption
                                        db.currentUser.assignedRestaurant = selectionOption
                                        db.loadTableData(db.currentUser)
                                        db.loadMenuItems(db.currentUser)
                                        db.loadOrderData(db.currentUser)
                                    }
                                    restaurantPickerExpanded = false
                                    adminUser = false
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
                                        scope.launch {
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
                                        }
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
                                    tableOptions.value.forEach { selectionOption ->
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
                                                items(menuOfferings.value) {menuOffering ->
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

                                                        AsyncImage(
                                                            model = ImageRequest.Builder(LocalContext.current)
                                                                .data(menuOffering.itemImages?.first().toString())
                                                                .crossfade(true)
                                                                .build(),
                                                            fallback = painterResource(id = R.drawable.logo),
                                                            error = painterResource(id = R.drawable.logo),
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
                                                                .clip(ShapeDefaults.ExtraSmall)
                                                        )
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
                                                        } catch (e: NumberFormatException){
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun OrderCard(order:Order) {
    var showOrderDetailsDialog = remember{ mutableStateOf(false) }
    var showEditOrderDialog = remember{ mutableStateOf(false) }
    if (!db.currentUser.assignedRestaurant.isNullOrEmpty()){
        AppTheme() {
            Surface(
                color = MaterialTheme.colorScheme.surfaceTint,
                shape = ShapeDefaults.ExtraSmall,
                modifier = Modifier
                    .combinedClickable(
                        interactionSource = MutableInteractionSource(),
                        indication = LocalIndication.current,
                        enabled = true,
                        onClickLabel = "Open order detail dialog.",
                        role = null,
                        onLongClickLabel = "",
                        onLongClick = {},
                        onDoubleClick = { showEditOrderDialog.value = true },
                        onClick = { showOrderDetailsDialog.value = true }
                    )
                    .padding(3.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(5.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.inverseSurface,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val fs = if (order.table.toString().length<=2) {70.sp}
                            else if ((order.table.toString().length==3) ||(order.table.toString().length==4)) {
                                abs(70-((order.table.toString().length-2)*15)).sp
                            } else {
                                25.sp
                            }
                            Text(
                                softWrap = true,
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
                    Spacer(modifier = Modifier.width(5.dp))
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ){
                        Text(text = "Value:")
                        Text(
                            softWrap = true,
                            fontWeight = FontWeight.Bold,
                            text = orderValue.toString())
                    }
                }
            }


        }
    }
    if (showOrderDetailsDialog.value) {
        OrderDetailsDialog(showOrderDetailsDialog, order)
    }

    if (showEditOrderDialog.value) {
        EditOrderDialog(showEditOrderDialog, order)
    }
}


@Composable
fun OrderDetailsDialog (showOrderDetailsDialog:MutableState<Boolean>, order:Order) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { showOrderDetailsDialog.value = false }) {
        Surface(
            shape = ShapeDefaults.Small,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.wrapContentSize()) {
            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize(0.95F)
            ){
                Row(
                    Modifier
                        .height(IntrinsicSize.Min)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = { showOrderDetailsDialog.value = false }) {
                        Icon(
                            Icons.Outlined.ArrowBack,
                            contentDescription = "Close order detail view."
                        )
                    }
                    Text(
                        text = "Order ID: ${order.orderID}",
                        modifier = Modifier
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .align(Alignment.CenterVertically)
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(8.dp, 0.dp)
                        .height(IntrinsicSize.Min)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        letterSpacing = 2.sp,
                        fontSize = 8.em,
                        fontWeight = FontWeight.Bold,
                        text = order.table
                    )
                    Column (
                        horizontalAlignment = Alignment.Start
                    ){
                        var totalPrice = 0.00

                        for (menuItem in order.orderItems) {
                            totalPrice += (menuItem.key.itemPrice*menuItem.value)
                        }
                        Text(text = "Total Value:")
                        Text(
                            fontSize = 5.em,
                            fontWeight = FontWeight.ExtraBold,
                            text = totalPrice.toString())
                    }
                }
                //menu items
                for (orderItem in order.orderItems) {
                    OrderItemRow(orderItem.key, orderItem.value)
                }
                //Order Details
                if (!(order.orderNotes.isNullOrEmpty()) || order.orderNotes != "null"){
                    Text(
                        modifier = Modifier.padding(8.dp, 0.dp),
                        text = "Order Notes")
                    OutlinedTextField(
                        readOnly = true,
                        shape = ShapeDefaults.Small,
                        value = TextFieldValue(order.orderNotes),
                        onValueChange = {  },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(8.dp, 0.dp)
                    )
                }

                //Order Status
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp, 0.dp),
                    textAlign = TextAlign.Center,
                    text = order.orderStatus.label)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EditOrderDialog (showEditOrderDialog:MutableState<Boolean>, order:Order){
    var scope = rememberCoroutineScope()
    var _order = cloneOrder(order)
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { showEditOrderDialog.value = false }) {
        Surface(
            shape = ShapeDefaults.Small,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.wrapContentSize()) {
            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize(0.95F)
            ){
                Row(
                    Modifier
                        .height(IntrinsicSize.Min)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = { showEditOrderDialog.value = false }) {
                        Icon(
                            Icons.Outlined.ArrowBack,
                            contentDescription = "Close order detail view."
                        )
                    }
                    Text(
                        text = "Order ID: ${_order.orderID}",
                        modifier = Modifier
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .align(Alignment.CenterVertically)
                    )
                    TextButton(
                        modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally, true),
                        enabled = true,
                        onClick = {
                            scope.launch {
                                /*ToDo*/
                            }
                            showEditOrderDialog.value = false
                        }) {
                        Icon(
                            ImageVector.vectorResource(id = R.drawable.baseline_update_24),
                            contentDescription = "Update order data."
                        )
                        Text(
                            text = "Update",
                            modifier = Modifier.wrapContentSize(Alignment.Center)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(8.dp, 0.dp)
                        .height(IntrinsicSize.Min)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        letterSpacing = 2.sp,
                        fontSize = 8.em,
                        fontWeight = FontWeight.Bold,
                        text = _order.table
                    )
                    Column (
                        horizontalAlignment = Alignment.Start
                    ){
                        var totalPrice = 0.00

                        for (menuItem in _order.orderItems) {
                            totalPrice += (menuItem.key.itemPrice*menuItem.value)
                        }
                        Text(text = "Total Value:")
                        Text(
                            fontSize = 5.em,
                            fontWeight = FontWeight.ExtraBold,
                            text = totalPrice.toString())
                    }
                }
                //menu items
                for (orderItem in _order.orderItems) {
                    OrderItemRow(
                        orderItem.key,
                        orderItem.value,
                        _order,
                        Modifier.combinedClickable(
                            interactionSource = MutableInteractionSource(),
                            indication = LocalIndication.current,
                            enabled = true,
                            onClickLabel = "Open order detail dialog.",
                            role = null,
                            onLongClickLabel = "",
                            onLongClick = {  },
                            onDoubleClick = {  },
                            onClick = {  }
                        ))
                }
                //Order Details
                Text(
                    modifier = Modifier.padding(8.dp, 0.dp),
                    text = "Order Notes")
                OutlinedTextField(
                    readOnly = true,
                    shape = ShapeDefaults.Small,
                    value = TextFieldValue(_order.orderNotes),
                    onValueChange = {
                        _order.orderNotes = it.text
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(8.dp, 0.dp)
                )

                //Order Status
                Text (text = "Set Order Status (currently: ${_order.orderStatus.label})", modifier = Modifier.padding(8.dp, 0.dp))

                var orderStatusPickerExpanded by remember { mutableStateOf(false) }
                var selectedOrderStatusText by remember { mutableStateOf("")}
                var orderStatusOptions = when(db.currentUser.userType) {
                    UserTypes.ADMIN -> listOf(OrderStatus.SentToKitchen.label, OrderStatus.Cooking.label, OrderStatus.ReadyToServe.label, OrderStatus.Served.label)
                    UserTypes.CASHIER -> listOf(OrderStatus.Served.label)
                    UserTypes.WAITER -> listOf(OrderStatus.SentToKitchen.label, OrderStatus.Served.label)
                    UserTypes.KITCHENSTAFF -> listOf(OrderStatus.Cooking.label, OrderStatus.ReadyToServe.label)
                    UserTypes.CUSTOMER -> listOf(OrderStatus.Served.label)
                }
                ExposedDropdownMenuBox(
                    expanded = orderStatusPickerExpanded,
                    onExpandedChange = {
                        orderStatusPickerExpanded = !orderStatusPickerExpanded
                    }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedOrderStatusText,
                        onValueChange = {  },
                        shape = ShapeDefaults.Small,
                        label = { Text(text = "Set Order Status") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .padding(10.dp, 0.dp, 10.dp, 10.dp),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = orderStatusPickerExpanded
                            )
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors()
                    )
                    ExposedDropdownMenu(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 0.dp),
                        expanded = orderStatusPickerExpanded,
                        onDismissRequest = {
                            orderStatusPickerExpanded = false
                        }
                    ) {
                        orderStatusOptions.forEach { selectionOption ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedOrderStatusText = selectionOption
                                    _order.orderStatus = when (selectionOption) {
                                        OrderStatus.SentToKitchen.label -> OrderStatus.SentToKitchen
                                        OrderStatus.Cooking.label ->OrderStatus.Cooking
                                        OrderStatus.ReadyToServe.label -> OrderStatus.ReadyToServe
                                        OrderStatus.Served.label -> OrderStatus.Served
                                        else -> {throw OrderStatusException("Order Status Error on EditOrderDialog")}
                                    }
                                    orderStatusPickerExpanded = false
                                },
                                text = { Text(text = selectionOption) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemRow(menuItem:MenuItem, quantity: Int){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(8.dp, 0.dp)
            .height(55.dp)
            .fillMaxWidth()
    ) {
        Row(
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(menuItem.itemImages?.first() .toString())
                    .crossfade(true)
                    .build(),
                fallback = painterResource(id = R.drawable.logo),
                error = painterResource(id = R.drawable.logo),
                placeholder = painterResource(id = R.drawable.logo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(3.dp)
                    .clip(ShapeDefaults.Small)
            )
            Text(
                fontSize = 5.em,
                fontWeight = FontWeight.ExtraBold,
                text = menuItem.itemName
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ){
            Text(text = "x${quantity.toString()}")
            Text(
                fontSize = 3.em,
                fontWeight = FontWeight.Bold,
                text = menuItem.itemPrice.toString()
            )
        }
    }
}

@Composable
fun OrderItemRow(menuItem:MenuItem, quantity: Int, parentOrder:Order, modifier: Modifier){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(8.dp, 0.dp)
            .height(55.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = modifier
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(menuItem.itemImages?.first() .toString())
                    .crossfade(true)
                    .build(),
                fallback = painterResource(id = R.drawable.logo),
                error = painterResource(id = R.drawable.logo),
                placeholder = painterResource(id = R.drawable.logo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(3.dp)
                    .clip(ShapeDefaults.Small)
            )
            Text(
                fontSize = 5.em,
                fontWeight = FontWeight.ExtraBold,
                text = menuItem.itemName
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ){
            Text(text = "x${quantity.toString()}")
            Text(
                fontSize = 3.em,
                fontWeight = FontWeight.Bold,
                text = menuItem.itemPrice.toString()
            )
        }
    }
}
