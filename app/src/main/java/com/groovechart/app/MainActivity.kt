package com.groovechart.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.groovechart.app.network.NetworkProvider
import com.groovechart.app.theme.GrooveChartTheme
import com.groovechart.app.view.home.HomeView
import com.groovechart.app.view.OnboardingView
import com.groovechart.app.view.TopItemView
import com.groovechart.app.view.music.CustomizeSoundView
import com.groovechart.app.view.music.FinalizeDetailView
import com.groovechart.app.view.music.GenreSearchView
import com.spotify.sdk.android.auth.AuthorizationClient
import com.tencent.mmkv.MMKV

class MainActivity : ComponentActivity() {

    lateinit var navigationController: NavHostController

    companion object {
        const val TOKEN_REQUEST_CODE = 0x17
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MMKV.initialize(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            GrooveChartTheme {
                NavHost()
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun NavHost() {
        navigationController = rememberAnimatedNavController()
        AnimatedNavHost(
            navController = navigationController,
            startDestination = if (MMKV.defaultMMKV().decodeBool("ONBOARDING_COMPLETE")) {
                NavDestination.Home
            } else {
                NavDestination.Onboarding
            },
            modifier = Modifier.fillMaxSize()
        ) {
            composable(NavDestination.Onboarding) {
                OnboardingView(navigationController)
            }
            composable(NavDestination.Home) {
                HomeView(navigationController)
            }
            composable("${NavDestination.TopItems}/{type}") {
                TopItemView(
                    navController = navigationController,
                    type = it.arguments!!.getString("type")!!.toInt()
                )
            }
            composable("${NavDestination.FinalizeDetailView}/{type}") {
                FinalizeDetailView(
                    navController = navigationController,
                    playlistType = it.arguments!!.getString("type")!!
                )
            }
            composable("${NavDestination.FinalizeDetailView}/{type}/{genres}") {
                FinalizeDetailView(
                    navController = navigationController,
                    playlistType = it.arguments!!.getString("type")!!,
                    selectedGenres = it.arguments!!.getString("genres")
                )
            }
            composable("${NavDestination.FinalizeDetailView}/{type}/{genres}/{sound}") {
                FinalizeDetailView(
                    navController = navigationController,
                    playlistType = it.arguments!!.getString("type")!!,
                    selectedGenres = it.arguments!!.getString("genres"),
                    customizedSound = it.arguments!!.getString("sound")!!
                )
            }
            composable("${NavDestination.CustomizeSound}/{genres}") {
                CustomizeSoundView(
                    navController = navigationController,
                    selectedGenres = it.arguments!!.getString("genres")!!
                )
            }
            composable("${NavDestination.GenreSearch}/{type}") {
                GenreSearchView(
                    navController = navigationController,
                    multipleGenres = it.arguments!!.getString("type").toBoolean()
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val response = AuthorizationClient.getResponse(resultCode, data)
        val mmkv = MMKV.defaultMMKV()
        if (response.accessToken != null) {
            mmkv.putString("AUTH_TOKEN", response.accessToken)
            mmkv.putBoolean("ONBOARDING_COMPLETE", true)
            mmkv.putLong("AUTH_EXPIRY", System.currentTimeMillis() + 3600000)
            navigationController.navigate(NavDestination.Home)
        }
    }

}
