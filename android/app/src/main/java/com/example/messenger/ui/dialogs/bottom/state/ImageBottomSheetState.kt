package com.example.messenger.ui.dialogs.bottom.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Stable
class ImageBottomSheetState(
    private val state:ImagePickerState
) {

    val progress by derivedStateOf {
            val p =  state.bottomSheetState.value.progress
            return@derivedStateOf max(0f, (p - 0.75f) / 0.25f)
        }

    val showSpacer by derivedStateOf {
            progress < 0.96f
        }

    val showToolbar by
        derivedStateOf {
            progress > 0
        }

    val diff = 64.dp - 16.dp
    val offset by derivedStateOf {
            (diff * progress) + 16.dp
        }
}

@Composable
fun rememberImageBottomState(state:ImagePickerState): ImageBottomSheetState {
    return remember {
        ImageBottomSheetState(state)
    }
}