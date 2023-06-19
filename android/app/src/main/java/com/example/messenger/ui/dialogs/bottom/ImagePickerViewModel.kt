package com.example.messenger.ui.dialogs.bottom

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.messenger.data.local.provider.images.Image
import com.example.messenger.data.repository.images.LocalImagesRepository
import com.example.messenger.ui.dialogs.bottom.state.ImagePickerState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImagePickerViewModel @Inject constructor(
    localImagesRepository: LocalImagesRepository,
    savedStateHandle: SavedStateHandle
):ViewModel() {

    var state:ImagePickerState = ImagePickerState()

    val images = localImagesRepository
        .getImages()
        .cachedIn(viewModelScope)

    init {
        val initList = savedStateHandle
            .get<List<Image>?>(ImagePickerBottomDialog.INIT_SELECTED_LIST) ?: emptyList()
        state.selectedImages.addAll(initList)
    }

    fun updateBottomSheetProgress(p:Float){
        state.bottomSheetState.value = state.bottomSheetState.value.copy(progress = p)
    }

    fun updateBottomSheetState(state:Int){
        this.state.bottomSheetState.value = this.state.bottomSheetState.value.copy(state = state)
    }
}