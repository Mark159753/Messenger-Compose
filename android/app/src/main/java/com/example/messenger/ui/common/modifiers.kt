package com.example.messenger.ui.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp

fun Modifier.placeVerticallyRelative(y:Float) = then(
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(constraints.maxWidth, constraints.maxHeight) {
            placeable.placeRelative(
                x = 0,
                y = (constraints.maxHeight * y).toInt()
            )
        }
    }
)