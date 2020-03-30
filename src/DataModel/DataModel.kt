package DataModel

data class Player(val lp: String, val nickname: String, val account_id: Int)
data class PlayerList(val playerList: ArrayList<Player>)
data class PlayerPersonalData (
        val wins: Int,
        val draws: Int,
        val lossess: Int,
        val clanTag: String,
        val clanEmblemsURL: String,
        val clanID: Int,
        val battle_avg_xp: Int,
        val frags: Int,
        val avg_damage_assisted: Float,
        val hits_percents: Int,
        val spotted: Int,
        val dropped_capture_points: Int,
        val capture_points: Int
)