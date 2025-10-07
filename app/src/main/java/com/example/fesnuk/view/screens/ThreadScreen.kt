package com.example.fesnuk.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fesnuk.data.ThreadRepository
import com.example.fesnuk.data.CommentRepository
import com.example.fesnuk.data.RetrofitClient
import com.example.fesnuk.view.components.PostCard
import com.example.fesnuk.view.components.PostRenderMode
import com.example.fesnuk.view.components.ThreadReply
import com.example.fesnuk.view.components.CommentCard
import com.example.fesnuk.view.components.ReplyInputSection
import com.example.fesnuk.view.theme.*
import com.example.fesnuk.viewmodel.ThreadViewModel
import com.example.fesnuk.viewmodel.ThreadViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreadScreen(
    postId: String,
    onlineCount: String = "10 Online",
    postCount: String = "45 Posts",
    onBackClick: () -> Unit = {},
    onNookClick: () -> Unit = {},
    viewModel: ThreadViewModel = viewModel(factory = ThreadViewModelFactory(ThreadRepository(), CommentRepository(RetrofitClient.apiService)))
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(postId) {
        viewModel.loadPost(postId)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1C1C))
    ) {
        // Custom toolbar
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
            },
            actions = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Online info
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(OnlineGreen, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = onlineCount,
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "|",
                            color = Color(0xFF555555),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Text(
                            text = postCount,
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Action buttons
                    IconButton(onClick = { /* Search */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextPrimary
                        )
                    }
                    IconButton(onClick = { /* Filter */ }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = TextPrimary
                        )
                    }
                    IconButton(onClick = { /* More */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = TextPrimary
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF2C2C2C)
            )
        )

        // Thread content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            when {
                uiState.isLoading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
                uiState.errorMessage != null -> {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF2C2C2C)
                            )
                        ) {
                            Text(
                                text = "Error: ${uiState.errorMessage}",
                                color = Color(0xFFFF6B6B),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
                uiState.post != null -> {
                    // Original post
                    item {
                        uiState.post?.let { post ->
                            PostCard(
                                post = post,
                                renderMode = PostRenderMode.FULL,
                                onPostClick = { /* Already in thread view */ },
                                onNookClick = onNookClick
                            )
                        }
                    }
                    
                    // Comment input section
                    item {
                        CommentInputSection(
                            onCommentSubmit = { content ->
                                 viewModel.postComment(postId.toInt(), content)
                             },
                            isLoading = uiState.isPostingComment
                        )
                    }
                    
                    // Reply input section (shown when replying)
                    uiState.replyingToCommentId?.let { replyingToId ->
                        item {
                            ReplyInputSection(
                                replyingToCommentId = replyingToId,
                                onReplySubmit = { content, parentId ->
                                    viewModel.replyToComment(postId.toInt(), content, parentId)
                                },
                                onCancelReply = {
                                    viewModel.cancelReply()
                                },
                                isLoading = uiState.isPostingComment
                            )
                        }
                    }
                    
                    // Comments section
                    if (uiState.isLoadingComments) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                    } else {
                        items(uiState.comments) { comment ->
                            CommentCard(
                                comment = comment,
                                isReply = false,
                                onReplyClick = {
                                    viewModel.startReplyToComment(comment.id)
                                },
                                onExpandClick = {
                                    viewModel.toggleCommentExpansion(comment.id)
                                },
                                isExpanded = uiState.expandedComments.contains(comment.id)
                            )
                            
                            // Show replies if expanded
                            if (uiState.expandedComments.contains(comment.id)) {
                                uiState.commentReplies[comment.id]?.let { replies ->
                                    replies.forEach { reply ->
                                        CommentCard(
                                            comment = reply,
                                            isReply = true
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentInputSection(
    onCommentSubmit: (String) -> Unit,
    isLoading: Boolean = false
) {
    var commentText by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C2C2C)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = {
                    Text(
                        text = "Write a comment...",
                        color = TextSecondary
                    )
                },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = Color(0xFF4CAF50),
                    unfocusedBorderColor = Color(0xFF555555),
                    cursorColor = Color(0xFF4CAF50)
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (commentText.isNotBlank() && !isLoading) {
                            onCommentSubmit(commentText)
                            commentText = ""
                        }
                    }
                ),
                enabled = !isLoading
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            IconButton(
                onClick = {
                    if (commentText.isNotBlank() && !isLoading) {
                        onCommentSubmit(commentText)
                        commentText = ""
                    }
                },
                enabled = commentText.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFF4CAF50),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send comment",
                        tint = if (commentText.isNotBlank()) Color(0xFF4CAF50) else TextSecondary
                    )
                }
            }
        }
    }
}

data class ReplyData(
    val replyingTo: String,
    val replyingToAuthor: String,
    val author: String,
    val time: String,
    val content: String,
    val isOP: Boolean = false
)

private fun createSampleReplies(): List<ReplyData> {
    return listOf(
        ReplyData(
            "I swear, no matter what I do he just ults and one taps me",
            "Anonymous1701",
            "Anonymous341",
            "51m",
            "Wahh Waahhh wahhhh the Assassin role does what they're supposed to do best!! whaaahh wahhhhhhhh"
        ),
        ReplyData(
            "I swear, no matter what I do he just ults and one taps me",
            "Anonymous1701",
            "Anonymous892",
            "45m",
            "Try building Zhonya's first item and always save your dash for his ultimate. The key is to predict when he'll use it.",
            isOP = true
        ),
        ReplyData(
            "Try building Zhonya's first item",
            "Anonymous892",
            "Anonymous234",
            "32m",
            "Zhonya's is good but also try building Seeker's Armguard early. It really helps with his burst damage."
        ),
        ReplyData(
            "I swear, no matter what I do he just ults and one taps me",
            "Anonymous1701",
            "Anonymous567",
            "28m",
            "Play a champion with CC or build magic resist. Lissandra, Malzahar, or even just buying QSS can help a lot."
        ),
        ReplyData(
            "Try building Zhonya's first item",
            "Anonymous892",
            "Anonymous1701",
            "15m",
            "Thanks for the advice! I'll try Zhonya's first next game and see how it goes.",
            isOP = true
        )
    )
}