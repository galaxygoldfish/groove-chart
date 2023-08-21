package com.groovechart.app.view.home

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
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
            background = painterResource(id = R.drawable.monthly_discovery_bg_light),
            title = stringResource(id = R.string.recommendation_monthly_title),
            subtitle = stringResource(id = R.string.recommendation_monthly_subtitle),
            onClick = {
                navController.navigate("${NavDestination.FinalizeDetailView}/month")
            }
        )
        RecommendationCard(
            background = painterResource(id = R.drawable.year_in_review_bg_light),
            title = stringResource(id = R.string.recommendation_year_title),
            subtitle = stringResource(id = R.string.recommendation_year_subtitle),
            onClick = {
                navController.navigate("${NavDestination.FinalizeDetailView}/year")
            }
        )
        RecommendationCard(
            background = painterResource(id = R.drawable.genre_revolution_bg_light),
            title = stringResource(id = R.string.recommendation_genre_title),
            subtitle = stringResource(id = R.string.recommendation_genre_subtitle),
            onClick = {
                navController.navigate("${NavDestination.GenreSearch}/false")
            }
        )
        RecommendationCard(
            background = painterResource(id = R.drawable.custom_bg_light),
            title = stringResource(id = R.string.recommendation_custom_title),
            subtitle = stringResource(id = R.string.recommendation_custom_subtitle),
            onClick = {
                navController.navigate("${NavDestination.GenreSearch}/" + true)
            }
        )
    }
}

@Composable
fun RecommendationCard(
    background: Painter,
    title: String,
    subtitle: String,
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
        Column(
            Modifier
                .fillMaxWidth()
                .paint(background, contentScale = ContentScale.Crop)
                .padding(top = 15.dp, start = 17.dp, end = 17.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = subtitle,
                    modifier = Modifier.padding(top = 3.dp),
                    style = MaterialTheme.typography.bodyMedium
                            + TextStyle(fontSize = 18.sp, letterSpacing = -(0.3).sp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_down_end),
                    contentDescription = null
                )
            }
        }
    }
}