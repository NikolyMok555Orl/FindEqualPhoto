package com.example.findequalphoto.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.findequalphoto.ui.theme.FindEqualPhotoTheme


@Composable
fun AppButtonUI(text: String, onClick: () -> Unit, modifier: Modifier=Modifier) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colors.onPrimary
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier.clip( shape = RoundedCornerShape(10.dp))
    ) {
        Box(contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF3885F9), Color(0xFF56C9FF))

                    )
                )
        ) {
            Text(
                text = text.uppercase(),
                textAlign = TextAlign.Center, style = MaterialTheme.typography.button,
                modifier = Modifier.padding(vertical = 15.dp)
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun AppButtonUIPreview() {
    FindEqualPhotoTheme() {
        Row(modifier = Modifier.padding(10.dp)) {
            AppButtonUI("Кнопка", {})
        }
    }
}