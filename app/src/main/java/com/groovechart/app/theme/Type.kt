package com.groovechart.app.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.groovechart.app.R

val QueeringFont = FontFamily(
    Font(resId = R.font.queering, weight = FontWeight.Bold)
)
val InterFont = FontFamily(
    Font(resId = R.font.inter_light, weight = FontWeight.Light),
    Font(resId = R.font.inter_medium, weight = FontWeight.Medium),
    Font(resId = R.font.inter_regular, weight = FontWeight.Normal)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = QueeringFont,
        fontWeight = FontWeight.Bold,
        fontSize = 140.sp
    ),
    titleSmall = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        letterSpacing = (-0.8).sp
    ),
    titleMedium = TextStyle(
        fontFamily = QueeringFont,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp
    ),
    titleLarge = TextStyle(
        fontFamily = QueeringFont,
        fontWeight = FontWeight.Bold,
        fontSize = 50.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        letterSpacing = -(0.5).sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Light,
        fontSize = 17.sp,
        letterSpacing = -(0.2).sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Light,
        fontSize = 15.sp,
        letterSpacing = -(0.2).sp
    ),
    labelMedium = TextStyle(
        fontFamily = QueeringFont,
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp,
        letterSpacing = 0.5.sp
    ),
    labelLarge = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        letterSpacing = -(0.4).sp
    )
)