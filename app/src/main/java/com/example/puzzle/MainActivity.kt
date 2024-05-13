package com.example.puzzle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.puzzle.model.entity.Puzzle
import com.example.puzzle.ui.PuzzleView
import com.example.puzzle.ui.theme.PuzzleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PuzzleTheme {
                PuzzleView(
                    Puzzle(
                        image = "https://th.bing.com/th/id/OIP.XOFJGxxceXXXPGtSnA0V3QHaHa?rs=1&pid=ImgDetMain",
                        rows = 3,
                        columns = 2,
                        successLink = "",
                        successContent = "Félicitation ! Vous avez gagné"
                    )
                )
            }
        }
    }
}