package com.shimmermare.stuffiread.tags

interface TagTreeService {
    suspend fun getTree(): TagTree

    suspend fun updateTree(tree: TagTree)
}