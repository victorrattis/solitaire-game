package com.vhra.game.solitaire

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.viewinterop.AndroidView
import com.vhra.game.solitaire.gl.CardMenuSurfaceView
import com.vhra.game.solitaire.ui.theme.SolitaireTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SolitaireTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                   Column(
                       modifier = Modifier.fillMaxSize()
                           .padding(innerPadding)
                   ) {
                       SolitaireGameCanvas(
                           modifier = Modifier
                               .weight(1f)
                       )
                       Text(
                           text = "Solitaire Game",
                           modifier = Modifier
                       )
                   }
                }
            }
        }
    }
}

@Composable
fun SolitaireGameCanvas(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.clipToBounds(),
        factory = { context ->
            CardMenuSurfaceView(context)
        },
        update = { view ->

        }
    )
}
