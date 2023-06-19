package com.example.messenger.ui.dialogs.question

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.example.messenger.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun QuestionDialog(
    msg:String,
    title:String? = null,
    okBtn:String = stringResource(id = R.string.ok),
    cancelBtn:String = stringResource(id = R.string.cancel),
    dialogState: MutableState<Boolean> = remember { mutableStateOf(false) },
    onConfirm:()->Unit = {},
    onDismiss:()->Unit = {}
){

    if (dialogState.value) {
        Dialog(
            onDismissRequest = {
                dialogState.value = false
                onDismiss()
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            (LocalView.current.parent as DialogWindowProvider)
                .window.attributes.windowAnimations = R.style.Theme_Splash_DialogTheme

            QuestionDialogContent(
                title = title,
                msg = msg,
                dialogState = dialogState,
                onConfirm = onConfirm,
                onDismiss = onDismiss,
                okBtn = okBtn,
                cancelBtn = cancelBtn
            )
        }
    }
}

@Composable
internal fun QuestionDialogContent(
    title: String?,
    msg: String,
    dialogState: MutableState<Boolean>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    okBtn:String,
    cancelBtn:String
){
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.surface)
            .padding(vertical = 8.dp)
            .padding(top = 8.dp)
    ) {
        if (!title.isNullOrBlank()){
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            )
        }

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = msg,
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TextButton(
                modifier = Modifier
                    .weight(1f),
                onClick = {
                    dialogState.value = false
                    onConfirm()
                },
            ) {
                Text(text = okBtn)
            }

            TextButton(
                modifier = Modifier
                    .weight(1f),
                onClick = {
                    dialogState.value = false
                    onDismiss()
                },
            ) {
                Text(text = cancelBtn)
            }
        }
    }
}


@Composable
internal fun AnimatedTranslateInTransition(
    visible: MutableState<Boolean>,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {

    AnimatedVisibility(
        visible = visible.value,
        enter = scaleIn(
            animationSpec = tween(ANIMATION_TIME.toInt())
        ),
        exit = scaleOut(
            animationSpec = tween(ANIMATION_TIME.toInt())
        ),
        content = content
    )
}

suspend fun startDismissWithExitAnimation(
    animateTrigger: MutableState<Boolean>,
    onDismissRequest: () -> Unit
) {
    animateTrigger.value = false
    delay(ANIMATION_TIME)
    onDismissRequest()
}

private const val ANIMATION_TIME = 300L

@Composable
@Preview(showBackground = true)
fun QuestionDialogPreview(){
    Surface(modifier = Modifier.fillMaxSize()) {
        val dialogState = remember { mutableStateOf(true) }
        QuestionDialog(
            msg = "Do you really want to do it?",
            title = "Hello my Friend",
            dialogState = dialogState
        )
    }
}