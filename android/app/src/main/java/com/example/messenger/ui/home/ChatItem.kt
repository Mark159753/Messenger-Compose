package com.example.messenger.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.lang.Float.min
import kotlin.math.roundToInt
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.composed
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.example.messenger.BuildConfig
import com.example.messenger.data.local.db.entities.ChatEntity
import com.example.messenger.data.local.db.entities.MessageEntity
import com.example.messenger.data.local.db.entities.UserEntity
import com.example.messenger.data.local.db.entities.relation.ChatWithMessageAndUser
import com.example.messenger.domain.date.relativeFormatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatItem(
    modifier: Modifier = Modifier,
    item:ChatWithMessageAndUser,
    onClick: ( item:ChatWithMessageAndUser ) -> Unit = {}
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick(item) }
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
                text = item.user.first_name.first().uppercase(),
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

        Column(modifier = Modifier
            .padding(start = 16.dp)
            .weight(1f)
        ) {
            Text(
                text =  "${item.user.first_name} ${item.user.last_name}",
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
            Text(
                text = item.message?.created_at?.relativeFormatDate() ?: "",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodySmall
            )
//            Badge(
//                modifier = Modifier
//                    .align(Alignment.CenterHorizontally)
//                    .padding(top = 5.dp)
//            ){
//                Text(text = "1")
//            }
        }
    }
}


@Composable
fun SwipeableChatItem(
    modifier: Modifier = Modifier,
    swipeState: SwipeToRevealState = rememberSwipeToRevealState(),
    item:ChatWithMessageAndUser,
    onClick:(item:ChatWithMessageAndUser)->Unit = {},
    onRemove:(item:ChatWithMessageAndUser)->Unit = {}
){
    SwipeRevealItem(
        backgroundContent = {
            RevealContentItem(
                modifier = modifier
            )
        },
        content = {
            ChatItem(
                modifier = modifier,
                item = item,
                onClick = onClick
            )
        },
        swipeState = swipeState,
        onRemoved = {
            onRemove(item)
        }
    )
}

@Composable
fun RevealContentItem(
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.96f)
            )
    ) {
        Text(
            text = "Remove",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onError,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}


@Composable
fun SwipeRevealItem(
    backgroundContent:@Composable ()->Unit,
    content:@Composable ()->Unit,
    modifier: Modifier = Modifier,
    swipeState: SwipeToRevealState = rememberSwipeToRevealState(),
    onRemoved:()->Unit
){
    BoxWithConstraints(
        modifier = modifier
    ) {

        val maxValue = -with(LocalDensity.current){ maxWidth.toPx() }

        val progress = swipeState.offset / maxValue

        val isSwiped = remember{
            derivedStateOf { return@derivedStateOf (swipeState.offset / maxValue) > 0.9f }
        }

        LaunchedEffect(key1 = isSwiped.value){
            if (isSwiped.value)
                onRemoved()
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .offset {
                    IntOffset(
                        x = swipeState.offset.roundToInt() - maxValue.roundToInt(),
                        y = 0
                    )
                }
                .graphicsLayer {
                    val fraction = min(1f, 0.7f + progress)
                    scaleY = fraction
                    alpha = fraction
                }
        ) {
            backgroundContent()
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .swipeToReveal(
                    swipeState = swipeState
                )
                .offset { IntOffset(x = swipeState.offset.roundToInt(), y = 0) }
        ) {
            content()
        }

    }
}

fun Modifier.swipeToReveal(
    swipeState: SwipeToRevealState
):Modifier = composed {
    val scope = rememberCoroutineScope()

    pointerInput(key1 = Unit){
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            val drag = awaitHorizontalTouchSlopOrCancellation(
                pointerId = down.id
            ) { change, over ->
                if (over < 0 || swipeState.offset < 0f) {
                    change.consume()
                }
            }
            if (drag != null) {
                if (horizontalDrag(drag.id) { change ->
                        scope.launch {
                            swipeState.performDrag(
                                (swipeState.offset + change.positionChange().x).coerceIn(
                                    -size.width.toFloat(),
                                    0f
                                )
                            )
                        }
                    }
                ) {
                    scope.launch {
                        val anchor = -(size.width * 0.46f)
                        if (swipeState.offset < anchor) {
                            swipeState.animator.animateTo(
                                -(size.width.toFloat()))
                        } else {
                            swipeState.close()
                        }
                    }
                } else {
                    scope.launch {
                        swipeState.close()
                    }
                }
            }
        }
    }

}


@Stable
class SwipeToRevealState(
    initialValue:Float = 0f
){

    val animator by mutableStateOf(Animatable(initialValue))

    val offset:Float
        get() = animator.value

    suspend fun performDrag(delta:Float) = animator.snapTo(delta)

    suspend fun animateDecay(
        initialVelocity: Float,
        animationSpec: DecayAnimationSpec<Float>
    ) = animator.animateDecay(initialVelocity, animationSpec)


    suspend fun close() = animator.animateTo(0f)

    companion object{
        fun Saver() =
            Saver<SwipeToRevealState, Float>(
                save = { it.animator.value },
                restore = { SwipeToRevealState(it) }
            )
    }
}

@Composable
fun rememberSwipeToRevealState(initialValue: Float = 0f): SwipeToRevealState {
    return rememberSaveable(saver = SwipeToRevealState.Saver()){
        SwipeToRevealState(initialValue)
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatItemPreview(){
    val item = ChatWithMessageAndUser(
        chat = ChatEntity(
            id = "",
            messageId = "",
            userId = ""
        ),
        message = MessageEntity(
            id = "",
            chatId = "",
            created_at = "2023-06-06T12:41:13.547Z",
            message = "Hello",
            updated_at = "2023-06-06T12:41:13.547Z",
            authorId = ""
        ),
        user = UserEntity(
            id = "",
            created_at = "2023-06-06T12:41:13.547Z",
            email = "",
            first_name = "Mark",
            last_name = "Mel",
            nick_name = "mark",
            phone = "1231231231",
            updated_at = "2023-06-06T12:41:13.547Z",
            avatar = null,
            isOnline = false
        )
    )

    Surface(modifier = Modifier) {
        ChatItem(item = item)
    }
}