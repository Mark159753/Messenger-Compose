package com.example.messenger.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StepsProgressIndicator(
    modifier: Modifier = Modifier,
    totalSteps:Int = 3,
    selectedStep:Int = 1,
    strokeWidth: Dp = 4.dp,
    trackColor: Color = MaterialTheme.colorScheme.outlineVariant,
    indicatorColor: Color = MaterialTheme.colorScheme.primary
){
    check(totalSteps > 1){ "Total steps can't be less then 2" }

    val progress = animateFloatAsState((selectedStep - 1) / (totalSteps - 1).toFloat())

    Canvas(
        modifier =
        modifier
            .padding(16.dp)
            .heightIn(min = 40.dp)
            .graphicsLayer(alpha = 0.99f)
    ){
        val width =  this.size.width
        val height = this.size.height
        val vCenter = height / 2

        val radius = 10.dp.toPx()
        val smallerRadius = 6.dp.toPx()


        val indicatorDistance = (width - radius * 2) / (totalSteps - 1)

        val circlePath = Path()
        val trackPath = Path().also {
            it.moveTo(radius, vCenter)
        }

        for (i in 0 until totalSteps){
            val center = Offset(x = i * indicatorDistance + radius, y = vCenter)

            circlePath.addOval(Rect(center = center, radius = smallerRadius))
            trackPath.addOval(Rect(center = center, radius))
            if (i < (totalSteps - 1)){
                val stroke = strokeWidth.toPx() / 2
                trackPath.moveTo(x = center.x + radius, y = vCenter - stroke)
                trackPath.lineTo(x = (indicatorDistance * (i + 1)), y = vCenter - stroke)
                trackPath.lineTo(x = (indicatorDistance * (i + 1)), y = vCenter + stroke)
                trackPath.lineTo(x = center.x + radius, y = vCenter + stroke)
                trackPath.lineTo(x = center.x + radius, y = vCenter - stroke)
            }

        }

        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
        paint.isAntiAlias = true


        clipPath(
            path = trackPath,
            clipOp = ClipOp.Intersect
        ){
            clipPath(circlePath, clipOp = ClipOp.Difference){
                drawPath(trackPath, color = trackColor)
            }
            drawRect(
                color = indicatorColor,
                size = Size(width = (width - radius * 2) * progress.value + radius * 2, height = height),
                topLeft = Offset(x = 0f, y = 0f)
            )
        }

    }
}

@Preview
@Composable
fun RegistrationProgressIndicatorPreview(){
    Surface(modifier = Modifier.fillMaxSize()) {
        StepsProgressIndicator(
            modifier = Modifier,
            totalSteps = 5,
            selectedStep = 3,
        )
    }
}