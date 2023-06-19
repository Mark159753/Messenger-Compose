package com.example.messenger.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.messenger.domain.date.HH_MM
import com.example.messenger.domain.date.formatDate
import com.example.messenger.ui.chat.model.MessageItemUi
import com.example.messenger.ui.chat.model.MessageUi
import com.example.messenger.ui.chat.model.UserUi


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MyChatItem(
    item: MessageItemUi.MessageUiItem,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ){

        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.End
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 8.dp))
                    .background(MaterialTheme.colorScheme.inversePrimary)
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
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(end = 6.dp),
                        text = item.message.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.End
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
}

@Composable
fun StickyDate(
    date:String,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(25.dp),
        contentAlignment = Alignment.Center
    ){
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(100))
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .fillMaxHeight()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Preview()
@Composable
private fun MyChatItemPreview(){
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
        images = listOf(
            "https://paradepets.com/.image/t_share/MTkxMzY1Nzg4NjczMzIwNTQ2/cutest-dog-breeds-jpg.jpg",
            "https://publish.purewow.net/wp-content/uploads/sites/2/2021/06/smallest-dog-breeds-toy-poodle.jpg?fit=728%2C524"
        )
    )

    MyChatItem(
        item = MessageItemUi.MessageUiItem(message = message, user = user, isCompanion = false)
    )
}

@Preview()
@Composable
private fun StickyDatePreview(){
    StickyDate(date = "June 12",)
}