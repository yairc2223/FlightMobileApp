package com.example.flightsimapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DateFormat
// this class represents the server entity in the table of the DB.
@Entity(tableName = "server_table")
class ServerEntity() {
    @PrimaryKey
    var server_id : Int = 0
    @ColumnInfo(name = "URL")
    var server_url :String= ""
    @ColumnInfo(name = "Time")
    var server_lru : Long = 0

}

