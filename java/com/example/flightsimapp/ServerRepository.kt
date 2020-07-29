package com.example.flightsimapp

import androidx.lifecycle.LiveData
// this class is the server repository class it craetes a connection between tha data base DAO to the MODEL.
class ServerRepository(private val servaerDao: Servaer_DAO) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allWords: LiveData<List<ServerEntity>> = servaerDao.getAlphabetizedWords()
// insertd a word using the adaptee.
    suspend fun insert(serverEntity: ServerEntity) {
        servaerDao.insert(serverEntity)
    }
    // deletes a server from the DB.
    suspend fun deleteServer(serverId:Int) {
        servaerDao.deleteServer(serverId)
    }
}