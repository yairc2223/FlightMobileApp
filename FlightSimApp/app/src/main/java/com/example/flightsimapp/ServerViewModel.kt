package com.example.flightsimapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
// the server view model class.
class ServerViewModel (application: Application) : AndroidViewModel(application) {

    private val repository: ServerRepository

    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allWords: LiveData<List<ServerEntity>>

    init {
        val wordsDao = AppDataBase.getDatabase(application, viewModelScope).serverDAO()
        repository = ServerRepository(wordsDao)
        allWords = repository.allWords
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(serverEntity: ServerEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(serverEntity)
    }

    /**
     * Deleting a new coroutine to insert the data in a non-blocking way
     */
    fun deleteserver(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteServer(id)
    }
}
