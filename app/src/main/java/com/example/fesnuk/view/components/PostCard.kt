package com.example.fesnuk.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.outlined.AddReaction
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Reply
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Reply
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fesnuk.data.ReactionStorage
import com.example.fesnuk.data.NookRepository
import com.example.fesnuk.model.PostApiData
import com.example.fesnuk.ui.components.EmojiPicker
import com.example.fesnuk.view.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.outlined.Forum

enum class PostRenderMode {
    PREVIEW,
    FULL
}

@Composable
fun PostCard(
    post: PostApiData,
    renderMode: PostRenderMode = PostRenderMode.PREVIEW,
    onPostClick: () -> Unit = {},
    onNookClick: () -> Unit = {},
    onReplyClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onMoreOptionsClick: () -> Unit = {},
    onPostUpdated: () -> Unit = {} // Callback to refresh posts after reaction
) {
    val context = LocalContext.current
    val reactionStorage = remember { ReactionStorage(context) }
    val repository = remember { NookRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    // Local state for reactions to prevent flickering
    var localReactions by remember { mutableStateOf(post.reactions) }
    
    // Local state to track user reactions for immediate UI feedback
    var localUserReactions by remember { 
        mutableStateOf(
            post.reactions.keys.filter { unicode ->
                reactionStorage.hasUserReacted(post.id, unicode)
            }.toSet()
        )
    }
    var showEmojiPicker by remember { mutableStateOf(false) }

    // Handle reaction logic
    fun handleReaction(unicode: String, isAdding: Boolean) {
        coroutineScope.launch {
            Log.d("PostCard", "handleReaction called - postId: ${post.id}, unicode: $unicode, isAdding: $isAdding")
            
            // Check if user has already reacted with this emoji
            val hasReacted = reactionStorage.hasUserReacted(post.id, unicode)
            Log.d("PostCard", "User has already reacted: $hasReacted")
            
            // Prevent spam - if user is trying to add a reaction they already have
            if (isAdding && hasReacted) {
                Log.w("PostCard", "Preventing duplicate reaction - user already reacted with $unicode")
                return@launch
            }
            
            // Prevent removing a reaction they don't have
            if (!isAdding && !hasReacted) {
                Log.w("PostCard", "Preventing removal of non-existent reaction - user hasn't reacted with $unicode")
                return@launch
            }
            
            val action = if (isAdding) 1 else -1
            Log.d("PostCard", "Making API call - postId: ${post.id}, unicode: $unicode, action: $action")
            
            // Optimistically update local reactions for immediate UI feedback
            val currentCount = localReactions[unicode] ?: 0
            val newCount = if (isAdding) currentCount + 1 else maxOf(0, currentCount - 1)
            
            localReactions = localReactions.toMutableMap().apply {
                if (newCount > 0) {
                    put(unicode, newCount)
                } else {
                    remove(unicode)
                }
            }
            
            // Optimistically update user reaction state for immediate highlighting
            localUserReactions = if (isAdding) {
                localUserReactions + unicode
            } else {
                localUserReactions - unicode
            }
            
            // Call API
            repository.addReaction(post.id, unicode, action)
                .onSuccess {
                    Log.d("PostCard", "API call successful - updating local storage")
                    
                    // Update local storage
                    if (isAdding) {
                        reactionStorage.addUserReaction(post.id, unicode)
                        Log.d("PostCard", "Added reaction to local storage - postId: ${post.id}, unicode: $unicode")
                    } else {
                        reactionStorage.removeUserReaction(post.id, unicode)
                        Log.d("PostCard", "Removed reaction from local storage - postId: ${post.id}, unicode: $unicode")
                    }
                    
                    // No need to call onPostUpdated() - we're managing state locally
                    Log.d("PostCard", "Reaction updated successfully without full refresh")
                }
                .onFailure { exception ->
                    // Revert optimistic update on failure
                    Log.e("PostCard", "Failed to update reaction: ${exception.message}, reverting local state", exception)
                    
                    val revertedCount = if (isAdding) maxOf(0, newCount - 1) else newCount + 1
                    localReactions = localReactions.toMutableMap().apply {
                        if (revertedCount > 0) {
                            put(unicode, revertedCount)
                        } else {
                            remove(unicode)
                        }
                    }
                    
                    // Revert user reaction state
                    localUserReactions = if (isAdding) {
                        localUserReactions - unicode
                    } else {
                        localUserReactions + unicode
                    }
                }
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onPostClick() },
        colors = CardDefaults.cardColors(
            containerColor = CardBackground,
            contentColor = CardBackground
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with nook name, time, and more options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "::${post.nookName}",
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable { onNookClick() }
                    )
                    Text(
                        text = formatTimeAgo(post.createdAt),
                        color = TextTertiary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                IconButton(
                    onClick = onMoreOptionsClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Post title
            Text(
                text = post.title,
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Post attachments (images) - only show in preview if there are attachments
            if (post.attachments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                ImageCarousel(
                    attachments = post.attachments,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Post content - conditional rendering based on mode
            when (renderMode) {
                PostRenderMode.PREVIEW -> {
                    if (post.attachments.isEmpty()) {
                        // Show limited content when no attachments in preview mode
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (post.content.length > 255) {
                                "${post.content.take(255)}..."
                            } else {
                                post.content
                            },
                            color = TextPrimary,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    // If there are attachments, don't show content in preview mode
                }
                PostRenderMode.FULL -> {
                    // Show full content in full mode
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = post.content,
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reactions section (bottom left)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reactions on the left
                ReactionSection(
                    reactions = localReactions, // Use local state instead of post.reactions
                    postId = post.id,
                    reactionStorage = reactionStorage,
                    localUserReactions = localUserReactions, // Pass local user reactions
                    onEmojiPickerClick = { showEmojiPicker = true },
                    onReactionToggle = { unicode, isAdding ->
                        handleReaction(unicode, isAdding)
                    }
                )

                // Action buttons on the right
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Reply button
                    TextButton(
                        onClick = onReplyClick,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = TextTertiary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Forum,
                            contentDescription = "Reply",
                            modifier = Modifier.size(16.dp),
                            tint = TextTertiary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = post.commentCount.toString(),
                            color = TextTertiary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    // Emoji picker dialog
    EmojiPicker(
        isVisible = showEmojiPicker,
        onEmojiSelected = { unicode ->
            // Determine if we're adding or removing based on current local state
            val isAdding = !localUserReactions.contains(unicode)
            handleReaction(unicode, isAdding)
        },
        onDismiss = { showEmojiPicker = false }
    )
}

@Composable
private fun ReactionSection(
    reactions: Map<String, Int>,
    postId: Int,
    reactionStorage: ReactionStorage,
    localUserReactions: Set<String>, // Add local user reactions parameter
    onEmojiPickerClick: () -> Unit,
    onReactionToggle: (String, Boolean) -> Unit
) {
    // Use FlowRow-like behavior with Column and Row for wrapping
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val allReactions = mutableListOf<@Composable () -> Unit>()
        
        // Add emoji picker button
        allReactions.add {
            IconButton(
                onClick = onEmojiPickerClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AddReaction,
                    contentDescription = "Add reaction",
                    tint = TextTertiary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        // Add reaction chips
        reactions.entries.forEach { (unicode, count) ->
            if (count > 0) {
                allReactions.add {
                    ReactionChip(
                        unicode = unicode,
                        count = count,
                        isSelected = localUserReactions.contains(unicode), // Use local state
                        onClick = {
                            // Determine if we're adding or removing based on current local state
                            val isAdding = !localUserReactions.contains(unicode)
                            onReactionToggle(unicode, isAdding)
                        }
                    )
                }
            }
        }
        
        // Layout reactions in rows with wrapping
        var currentRowItems = mutableListOf<@Composable () -> Unit>()
        val maxItemsPerRow = 6 // Adjust based on your needs
        
        allReactions.chunked(maxItemsPerRow).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowItems.forEach { item ->
                    item()
                }
            }
        }
    }
}

@Composable
private fun ReactionChip(
    unicode: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val emoji = unicodeToEmoji(unicode)
    
    Surface(
        modifier = Modifier
            .clickable { onClick() }
            .clip(RoundedCornerShape(16.dp)),
        color = if (isSelected) Color(0xFF4A4A4A) else Color(0xFF2A2A2A),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = emoji,
                fontSize = 14.sp
            )
            Text(
                text = count.toString(),
                color = if (isSelected) TextPrimary else TextTertiary,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

private fun unicodeToEmoji(unicode: String): String {
    return try {
        val codePoint = unicode.removePrefix("U+").toInt(16)
        String(Character.toChars(codePoint))
    } catch (e: Exception) {
        "😀" // fallback emoji
    }
}

@Composable
private fun formatTimeAgo(createdAt: String): String {
    return try {
        // Parse the UTC timestamp from server
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Set timezone to UTC for parsing
        
        val utcDate = inputFormat.parse(createdAt)
        val now = Date() // Current time in local timezone
        val diffInMillis = now.time - (utcDate?.time ?: 0)
        
        val minutes = diffInMillis / (1000 * 60)
        val hours = diffInMillis / (1000 * 60 * 60)
        val days = diffInMillis / (1000 * 60 * 60 * 24)
        
        when {
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            else -> "${days}d ago"
        }
    } catch (e: Exception) {
        "now"
    }
}