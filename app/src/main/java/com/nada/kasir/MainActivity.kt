package com.nada.kasir

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import com.nada.kasir.navigation.NadaNavGraph

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // TODO Phase 4: bungkus dengan ThemeConfig dinamis (baca warnaUtama dari StoreEntity)
            MaterialTheme {
                Surface(modifier = Modifier) {
                    NadaNavGraph()
                }
            }
        }
    }
}
