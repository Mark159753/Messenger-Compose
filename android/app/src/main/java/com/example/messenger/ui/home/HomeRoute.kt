package com.example.messenger.ui.home

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.messenger.R
import com.example.messenger.Screen
import com.example.messenger.data.local.db.entities.relation.ChatWithMessageAndUser
import com.example.messenger.ui.chat.model.ChatArgs
import com.example.messenger.ui.dialogs.question.QuestionDialog
import com.example.messenger.ui.drawer.MyDrawerState
import com.example.messenger.ui.drawer.MyDrawerValue
import com.example.messenger.ui.drawer.rememberDrawerState
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun HomeRoute(
    viewModel:HomeViewModel,
    onNavigate:(Screen)->Unit,
    drawerState: MyDrawerState,
    onNavToChat:(args: ChatArgs, chatId:String?)->Unit = { _, _ -> }
){
    HomeScreen(
        onNavigate,
        drawerState = drawerState,
        chatItems = viewModel.pagingList.collectAsLazyPagingItems(),
        onChatItemClick = { item ->
            onNavToChat(
                ChatArgs(
                    userId = item.user.id,
                    name = "${item.user.first_name} ${item.user.last_name}",
                    avatar = item.user.avatar,
                    isOnline = false
                ), item.chat.id
            )
        },
        onRemoveChat = viewModel::removeChat
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onNavigate:(Screen)->Unit = {},
    drawerState: MyDrawerState = rememberDrawerState(initialValue = MyDrawerValue.Closed),
    chatItems: LazyPagingItems<ChatWithMessageAndUser>? = null,
    onChatItemClick:(item:ChatWithMessageAndUser)->Unit = {},
    onRemoveChat:(item:ChatWithMessageAndUser)->Unit = {}
){
    val coroutineScope = rememberCoroutineScope()

    val toolbarOffset = with(LocalDensity.current) { (MaxToolbarOffset).toPx() }

    val toolbarState = rememberExpendableToolbarState(initialValue = ExpendableValue.Collapsed)
    val lazyListState = rememberLazyListState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {  }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.create_pen_icon),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    ) { paddings ->
        Box(
            modifier = Modifier
                .padding(paddings)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .nestedScroll(
                    object : NestedScrollConnection {
                        override fun onPreScroll(
                            available: Offset,
                            source: NestedScrollSource
                        ): Offset {
                            val delta = available.y
                            return if (delta < 0) {
                                toolbarState.performDrag(delta)
                            } else {
                                Offset.Zero
                            }
                        }

                        override fun onPostScroll(
                            consumed: Offset,
                            available: Offset,
                            source: NestedScrollSource
                        ): Offset {
                            val delta = available.y
                            return toolbarState.performDrag(delta)
                        }

                        override suspend fun onPreFling(available: Velocity): Velocity {
                            val isReachedTop =
                                lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0
                            return if (available.y < 0 && isReachedTop) {
                                toolbarState.performFling(available.y)
                                available
                            } else {
                                Velocity.Zero
                            }
                        }

                        override suspend fun onPostFling(
                            consumed: Velocity,
                            available: Velocity
                        ): Velocity {
                            toolbarState.performFling(velocity = available.y)
                            return super.onPostFling(consumed, available)
                        }
                    }
                )
        ) {

            ExpendableToolbar(
                title = stringResource(id = R.string.home_screen_toolbar_title),
                toolbarState = toolbarState,
                onDrawerClick = {
                    coroutineScope.launch {
                        drawerState.open()
                    }
                },
                onSearchClick = { onNavigate(Screen.Search) }
            )

            LazyColumn(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = 0,
                            y = toolbarOffset.roundToInt() + toolbarState.offset.value.roundToInt()
                        )
                    }
                    .padding(top = 64.dp)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface),
                state = lazyListState
            ){

                if (chatItems != null){
                    items(
                        count = chatItems.itemCount,
                        key = chatItems.itemKey { it.chat.id }
                    ){ index ->
                        val item = chatItems[index]
                        item?.let {
                            SwipeableChatItem(
                                item = it,
                                onClick = onChatItemClick,
                                modifier = Modifier.animateItemPlacement(),
                                onRemove = onRemoveChat
                            )
                            Divider(
                                modifier = Modifier
                                    .padding(start = 72.dp, end = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LogoutDialog(
    dialogState: MutableState<Boolean> = remember { mutableStateOf(false) },
    onConfirm:()->Unit = {}
){
    QuestionDialog(
        msg = stringResource(id = R.string.logout_dialog_question),
        dialogState = dialogState,
        onConfirm = onConfirm,
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview(){
    Surface(modifier = Modifier.fillMaxSize()) {
        HomeScreen()
    }
}

