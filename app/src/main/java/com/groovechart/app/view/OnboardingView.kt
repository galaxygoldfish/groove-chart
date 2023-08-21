package com.groovechart.app.view

import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.groovechart.app.MainActivity
import com.groovechart.app.R
import com.groovechart.app.network.Credentials
import com.groovechart.app.network.NetworkProvider
import com.groovechart.app.network.NetworkServiceBuilder
import com.groovechart.app.theme.GrooveChartTheme

@Composable
fun OnboardingView(navController: NavController) {
    GrooveChartTheme {
        Surface {
            Box(Modifier.fillMaxSize()) {
                Image(
                    painter = if (isSystemInDarkTheme()) {
                        painterResource(id = R.drawable.bg_onboarding_gradient_dark)
                    } else {
                        painterResource(id = R.drawable.bg_onboarding_gradient_light)
                    },
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillWidth
                )
                Column(Modifier.padding(start = 5.dp)) {
                    val displayLarge = MaterialTheme.typography.displayLarge
                    var textStyle by remember { mutableStateOf(displayLarge) }
                    var readyToDraw by remember { mutableStateOf(false) }
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = textStyle,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(top = 20.dp)
                            .rotate(-90F)
                            .vertical()
                            .drawWithContent {
                                if (readyToDraw) drawContent()
                            },
                        onTextLayout = { textLayoutResult ->
                            if (textLayoutResult.didOverflowWidth) {
                                textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9)
                            } else {
                                readyToDraw = true
                            }
                        }
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_groovechart_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 15.dp)
                            .size(90.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .systemBarsPadding()
                        .padding(bottom = 20.dp)
                        .clip(CutCornerShape(corner = CornerSize(50.dp)))
                        .background(MaterialTheme.colorScheme.onBackground)
                        .clickable {
                            NetworkProvider()
                                .promptUserLogin(navController.context as MainActivity)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                        Text(
                            text = stringResource(id = R.string.onboarding_log_in_button),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 20.dp, bottom = 20.dp, start = 50.dp, end = 30.dp),
                            color = MaterialTheme.colorScheme.background
                        )
                        Box(
                            modifier = Modifier
                                .padding(top = 5.dp, bottom = 5.dp, end = 5.dp)
                                .size(70.dp)
                                .clip(CutCornerShape(corner = CornerSize(50.dp)))
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_login),
                                contentDescription = null,
                                modifier = Modifier.padding(end = 5.dp)
                                    .size(24.dp)
                                    .align(Alignment.Center),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                }
            }
        }
    }
}

fun Modifier.vertical() =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.height, placeable.width) {
            placeable.place(
                x = -(placeable.width / 2 - placeable.height / 2),
                y = -(placeable.height / 2 - placeable.width / 2)
            )
        }
    }