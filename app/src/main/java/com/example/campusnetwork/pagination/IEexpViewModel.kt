package com.example.campusnetwork.pagination

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch

class IEexpViewModel : ViewModel() {

    private val repository = Repository()
    var state by mutableStateOf(IEScreenState())
    private val paginator = DefaultPaginator(
        initialKey = state.page,
        onLoadUpdated = {
            state = state.copy(isLoading = it)
        },
        onRequest = { nextPage ->
            repository.getItems(nextPage, 20)
        },
        getNextKey = {
            try {
                state.page.startAfter(it.last())
            } catch (e: Exception) {
                state.page
            }
        },
        onError = {
            state = state.copy(error = it?.localizedMessage)
        },
        onSucess = { items, newKey ->
            var newItems = state.items
            newItems.addAll(items)
            state = state.copy(
                items = newItems,
                page = newKey,
                endReached = items.isEmpty()
            )
        }
    )

    init {
        loadNextItems()
    }

    fun loadNextItems() {
        viewModelScope.launch {
//            println("called " + state.page)
            paginator.loadNextItems()
        }
    }
    fun reset(){
        viewModelScope.launch {
            paginator.reset()
        }
    }
}
data class IEScreenState(
    val isLoading: Boolean = false,
    val items: ArrayList<DocumentSnapshot> = ArrayList(),
    val error: String? = null,
    val endReached: Boolean = false,
    val page: Query = FirebaseFirestore.getInstance().collection("IEexps").orderBy("timestamp")
)