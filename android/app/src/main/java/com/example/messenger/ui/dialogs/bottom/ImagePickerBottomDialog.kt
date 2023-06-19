package com.example.messenger.ui.dialogs.bottom

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.messenger.R
import com.example.messenger.data.local.provider.images.Image
import com.example.messenger.ui.dialogs.bottom.state.ImagePickerState
import com.example.messenger.ui.dialogs.bottom.state.rememberImageBottomState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt


@AndroidEntryPoint
class ImagePickerBottomDialog(): BottomSheetDialogFragment() {

    private val viewModel: ImagePickerViewModel by viewModels()

    private var roundedBg: GradientDrawable? = null
    private val mCornerRaii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)


    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback(){
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            viewModel.updateBottomSheetState(newState)
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            viewModel.updateBottomSheetProgress(slideOffset)
            animRoundedCorner(
                if (slideOffset >=0) slideOffset else 0f
            )
        }

    }

    override fun getTheme(): Int {
        return R.style.Theme_ImagePickerBottomSheet
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            (it as BottomSheetDialog).let { bottomSheetDialog ->
                val containerLayout = bottomSheetDialog.findViewById(
                    com.google.android.material.R.id.container
                ) as? FrameLayout

                containerLayout?.addView(
                    createStickyView(),
                    createStickyLayoutParams()
                )

                val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                roundedBg = bottomSheet?.background as? GradientDrawable
                savedInstanceState?.getFloat(SAVE_SHEET_PROGRESS)?.let {
                    animRoundedCorner(it)
                }

                val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet!!)
                behavior.apply {
                    isDraggable = true
                    state = savedInstanceState?.getInt(SAVE_SHEET_STATE) ?: BottomSheetBehavior.STATE_COLLAPSED
                    addBottomSheetCallback(bottomSheetCallback)
                }
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    val pagingList = viewModel.images.collectAsLazyPagingItems()

                    ImageBottomSheet(
                        pagingList = pagingList,
                        state = viewModel.state,
                        onBackPress = {
                            dismiss()
                        },
                        onSelect = {images ->
                            setFragmentResult(ON_SELECTED_IMAGES_KEY, bundleOf(ON_SELECTED_IMAGES_KEY to ArrayList(images)))
                        }
                    )

                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        animRoundedCorner(0f)
        super.onDismiss(dialog)
    }



    private fun animRoundedCorner(offset: Float){
        val p = 1f - offset

        val radius = convertDpToPixel(16f) * p
        for (i in 0 until 4){
            mCornerRaii[i] = radius
        }
        roundedBg?.cornerRadii = mCornerRaii
    }

    private fun convertDpToPixel(dp: Float): Float {
        return dp * (requireContext().resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    private fun createStickyView(): ComposeView {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                StickyButton(
                    state = viewModel.state,
                    onSend = {
                        setFragmentResult(ON_SEND, bundleOf())
                        dismiss()
                    }
                )
            }
        }
    }

    private fun createStickyLayoutParams(): FrameLayout.LayoutParams {
        return  FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.BOTTOM or Gravity.END
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SAVE_SHEET_STATE, viewModel.state.bottomSheetState.value.state)
        outState.putFloat(SAVE_SHEET_PROGRESS, viewModel.state.bottomSheetState.value.progress)
    }

    companion object{
        const val ON_SELECTED_IMAGES_KEY = "com.example.messenger.ui.dialogs.bottom.ImagePickerBottomDialog.SELECTED_IMAGES_KEY"
        const val ON_SEND = "com.example.messenger.ui.dialogs.bottom.ImagePickerBottomDialog.ON_SEND"
        const val INIT_SELECTED_LIST = "com.example.messenger.ui.dialogs.bottom.INIT_SELECTED_LIST"

        private const val SAVE_SHEET_STATE = "com.example.messenger.ui.dialogs.bottom.SAVE_SHEET_STATE"
        private const val SAVE_SHEET_PROGRESS = "com.example.messenger.ui.dialogs.bottom.SAVE_SHEET_PROGRESS"

        fun create(
            list: List<Image> = emptyList()
        ): ImagePickerBottomDialog {
            return ImagePickerBottomDialog().also {
                it.arguments = bundleOf(INIT_SELECTED_LIST to list)
            }
        }
    }

}

@Composable
fun ImageBottomSheet(
    pagingList:LazyPagingItems<Image>? = null,
    state:ImagePickerState,
    onBackPress:()->Unit = {},
    onSelect: (list: List<Image>) -> Unit
){

    val sheetState = rememberImageBottomState(state)

    Box(modifier = Modifier.fillMaxSize()){

        if (sheetState.showSpacer){
            Spacer(
                modifier = Modifier
                    .graphicsLayer { alpha = 1f - sheetState.progress }
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(100))
                    .background(Color.Gray)
                    .width(50.dp)
                    .height(2.dp)
                    .align(Alignment.TopCenter)
            )
        }

        if (sheetState.showToolbar){
            ImagePickerToolbar(
                modifier = Modifier
                    .graphicsLayer {
                        alpha = sheetState.progress
                    },
                onBackPress = onBackPress
            )
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = sheetState.offset)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth(),
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ){
                if (pagingList != null){
                    items(
                        count = pagingList.itemCount,
                        key = pagingList.itemKey { it.id }
                    ){ index ->
                        val item = pagingList[index]

                        ImageItem(
                            item = item,
                            onCheckedChange = { isChecked, image ->
                                if (isChecked && image != null)
                                    state.selectedImages.add(image)
                                else
                                    state.selectedImages.removeIf { it.id == image?.id }
                                onSelect(state.selectedImages)
                            },
                            isChecked = item?.id in state.selectedImages.map { it.id }
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun ImagePickerToolbar(
    modifier: Modifier = Modifier,
    onBackPress:()->Unit = {},
    title:String = stringResource(id = R.string.image_picker_bottom_dialog_gallery)
){
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .drawBehind {
                val borderSize = 1.dp.toPx()
                drawLine(
                    color = Color.Black.copy(alpha = 0.8f),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = borderSize
                )
            }
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    onBackPress()
                },
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                modifier = Modifier
                    .padding(start = 4.dp),
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectButton(
    modifier: Modifier = Modifier,
    images:SnapshotStateList<Image> = mutableStateListOf(),
    onSend: () -> Unit = {}
){
    Box(
        modifier = modifier
    ) {
        IconButton(
            modifier = Modifier
                .clip(CircleShape)
                .size(60.dp),
            onClick = onSend,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.send_btn_ico),
                contentDescription = "Select",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        if (images.size > 0){
            Badge(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(top = 5.dp)
            ){
                Text(text = images.size.toString())
            }
        }
    }
}

@Composable
private fun StickyButton(
    state:ImagePickerState,
    onSend: () -> Unit = {}
){

    MaterialTheme {
        AnimatedVisibility(
            state.showStickyButton,
            enter = slideInVertically(initialOffsetY = {
                it * 2
            }) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = {
                    (it * 1.5).roundToInt()
                }
            ) + fadeOut()
        ) {
            SelectButton(
                modifier = Modifier
                    .padding(16.dp),
                images = state.selectedImages,
                onSend = onSend
            )
        }
    }
}


@Preview
@Composable
private fun ImagePickerToolbarPreview(){
    ImagePickerToolbar()
}
@Preview
@Composable
private fun SelectButtonPreview(){
    SelectButton()
}