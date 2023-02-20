package com.kodego.app.inventory.app.orgino.restoup.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.compose.AppTheme

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {    
            setContent {
                AppTheme() {
                    Home()
                }
            }
        }
    }
}

@Preview
@Composable
fun Home() {
    LazyColumn(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            item {
                Button(text = "Setup a New Restaurant")
            }
            item {
                Button(text = "Add Employee Account")
            }
            item {
                Button(text = "Setup Restaurant Menu")
            }
            item {
                Button(text = "Add a Table")
        }
    })
}

@Composable
fun Button(text:String) {
    FilledTonalButton(
        modifier = Modifier.width(258.dp),
        onClick =  {},
        colors = ButtonDefaults.filledTonalButtonColors()) {
        Text(text = text)
    }
}