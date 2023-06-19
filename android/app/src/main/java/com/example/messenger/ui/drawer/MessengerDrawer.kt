package com.example.messenger.ui.drawer

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.messenger.BuildConfig

@Composable
fun MessengerDrawer(
    data:DrawerHeaderData? = null,
    drawerState: MyDrawerState = rememberDrawerState(initialValue = MyDrawerValue.Closed),
    gesturesEnabled:Boolean = true,
    onNavigate:(DrawerDestination)->Unit = {},
    content: @Composable () -> Unit,
){
    MyDrawer(
        drawerState = drawerState,
        drawerContent = {
            MessengerDrawerContent(
                onNavigate = onNavigate,
                data = data
            )
        },
        content = content,
        gesturesEnabled = gesturesEnabled
//        scrimColor = Color.Transparent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessengerDrawerContent(
    onNavigate:(DrawerDestination)->Unit,
    data:DrawerHeaderData? = null
){
    ModalDrawerSheet(
        modifier = Modifier.requiredWidth(240.dp)
    ){
        Column(
            Modifier
                .width(240.dp)
                .fillMaxHeight(),
        ) {

            DrawerHeader(
                modifier = Modifier
                    .padding(16.dp),
                data = data
            )

            for (item in DrawerParams.drawerButtons){
                AppDrawerItem(
                    item = item,
                    onClick = {
                        onNavigate(it.destination)
                    }
                )
            }
        }
    }
}

@Composable
fun DrawerHeader(
    modifier: Modifier = Modifier,
    data:DrawerHeaderData? = null
){
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = data?.name?.first()?.uppercase() ?: "",
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                style = MaterialTheme.typography.bodyLarge
            )

            if (!data?.avatar.isNullOrBlank()){
                Image(
                    painter = rememberAsyncImagePainter(BuildConfig.BASE_URL + data?.avatar),
                    contentDescription = "User avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Text(
            modifier = Modifier
                .padding(top = 16.dp),
            text = data?.name ?: "",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = data?.email ?: "",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerItem(
    item: DrawerMenuItem,
    onClick:(DrawerMenuItem)->Unit = {}
){
    Surface(
        modifier = Modifier,
        onClick = { onClick(item) },
        shape = RoundedCornerShape(50),
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = item.icon),
                contentDescription = stringResource(id = item.descriptionId),
                modifier = Modifier
                    .size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(id = item.title),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun MessengerDrawerContentPreview(){
    Surface(modifier = Modifier.fillMaxSize()) {
        MessengerDrawer(
            drawerState = rememberDrawerState(initialValue = MyDrawerValue.Open),
            content = {}
        )
    }
}