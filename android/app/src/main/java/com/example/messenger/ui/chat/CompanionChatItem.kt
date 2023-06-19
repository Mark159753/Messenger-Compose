package com.example.messenger.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.messenger.BuildConfig
import com.example.messenger.domain.date.HH_MM
import com.example.messenger.domain.date.formatDate
import com.example.messenger.ui.chat.model.MessageItemUi
import com.example.messenger.ui.chat.model.MessageUi
import com.example.messenger.ui.chat.model.UserUi

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CompanionChatItem(
    item: MessageItemUi.MessageUiItem,
    modifier: Modifier = Modifier
){

    Row(modifier = modifier.fillMaxWidth(0.86f)){
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiaryContainer),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = item.user.name.first().uppercase(),
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                style = MaterialTheme.typography.bodyLarge
            )

            if (!item.user.avatar.isNullOrBlank()){
                Image(
                    painter = rememberAsyncImagePainter(BuildConfig.BASE_URL + item.user.avatar),
                    contentDescription = "User avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomEnd = 8.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(8.dp)
        ) {

            if (!item.message.images.isNullOrEmpty()){
                FlowRow(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp)),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item.message.images.forEach { img ->
                        Image(
                            modifier = Modifier.width(120.dp,).height(160.dp),
                            painter = rememberAsyncImagePainter(img),
                            contentDescription = "",
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            if (!item.message.message.isNullOrBlank()){
                Text(
                    modifier = Modifier.padding(end = 6.dp),
                    text = item.message.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Text(
                modifier = Modifier
                    .align(Alignment.End),
                text = item.message.created_at.formatDate(HH_MM),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Preview()
@Composable
private fun CompanionChatItemPreview(){
    val user = UserUi(
        id = "",
        created_at = "",
        name = "Mark Mel",
        email = "mark@mail.ru",
        nick_name = "@mark",
        phone = "+38323443232",
        avatar = null
    )
    val message = MessageUi(
        id = "",
        created_at = "2011-12-03T10:15:30+01:00",
        message = "Hello",
        images = null
    )
    CompanionChatItem(
        item = MessageItemUi.MessageUiItem(message = message, user = user, isCompanion = true)
    )
}