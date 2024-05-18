package com.example.videoapplication.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ContentTextBottomSheet(
    modifier: Modifier = Modifier,
    textNow: String
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(all = 16.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 12.dp)
            .then(modifier)
        ,
        text = textNow,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        textAlign = TextAlign.Center
    )
}