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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

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
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = uiState.isLoading)
    
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
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .background(
//                                Color.Green.copy(alpha = 0.2f),
//                                RoundedCornerShape(12.dp)
//                            )
//                            .padding(horizontal = 8.dp, vertical = 4.dp)
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .size(8.dp)
//                                .background(Color.Green, CircleShape)
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text(
//                            text = onlineCount,
//                            color = Color.Green,
//                            fontSize = 12.sp
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.width(8.dp))
//
//                    // Post count
//                    Text(
//                        text = postCount,
//                        color = TextSecondary,
//                        fontSize = 12.sp
//                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
        
        // Main content with SwipeRefresh
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.refreshPost(postId) },
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    uiState.isLoading && uiState.post == null -> {
                        // Initial loading state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                    
                    uiState.errorMessage != null -> {
                        // Error state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Error: ${uiState.errorMessage}",
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.refreshPost(postId) }
                                ) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                    
                    uiState.post != null -> {
                        // Content loaded
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 80.dp), // Space for floating input
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            // Main post
                            item {
                                PostCard(
                                    post = uiState.post!!,
                                    renderMode = PostRenderMode.FULL,
                                    onPostClick = { /* Already in detail view */ },
                                    onNookClick = { /* Navigate to nook */ },
                                    onReplyClick = { /* Show comment input */ },
                                    onShareClick = { /* Share post */ },
                                    onMoreOptionsClick = { /* Show options */ }
                                )
                            }
                            
                            // Comments header
                            if (uiState.comments.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Comments",
                                        color = TextPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                            
                            // Comments loading state
                            if (uiState.isLoadingComments) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                                    }
                                }
                            }
                            
                            // Comments
                            items(uiState.comments) { comment ->
                                CommentCard(
                                    comment = comment,
                                    isReply = false,
                                    onReplyClick = { viewModel.startReplyToComment(comment.id) },
                                    onExpandClick = { viewModel.toggleCommentExpansion(comment.id) },
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
                            
                            // Empty comments state
                            if (uiState.comments.isEmpty() && !uiState.isLoadingComments) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No comments yet",
                                            color = TextSecondary,
                                            fontSize = 16.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Floating comment input at bottom
                if (uiState.post != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .imePadding()
                    ) {
                        ReplyInputSection(
                            replyingToCommentId = uiState.replyingToCommentId,
                            onSubmit = { content, commentId ->
                                if (commentId != null) {
                                    viewModel.replyToComment(uiState.post!!.id, content, commentId)
                                } else {
                                    viewModel.postComment(uiState.post!!.id, content)
                                }
                            },
                            onCancelReply = { viewModel.cancelReply() },
                            isLoading = uiState.isPostingComment
                        )
                    }
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