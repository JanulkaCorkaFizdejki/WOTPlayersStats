package controller

import DataModel.DatabaseLocal
import java.sql.*

class DatabaseManager {
    private val localDatabaseName: String = DatabaseLocal.name
    private var connection: Connection? = null

    init {
        try {
            Class.forName("org.sqlite.JDBC")
            this.connection = DriverManager.getConnection("jdbc:sqlite:$localDatabaseName")
        } catch (e: Exception) {
            System.out.println(e)
        }
    }

    fun select(query: String): ResultSet {
        val stmt: Statement = this.connection!!.createStatement()
        return stmt.executeQuery(query)
    }

    fun insert(query: String) {
        val pstmt:  PreparedStatement = this.connection!!.prepareStatement(query)
        pstmt.executeUpdate()
        pstmt.close()
    }

    fun delete(query: String) {
        val pstmt:  PreparedStatement = this.connection!!.prepareStatement(query)
        pstmt.executeUpdate()
        pstmt.close()
    }



    fun closeConnection() {
        this.connection?.close()
    }


}