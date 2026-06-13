package com.example

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.example.data.ELmorsyViewModel
import com.example.data.ELmorsyViewModelFactory
import com.example.ui.screens.ELmorsyAppContent
import com.example.ui.theme.MyApplicationTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        // Initialize central Elmorsy ViewModel using our factory
        val viewModelFactory = ELmorsyViewModelFactory(application)
        val appViewModel = ViewModelProvider(this, viewModelFactory)[ELmorsyViewModel::class.java]

        setContent {
            val isDarkMode by appViewModel.isDarkMode.collectAsState()
            MyApplicationTheme(darkTheme = isDarkMode) {
                // Renders the interactive Arabic medical app content
                ELmorsyAppContent(viewModel = appViewModel)
            }
        }
    }
}
