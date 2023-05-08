package com.example.findequalphoto.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.findequalphoto.ui.theme.FindEqualPhotoTheme
import com.example.findequalphoto.ui.theme.background


@Composable
fun AppProgressBar(progress:Float, modifier: Modifier=Modifier) {
    LinearProgressIndicator(
        progress = progress,
        color = Color(0xFF50BAFE),
        backgroundColor =Color(0xFF232432),
        modifier = modifier
            .height(20.dp)
            .clip(
                RoundedCornerShape(10.dp)
            )
    )
}

@Preview(showBackground = true)
@Composable
private fun AppProgressBarPreview(){
        FindEqualPhotoTheme() {
            Column(modifier = Modifier.fillMaxSize().padding(25.dp),) {
                AppProgressBar(0.5f)
            }

        }
}

