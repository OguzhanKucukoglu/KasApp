package com.avatar.app.ui.components

import android.net.Uri
import android.view.ViewGroup
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.avatar.app.R

val KasAppVideoBgColor = Color.White

@Composable
fun AuthVideoBackground(
    modifier: Modifier = Modifier,
    rawResId: Int = R.raw.auth_bg_video,
    dimAlpha: Float = 0.50f,
    isLooping: Boolean = true,
    onCompletion: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val videoUri = remember(rawResId) {
        Uri.parse("android.resource://${context.packageName}/${rawResId}")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(KasAppVideoBgColor)
    ) {
        AndroidView(
            factory = { ctx ->
                VideoView(ctx).apply {
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    setVideoURI(videoUri)
                    setOnPreparedListener { mp ->
                        mp.isLooping = isLooping
                        mp.setVolume(0f, 0f)
                        start()
                    }
                    if (onCompletion != null) {
                        setOnCompletionListener {
                            onCompletion()
                        }
                    }
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Karartma ve kontrast katmanı
        if (dimAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = dimAlpha))
            )
        }
    }
}
