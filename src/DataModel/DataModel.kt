package DataModel

import kotlin.reflect.jvm.internal.impl.builtins.UnsignedType

data class Player(val lp: String, val nickname: String, val account_id: Int)
data class PlayerList(val playerList: ArrayList<Player>)
data class PlayerPersonalData (
        val wins: Long,
        val draws: Long,
        val lossess: Long,
        val clanTag: String,
        val clanEmblemsURL: String,
        val clanID: Int,
        val battle_avg_xp: Int,
        val frags: Long,
        val avg_damage_assisted: Float,
        val hits_percents: Int,
        val spotted: Long,
        val dropped_capture_points: Long,
        val capture_points: Long,
        val damage_dealt: Long,
        val damage_received:  Long,
        val survived_battles: Long,
        val xp: Long,
        val tanking_factor: Double
)