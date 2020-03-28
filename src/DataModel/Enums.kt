package DataModel

enum class INTERNET_ACCESS {
    INIT {
        override fun message() = "Sprawdzam dostęp do Internetu"
    },

    NO_CONNECTION {
        override fun message() = "Brak dostępu do Internetu"
    },

    CONNECTION {
        override fun message() = "Połączony"
    };


    abstract fun message(): String
}

