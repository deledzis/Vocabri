package com.vocabri.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vocabri.R

@Composable
fun DeleteBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.error)
            .padding(16.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Icon(
            tint = MaterialTheme.colorScheme.onError,
            modifier = Modifier.size(16.dp),
            imageVector = Icons.Default.Delete,
            contentDescription = stringResource(R.string.dictionary_delete_word),
        )
    }
}
