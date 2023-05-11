package com.example.findequalphoto.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.findequalphoto.ui.theme.FindEqualPhotoTheme
import com.example.findequalphoto.R


@Composable
fun LogoUI() {


    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(228.dp)) {


        Box(modifier = Modifier.fillMaxSize().padding(43.dp)) {
            Spacer(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(65.dp)
                    .clip(CircleShape)
                    .background(brush = Brush.radialGradient(
                        listOf( Color(0xFF3885F9), Color(0xFF111019)),
                    ), alpha = 0.1f).blur(65.dp)
            )

            Spacer(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(65.dp)
                    .clip(CircleShape)
                    .background(brush = Brush.radialGradient(
                        listOf( Color(0xFF62B8F3), Color(0xFF111019)),
                    ), alpha = 0.1f).blur(65.dp)
            )
        }

        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.group_577),
            contentDescription = null
        )

        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.group_426),
            contentDescription = null
        )


    }
}

@Preview(showBackground = true)
@Composable
private fun LogoUIPreview() {
    FindEqualPhotoTheme() {

        Surface(color = MaterialTheme.colors.background) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {

                LogoUI()

            }
        }

    }


}

