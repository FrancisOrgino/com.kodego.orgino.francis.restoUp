package com.kodego.app.inventory.app.orgino.restoup.ui.take_an_order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.compose.AppTheme
import com.example.compose.md_theme_dark_onPrimary
import com.example.compose.md_theme_dark_onPrimaryContainer
import com.example.compose.md_theme_light_secondary
import com.kodego.app.inventory.app.orgino.restoup.Data.Table
import com.kodego.app.inventory.app.orgino.restoup.auth
import com.kodego.app.inventory.app.orgino.restoup.databinding.FragmentTakeAnOrderBinding
import com.kodego.app.inventory.app.orgino.restoup.db

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
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(  ) {
            item {
                CreateOrder()
            }
//            items () {
//
//            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrder(){
    AppTheme {
        Surface(
            color = md_theme_light_secondary,
            shape = ShapeDefaults.Small,
            onClick = { /*TODO*/ }) {
            Row(
                modifier = Modifier.padding(5.dp)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Create new order" )
                Text (
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .wrapContentWidth(),
                    text = "Create New Order")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CreateOrderDialog() {
    val openCreateOrderDialog = remember { mutableStateOf(true) }
    val dialogTitle = "Create A New Order"
    AppTheme {
        if (openCreateOrderDialog.value) {
            Surface(
                shape = ShapeDefaults.ExtraSmall,
            ) {
                //Header
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
                        enabled = true,
                        onClick = { /*TODO*/ })
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

                //Order Data Fields

            }
        }
    }
}