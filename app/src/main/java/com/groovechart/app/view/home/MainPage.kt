package com.groovechart.app.view.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.groovechart.app.NavDestination
import com.groovechart.app.R
import com.groovechart.app.util.Chip
import com.groovechart.app.util.ContentListItem
import com.groovechart.app.viewmodel.HomeViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalAnimationApi::class)
@Composable
fun MainPage(
    viewModel: HomeViewModel,
    paddingValues: PaddingValues,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .verticalScroll(
                state = rememberScrollState()
            )
            .padding(paddingValues)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 17.dp)
        ) {
            Text(
                text = stringResource(id = R.string.home_title_top_genres),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            AnimatedVisibility(viewModel.topGenreList.isNotEmpty(),) {
                FlowRow {
                    viewModel.topGenreList.forEach { item ->
                        Chip(
                            text = item.uppercase(),
                            filled = false,
                            modifier = Modifier.padding(end = 10.dp, top = 10.dp)
                        )
                    }
                }
            }
            SectionHeader(
                text = stringResource(id = R.string.home_title_top_tracks),
                callback = {
                    navController.navigate("${NavDestination.TopItems}/0")
                },
                modifier = Modifier.padding(top = 10.dp)
            )
            AnimatedVisibility(viewModel.topSongList.isNotEmpty()) {
                Column {
                    viewModel.topSongList.forEach { song ->
                        ContentListItem(
                            title = song.name,
                            subtitle = song.album.artists[0].name,
                            imageUrl = song.album.images[0].url
                        )
                    }
                }
            }
            SectionHeader(
                text = stringResource(id = R.string.home_title_top_artists),
                callback = {
                    navController.navigate("${NavDestination.TopItems}/1")
                },
                modifier = Modifier.padding(top = 10.dp)
            )
            AnimatedVisibility(viewModel.topArtistList.isNotEmpty()) {
                Column {
                    viewModel.topArtistList.forEach { artist ->
                        ContentListItem(
                            title = artist.name,
                            subtitle = "",
                            imageUrl = artist.images[0].url,
                            cropCircular = true
                        )
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}