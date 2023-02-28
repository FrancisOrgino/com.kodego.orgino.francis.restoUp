package com.kodego.app.inventory.app.orgino.restoup.ui.home

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.Fragment
import com.example.compose.AppTheme
import com.kodego.app.inventory.app.orgino.restoup.Data.*
import com.kodego.app.inventory.app.orgino.restoup.auth
import com.kodego.app.inventory.app.orgino.restoup.db
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    Home()
                }
            }
        }
    }
}

@Composable
fun Home() {
    Surface(
        Modifier.fillMaxSize()
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
            content = {
                item {
                    AddNewRestaurant()
                }
                item {
                    AddNewEmployee()
                }
                item {
                    SetupRestaurantMenu()
                }
                item {
                    AddATable()
                }
            }
        )
    }
}
@Composable
fun AddNewRestaurant() {
    var openAddNewRestaurantDialog = remember {mutableStateOf(false)}
    var newRestaurantName by remember { mutableStateOf(TextFieldValue("")) }
    var newRestaurantAddress by remember { mutableStateOf(TextFieldValue("")) }
    if (openAddNewRestaurantDialog.value) {
        Dialog(
            onDismissRequest = {
                openAddNewRestaurantDialog.value = false},
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
                        IconButton(onClick = { openAddNewRestaurantDialog.value = false }) {
                            Icon(
                                Icons.Outlined.ArrowBack,
                                contentDescription = "Cancel setting up a new restaurant."
                            )
                        }
                        Text(
                            text = "Setup a New Restaurant",
                            modifier = Modifier
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .align(Alignment.CenterVertically)
                        )
                        var enableSaveButton = false
                        if (newRestaurantName.text.isNotEmpty() && newRestaurantAddress.text.isNotEmpty()) {enableSaveButton = true}
                        TextButton(
                            enabled = enableSaveButton,
                            onClick = {
                                db.addRestaurant(newRestaurantName.text, newRestaurantAddress.text)
                                db.ownedRestaurantsList.add(newRestaurantName.text)
                                openAddNewRestaurantDialog.value = false
                            }) {
                            Icon(
                                Icons.Outlined.Add,
                                contentDescription = "Save new restaurant data."
                            )
                            Text(
                                text = "Save",
                                modifier = Modifier.wrapContentSize(Alignment.Center)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    Text (text = "Restaurant Name", modifier =  Modifier.padding(10.dp, 0.dp))
                    OutlinedTextField(
                        value = newRestaurantName,
                        onValueChange = {
                            newRestaurantName = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 0.dp),
                        label = { Text(text = "Name your New Restaurant") }
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Text (text = "Restaurant Address", modifier = Modifier.padding(10.dp, 10.dp, 0.dp))
                    OutlinedTextField(
                        value = newRestaurantAddress,
                        onValueChange = {
                            newRestaurantAddress = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 0.dp, 10.dp, 10.dp),
                        label = { Text(text = "Where is your restaurant located?") }
                    )
                }
            }
        }
    }
    FilledTonalButton(
        modifier = Modifier.width(258.dp),
        enabled = true,
        onClick =  { openAddNewRestaurantDialog.value = true },
        colors = ButtonDefaults.filledTonalButtonColors()) {
        Text(text = "Setup a New Restaurant")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewEmployee() {
    val openAddNewEmployeeDialog = remember { mutableStateOf(false) }
    var newEmployeeFirstName by remember {mutableStateOf(TextFieldValue(""))}
    var newEmployeeMiddleName by remember {mutableStateOf(TextFieldValue(""))}
    var newEmployeeLastName by remember {mutableStateOf(TextFieldValue(""))}
    var newEmployeeBirthdate by remember {mutableStateOf<LocalDate?>(null)}
    var newEmployeeEmployerUID = auth.uid!!
    var newEmployeeRole by remember {mutableStateOf<UserTypes?>(null)}
    var newEmployeeEmail by remember {mutableStateOf(TextFieldValue(""))}
    var selectedRestaurant by remember {mutableStateOf(TextFieldValue(""))}
    var newEmployeeInitialPassword by remember {mutableStateOf(TextFieldValue(""))}
    db.loadRestaurantList(auth.uid!!)
    val restaurantOptions by remember {mutableStateOf(db.ownedRestaurantsList)}

    if (openAddNewEmployeeDialog.value) {
        Dialog(
            onDismissRequest = {openAddNewEmployeeDialog.value = false},
            properties =  DialogProperties (usePlatformDefaultWidth = false)
        ) {
            Surface(
                Modifier
                    .verticalScroll(rememberScrollState(0), true)
                    .fillMaxWidth(),
                shape = ShapeDefaults.Medium
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier
                            .height(IntrinsicSize.Min)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { openAddNewEmployeeDialog.value = false }) {
                            Icon(
                                Icons.Outlined.ArrowBack,
                                contentDescription = "Cancel adding a new employee."
                            )
                        }
                        Text(
                            text = "Add Restaurant Employee",
                            modifier = Modifier
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .align(Alignment.CenterVertically)
                        )

                        TextButton(
                            enabled =   selectedRestaurant.text.isNotEmpty() &&
                                    newEmployeeFirstName.text.isNotEmpty() &&
                                    newEmployeeMiddleName.text.isNotEmpty() &&
                                    newEmployeeLastName.text.isNotEmpty() &&
                                    newEmployeeBirthdate != null &&
                                    newEmployeeEmail.text.isNotEmpty() &&
                                    newEmployeeInitialPassword.text.isNotEmpty() &&
                                    newEmployeeRole != null,
                            onClick = {
                                val newEmployee = ConvertedUser(
                                    newEmployeeFirstName.text,
                                    newEmployeeMiddleName.text,
                                    newEmployeeLastName.text,
                                    newEmployeeBirthdate,
                                    newEmployeeEmail.text,
                                    newEmployeeEmail.text,
                                    newEmployeeInitialPassword.text,
                                    newEmployeeRole!!,
                                    newEmployeeEmployerUID,
                                    selectedRestaurant.text
                                )
                                db.addEmployeeAccount(newEmployeeEmail.text, newEmployeeInitialPassword.text, newEmployee)
                                openAddNewEmployeeDialog.value = false
                            }) {
                            Icon(
                                Icons.Outlined.Add,
                                contentDescription = "Save restaurant employee data."
                            )
                            Text(
                                text = "Save",
                                modifier = Modifier.wrapContentSize(Alignment.Center)
                            )
                        }
                    }

                    Text (text = "Currently Setting Up Employee Account for this Restaurant:", modifier = Modifier.padding(10.dp, 10.dp, 0.dp))

                    var restaurantPickerExpanded by remember { mutableStateOf(false) }
                    var selectedRestaurantText by remember { mutableStateOf("")}
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
                                        selectedRestaurant = TextFieldValue(selectionOption)
                                        restaurantPickerExpanded = false
                                    },
                                    text = { Text(text = selectionOption) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(15.dp))
                    Text (text = "First Name", modifier =  Modifier.padding(10.dp, 0.dp))
                    OutlinedTextField(
                        value = newEmployeeFirstName,
                        onValueChange = {
                            newEmployeeFirstName = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 0.dp),
                        label = { Text(text = "Enter employee's first name.") }
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Text (text = "Middle Name", modifier =  Modifier.padding(10.dp, 10.dp, 0.dp))
                    OutlinedTextField(
                        value = newEmployeeMiddleName,
                        onValueChange = {
                            newEmployeeMiddleName = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 0.dp),
                        label = { Text(
                            text = "Enter employee's middle name.",
                            modifier = Modifier.wrapContentWidth(Alignment.Start)) }
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Text (text = "Last Name", modifier =  Modifier.padding(10.dp, 10.dp, 0.dp))
                    OutlinedTextField(
                        value = newEmployeeLastName,
                        onValueChange = {
                            newEmployeeLastName = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 0.dp),
                        label = { Text(text = "Enter employee's last name.") }
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Text (text = "Employee's Birthdate", modifier =  Modifier.padding(10.dp, 10.dp, 0.dp))
                    val openEmployeeBirthdatePickerDialog = remember { mutableStateOf(false) }
                    val datePickerState = rememberDatePickerState()
                    val confirmEnabled = remember {derivedStateOf { datePickerState.selectedDateMillis != null }}
                    if (openEmployeeBirthdatePickerDialog.value) {
                        DatePickerDialog(
                            onDismissRequest = {
                                openEmployeeBirthdatePickerDialog.value = false
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        newEmployeeBirthdate = LocalDate.ofEpochDay(datePickerState.selectedDateMillis!!/86400000)
                                        openEmployeeBirthdatePickerDialog.value = false
                                    },
                                    enabled = confirmEnabled.value
                                ) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        openEmployeeBirthdatePickerDialog.value = false
                                    }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }
                    var selectedEmployeeBirthdate = if(datePickerState.selectedDateMillis==null) {
                        "Pick Employee's Birthday from Calendar"
                    } else {
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(LocalDate.ofEpochDay(datePickerState.selectedDateMillis!!/86400000))
                    }
                    TextButton(
                        shape = ShapeDefaults.Medium,
                        onClick = {
                            openEmployeeBirthdatePickerDialog.value = true
                        }) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Save new restaurant employee data."
                        )
                        Text(
                            text = selectedEmployeeBirthdate,
                            modifier = Modifier.wrapContentSize(Alignment.Center)
                        )
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    Text (text = "Employee Email", modifier =  Modifier.padding(10.dp, 10.dp, 0.dp))
                    OutlinedTextField(
                        value = newEmployeeEmail,
                        onValueChange = {
                            newEmployeeEmail = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 0.dp),
                        label = { Text(text = "Enter employee's login email.") }
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Text (text = "Employee's Initial Password", modifier =  Modifier.padding(10.dp, 10.dp, 0.dp))
                    OutlinedTextField(
                        value = newEmployeeInitialPassword,
                        onValueChange = {
                            newEmployeeInitialPassword = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 0.dp),
                        label = { Text(text = "Enter employee's login password.") }
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Text (text = "Employee Role", modifier = Modifier.padding(10.dp, 10.dp, 0.dp))
                    val options = listOf("Waiter", "Cashier", "Kitchen Staff")
                    var expanded by remember { mutableStateOf(false) }
                    var selectedOptionText by remember { mutableStateOf("")}

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = {
                            expanded = !expanded
                        }
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = selectedOptionText,
                            onValueChange = {  },
                            shape = ShapeDefaults.Small,
                            label = { Text("Select Employee Role") },
                            modifier = Modifier
                                .menuAnchor()
                                .padding(10.dp, 0.dp, 10.dp, 10.dp),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expanded
                                )
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = {
                                expanded = false
                            }
                        ) {
                            options.forEach { selectionOption ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedOptionText = selectionOption
                                        when (selectedOptionText) {
                                            "Waiter" -> newEmployeeRole = UserTypes.WAITER
                                            "Cashier" -> newEmployeeRole = UserTypes.CASHIER
                                            "Kitchen Staff" -> newEmployeeRole = UserTypes.KITCHENSTAFF
                                        }
                                        expanded = false
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
    FilledTonalButton(
        modifier = Modifier.width(258.dp),
        enabled = restaurantOptions.isNotEmpty(),
        onClick =  { openAddNewEmployeeDialog.value = true },
        colors = ButtonDefaults.filledTonalButtonColors()) {
        Text(text = "Add Employee Account")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun SetupRestaurantMenu() {
    var openSetupRestaurantMenuDialog = remember { mutableStateOf(false) }
    var openCreateMenuCategoryDialog = remember { mutableStateOf(false) }
    var selectedRestaurant by remember {mutableStateOf(TextFieldValue(""))}
    var menuItemCategory by remember {mutableStateOf(TextFieldValue(""))}
    var menuItemName by remember {mutableStateOf(TextFieldValue(""))}
    var menuItemPrice by remember {mutableStateOf(TextFieldValue(""))}
    var menuItemImage = remember {mutableStateOf<List<Uri>?>(null)}
    db.loadRestaurantList(auth.uid!!)
    val restaurantOptions by remember {mutableStateOf(db.ownedRestaurantsList)}

    if (openSetupRestaurantMenuDialog.value) {
        Dialog(
            onDismissRequest = { openSetupRestaurantMenuDialog.value = false }
        ) {
            Surface(
                Modifier
                    .verticalScroll(rememberScrollState(0), true)
                    .fillMaxWidth(),
                shape = ShapeDefaults.Medium
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier
                            .height(IntrinsicSize.Min)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { openSetupRestaurantMenuDialog.value = false }) {
                            Icon(
                                Icons.Outlined.ArrowBack,
                                contentDescription = "Cancel adding a new menu item."
                            )
                        }
                        Text(
                            text = "Setup Restaurant Menu",
                            modifier = Modifier
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .align(Alignment.CenterVertically)
                        )

                        TextButton(
                            enabled = selectedRestaurant.text.isNotEmpty() &&
                                    menuItemCategory.text.isNotEmpty() &&
                                    menuItemName.text.isNotEmpty() &&
                                    menuItemPrice.text.isNotEmpty(),
                            onClick = {
                                val newMenuItemData = if (menuItemImage.value==null) {
                                    MenuItem(selectedRestaurant.text, menuItemCategory.text, menuItemName.text, menuItemPrice.text.toDouble())
                                } else {
                                    MenuItem(selectedRestaurant.text, menuItemCategory.text, menuItemName.text, menuItemPrice.text.toDouble(), menuItemImage.value!!)
                                }
                                db.addMenuItem(newMenuItemData, auth.uid!!)
                                openSetupRestaurantMenuDialog.value = false
                            })
                        {
                            Icon(
                                Icons.Outlined.Add,
                                contentDescription = "Save restaurant menu item data."
                            )
                            Text(
                                text = "Save",
                                modifier = Modifier.wrapContentSize(Alignment.Center)
                            )
                        }
                    }

                    //RestaurantPicker
                    Text (text = "Currently Setting Up the Menu for this Restaurant:", modifier = Modifier.padding(10.dp, 10.dp, 0.dp))

                    var restaurantPickerExpanded by remember { mutableStateOf(false) }
                    var selectedRestaurantText by remember { mutableStateOf("")}
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
                                        selectedRestaurant = TextFieldValue(selectionOption)
                                        restaurantPickerExpanded = false
                                    },
                                    text = { Text(text = selectionOption) }
                                )
                            }
                        }
                    }

                    Text (text = "Item Menu Category", modifier = Modifier.padding(10.dp, 10.dp, 0.dp))
                    db.loadMenuCategoryList(selectedRestaurant.text, auth.uid!!)
                    val context = LocalContext.current
                    val menuCategoryOptions by remember { mutableStateOf(db.restaurantMenuCategoryList) }
                    var menuCategoryOptionsExpanded by remember { mutableStateOf(false) }
                    var selectedMenuCategoryOptionText by remember { mutableStateOf("")}
                    ExposedDropdownMenuBox(
                        expanded = menuCategoryOptionsExpanded,
                        onExpandedChange = {
                            menuCategoryOptionsExpanded =
                                if (selectedRestaurant.text.isNotBlank()) {
                                    !menuCategoryOptionsExpanded
                                } else {
                                    Toast.makeText(context, "Please choose a restaurant first.", Toast.LENGTH_SHORT).show()
                                    false
                                }
                        }
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = selectedMenuCategoryOptionText,
                            onValueChange = {  },
                            shape = ShapeDefaults.Small,
                            label = { Text(text = "Select Menu Item Category") },
                            modifier = Modifier
                                .menuAnchor()
                                .padding(10.dp, 0.dp, 10.dp, 10.dp),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = menuCategoryOptionsExpanded
                                )
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = menuCategoryOptionsExpanded,
                            onDismissRequest = {
                                menuCategoryOptionsExpanded = false
                            }
                        ) {
                            menuCategoryOptions.forEach { selectionOption ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedMenuCategoryOptionText = selectionOption
                                        menuItemCategory = TextFieldValue(selectionOption)
                                        menuCategoryOptionsExpanded = false
                                    },
                                    text = { Text(text = selectionOption) }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text(text = "Add New Menu Category") },
                                onClick = {
                                    openCreateMenuCategoryDialog.value = true
                                }
                            )
                            //CreateMenuCategoryDialog
                            var newMenuCategory by remember { mutableStateOf(TextFieldValue("")) }
                            if (openCreateMenuCategoryDialog.value) {
                                Dialog(
                                    onDismissRequest = { openCreateMenuCategoryDialog.value = false },
                                ) {
                                    Surface(
                                        Modifier
                                            .verticalScroll(rememberScrollState(0), true)
                                            .fillMaxWidth(),
                                        shape = ShapeDefaults.Medium
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                Modifier
                                                    .height(IntrinsicSize.Min)
                                                    .fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                IconButton(onClick = { openCreateMenuCategoryDialog.value = false }) {
                                                    Icon(
                                                        Icons.Outlined.ArrowBack,
                                                        contentDescription = "Cancel adding a new menu category."
                                                    )
                                                }
                                                Text(
                                                    text = "Add Menu Category",
                                                    modifier = Modifier
                                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                                        .align(Alignment.CenterVertically)
                                                )

                                                TextButton(
                                                    enabled = newMenuCategory.text.isNotEmpty(),
                                                    onClick = {
                                                        db.addMenuCategory(newMenuCategory.text, selectedRestaurant.text, auth.uid!!)
                                                        menuItemCategory = newMenuCategory
                                                        selectedMenuCategoryOptionText = newMenuCategory.text
                                                        menuCategoryOptionsExpanded = false
                                                        openCreateMenuCategoryDialog.value = false
                                                    })
                                                {
                                                    Icon(
                                                        Icons.Outlined.Add,
                                                        contentDescription = "Save restaurant menu category."
                                                    )
                                                    Text(
                                                        text = "Save",
                                                        modifier = Modifier.wrapContentSize(Alignment.Center)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(15.dp))
                                            Text(text = "Menu Category", modifier = Modifier.padding(10.dp, 0.dp))
                                            OutlinedTextField(
                                                value = newMenuCategory,
                                                onValueChange = {
                                                    newMenuCategory = it
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(10.dp, 0.dp, 10.dp, 10.dp),
                                                label = { Text(text = "Enter New Menu Category") }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    Text (text = "Menu Item Name", modifier =  Modifier.padding(10.dp, 0.dp))
                    OutlinedTextField(
                        value = menuItemName,
                        onValueChange = {
                            menuItemName = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 0.dp),
                        label = { Text(text = "What's your menu offering called?") }
                    )

                    Spacer(modifier = Modifier.width(15.dp))
                    Text (text = "Menu Item Price", modifier =  Modifier.padding(10.dp, 0.dp))
                    val priceContext = LocalContext.current
                    OutlinedTextField(
                        value = menuItemPrice,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        onValueChange = {
                            try {
                                it.text.toDouble()
                                menuItemPrice = it
                            } catch (e:java.lang.NumberFormatException) {
                                Toast.makeText(priceContext, "Please enter valid price value.", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 0.dp),
                        label = { Text(text = "Give your menu offering a price!") }
                    )

                    Spacer(modifier = Modifier.width(25.dp))
                    var imageData = remember {mutableStateOf<List<Uri>?>(null)}
                    val imagePickerContext = LocalContext.current

                    val contracts = ActivityResultContracts.GetMultipleContents()
                    val launcher = rememberLauncherForActivityResult(contract = contracts, onResult = {
                        if (it.isEmpty()) {
                            imageData.value = null
                        } else {
                            imageData.value = it
                            menuItemImage.value = it
                        }
                    })
                    if (imageData.value==null) {
                        Button(
                            modifier = Modifier.fillMaxWidth()
                                .padding(10.dp, 0.dp),
                            onClick = {
                                launcher.launch("image/*")
                            }){
                            Text(text = "Pick Menu Item Image")
                        }
                    }
                    if (imageData.value!=null) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier
                                .aspectRatio(3F/1F, true),
                            contentPadding = PaddingValues(10.dp)
                        ) {
                            imageData.value?.let { items(it) {imageUri:Uri ->
                                if (Build.VERSION.SDK_INT <28) {
                                    val imageBitmap = MediaStore.Images.Media.getBitmap(imagePickerContext.contentResolver, imageUri)
                                    MenuImage(imageBitmap)
                                }
                                else {
                                    val imageSource = ImageDecoder.createSource(imagePickerContext.contentResolver, imageUri)
                                    val imageBitmap = ImageDecoder.decodeBitmap(imageSource)
                                    MenuImage(imageBitmap)
                                } }
                            }
                        }
                    }
                }
            }
        }
    }
    FilledTonalButton(
        modifier = Modifier.width(258.dp),
        enabled = restaurantOptions.isNotEmpty(),
        onClick =  { openSetupRestaurantMenuDialog.value = true },
        colors = ButtonDefaults.filledTonalButtonColors()) {
        Text(text = "Setup Restaurant Menu")
    }
}

@Composable
fun MenuImage(imageBitmap: Bitmap) {
    Image(
        painter = BitmapPainter(imageBitmap.asImageBitmap()),
        contentScale = ContentScale.None,
        modifier = Modifier
            .clip(RoundedCornerShape(CornerSize(20.dp)))
            .padding(5.dp)
            .aspectRatio(1F / 1F, false),
        contentDescription = null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddATable(){
    var openAddATableDialog = remember { mutableStateOf(false) }
    var selectedRestaurant by remember {mutableStateOf(TextFieldValue(""))}
    var tableName by remember {mutableStateOf(TextFieldValue(""))}
    var tableCapacity by remember {mutableStateOf(TextFieldValue(""))}
    db.loadRestaurantList(auth.uid!!)
    val restaurantOptions by remember {mutableStateOf(db.ownedRestaurantsList)}

    if (openAddATableDialog.value) {
        Dialog(
            onDismissRequest = { openAddATableDialog.value = false }
        ) {
            Surface(
                Modifier
                    .verticalScroll(rememberScrollState(0), true)
                    .fillMaxWidth(),
                shape = ShapeDefaults.Medium
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier
                            .height(IntrinsicSize.Min)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { openAddATableDialog.value = false }) {
                            Icon(
                                Icons.Outlined.ArrowBack,
                                contentDescription = "Cancel adding a table."
                            )
                        }
                        Text(
                            text = "Add A Table",
                            modifier = Modifier
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .align(Alignment.CenterVertically)
                        )

                        TextButton(
                            enabled = selectedRestaurant.text.isNotEmpty() &&
                                    tableCapacity.text.isNotEmpty() &&
                                    tableName.text.isNotEmpty(),
                            onClick = {
                                val newTable = Table(tableName.text, tableCapacity.text.toInt(), selectedRestaurant.text)
                                db.addTable(newTable, auth.uid!!)
                                openAddATableDialog.value = false
                            })
                        {
                            Icon(
                                Icons.Outlined.Add,
                                contentDescription = "Save restaurant table data."
                            )
                            Text(
                                text = "Save",
                                modifier = Modifier.wrapContentSize(Alignment.Center)
                            )
                        }
                    }

                    Text (text = "Currently Setting Up Table Data for this Restaurant:", modifier = Modifier.padding(10.dp, 10.dp, 0.dp))

                    var restaurantPickerExpanded by remember { mutableStateOf(false) }
                    var selectedRestaurantText by remember { mutableStateOf("")}
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
                                        selectedRestaurant = TextFieldValue(selectionOption)
                                        restaurantPickerExpanded = false
                                    },
                                    text = { Text(text = selectionOption) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(15.dp))
                    Text (text = "Table", modifier =  Modifier.padding(10.dp, 0.dp))
                    OutlinedTextField(
                        value = tableName,
                        onValueChange = {
                            tableName = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 0.dp),
                        label = { Text(text = "Give your table a number or a name.") }
                    )

                    Spacer(modifier = Modifier.width(15.dp))
                    Text (text = "Table Capacity", modifier =  Modifier.padding(10.dp, 0.dp))
                    val priceContext = LocalContext.current
                    OutlinedTextField(
                        value = tableCapacity,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        onValueChange = {
                            try {
                                it.text.toInt()
                                tableCapacity = it
                            } catch (e:java.lang.NumberFormatException) {
                                Toast.makeText(priceContext, "Only whole number inputs are allowed here.", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 0.dp, 0.dp, 10.dp),
                        label = { Text(text = "How many people can be seated on that table?") }
                    )
                }
            }
        }
    }
    FilledTonalButton(
        modifier = Modifier.width(258.dp),
        enabled = restaurantOptions.isNotEmpty(),
        onClick =  { openAddATableDialog.value = true },
        colors = ButtonDefaults.filledTonalButtonColors()) {
        Text(text = "Setup Restaurant Table Data")
    }
}
