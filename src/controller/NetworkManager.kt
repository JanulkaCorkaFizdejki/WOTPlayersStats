package controller

import DataModel.DATA_ERROR
import DataModel.Player
import DataModel.PlayerList
import DataModel.PlayerPersonalData
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class NetworkManager {
    private val timeout: Int = 5000

    fun getPlayerLogin (url: String) : Any {
        val obj = getJSONData(url)

        if (obj is DATA_ERROR) return obj

        val jsonObj = obj as JSONObject

        val jsonData = jsonObj.getJSONArray("data")
        val playerList: ArrayList<Player> = ArrayList()

        var iterator = 1
        for (player in jsonData) {
            val playerJSONObject = JSONObject(player.toString())
            val lp = "${iterator}."
            val playerObj = Player(lp, playerJSONObject.getString("nickname"), playerJSONObject.getInt("account_id"))
            playerList.add(playerObj)
            iterator += 1
        }

        return PlayerList(playerList)
    }

    fun getPlayerPersonalData (url: String, id: String) : Any {

       val obj = getJSONData(url)

        if (obj is DATA_ERROR) return obj

        val jsonObj = obj as JSONObject


        val jsonData    = jsonObj.getJSONObject("data")
        val data  = jsonData.getJSONObject(id).getJSONObject("statistics").getJSONObject("all")


        var clan_id  = jsonData.getJSONObject(id).get("clan_id")

        var clan_tag        = "0"
        var clan_emblems    = "0"

        if (clan_id != null) {
            val clan_domain     = url.replace("account", "clans", true).split("&")
            val clan_url        = "${clan_domain[0]}&clan_id=${clan_id}"
            val objClan = getJSONData(clan_url)

            if (objClan is DATA_ERROR) {
                clan_id = 0
            } else {
                val jsonObjClan = objClan  as JSONObject
                clan_tag = jsonObjClan.getJSONObject("data").getJSONObject(clan_id.toString()).getString("tag")
                clan_emblems = jsonObjClan.getJSONObject("data").getJSONObject(clan_id.toString()).getJSONObject("emblems").getJSONObject("x64").getString("wot")
            }

        }

        return PlayerPersonalData(
                data.getLong("wins"),
                data.getLong("draws"),
                data.getLong("losses"),
                clan_tag,
                clan_emblems,
                clan_id as Int,
                data.getInt("battle_avg_xp"),
                data.getLong("frags"),
                data.getFloat("avg_damage_assisted"),
                data.getInt("hits_percents"),
                data.getLong("spotted"),
                data.getLong("dropped_capture_points"),
                data.getLong("capture_points"),
                data.getLong("damage_dealt"),
                data.getLong("damage_received"),
                data.getLong("survived_battles"),
                data.getLong("xp"),
                data.getDouble("tanking_factor")
        )
    }



    private fun getJSONData (url: String) : Any {
        val url_ = URL(url)
        val connection = url_!!.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        connection.connectTimeout = timeout
        connection.doOutput = true

        if (connection.responseCode != HttpURLConnection.HTTP_OK) return DATA_ERROR.SERVER_ERROR


        val bufferedReader = BufferedReader(InputStreamReader(connection.inputStream, "UTF-8"))
        val stringBuilder = StringBuilder()

        var str: String?
        while (null != bufferedReader.readLine().also { str = it }) {
            stringBuilder.append(str)
        }

        val jsonObj     =  JSONObject (stringBuilder.toString())

        if (!jsonObj.getString("status").equals("ok")) return DATA_ERROR.JSON_EMPTY

        return  jsonObj
    }
}