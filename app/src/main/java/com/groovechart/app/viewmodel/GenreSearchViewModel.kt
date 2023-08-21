package com.groovechart.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.groovechart.app.MainActivity
import com.groovechart.app.network.NetworkProvider

class GenreSearchViewModel : ViewModel() {

    private val networkProvider = NetworkProvider()

    var genreSearchValue by mutableStateOf(TextFieldValue())
    var selectedGenres = mutableStateListOf<String>()
    var availableGenres = mutableStateListOf<String>()

    suspend fun fetchAvailableGenres(context: MainActivity) {
        availableGenres.apply {
            clear()
            addAll(networkProvider.getAvailableGenreSeeds(context).genres)
        }
    }

}