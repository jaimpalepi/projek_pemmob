package com.example.fesnuk.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Reply
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fesnuk.view.theme.*

@Composable
fun ThreadReply(
    replyingTo: String = "I swear, no matt...",
    replyingToAuthor: String = "Anonymous1701",
    author: String = "Anonymous341",
    time: String = "51m",
    content: String = "Wahh Waahhh wahhhh the Assassin role does what they're supposed to do best!! whaaahh wahhhhhhhh",
    imageUrl: String? = null,
    replyCount: String = "45",
    isOP: Boolean = false,
    onMoreOptionsClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C2C2C)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Replying to text
            Text(
                text = "REPLYING TO: \"$replyingTo\" — $replyingToAuthor",
                color = Color(0xFFAFAFAF),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Author and time row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = author,
                        color = Color(0xFFE0E0E0),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "· $time",
                        color = Color(0xFFAFAFAF),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    if (isOP) {
                        Surface(
                            modifier = Modifier.padding(start = 8.dp),
                            color = Color(0xFF424242),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "OP",
                                color = TextPrimary,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(
                                    horizontal = 6.dp,
                                    vertical = 2.dp
                                )
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onMoreOptionsClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = Color(0xFFAFAFAF),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Reply image (if available)
            imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Reply image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Reply content
            Text(
                text = content,
                color = Color(0xFFE0E0E0),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 12.dp)
            )

            // Reply count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Reply,
                    contentDescription = "Replies",
                    tint = Color(0xFFAFAFAF),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = replyCount,
                    color = Color(0xFFAFAFAF),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}