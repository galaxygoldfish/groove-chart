package com.groovechart.app.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.groovechart.app.MainActivity
import com.groovechart.app.R
import com.groovechart.app.theme.GrooveChartTheme
import com.groovechart.app.util.Chip
import com.groovechart.app.util.ContentListItem
import com.groovechart.app.util.LoadingView
import com.groovechart.app.viewmodel.TopItemViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TopItemView(navController: NavController, type: Int) {
    val viewModel: TopItemViewModel = viewModel()
    LaunchedEffect(true) {
        viewModel.fetchTopItems(navController.context as MainActivity, type)
    }
    GrooveChartTheme {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .statusBarsPadding()
                        .padding(15.dp)
                        .padding(bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back_arrow),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    AnimatedContent(
                        targetState = type,
                        label = "nav-toplabel"
                    ) { state ->
                        Text(
                            text = when (state) {
                                0 -> stringResource(id = R.string.top_track_header)
                                else -> stringResource(id = R.string.top_artist_header)
                            },
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(start = 15.dp)
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    stringArrayResource(id = R.array.top_item_time_choices).forEachIndexed { index, item ->
                        AnimatedContent(
                            targetState = viewModel.timeRangeSelection == index,
                            label = "top-time-chip"
                        ) { state ->
                            Chip(
                                text = item,
                                filled = state,
                                onClick = {
                                    viewModel.timeRangeSelection = index
                                    CoroutineScope(Dispatchers.Default).launch {
                                        viewModel.fetchTopItems(navController.context as MainActivity, type)
                                    }
                                },
                                modifier = Modifier.padding(end = 10.dp)
                            )
                        }
                    }
                }
                AnimatedContent(
                    targetState = if (type == 0) {
                        viewModel.topSongList.isEmpty()
                    } else {
                        viewModel.topArtistList.isEmpty()
                    },
                    label = "loading"
                ) { state ->
                    if (state) {
                        LoadingView()
                    }
                }
                AnimatedContent(
                    targetState = if (type == 0) {
                        viewModel.topSongList.isNotEmpty()
                    } else {
                        viewModel.topArtistList.isNotEmpty()
                    },
                    label = "top-content"
                ) { state ->
                    if (state) {
                        LazyColumn(
                            modifier = Modifier.padding(top = 15.dp, start = 15.dp, end = 15.dp)
                        ) {
                            if (type == 0) {
                                items(viewModel.topSongList) { item ->
                                    ContentListItem(
                                        title = item.name,
                                        subtitle = item.album.artists[0].name,
                                        imageUrl = item.album.images[0].url
                                    )
                                }
                            } else {
                                items(viewModel.topArtistList) { item ->
                                    ContentListItem(
                                        title = item.name,
                                        subtitle = "",
                                        imageUrl = item.images[0].url,
                                        cropCircular = true
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