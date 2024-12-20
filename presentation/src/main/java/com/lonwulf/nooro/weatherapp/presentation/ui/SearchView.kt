package com.lonwulf.nooro.weatherapp.presentation.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lonwulf.nooro.weatherapp.presentation.R
import kotlinx.coroutines.delay

@Composable
fun SearchBar(
    modifier: Modifier,
    onSearch: (String) -> Unit,
    onClick: () -> Unit = {},
    isSearching: (Boolean) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val keyBoardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }
    var isSearching by remember { mutableStateOf(false) }
    var lastSearchQuery by remember { mutableStateOf("") }
    val searchJob = rememberUpdatedState(onSearch)


    LaunchedEffect(searchQuery) {
        if (searchQuery != lastSearchQuery) {
            lastSearchQuery = searchQuery
            isSearching = true
            delay(500L)
            searchJob.value(searchQuery)
            isSearching = false
        }
    }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    onClick.invoke()
                }
            }
        }
    }

    TextField(
        interactionSource = interactionSource,
        value = searchQuery,
        onValueChange = {
            searchQuery = it
        },
        placeholder = {
            Text(
                text = stringResource(R.string.placeholder_text),
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(onSearch = {
            onSearch(searchQuery)
            keyBoardController?.hide()
        }),
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = {
                if (searchQuery.isNotEmpty()) {
                    onSearch(searchQuery)
                } else {
                    searchQuery = ""
                }
            }) {
                Icon(
                    imageVector = if (searchQuery.isEmpty()) Icons.Default.Search else Icons.Default.Clear,
                    contentDescription = stringResource(R.string.search_icn),
                    tint = Color.Gray
                )
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF5F5F5),
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedLabelColor = Color.Cyan,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Gray
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp)
            .clip(RoundedCornerShape(25.dp))
    )

    if (isSearching) {
//        CircularProgressIndicator(modifier = modifier.align(Alignment.Center))
    }
}
