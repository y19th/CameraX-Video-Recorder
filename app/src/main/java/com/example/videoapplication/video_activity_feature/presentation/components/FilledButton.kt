package com.example.videoapplication.video_activity_feature.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FilledButton(
    modifier: Modifier = Modifier,
    contentText: String,
    shape: RoundedCornerShape = RoundedCornerShape(5.dp),
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    enabled: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = if (enabled) colors.containerColor else colors.disabledContainerColor,
                shape = shape
            )
            .clip(shape)
            .semantics { role = Role.Button }
            .clickable { onClick.invoke() }
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .then(modifier)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = contentText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (enabled) colors.contentColor else colors.disabledContentColor,
            textAlign = TextAlign.Center
        )
    }
}
