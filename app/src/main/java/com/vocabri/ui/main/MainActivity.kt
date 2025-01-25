package com.vocabri.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vocabri.domain.util.logger
import com.vocabri.ui.theme.VocabriTheme
import org.koin.androidx.compose.KoinAndroidContext

class MainActivity : ComponentActivity() {
    private val log = logger()

    override fun onCreate(savedInstanceState: Bundle?) {
        log.i { "onCreate called" }
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            VocabriTheme {
                KoinAndroidContext {
                    MainScreen()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        log.i { "onStart called" }
    }

    override fun onResume() {
        super.onResume()
        log.i { "onResume called" }
    }

    override fun onPause() {
        super.onPause()
        log.i { "onPause called" }
    }

    override fun onStop() {
        super.onStop()
        log.i { "onStop called" }
    }

    override fun onDestroy() {
        log.i { "onDestroy called" }
        super.onDestroy()
    }
}
