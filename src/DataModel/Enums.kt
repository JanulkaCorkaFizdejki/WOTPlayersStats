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


enum class DATA_ERROR {
    SERVER_ERROR {
        override fun message() = "Server niedostępny"
    },

    JSON_ERROR {
        override fun message() = "Błąd danych"
    },

    JSON_EMPTY {
        override fun message() = "Brak danych"
    };

    abstract fun message(): String
}

enum class SERVERS_APPLICATION {
    EU {
        override fun tag()      = "EU"
        override fun domain()   = URLS.api_wot_url_eu
    },
    RU {
        override fun tag()      = "RU"
        override fun domain()   = URLS.api_wot_url_ru
    },
    NA {
        override fun tag()      = "NA"
        override fun domain()   = URLS.api_wot_url_na
    },
    ASIA {
        override fun tag()      = "ASIA"
        override fun domain()   = URLS.api_wot_url_asia
    };

    abstract fun tag(): String
    abstract fun domain(): String
}
