package com.groovechart.app.view.home

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.groovechart.app.MainActivity
import com.groovechart.app.R
import com.groovechart.app.util.LoadingView
import com.groovechart.app.util.calculateTimeDifference
import com.groovechart.app.util.isWithin5Minutes
import com.groovechart.app.viewmodel.FriendViewModel
import com.groovechart.app.viewmodel.HomeViewModel
import com.tencent.mmkv.MMKV
import kotlin.math.abs


@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun FriendsPage(viewModel: HomeViewModel, paddingValues: PaddingValues, navController: NavController) {
    val FAViewModel: FriendViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    Column(
        Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        if (MMKV.defaultMMKV().getString("FRIEND_ACTIVITY_COOKIE", "")!!.isBlank()) {
            AndroidView(
                factory = {
                    WebView(it).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        webViewClient = GCWebViewClient()
                        settings.javaScriptEnabled = true
                        loadUrl("https://accounts.spotify.com/en/login?continue=https%3A%2F%2Fopen.spotify.com%2F")
                    }
                },
                update = {
                    it.loadUrl("https://accounts.spotify.com/en/login?continue=https%3A%2F%2Fopen.spotify.com%2F")
                }
            )
        }
        if (MMKV.defaultMMKV().getString("FRIEND_ACTIVITY_COOKIE", "")!!.isNotBlank()) {
            LaunchedEffect(true) {
                FAViewModel.getFriendActivity()
            }
            AnimatedContent(
                targetState = FAViewModel.friendActivityList.isNotEmpty(),
                label = "friend-list"
            ) { state ->
                if (state) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(FAViewModel.friendActivityList) { index, item ->
                            Row(
                                modifier = Modifier
                                    .animateEnterExit(
                                        enter = slideInVertically(animationSpec = spring(1F)) { it * (index + 1) }
                                    )
                                    .fillMaxWidth()
                                    .padding(start = 20.dp, end = 20.dp, bottom = 15.dp)
                                    .border(
                                        width = 1.5.dp,
                                        shape = RoundedCornerShape(7.dp),
                                        color = MaterialTheme.colorScheme.onBackground
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(model = item.user.imageUrl),
                                    contentScale = ContentScale.Crop,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(start = 15.dp)
                                        .size(65.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                )
                                Column(
                                    Modifier.padding(
                                        top = 10.dp,
                                        end = 15.dp,
                                        bottom = 15.dp,
                                        start = 15.dp
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = item.user.name,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        if (isWithin5Minutes(item.timestamp)) {
                                            LottieAnimation(
                                                composition = rememberLottieComposition(
                                                    LottieCompositionSpec.RawRes(
                                                        if (isSystemInDarkTheme()) {
                                                            R.raw.anim_music_playing_dark
                                                        } else {
                                                            R.raw.anim_music_playing_light
                                                        }
                                                    )
                                                ).value,
                                                iterations = 1000,
                                                modifier = Modifier.size(25.dp)
                                            )
                                        } else {
                                            Text(
                                                text = calculateTimeDifference(item.timestamp),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_music),
                                            contentDescription = null,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Text(
                                            text = item.track.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(start = 8.dp, end = 15.dp),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.padding(top = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_user),
                                            contentDescription = null,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Text(
                                            text = item.track.artist.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(start = 8.dp, end = 15.dp),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    LoadingView()
                }
            }
        }
    }
}

class GCWebViewClient : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        url?.let {
            val cookieManager = android.webkit.CookieManager.getInstance()
            val cookies = cookieManager.getCookie(url)?.split(";")
            cookies?.forEach { cookie ->
                val splitCookie = cookie.split("=")
                if (splitCookie[0] == "sp_dc") {
                    MMKV.defaultMMKV().putString("FRIEND_ACTIVITY_COOKIE", splitCookie[1])
                    view?.destroy()
                }
            }
        }
    }

}