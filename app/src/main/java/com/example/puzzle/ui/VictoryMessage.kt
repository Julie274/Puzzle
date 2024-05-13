package com.example.puzzle.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VictoryMessage(
    successContent : String,
    successLink : String,
    onClick: () -> Unit
){
    Box (
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(text = successContent)
            if(successLink != ""){
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onClick() }
                ) {
                    Text(text = "Plus d'infos")
                }
            }
        }
    }
}