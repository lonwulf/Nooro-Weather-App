package com.lonwulf.nooro.weatherapp.presentation.ui

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size


@Composable
fun LoadImageFromUrl(
    url: String,
    modifier: Modifier = Modifier,
    defaultImg: Int = 0,
    ctx: Context,
    contentScale: ContentScale = ContentScale.Crop
) {
    val imageRequest = ImageRequest.Builder(LocalContext.current).apply {
        if (url.isNotEmpty()) {
            data(url)
            size(Size.ORIGINAL)
            crossfade(true)
        } else {
            data(getDrawable(ctx, defaultImg))
        }
    }.build()

    val painter = rememberAsyncImagePainter(model = imageRequest)
    Box(modifier = modifier) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = modifier.fillMaxSize(),
            contentScale = contentScale
        )

        when (painter.state) {
            is AsyncImagePainter.State.Loading -> CircularProgressIndicator(modifier.align(Alignment.Center))
            is AsyncImagePainter.State.Error -> {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = modifier.fillMaxSize(),
                    contentScale = contentScale
                )
            }

            else -> {}
        }
    }
}
