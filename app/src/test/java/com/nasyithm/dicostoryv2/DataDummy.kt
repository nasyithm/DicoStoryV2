package com.nasyithm.dicostoryv2

import com.nasyithm.dicostoryv2.data.remote.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "photoUrl $i",
                "createdAt + $i",
                "name $i",
                "description $i",
                i.toDouble(),
                i.toString(),
                i.toDouble(),
            )
            items.add(story)
        }
        return items
    }
}