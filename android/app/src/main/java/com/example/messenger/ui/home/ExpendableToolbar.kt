package com.example.messenger.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.messenger.R
import java.lang.Float.max
import kotlin.math.roundToInt

enum class ExpendableValue{
    Expended, Collapsed
}


@OptIn(ExperimentalMaterialApi::class)
@Stable
class ExpendableToolbarState(
    initialValue:ExpendableValue = ExpendableValue.Expended
){

    internal val swipeableState = SwipeableState(
        initialValue = initialValue
    )

    val currentValue:ExpendableValue
        get() = swipeableState.currentValue

    val isCollapsed:Boolean
        get() = currentValue == ExpendableValue.Collapsed

    val isExpended:Boolean
        get() = currentValue == ExpendableValue.Expended

    val isAnimationRunning: Boolean
        get() {
            return swipeableState.isAnimationRunning
        }

    suspend fun collapse() = swipeableState.animateTo(ExpendableValue.Collapsed)

    suspend fun expand() = swipeableState.animateTo(ExpendableValue.Expended)

    val offset: State<Float>
        get() = swipeableState.offset

    fun performDrag(delta:Float) = swipeableState.performDrag(delta).toOffset()

    suspend fun performFling(velocity:Float) = swipeableState.performFling(velocity)

    private fun Float.toOffset() = Offset(x = 0f, y = this)

    companion object {
        fun Saver() =
            Saver<ExpendableToolbarState, ExpendableValue>(
                save = { it.currentValue },
                restore = { ExpendableToolbarState(it) }
            )
    }
    
}

@Composable
fun rememberExpendableToolbarState(initialValue: ExpendableValue = ExpendableValue.Expended): ExpendableToolbarState {
    return rememberSaveable(saver = ExpendableToolbarState.Saver()){
        ExpendableToolbarState(initialValue)
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExpendableToolbar(
    title:String,
    modifier: Modifier = Modifier,
    toolbarState:ExpendableToolbarState = rememberExpendableToolbarState(),
    onDrawerClick:()->Unit = {},
    onSearchClick:()->Unit = {}
){

    val minValue = -with(LocalDensity.current){ MaxToolbarOffset.toPx() }
    val maxValue = 0f

    val anchors = mapOf(minValue to ExpendableValue.Collapsed, maxValue to ExpendableValue.Expended)

    // Collapsed = 0f; Expended = 1f
    val progress = 1f - (toolbarState.offset.value / minValue)

    val alphaBigTitle = max(0f, (progress - 0.4f) / 0.6f)
    val alphaSmallTitle = max(0f, 1.0f - progress / 0.6f)

    Box(modifier = modifier
        .swipeable(
            state = toolbarState.swipeableState,
            orientation = Orientation.Vertical,
            anchors = anchors
        )
        .fillMaxWidth()
        .height(CollapsedToolbarHeight + MaxToolbarOffset)
        .offset { IntOffset(x = 0, y = toolbarState.offset.value.roundToInt()) }
    ){
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier
                    .padding(6.dp),
                onClick = onDrawerClick
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.menu_icon),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            AnimateTitle(
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer {
                        alpha = alphaSmallTitle
                    },
                title = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(
                modifier = Modifier
                    .padding(6.dp),
                onClick = onSearchClick
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.search_icon),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        AnimateTitle(
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    alpha = alphaBigTitle
                },
            title = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimateTitle(
    title: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    color:Color = MaterialTheme.colorScheme.onSurface
){
    AnimatedContent(
        modifier = modifier,
        targetState = title,
        transitionSpec = {
            slideInVertically { it } with slideOutVertically { -it }
        }
    ) { targetTitle ->
        Text(
            text = targetTitle,
            style = style,
            softWrap = false,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

val MaxToolbarOffset = 140.dp
val CollapsedToolbarHeight = 64.dp

@Preview()
@Composable
private fun ExpendableToolbarPreview(){
    Surface(
        modifier = Modifier.fillMaxWidth()
    ) {
        ExpendableToolbar(
            title = "Chat app"
        )
    }
}
