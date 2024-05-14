package com.example.puzzle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.puzzle.model.entity.Puzzle
import com.example.puzzle.navigation.Navigation
import com.example.puzzle.ui.PuzzleView
import com.example.puzzle.ui.theme.PuzzleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PuzzleTheme {
                Navigation()
            }
        }
    }
}