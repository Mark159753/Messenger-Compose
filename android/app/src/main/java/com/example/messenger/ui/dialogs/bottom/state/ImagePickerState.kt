package com.example.messenger.ui.dialogs.bottom.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.messenger.data.local.provider.images.Image
import com.google.android.material.bottomsheet.BottomSheetBehavior


data class BottomSheetState(
    val state:Int = BottomSheetBehavior.STATE_COLLAPSED,
    val progress:Float = 0f
)

data class ImagePickerState(
    val bottomSheetState: MutableState<BottomSheetState> = mutableStateOf(BottomSheetState()),
    val selectedImages: SnapshotStateList<Image> = mutableStateListOf<Image>(),
){
    val showStickyButton by derivedStateOf {
        selectedImages.isNotEmpty() &&
                (bottomSheetState.value.progress > -0.54f || bottomSheetState.value.state in
                        listOf(BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_EXPANDED))
    }
}