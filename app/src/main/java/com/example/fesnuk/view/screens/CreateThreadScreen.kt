package com.example.fesnuk.view.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.fesnuk.view.theme.*
import com.example.fesnuk.viewmodel.CreateThreadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateThreadScreen(
    onCloseClick: () -> Unit = {},
    onPostCreated: (String) -> Unit = {},
    context: Context,
    preselectedNookId: String? = null,
    viewModel: CreateThreadViewModel = viewModel { CreateThreadViewModel(context) }
) {
    val uiState by viewModel.uiState.collectAsState()
    var showNookPicker by remember { mutableStateOf(false) }
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.addImages(uris)
        }
    }

    // Handle post creation success
    LaunchedEffect(uiState.isPostCreated) {
        if (uiState.isPostCreated && uiState.createdPostId != null) {
            onPostCreated(uiState.createdPostId!!)
        }
    }

    // Handle preselected nook
    LaunchedEffect(preselectedNookId) {
        preselectedNookId?.let { nookId ->
            viewModel.selectNookById(nookId)
        }
    }

    // Show error messages
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // You can show a snackbar or toast here if needed
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Custom toolbar
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = onCloseClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Close",
                        tint = TextPrimary
                    )
                }
            },
            actions = {
                val selectedNook = uiState.selectedNook
                Text(
                    text = if (preselectedNookId != null && selectedNook != null) {
                        "Start A Thread in ${selectedNook.name}"
                    } else {
                        "Start A Thread"
                    },
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Button(
                    onClick = { 
                        if (uiState.isUploadingFiles || uiState.isCreatingPost) {
                            // Do nothing while processing
                        } else {
                            viewModel.createPost()
                        }
                    },
                    enabled = !uiState.isUploadingFiles && !uiState.isCreatingPost,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.isUploadingFiles || uiState.isCreatingPost) 
                            Color.Gray else CardBackground
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    if (uiState.isUploadingFiles || uiState.isCreatingPost) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = TextPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when {
                                    uiState.isUploadingFiles -> "Uploading..."
                                    uiState.isCreatingPost -> "Posting..."
                                    else -> "Post"
                                },
                                color = TextPrimary
                            )
                        }
                    } else {
                        Text(
                            text = "Post",
                            color = TextPrimary
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = DarkBackground
            )
        )

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Progress bar for uploads and post creation
            if (uiState.isUploadingFiles || uiState.isCreatingPost || uiState.uploadProgress > 0f) {
                LinearProgressIndicator(
                    progress = { uiState.uploadProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    color = Color.Blue,
                    trackColor = Color.Gray
                )
                
                Text(
                    text = when {
                        uiState.isUploadingFiles -> "Uploading images... ${(uiState.uploadProgress * 100).toInt()}%"
                        uiState.isCreatingPost -> "Creating post... ${(uiState.uploadProgress * 100).toInt()}%"
                        uiState.uploadProgress >= 1f -> "Complete!"
                        else -> ""
                    },
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Error message
            uiState.errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = error,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Title input
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.updateTitle(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Title*") },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = CardBackground,
                    focusedContainerColor = CardBackground,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedPlaceholderColor = Color(0xFF9E9E9E),
                    focusedPlaceholderColor = Color(0xFF9E9E9E),
                    unfocusedTextColor = TextPrimary,
                    focusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Pick nook button - only show if no preselected nook
            if (preselectedNookId == null) {
                Button(
                    onClick = { showNookPicker = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CardBackground
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.wrapContentWidth()
                ) {
                    if (uiState.isLoadingNooks) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = TextPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Loading nooks...",
                                color = TextPrimary
                            )
                        }
                    } else {
                        Text(
                            text = "Pick a Nook ${uiState.selectedNook?.let { "::${it.name}" } ?: ""}",
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Attach image area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (uiState.selectedImages.isNotEmpty()) 200.dp else 180.dp)
                    .border(
                        width = 2.dp,
                        color = Color(0xFF9E9E9E)
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardBackground)
                    .clickable { 
                        imagePickerLauncher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.selectedImages.isNotEmpty()) {
                    LazyColumn {
                        items(uiState.selectedImages) { uri ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Selected image",
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Text(
                                    text = "Image attached",
                                    color = TextPrimary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                IconButton(
                                    onClick = { viewModel.removeImage(uri) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove image",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Attach Image",
                            tint = Color(0xFF9E9E9E),
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "Attach Images",
                            color = Color(0xFF9E9E9E),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text(
                            text = "Supports multiple images and GIFs",
                            color = Color(0xFF9E9E9E),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Body text input
            OutlinedTextField(
                value = uiState.content,
                onValueChange = { viewModel.updateContent(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp),
                placeholder = { Text("Body Text") },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = CardBackground,
                    focusedContainerColor = CardBackground,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedPlaceholderColor = Color(0xFF9E9E9E),
                    focusedPlaceholderColor = Color(0xFF9E9E9E),
                    unfocusedTextColor = TextPrimary,
                    focusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Nook picker dialog
    if (showNookPicker) {
        Dialog(onDismissRequest = { showNookPicker = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Select a Nook",
                        color = TextPrimary,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (uiState.isLoadingNooks) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.Blue)
                        }
                    } else {
                        LazyColumn {
                            items(uiState.nooks) { nook ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            viewModel.selectNook(nook)
                                            showNookPicker = false
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (uiState.selectedNook?.id == nook.id) 
                                            Color.Blue.copy(alpha = 0.2f) else DarkBackground
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = nook.backgroundImageUrl,
                                            contentDescription = nook.name,
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        
                                        Spacer(modifier = Modifier.width(12.dp))
                                        
                                        Column {
                                            Text(
                                                text = "::${nook.name}",
                                                color = TextPrimary,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = nook.description,
                                                color = Color.Gray,
                                                style = MaterialTheme.typography.bodySmall,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
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
}