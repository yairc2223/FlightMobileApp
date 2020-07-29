package com.example.flightsimapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [(ServerEntity::class)],version = 1,exportSchema = false)
abstract class AppDataBase: RoomDatabase (){
    abstract fun serverDAO() :Servaer_DAO

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDataBase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "server_database"
                ).addCallback(ServerDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }
        // an inside class for the DB in order tocall back
        private class ServerDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            // opens the database and also returns it if it exists.
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database.serverDAO())
                    }
                }
            }
            // this method populates the database.
            suspend fun populateDatabase(servaerDao: Servaer_DAO) {
                // Delete all content here.
                //servaerDao.deleteAll()

                var server = ServerEntity()
                server.server_id = 1
                server.server_url = "www.google.co.il"
                server.server_lru = System.currentTimeMillis()
                servaerDao.insert(server)
                server = ServerEntity()
                server.server_id = 2
                server.server_url = "localhost1"
                server.server_lru = System.currentTimeMillis()
                servaerDao.insert(server)
                server.server_id = 3
                server.server_url = "localhost2"
                server.server_lru = System.currentTimeMillis()
                servaerDao.insert(server)
                server.server_id = 4
                server.server_url = "localhost3"
                server.server_lru = System.currentTimeMillis()
                servaerDao.insert(server)
                server.server_id = 5
                server.server_url = "http://10.0.2.2:5001/"
                server.server_lru = System.currentTimeMillis()
                servaerDao.insert(server)
            }
        }
    }
}
