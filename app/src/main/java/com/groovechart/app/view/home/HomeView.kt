package com.groovechart.app.view.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.groovechart.app.MainActivity
import com.groovechart.app.R
import com.groovechart.app.network.NetworkProvider
import com.groovechart.app.theme.GrooveChartTheme
import com.groovechart.app.util.LoadingView
import com.groovechart.app.viewmodel.HomeViewModel
import com.tencent.mmkv.MMKV

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeView(navController: NavController, authCode: String? = null) {
    val viewModel: HomeViewModel = viewModel()
    LaunchedEffect(true) {
        if (authCode != null) {
            MMKV.defaultMMKV().putString("AUTH_CODE", authCode)
        }
        viewModel.initialize(navController.context as MainActivity)
    }
    GrooveChartTheme {
        Column(Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = viewModel.userName.isBlank(),
                label = "loading-indicator"
            ) { state ->
                if (state) {
                    LoadingView()
                }
            }
            AnimatedContent(
                targetState = viewModel.userName.isNotBlank(),
                label = "main-content"
            ) { state ->
                if (state) {
                    Scaffold(
                        bottomBar = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                                    .background(MaterialTheme.colorScheme.onBackground.copy(0.1F))
                                    .navigationBarsPadding(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val navIconList = listOf(
                                    R.drawable.ic_home,
                                    R.drawable.ic_user,
                                    R.drawable.ic_music_disc
                                )
                                val navLabelList = stringArrayResource(id = R.array.home_nav_labels)
                                navIconList.forEachIndexed { index, icon ->
                                    Row(
                                        modifier = Modifier
                                            .padding(end = 15.dp, top = 15.dp, bottom = 10.dp)
                                            .padding(start = if (index == 0) 25.dp else 0.dp)
                                            .clip(RoundedCornerShape(5.dp))
                                            .background(
                                                if (viewModel.currentNavDestination == index) {
                                                    MaterialTheme.colorScheme.onBackground
                                                } else {
                                                    Color.Transparent
                                                }
                                            )
                                            .clickable {
                                                viewModel.currentNavDestination = index
                                            },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = icon),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .padding(10.dp)
                                                .size(20.dp),
                                            tint = if (viewModel.currentNavDestination == index) {
                                                MaterialTheme.colorScheme.background
                                            } else {
                                                MaterialTheme.colorScheme.onBackground
                                            }
                                        )
                                        AnimatedContent(
                                            targetState = viewModel.currentNavDestination == index,
                                            label = "bottomnav-label"
                                        ) {
                                            if (it) {
                                                Text(
                                                    text = navLabelList[index],
                                                    style = MaterialTheme.typography.labelMedium,
                                                    modifier = Modifier.padding(end = 15.dp),
                                                    color = MaterialTheme.colorScheme.background
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        topBar = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                                    .statusBarsPadding()
                                    .padding(15.dp)
                                    .padding(bottom = 20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                AnimatedContent(
                                    targetState = viewModel.currentNavDestination,
                                    label = "nav-toplabel"
                                ) { state ->
                                    Text(
                                        text = when (state) {
                                            0 -> stringResource(id = R.string.app_name)
                                            1 -> stringResource(id = R.string.home_header_friends)
                                            else -> stringResource(id = R.string.home_header_music)
                                        },
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }

                                Image(
                                    painter = rememberAsyncImagePainter(model = viewModel.userProfileImage),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(35.dp),
                                    contentScale = ContentScale.FillWidth
                                )
                            }
                        }
                    ) { paddingValues ->
                        AnimatedContent(
                            targetState = viewModel.currentNavDestination,
                            label = "nav-main"
                        ) { state ->
                            when (state) {
                                0 -> MainPage(viewModel, paddingValues, navController)
                                1 -> FriendsPage(viewModel, paddingValues, navController)
                                2 -> MusicPage(viewModel, paddingValues, navController)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    text: String,
    callback: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall
        )
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
        val onBackground = MaterialTheme.colorScheme.onBackground
        Canvas(
            Modifier
                .fillMaxWidth(0.85F)
                .padding(top = 5.dp)
                .height(2.dp)
                .padding(horizontal = 25.dp)
        ) {
            drawLine(
                color = onBackground.copy(0.4F),
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                pathEffect = pathEffect,
                strokeWidth = 5F
            )
        }
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(35.dp)
                .rotate(45F)
                .clip(RoundedCornerShape(3.dp))
                .background(MaterialTheme.colorScheme.onBackground)
                .clickable {
                    callback.invoke()
                }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_forward),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .size(17.dp)
                    .align(Alignment.Center)
                    .rotate(-45F)
            )
        }
    }
}