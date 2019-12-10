package doit.study.droid.data

sealed class Outcome<out R> {

    data class Success<out T>(val data: T) : Outcome<T>()
    data class Error(val exception: Exception) : Outcome<Nothing>()
    object Loading : Outcome<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}

/**
 * `true` if [Outcome] is of type [Success] & holds non-null [Success.data].
 */
val Outcome<*>.succeeded
    get() = this is Outcome.Success && data != null
