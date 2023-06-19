package com.example.messenger.ui.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.example.messenger.R
import com.example.messenger.domain.date.YYY_DD_MM
import com.example.messenger.domain.date.formatDate
import com.example.messenger.ui.chat.model.MessageItemUi

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatList(
    modifier: Modifier = Modifier,
    messagesList: LazyPagingItems<MessageItemUi>? = null,
    listState: LazyListState = rememberLazyListState()
){

    val headerIndexes = remember { mutableMapOf<Int, Int>() }
    val headerId = remember { mutableMapOf<String, Int>() }

    var stickyItem by remember {
        mutableStateOf<MessageItemUi.StickyHeader?>(null)
    }

    var stickyOffset by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .collect { info ->
                if (messagesList == null) return@collect

                val headersList = info.visibleItemsInfo
                    .filter{ messagesList.peek(it.index) is MessageItemUi.StickyHeader}
                    .sortedBy { it.offset }

                val lastVisibleHeaderInfo = headersList.lastOrNull()
                val lastStickyItem = headersList.lastOrNull()?.index?.let { messagesList.peek(it)?.let { item -> toStickyItem(item) } }
                val maxHeaderOffset = (lastVisibleHeaderInfo?.size ?: 0) + (-info.viewportStartOffset)

                val itemOffset = info.viewportSize.height - ((lastVisibleHeaderInfo?.offset ?: 0) + (-info.viewportStartOffset))
                val itemSize = lastVisibleHeaderInfo?.size ?: 0

                stickyItem = calculateStickyHeader(messagesList, lastVisibleHeaderInfo, maxHeaderOffset, itemOffset, info.visibleItemsInfo.lastOrNull()?.index ?: 0)

                val isVisible = itemOffset in 0..maxHeaderOffset
                lastStickyItem?.let { itemInfo ->
                    headerId[itemInfo.id] = if (isVisible) -200 else 0
                }

                val s = (-info.viewportStartOffset) + itemSize
                val p = ((itemOffset - maxHeaderOffset) / s.toFloat()).coerceIn(0f, 1f)
                val offset = if (isVisible) 0f else (1 - p) * s
                stickyOffset = -info.viewportStartOffset - offset.toInt()
            }
    }


    Box(
        modifier = modifier
    ){
        LazyColumn(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
            reverseLayout = true,
            state = listState
        ){
            if (messagesList != null){

                items(
                    count = messagesList.itemCount,
                    key = messagesList.itemKey {
                        when(it){
                            is MessageItemUi.MessageUiItem -> it.message.id
                            is MessageItemUi.StickyHeader -> it.id
                        }
                    }
                ){ index ->

                    when(val item = messagesList[index]){
                        is MessageItemUi.MessageUiItem -> {
                            if (item.isCompanion)
                                CompanionChatItem(
                                    item = item,
                                    modifier = Modifier.animateItemPlacement()
                                )
                            else
                                MyChatItem(
                                    item = item,
                                    modifier = Modifier.animateItemPlacement()
                                )
                        }
                        is MessageItemUi.StickyHeader -> {
                            headerIndexes[index] = 0
                            StickyDate(
                                date = item.title,
                                modifier = Modifier
                                    .offset { IntOffset(x = 0, y = headerId[item.id] ?: 0) }
                            )
                        }
                        null -> null
                    }
                }
            }

        }

        stickyItem?.let { sticky ->
            StickyDate(
                date = sticky.title,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset { IntOffset(x = 0, stickyOffset) },
            )
        }

        if (messagesList?.itemCount == 0) {
            EmptyChatStub(
                modifier = Modifier.align(Alignment.Center)
            )
        }

    }
}

@Composable
private fun EmptyChatStub(
    modifier: Modifier = Modifier,
    msg:String = stringResource(id = R.string.chat_screen_empty_message_list)
){
    Box(
        modifier = modifier
            .fillMaxWidth(0.66f)
            .aspectRatio(1.6f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ){
        Text(
            modifier = Modifier.padding(20.dp),
            text = msg,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


private fun calculateStickyHeader(
    pagingItems:LazyPagingItems<MessageItemUi>,
    lastVisibleHeaderInfo: LazyListItemInfo?,
    maxHeaderOffset: Int,
    itemOffset:Int,
    lastItemIndex:Int
): MessageItemUi.StickyHeader? {
    return when {
        pagingItems.itemCount == 0 -> null
        lastVisibleHeaderInfo == null -> pagingItems.peek(lastItemIndex)?.let { toStickyItem(it) }
        itemOffset > maxHeaderOffset && pagingItems.itemCount > (lastVisibleHeaderInfo.index + 1) -> {
            pagingItems.peek(lastVisibleHeaderInfo.index + 1)?.let { toStickyItem(it) }
        }
        else -> pagingItems.peek(lastVisibleHeaderInfo.index)?.let { toStickyItem(it) }
    }
}

private fun toStickyItem(item:MessageItemUi): MessageItemUi.StickyHeader {
    return when(item){
        is MessageItemUi.MessageUiItem -> MessageItemUi.StickyHeader(id = "", title = item.message.created_at.formatDate(YYY_DD_MM))
        is MessageItemUi.StickyHeader -> item
    }
}