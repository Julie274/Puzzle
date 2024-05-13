package com.example.puzzle.model.entity

import android.graphics.Bitmap

data class Puzzle (
    var image : String,

    var rows : Int,

    var columns : Int,

    var successLink : String,

    var successContent : String,
)

data class PuzzlePiece (
    var id : Int,

    var image : Bitmap,

    var xCoord : Int,

    var yCoord : Int,

    var xCoordGoal : Int,

    var yCoordGoal : Int,

    var pieceWidth : Int,

    var pieceHeight : Int,

    var canMove : Boolean
)
