package DataModel

object Colors {
    const val REDhexff6059: String      = "#ff6059"
    const val YELLOWhexffbd2e: String   = "#ffbd2e"
    const val GREENhex28ca42: String    = "#28ca42"
}

object WinTittles {
    const val Main: String = "World of Tanks - Player Statistics"
}

object URLS {
    const val api_wot_url_eu: String        = "https://api.worldoftanks.eu/"
    const val api_wot_url_ru: String        = "https://api.worldoftanks.ru/"
    const val api_wot_url_na: String        = "https://api.worldoftanks.com/"
    const val api_wot_url_asia: String      = "https://api.worldoftanks.asia/"
    const val id_wot_application: String    = "application_id=7299f1f9039c42faa0fd88ef78bae25a"
    const val language: String              = "language=pl"
    const val account_list: String          = "wot/account/list/"
    const val account_info: String          = "wot/account/info/"
}

object DatabaseLocal {
    const val name: String = "wot_player_statistics.db"
    object tabels {
        const val players: String = "players"

    }
}
