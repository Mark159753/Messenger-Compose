package com.example.messenger.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.messenger.BuildConfig
import com.example.messenger.ui.chat.model.ChatArgs
import com.example.messenger.ui.search.model.ItemMessage
import com.example.messenger.ui.search.model.ItemSearchModel


@Composable
fun SearchItem(
    item:ItemSearchModel,
    modifier: Modifier = Modifier,
    onClick:(args:ChatArgs, chatId:String?)->Unit = { _, _, -> }
){
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick(
                ChatArgs(
                    userId = item.id,
                    name = item.name,
                    avatar = item.avatar,
                    isOnline = item.isOnline
                ),
                item.message?.chatId
            ) }
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ){
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiaryContainer),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = item.name.first().uppercase(),
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                style = MaterialTheme.typography.bodyLarge
            )

            if (!item.avatar.isNullOrBlank()){
                Image(
                    painter = rememberAsyncImagePainter(BuildConfig.BASE_URL + item.avatar),
                    contentDescription = "User avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Column(
            modifier = Modifier
            .height(40.dp)
            .padding(start = 16.dp)
            .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = item.name,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge
            )
            if (!item.message?.message.isNullOrBlank()){
                Text(
                    text = item.message?.message ?: "",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Column(
            modifier = Modifier
        ) {
            if (!item.message?.created.isNullOrBlank()) {
                Text(
                    text = item.message?.created ?: "",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


@Preview()
@Composable
private fun SearchItemPreview(){
    val dummyItem = ItemSearchModel(
        id = "id",
        avatar = null,
        name = "Mark Amstrog",
        isOnline = false,
        message = ItemMessage(
            id = "id",
            chatId = "chat_id",
            message = "Hello",
            created = "12:24"
        )
    )
    Surface {
        SearchItem(
            item = dummyItem
        )
    }
}
