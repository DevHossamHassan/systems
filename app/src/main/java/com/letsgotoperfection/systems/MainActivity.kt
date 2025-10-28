package com.letsgotoperfection.systems

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.letsgotoperfection.systems.navigation.AppNavigation
import com.letsgotoperfection.systems.ui.theme.SystemsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SystemsTheme {
                AppNavigation()
            }
        }
    }
}