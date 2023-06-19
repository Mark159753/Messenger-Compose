package com.example.messenger.ui.dialogs.bottom

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.messenger.R
import com.example.messenger.data.local.provider.images.Image

@Composable
fun ImageItem(
    modifier: Modifier = Modifier,
    item: Image? = null,
    isChecked:Boolean = false,
    onCheckedChange:(Boolean, Image?)->Unit = { _, _ ->  }
){
    Box(
        modifier = modifier
            .clickable { onCheckedChange(!isChecked, item) }
            .background(Color.Gray)
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {

        if (item != null){
            Image(
                painter = rememberAsyncImagePainter(item.uri),
                contentDescription = item.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        RoundedCheckBox(
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.TopEnd),
            isChecked = isChecked,
            onCheckedChange = {
                onCheckedChange(it, item)
            }
        )
    }
}

@Composable
fun RoundedCheckBox(
    modifier: Modifier = Modifier,
    isChecked:Boolean = false,
    onCheckedChange:(Boolean)->Unit = {  }
){
    IconButton(
        modifier = modifier
            .size(20.dp),
        onClick = {
            onCheckedChange(!isChecked)
        },
    ) {
        Icon(
            painter = painterResource(
                id = if (isChecked) R.drawable.checked_circle else R.drawable.unchecked_circle
            ),
            contentDescription = "",
            tint = if (isChecked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
private fun ImageItemPreview(){
    ImageItem()
}