package com.example.videoapplication.video_activity_feature.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp

@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit
) {
    Box(modifier = Modifier
        .background(
            color = Color.White,
            shape = RoundedCornerShape(5.dp)
        )
        .padding(all = 6.dp)
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.Center)
                .size(24.dp)
            ,
            imageVector = ImageVector.vectorResource(id = iconRes),
            contentDescription = "icon_button_vector"
        )
    }
}

@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    imageVector: ImageVector,
    onClick: () -> Unit
) {
    Box(modifier = Modifier
        .background(
            color = if(enabled) Color.White else Color.LightGray,
            shape = RoundedCornerShape(10.dp)
        )
        .clickable {
            onClick.invoke()
        }
        .padding(all = 8.dp)
        .then(modifier)
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.Center)
                .size(36.dp)
            ,
            imageVector = imageVector,
            contentDescription = "icon_button_vector"
        )
    }
}