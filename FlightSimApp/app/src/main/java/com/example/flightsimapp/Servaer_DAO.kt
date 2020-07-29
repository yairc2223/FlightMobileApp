package com.example.flightsimapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
// dao class is the class that uses sql to manage th DB.

@Dao
interface Servaer_DAO {

    @Query("SELECT * from server_table ORDER BY Time DESC\nLIMIT 5;")
    fun getAlphabetizedWords(): LiveData<List<ServerEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(serverEntity: ServerEntity)

    @Query("DELETE FROM server_table")
    suspend fun deleteAll()

    @Query("DELETE FROM server_table Where server_id=:id")
    suspend fun deleteServer(id:Int)
}