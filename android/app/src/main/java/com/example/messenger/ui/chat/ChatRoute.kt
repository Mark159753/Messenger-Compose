package com.example.messenger.ui.chat

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.example.messenger.BuildConfig
import com.example.messenger.R
import com.example.messenger.data.local.provider.images.Image
import com.example.messenger.ui.chat.model.ChatArgs
import com.example.messenger.ui.chat.model.MessageItemUi
import com.example.messenger.ui.chat.state.ChatEvents
import com.example.messenger.ui.dialogs.bottom.ImagePickerBottomDialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun ChatRoute(
    viewModel: ChatViewModel,
    onNavBack:()->Unit
){
    val args by viewModel.args.collectAsStateWithLifecycle(initialValue = null)

    ChatScreen(
        onBackPress = onNavBack,
        messagesList = viewModel.messagesList.collectAsLazyPagingItems(),
        imagesList = viewModel.selectedImages,
        message = viewModel.messageText,
        onUpdateMessage = viewModel::onUpdateMessageText,
        onSend = viewModel::sendMsg,
        chatArgs = args,
        events = viewModel.events,
        onSelectImages = viewModel::setImages
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class
)
@Composable
fun ChatScreen(
    onBackPress: () -> Unit = {},
    messagesList: LazyPagingItems<MessageItemUi>? = null,
    imagesList: SnapshotStateList<Image> = mutableStateListOf(),
    message: String = "",
    onUpdateMessage:(m:String) -> Unit = {},
    onSend:() -> Unit = {},
    chatArgs: ChatArgs? = null,
    events:Flow<ChatEvents> = flowOf(),
    onSelectImages: (list: List<Image>)->Unit = {}
){

    val listState = rememberLazyListState()
    val context = LocalContext.current
    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)

    val permissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    LaunchedEffect(Unit){
        events.collect() { event ->
            when(event){
                ChatEvents.OnScrollToFirstItem -> {
                    delay(100L)
                    listState.animateScrollToItem(0)
                }
                is ChatEvents.OnError -> {
                    Toast.makeText(context, event.msg, Toast.LENGTH_LONG).show()
                }

                ChatEvents.OnNewMessage -> {
                    if (listState.firstVisibleItemIndex < 2) {
                        delay(100L)
                        listState.animateScrollToItem(0)
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = {
            ChatToolbar(
                onBackPress = onBackPress,
                chatArgs = chatArgs
            )
        },
        bottomBar = {
            ChatBottomBar(
                imagesList = imagesList,
                message = message,
                onUpdateMessage = onUpdateMessage,
                onSend = onSend,
                onPickFile = {
                    if (permissionState.status.isGranted) {
                        (context as? AppCompatActivity)?.let { activity ->
                            activity.supportFragmentManager.setFragmentResultListener(
                                ImagePickerBottomDialog.ON_SELECTED_IMAGES_KEY,
                                lifecycleOwner
                            ) { key, b ->
                                val l: List<Image>? =
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        b.getParcelableArrayList(key, Image::class.java)
                                    } else {
                                        b.getParcelableArrayList(key)
                                    }
                                onSelectImages(l ?: emptyList())
                            }
                            activity.supportFragmentManager.setFragmentResultListener(
                                ImagePickerBottomDialog.ON_SEND,
                                lifecycleOwner
                            ) { _, _ ->
                                onSend()
                            }
                            ImagePickerBottomDialog.create(
                                list = ArrayList(imagesList)
                            ).show(
                                activity.supportFragmentManager,
                                ImagePickerBottomDialog::class.simpleName
                            )
                        }
                    }else{
                        permissionState.launchPermissionRequest()
                    }
                }
            )
        }
    ) { paddingValues ->

        ChatList(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface),
            messagesList = messagesList,
            listState = listState
        )

    }
}

@Composable
private fun ChatToolbar(
    modifier:Modifier = Modifier,
    onBackPress:()->Unit = {},
    chatArgs: ChatArgs? = null
){
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        IconButton(
            onClick = {
                onBackPress()
            },
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(
            modifier = Modifier
                .padding(start = 16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiaryContainer),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = chatArgs?.name?.first()?.uppercase() ?: "",
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                style = MaterialTheme.typography.bodyLarge
            )
            if (!chatArgs?.avatar.isNullOrBlank()){
                Image(
                    painter = rememberAsyncImagePainter(BuildConfig.BASE_URL + chatArgs?.avatar),
                    contentDescription = "User avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Column(
            modifier = Modifier
                .height(40.dp)
                .padding(start = 8.dp)
        ) {
            Text(
                text = chatArgs?.name ?: "",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "online",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatBottomBar(
    imagesList: SnapshotStateList<Image> = mutableStateListOf(),
    message:String = "",
    onUpdateMessage:(m:String) -> Unit = {},
    onSend:()->Unit = {},
    onPickFile:()->Unit = {}
){
    TextField(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth(),
        value = message,
        onValueChange = onUpdateMessage,
        maxLines = 5,
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        textStyle = MaterialTheme.typography.titleMedium,
        placeholder = {
            Text(
                text = stringResource(id = R.string.chat_screen_hint),
                style = MaterialTheme.typography.titleMedium
            )
        },
        trailingIcon = {
            IconButton(
                modifier = Modifier.padding(start = 8.dp),
                onClick = onSend
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    painter = painterResource(id = R.drawable.send_btn_ico),
                    contentDescription = "Send",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
        leadingIcon = {
            Box(modifier = Modifier
                .clip(CircleShape)
                .size(50.dp)
                .padding(5.dp)
                .clickable { onPickFile() },
                contentAlignment = Alignment.Center
            ){
                Icon(
                    painter = painterResource(id = R.drawable.paper_clip_icon),
                    contentDescription = "Pick File",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )

                if (imagesList.size > 0){
                    Badge(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                    ){
                        Text(text = imagesList.size.toString())
                    }
                }
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
private fun ChatScreenPreview(){
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ChatScreen()
    }
}