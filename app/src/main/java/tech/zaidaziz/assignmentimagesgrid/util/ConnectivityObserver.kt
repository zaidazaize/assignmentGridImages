package tech.zaidaziz.assignmentimagesgrid.util

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun isOnline() : Flow<Boolean>

}