package com.example.fesnuk.view.components

import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.fesnuk.model.Attachment

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCarousel(
    attachments: List<Attachment>,
    modifier: Modifier = Modifier
) {
    if (attachments.isEmpty()) return
    
    val imageAttachments = attachments.filter { it.type == "image" }
    if (imageAttachments.isEmpty()) return
    
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    
    Box(modifier = modifier) {
        if (imageAttachments.size == 1) {
            // Single image - no pager needed
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data("https://fesnukberust.blob.core.windows.net/storage/attachments/${imageAttachments[0].content}")
                    .build(),
                imageLoader = imageLoader,
                contentDescription = imageAttachments[0].originalFileName,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
        } else {
            // Multiple images - use horizontal pager
            val pagerState = rememberPagerState(pageCount = { imageAttachments.size })
            
            Column {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) { page ->
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data("https://fesnukberust.blob.core.windows.net/storage/attachments/${imageAttachments[page].content}")
                            .build(),
                        imageLoader = imageLoader,
                        contentDescription = imageAttachments[page].originalFileName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit
                    )
                }
                
                // Page indicators
                if (imageAttachments.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(imageAttachments.size) { index ->
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index == pagerState.currentPage) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            Color.Gray.copy(alpha = 0.5f)
                                        }
                                    )
                            )
                            if (index < imageAttachments.size - 1) {
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}