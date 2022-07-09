package com.example.campusnetwork.pagination

import java.util.*
import kotlin.collections.ArrayList

class DefaultPaginator<Key, Item> (
    private val initialKey : Key,
    private inline val onLoadUpdated: (Boolean) -> Unit,
    private inline val onRequest: suspend (nextKey : Key) -> Result<ArrayList<Item>>,
    private inline val getNextKey: suspend (ArrayList<Item>) -> Key,
    private inline val onError: suspend (Throwable?) -> Unit,
    private inline val onSucess : suspend (items : ArrayList<Item>, newKey : Key) -> Unit
) : Paginator<Key, Item>{
    private var currentKey = initialKey
    private var isMakingRequest = false
    override suspend fun loadNextItems() {
        if(isMakingRequest){
            return
        }
        isMakingRequest = true
        onLoadUpdated(true)
//        println(currentKey)
        val result = onRequest(currentKey)
        isMakingRequest = false
        val items = result.getOrElse {
            onError(it)
            onLoadUpdated(false)
            return
        }
        currentKey = getNextKey(items)
        onSucess(items, currentKey)
        onLoadUpdated(false)
    }

    override fun reset() {
        currentKey = initialKey
    }
}