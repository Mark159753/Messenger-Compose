package com.example.messenger.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.messenger.R
import com.example.messenger.ui.chat.model.ChatArgs
import com.example.messenger.ui.search.model.ItemSearchModel

@Composable
fun SearchRoute(
    viewModel: SearchViewModel,
    onNavBack:()->Unit,
    onNavToChat:(args:ChatArgs, chatId:String?)->Unit = { _, _ -> }
){

    val pagingItems = viewModel.searchList.collectAsLazyPagingItems()

    SearchScreen(
        query = viewModel.query,
        onUpdateQuery = viewModel::updateQuery,
        onNavBack = onNavBack,
        searchItems = pagingItems,
        onItemClick = onNavToChat
    )
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchScreen(
    query: String = "",
    onUpdateQuery:(q:String) -> Unit = {},
    onNavBack:()->Unit = {},
    onItemClick:(args: ChatArgs, chatId:String? )->Unit = { _, _, ->},
    searchItems: LazyPagingItems<ItemSearchModel>? = null
){

    val focusManager: FocusManager = LocalFocusManager.current
    val keyboard: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .systemBarsPadding()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .fillMaxSize()

    ) {
        SearchBar(
            query = query,
            onBackPress = onNavBack,
            onUpdateQuery = onUpdateQuery,
            focusManager = focusManager,
            keyboard = keyboard
        )

        Box(modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
        ){
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
            ){
                if (searchItems != null){
                    items(
                        count = searchItems.itemCount,
                        key = searchItems.itemKey { it.id }
                    ){ index ->
                        val item = searchItems[index]

                        item?.let {
                            SearchItem(
                                item = it,
                                onClick = { args, chatId ->
                                    focusManager.clearFocus()
                                    keyboard?.hide()
                                    onItemClick(args, chatId)
                                }
                            )

                            Divider(
                                modifier = Modifier
                                    .padding(start = 72.dp, end = 16.dp)
                            )
                        }
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    query:String = "",
    onUpdateQuery:(q:String) -> Unit = {},
    onBackPress:()->Unit = {},
    focusManager: FocusManager = LocalFocusManager.current,
    keyboard: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current
){

    val windowInfo = LocalWindowInfo.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = windowInfo){
        snapshotFlow { windowInfo.isWindowFocused }.collect { isWindowFocused ->
            if (isWindowFocused) {
                focusRequester.requestFocus()
            }
        }
    }

    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                focusManager.clearFocus()
                keyboard?.hide()
                onBackPress()
            },
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        TextField(
            modifier = Modifier
                .focusRequester(focusRequester)
                .weight(1f),
            value = query,
            onValueChange = onUpdateQuery,
            maxLines = 1,
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                containerColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = MaterialTheme.typography.headlineSmall,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.search_screen_hint),
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(
                        onClick = {
                            onUpdateQuery("")
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_close_24),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = {
                focusManager.clearFocus()
                keyboard?.hide()
            })
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview(){
    Surface(modifier = Modifier.fillMaxSize()) {
        SearchScreen()
    }
}