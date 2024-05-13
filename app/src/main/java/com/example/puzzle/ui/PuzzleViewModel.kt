package com.example.puzzle.ui

import android.graphics.Bitmap
import android.health.connect.datatypes.HeightRecord
import android.icu.text.ListFormatter.Width
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PuzzleViewModel @Inject constructor(): ViewModel() {


    fun splitImage(bitmap: Bitmap, rows: Int, column: Int): MutableList<Bitmap> {

        //Plus level est petit plus la taille de la pièce est petite
        val width = bitmap.width.toFloat()
        val height = bitmap.height.toFloat()
        //val nChunks = ceil(max(width, height) / level.toDouble()).toInt()
        val pieceWidth : Float = width/column
        val pieceHeight : Float = height/rows

        val bitmaps: MutableList<Bitmap> = ArrayList()
        var startR = 0
        var startC = 0
        /*
        for (i in 1..rows) {
            bitmaps.add(

                if (width >= height)
                    Bitmap.createBitmap(bitmap, start, 0, width / maxSize, height)
                else
                    Bitmap.createBitmap(bitmap, 0, start, width, height / maxSize)
            )
            startR +=
        }

         */
        return bitmaps
    }
}
/*
int x,
int y,
int width,
int height
 */