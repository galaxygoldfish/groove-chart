package com.groovechart.app.view.music

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.galaxygoldfish.waveslider.WaveSlider
import com.galaxygoldfish.waveslider.WaveSliderDefaults
import com.groovechart.app.MainActivity
import com.groovechart.app.R
import com.groovechart.app.theme.GrooveChartTheme
import com.groovechart.app.util.Chip
import com.groovechart.app.util.GCTopBarLarge
import com.groovechart.app.util.LoadingView
import com.groovechart.app.util.isWithin5Minutes
import com.groovechart.app.viewmodel.MusicViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.round

@OptIn(ExperimentalAnimationApi::class, ExperimentalLayoutApi::class)
@Composable
fun FinalizeDetailView(
    navController: NavController,
    playlistType: String,
    selectedGenres: String? = null,
    customizedSound: String? = null
) {
    val viewModel: MusicViewModel = viewModel()
    var showLoadingView by remember { mutableStateOf(false) }
    LaunchedEffect(true) {
        viewModel.playlistType = playlistType
        viewModel.selectedGenres = selectedGenres.toString()
        viewModel.apply {
            customizedSound?.let {
                it.split(",").let { custom ->
                    danceability.value = custom[0].toFloat()
                    energy.value = custom[1].toFloat()
                    liveness.value = custom[2].toFloat()
                    instrumentalness.value = custom[3].toFloat()
                    popularity.value = custom[4].toFloat()
                    speechiness.value = custom[5].toFloat()
                }
            }
        }
    }
    GrooveChartTheme {
        AnimatedContent(
            targetState = showLoadingView,
            label = "finalize-detail-content"
        ) { state ->
            if (state) {
                LoadingView(
                    message = stringResource(id = R.string.playlist_creator_loading_label)
                )
            } else {
                Scaffold(
                    modifier = Modifier.statusBarsPadding(),
                    topBar = {
                        GCTopBarLarge(
                            title = stringResource(id = R.string.playlist_creator_finalize_title),
                            navController = navController
                        )
                    },
                    floatingActionButton = {
                        Box(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .rotate(45F)
                                .clip(RoundedCornerShape(3.dp))
                                .background(MaterialTheme.colorScheme.onBackground)
                                .size(50.dp)
                                .clickable {
                                    val context = navController.context as MainActivity
                                    showLoadingView = true
                                    CoroutineScope(Dispatchers.Default).launch {
                                        val intent = Intent(ACTION_VIEW)
                                        intent.data = Uri.parse(
                                            when (viewModel.playlistType) {
                                                "month" -> viewModel.generateMonthlyPlaylist(
                                                    context
                                                )

                                                "genre" -> viewModel.generateGenrePlaylist(
                                                    context
                                                )

                                                "custom" -> viewModel.generateCustomPlaylist(
                                                    context
                                                )

                                                else -> viewModel.generateYearPlaylist(
                                                    context
                                                )
                                            }

                                        )
                                        context.startActivity(intent)
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
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                    ) {
                        OutlinedTextField(
                            value = viewModel.playlistTitle,
                            onValueChange = {
                                viewModel.playlistTitle = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp)
                                .padding(horizontal = 20.dp),
                            label = {
                                Text(text = stringResource(id = R.string.playlist_creator_name_hint))
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                                focusedLabelColor = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp, start = 20.dp, end = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(id = R.string.playlist_creator_length_title),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = (round((viewModel.playlistLength * 100F.toInt()) / 1) * 1).toString()
                                    .split(".")[0],
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        WaveSlider(
                            value = viewModel.playlistLength,
                            onValueChange = {
                                viewModel.playlistLength = it
                            },
                            animationOptions = WaveSliderDefaults.animationOptions(
                                reverseFlatline = true
                            ),
                            colors = WaveSliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.onBackground,
                                activeTickColor = MaterialTheme.colorScheme.onBackground,
                                inactiveTrackColor = MaterialTheme.colorScheme.onBackground.copy(
                                    0.3F
                                ),
                                activeTrackColor = MaterialTheme.colorScheme.onBackground,
                                inactiveTickColor = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp, start = 15.dp, end = 15.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.apply {
                                        playlistPublic = !playlistPublic
                                    }
                                }
                                .padding(start = 20.dp, end = 20.dp, top = 40.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.playlist_creator_public_title),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Switch(
                                checked = viewModel.playlistPublic,
                                onCheckedChange = {
                                    viewModel.playlistPublic = it
                                },
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = MaterialTheme.colorScheme.onBackground,
                                    checkedThumbColor = MaterialTheme.colorScheme.background,
                                    checkedBorderColor = MaterialTheme.colorScheme.onBackground,
                                    uncheckedBorderColor = MaterialTheme.colorScheme.onBackground,
                                    uncheckedThumbColor = MaterialTheme.colorScheme.onBackground,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.onBackground.copy(
                                        0.2F
                                    )
                                )
                            )
                        }
                        selectedGenres?.let { genres ->
                            Text(
                                text = stringResource(id = R.string.playlist_creator_selected_genres_title),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 40.dp, start = 20.dp, bottom = 20.dp)
                            )
                            FlowRow(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)) {
                                genres.split(",").forEach { item ->
                                    Chip(
                                        text = item,
                                        filled = false,
                                        modifier = Modifier.padding(end = 15.dp)
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