@file:OptIn(ExperimentalMaterialApi::class)

package com.example.messenger.ui.drawer

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.ThresholdConfig
import androidx.compose.material.swipeable
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDrawer(
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    drawerState:MyDrawerState = rememberDrawerState(initialValue = MyDrawerValue.Closed),
    scrimColor: Color = DrawerDefaults.scrimColor,
    content: @Composable () -> Unit,
    drawerWidth: Dp = 240.dp,
    gesturesEnabled: Boolean = true
){
    val scope = rememberCoroutineScope()
    val navigationMenu = "NavMenu"
    val minValue = -with(LocalDensity.current) { drawerWidth.toPx() }
    val maxValue = 0f

    val anchors = mapOf(minValue to MyDrawerValue.Closed, maxValue to MyDrawerValue.Open)
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    Box(
        modifier
            .fillMaxSize()
            .swipeable(
                state = drawerState.swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> MyFractionalThreshold(0.5f) },
                orientation = Orientation.Horizontal,
                reverseDirection = isRtl,
                velocityThreshold = DrawerVelocityThreshold,
                resistance = null,
                enabled = gesturesEnabled
            )
    ){
        Box {
            content()
        }

        Scrim(
            open = drawerState.isOpen,
            onClose = {
                scope.launch { drawerState.close() }
            },
            fraction = {
                calculateFraction(minValue, maxValue, drawerState.offset.value)
            },
            color = scrimColor
        )

        Box(
            Modifier
                .offset { IntOffset(drawerState.offset.value.roundToInt(), 0) }
                .semantics {
                    paneTitle = navigationMenu
                    if (drawerState.isOpen) {
                        dismiss {
                            scope.launch { drawerState.close() }; true
                        }
                    }
                },
        ) {
            drawerContent()
        }
    }
}

private fun calculateFraction(a: Float, b: Float, pos: Float) =
    ((pos - a) / (b - a)).coerceIn(0f, 1f)

enum class MyDrawerValue{
    Closed,
    Open
}

@OptIn(ExperimentalMaterialApi::class)
class MyDrawerState(
    initialValue: MyDrawerValue,
    confirmStateChange: (MyDrawerValue) -> Boolean = { true },
){

    internal val swipeableState = SwipeableState(
        initialValue = initialValue,
        animationSpec = AnimationSpec,
        confirmStateChange = confirmStateChange
    )

    val currentValue: MyDrawerValue
        get() {
            return swipeableState.currentValue
        }

    val isOpen: Boolean
        get() = currentValue == MyDrawerValue.Open

    val isClose: Boolean
        get() = currentValue == MyDrawerValue.Closed

    val isAnimationRunning: Boolean
        get() {
            return swipeableState.isAnimationRunning
        }

    suspend fun open() = animateTo(MyDrawerValue.Open, AnimationSpec)

    suspend fun close() = animateTo(MyDrawerValue.Closed, AnimationSpec)

    suspend fun animateTo(targetValue: MyDrawerValue, anim: AnimationSpec<Float>) {
        swipeableState.animateTo(targetValue, anim)
    }

    suspend fun snapTo(targetValue: MyDrawerValue) {
        swipeableState.snapTo(targetValue)
    }

    val offset: State<Float>
        get() = swipeableState.offset

    val progress:Float
        get() = swipeableState.progress.fraction

    companion object{
        fun Saver(confirmStateChange: (MyDrawerValue) -> Boolean) =
            androidx.compose.runtime.saveable.Saver<MyDrawerState, MyDrawerValue>(
                save = { it.currentValue },
                restore = { MyDrawerState(it, confirmStateChange) }
            )
    }
}

@Composable
fun rememberDrawerState(
    initialValue: MyDrawerValue,
    confirmStateChange: (MyDrawerValue) -> Boolean = { true }
): MyDrawerState {
    return rememberSaveable(saver = MyDrawerState.Saver(confirmStateChange)) {
        MyDrawerState(initialValue, confirmStateChange)
    }
}

@Composable
private fun Scrim(
    open: Boolean,
    onClose: () -> Unit,
    fraction: () -> Float,
    color: Color
) {
    val closeDrawer = "Close"
    val dismissDrawer = if (open) {
        Modifier
            .pointerInput(onClose) { detectTapGestures { onClose() } }
            .semantics(mergeDescendants = true) {
                contentDescription = closeDrawer
                onClick { onClose(); true }
            }
    } else {
        Modifier
    }

    Canvas(
        Modifier
            .fillMaxSize()
            .then(dismissDrawer)
    ) {
        drawRect(color, alpha = fraction())
    }
}

private val AnimationSpec = TweenSpec<Float>(durationMillis = 256)
private val DrawerVelocityThreshold = 400.dp

@Immutable
internal data class MyFractionalThreshold(
    private val fraction: Float
) : ThresholdConfig {
    override fun Density.computeThreshold(fromValue: Float, toValue: Float): Float {
        return lerp(fromValue, toValue, fraction)
    }
}