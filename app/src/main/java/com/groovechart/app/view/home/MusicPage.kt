package com.groovechart.app.view.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.groovechart.app.NavDestination
import com.groovechart.app.R
import com.groovechart.app.viewmodel.HomeViewModel

@Composable
fun MusicPage(
    viewModel: HomeViewModel,
    paddingValues: PaddingValues,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        RecommendationCard(
            background = painterResource(id = R.drawable.montlhy_discovery),
            onClick = {
                navController.navigate("${NavDestination.FinalizeDetailView}/month")
            }
        )
        RecommendationCard(
            background = painterResource(id = R.drawable.year_in_review),
            onClick = {
                navController.navigate("${NavDestination.FinalizeDetailView}/year")
            }
        )
        RecommendationCard(
            background = painterResource(id = R.drawable.genre_revolution),
            onClick = {
                navController.navigate("${NavDestination.GenreSearch}/false")
            }
        )
        RecommendationCard(  
            background = painterResource(id = R.drawable.completely_custom),
            onClick = {
                navController.navigate("${NavDestination.GenreSearch}/" + true)
            }
        )
    }
}

@Composable
fun RecommendationCard(
    background: Painter,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
            .clip(RoundedCornerShape(5.dp))
            .border(
                width = 1.5.dp,
                shape = RoundedCornerShape(7.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            .clickable {
                onClick.invoke()
            }
    ) {
        Image(
            painter = background,
            contentDescription = null,
            contentScale = ContentScale.FillWidth
        )
    }
}