package com.example.messenger.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.messenger.data.local.db.entities.SearchEntity
import com.example.messenger.data.repository.search.SearchRepository
import com.example.messenger.ui.search.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
):ViewModel() {

    var query by mutableStateOf("")
        private set

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchList = snapshotFlow{ query }
        .debounce(200L)
        .flatMapLatest {q ->
            if (q.isBlank())
                getEmptyResult()
            else
                searchRepository.getSearch(q)
        }
        .map { it.map { item ->
            item.toUiModel()
        }}
        .cachedIn(viewModelScope)

    fun updateQuery(q:String){
        query = q
    }

    private fun getEmptyResult() = flow<PagingData<SearchEntity>> {
        emit(PagingData.empty())
    }

}