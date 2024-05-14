package com.example.puzzle.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.puzzle.model.entity.Puzzle
import com.example.puzzle.ui.PuzzleView
import com.example.puzzle.ui.RedirectionInApp

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController,
        startDestination = "puzzle_view") {
        composable("puzzle_view") {
            PuzzleView(
                puzzle = Puzzle(
                    image = "https://ceciliasluxelife.com/wp-content/uploads/2019/05/Nice-city-view.jpg",
                    rows = 2,
                    columns = 3,
                    successLink = "redirection_in_app",
                    successContent = "Baie de Nice"
                    ),
                navController
            )
        }
        composable("redirection_in_app") {
            RedirectionInApp()
        }
    }
}