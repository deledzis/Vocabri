/*
 * MIT License
 *
 * Copyright (c) 2025 Aleksandr Stiagov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.vocabri.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vocabri.logger.logger
import com.vocabri.ui.theme.VocabriTheme

class MainActivity : ComponentActivity() {
    private val log = logger()

    override fun onCreate(savedInstanceState: Bundle?) {
        log.i { "onCreate called" }
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            VocabriTheme {
                MainScreen()
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
