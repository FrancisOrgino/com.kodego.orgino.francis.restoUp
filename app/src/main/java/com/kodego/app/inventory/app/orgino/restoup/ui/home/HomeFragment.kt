package com.kodego.app.inventory.app.orgino.restoup.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.Fragment
import com.example.compose.AppTheme
import com.kodego.app.inventory.app.orgino.restoup.Data.User
import com.kodego.app.inventory.app.orgino.restoup.Data.UserTypes
import com.kodego.app.inventory.app.orgino.restoup.auth
import com.kodego.app.inventory.app.orgino.restoup.db
import java.time.LocalDate
import androidx.lifecycle.lifecycleScope
import com.kodego.app.inventory.app.orgino.restoup.Data.ConvertedUser
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
                    Button(text = "Setup Restaurant Menu")
                }
                item {
                    Button(text = "Add a Table")
                }
            }
        )
    }
}

@Composable
fun Button(text:String) {
    FilledTonalButton(
        modifier = Modifier.width(258.dp),
        enabled = true,
        onClick =  {  },
        colors = ButtonDefaults.filledTonalButtonColors()) {
        Text(text = text)
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
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
@Preview
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
    var newEmployeeInitialPassword by remember {mutableStateOf(TextFieldValue(""))}

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
                            enabled =   newEmployeeFirstName.text.isNotEmpty() &&
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
                                    newEmployeeEmployerUID)
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
                    Text (text = "Restaurant Address", modifier = Modifier.padding(10.dp, 10.dp, 0.dp))
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
        enabled = true,
        onClick =  { openAddNewEmployeeDialog.value = true },
        colors = ButtonDefaults.filledTonalButtonColors()) {
        Text(text = "Add Employee Account")
    }
}

//@Preview
//@Composable
