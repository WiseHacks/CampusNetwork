package com.example.campusnetwork.pagination

interface Paginator <Key, Item> {
    suspend fun loadNextItems()
    fun reset()

}