package com.example.findequalphoto.ui.component

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

@Composable
fun HeaderTextUI(text:String){
    Text(text = text, style = MaterialTheme.typography.h6.copy(fontSize = 20.sp, lineHeight = (23.44).sp))

}