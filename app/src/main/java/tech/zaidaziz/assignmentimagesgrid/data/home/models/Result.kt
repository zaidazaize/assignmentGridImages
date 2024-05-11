package tech.zaidaziz.assignmentimagesgrid.data.home.models

sealed class Result<out T: Any> {
data class Success<out T: Any>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()

    data class Loading(val loadingStatus: Boolean): Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            is Loading -> "Loading[data=$loadingStatus"
        }
    }

}