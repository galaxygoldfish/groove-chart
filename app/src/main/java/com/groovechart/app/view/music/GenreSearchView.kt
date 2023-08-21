package com.groovechart.app.view.music

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.groovechart.app.MainActivity
import com.groovechart.app.NavDestination
import com.groovechart.app.R
import com.groovechart.app.theme.GrooveChartTheme
import com.groovechart.app.util.GCTopBarLarge
import com.groovechart.app.util.getSimilarStrings
import com.groovechart.app.viewmodel.GenreSearchViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GenreSearchView(navController: NavController, multipleGenres: Boolean) {
    val viewModel: GenreSearchViewModel = viewModel()
    LaunchedEffect(true) {
        viewModel.fetchAvailableGenres(navController.context as MainActivity)
    }
    GrooveChartTheme {
        Scaffold(
            modifier = Modifier
                .statusBarsPadding()
                .imePadding(),
            topBar = {
                AnimatedContent(
                    targetState = viewModel.genreSearchValue.text.isNotBlank(),
                    label = "full-search-genre"
                ) { state ->
                    if (!state) {
                        GCTopBarLarge(
                            title = if (multipleGenres) {
                                stringResource(id = R.string.playlist_creator_pick_multiple_genres_title)
                            } else {
                                stringResource(id = R.string.playlist_creator_pick_genre_title)
                            },
                            navController = navController
                        )
                    }
                }
            },
            floatingActionButton = {
                AnimatedContent(
                    targetState = viewModel.selectedGenres.isNotEmpty(),
                    label = "genre-fab"
                ) { ready ->
                    if (ready) {
                        Box(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .rotate(45F)
                                .clip(RoundedCornerShape(3.dp))
                                .background(MaterialTheme.colorScheme.onBackground)
                                .size(50.dp)
                                .clickable {
                                    if (multipleGenres) {
                                        navController.navigate(
                                            "${NavDestination.CustomizeSound}/${
                                                viewModel.selectedGenres.joinToString(
                                                    ","
                                                )
                                            }"
                                        )
                                    } else {
                                        navController.navigate(
                                            "${NavDestination.FinalizeDetailView}/genre/${
                                                viewModel.selectedGenres.joinToString(
                                                    ","
                                                )
                                            }"
                                        )
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_arrow_forward),
                                contentDescription = null,
                                modifier = Modifier.rotate(-45F),
                                tint = MaterialTheme.colorScheme.background
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(Modifier.padding(paddingValues)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedContent(
                        targetState = viewModel.genreSearchValue.text.isNotBlank(),
                        label = "search-back-genre"
                    ) { state ->
                        if (state) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.padding(start = 10.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_back_arrow),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colorScheme.onBackground.copy(0.15F)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = null,
                            modifier = Modifier.padding(20.dp)
                        )
                        Box(Modifier.padding(end = 20.dp)) {
                            if (viewModel.genreSearchValue.text.isBlank()) {
                                Text(
                                    text = stringResource(id = R.string.playlist_creator_pick_genre_search_hint),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(0.5F)
                                )
                            }
                            BasicTextField(
                                value = viewModel.genreSearchValue,
                                onValueChange = {
                                    viewModel.genreSearchValue = it
                                },
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                AnimatedContent(
                    targetState = getSimilarStrings(
                        query = viewModel.genreSearchValue.text,
                        stringList = viewModel.availableGenres
                    ),
                    label = "genre-list"
                ) { genres ->
                    LazyColumn(modifier = Modifier.padding(horizontal = 20.dp)) {
                        if (genres.isEmpty()) {
                            items(viewModel.selectedGenres) { genre ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 15.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .border(
                                            width = 1.5.dp,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            shape = RoundedCornerShape(5.dp)
                                        ),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = genre,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(
                                            vertical = 20.dp,
                                            horizontal = 25.dp
                                        )
                                    )
                                    IconButton(
                                        onClick = { viewModel.selectedGenres.remove(genre) },
                                        modifier = Modifier.padding(15.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_trash),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onBackground
                                        )
                                    }

                                }
                            }
                        }
                        items(genres) { genre ->
                            AnimatedContent(
                                targetState = viewModel.selectedGenres.contains(genre),
                                label = "genre-select-card"
                            ) { selected ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 15.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(
                                            if (selected) {
                                                MaterialTheme.colorScheme.onBackground
                                            } else {
                                                MaterialTheme.colorScheme.background
                                            }
                                        )
                                        .border(
                                            width = 1.5.dp,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            shape = RoundedCornerShape(5.dp)
                                        )
                                        .clickable {
                                            if (selected) {
                                                viewModel.selectedGenres.remove(genre)
                                            }
                                            if (multipleGenres && viewModel.selectedGenres.size < 5) {
                                                viewModel.selectedGenres.add(genre)
                                            } else {
                                                viewModel.selectedGenres.apply {
                                                    clear()
                                                    add(genre)
                                                }
                                            }
                                        },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = genre,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(
                                            vertical = 20.dp,
                                            horizontal = 25.dp
                                        ),
                                        color = if (selected) {
                                            MaterialTheme.colorScheme.background
                                        } else {
                                            MaterialTheme.colorScheme.onBackground
                                        }
                                    )
                                    if (selected) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_check),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.background,
                                            modifier = Modifier.padding(25.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}