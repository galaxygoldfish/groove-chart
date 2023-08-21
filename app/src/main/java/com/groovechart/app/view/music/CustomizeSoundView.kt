package com.groovechart.app.view.music

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import com.groovechart.app.NavDestination
import com.groovechart.app.R
import com.groovechart.app.theme.GrooveChartTheme
import com.groovechart.app.util.GCTopBarLarge
import com.groovechart.app.viewmodel.MusicViewModel

@Composable
fun CustomizeSoundView(navController: NavController, selectedGenres: String) {
    val viewModel: MusicViewModel = viewModel()
    LaunchedEffect(true) {
        viewModel.selectedGenres = selectedGenres
    }
    GrooveChartTheme {
        GrooveChartTheme {
            Scaffold(
                modifier = Modifier
                    .statusBarsPadding()
                    .imePadding(),
                topBar = {
                    GCTopBarLarge(
                        title = stringResource(id = R.string.playlist_creator_customize_sound_title),
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
                                viewModel.apply {
                                    navController.navigate(
                                        "${NavDestination.FinalizeDetailView}/custom/${selectedGenres}/" +
                                        "${danceability.value},${energy.value},${liveness.value},${instrumentalness.value}," +
                                        "${popularity.value},${speechiness.value}"
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
            ) { paddingValues ->
                Column(
                    Modifier
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    SoundSlider(
                        value = viewModel.danceability,
                        label = stringResource(id = R.string.customize_sound_danceability)
                    )
                    SoundSlider(
                        value = viewModel.liveness,
                        label = stringResource(id = R.string.customize_sound_liveness)
                    )
                    SoundSlider(
                        value = viewModel.energy,
                        label = stringResource(id = R.string.customize_sound_energy)
                    )
                    SoundSlider(
                        value = viewModel.instrumentalness,
                        label = stringResource(id = R.string.customize_sound_instrumentalness)
                    )
                    SoundSlider(
                        value = viewModel.popularity,
                        label = stringResource(id = R.string.customize_sound_popularity)
                    )
                    SoundSlider(
                        value = viewModel.speechiness,
                        label = stringResource(id = R.string.customize_sound_speechiness)
                    )
                }
            }
        }
    }
}

@Composable
fun SoundSlider(value: MutableState<Float>, label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(top = 20.dp, start = 20.dp)
    )
    WaveSlider(
        value = value.value,
        onValueChange = {
            value.value = it
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
}