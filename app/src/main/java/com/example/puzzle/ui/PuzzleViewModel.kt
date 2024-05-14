package com.example.puzzle.ui

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.health.connect.datatypes.HeightRecord
import android.icu.text.ListFormatter.Width
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.lifecycle.ViewModel
import com.example.puzzle.model.entity.Puzzle
import com.example.puzzle.model.entity.PuzzlePiece
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.stream.IntStream
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

@HiltViewModel
class PuzzleViewModel @Inject constructor(): ViewModel() {

    fun splitImage(
        puzzle: Puzzle,
        bitmapPiece: ImageBitmap,
        imagePosition: Offset
    ) : List<PuzzlePiece> {
        val rows = puzzle.rows
        val columns = puzzle.columns
        val pieceWidth = bitmapPiece.width/columns
        val pieceHeight = bitmapPiece.height/rows
        val bumpSize = pieceHeight / 4
        val puzzlePieces : MutableList<PuzzlePiece> = mutableListOf()

        var yCoord = 0
        var imagePositionY = imagePosition.y
        for (row in IntStream.range(0, rows)) {
            var xCoord = 0
            var imagePositionX = imagePosition.x
            for (col in IntStream.range(0, columns)) {
                var offsetX = 0
                var offsetY = 0
                if (col > 0) {
                    offsetX = pieceWidth / 3
                }
                if (row > 0) {
                    offsetY = pieceHeight / 3
                }

                val pieceBitmap = Bitmap.createBitmap(
                    bitmapPiece.asAndroidBitmap(),
                    xCoord - offsetX,
                    yCoord - offsetY,
                    pieceWidth + offsetX,
                    pieceHeight + offsetY
                )
                val piece = PuzzlePiece(
                    id = col+(columns*row),
                    image = pieceBitmap,
                    xCoord = xCoord,
                    yCoord = yCoord,
                    xCoordGoal = imagePositionX.toInt(),
                    yCoordGoal = imagePositionY.toInt(),
                    pieceWidth = pieceWidth,
                    pieceHeight = pieceHeight,
                    canMove = true)

                piece.image = pieceBitmap
                piece.xCoord = xCoord - offsetX
                piece.yCoord = yCoord - offsetY
                piece.pieceWidth = pieceWidth + offsetX
                piece.pieceHeight = pieceHeight + offsetY

                val puzzlePiece = Bitmap.createBitmap(
                    pieceWidth + offsetX,
                    pieceHeight + offsetY,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = android.graphics.Canvas(puzzlePiece)

                val clipPath = android.graphics.Path().apply {
                    moveTo(offsetX.toFloat(), offsetY.toFloat())
                    if (row == 0) {
                        lineTo(offsetX.toFloat() + pieceWidth, offsetY.toFloat())
                    } else {
                        lineTo(offsetX.toFloat() + pieceWidth / 3, offsetY.toFloat())
                        cubicTo(
                            offsetX.toFloat() + pieceWidth / 6,
                            offsetY.toFloat() - bumpSize,
                            offsetX.toFloat() + pieceWidth / 6 * 5,
                            offsetY.toFloat() - bumpSize,
                            offsetX.toFloat() + pieceWidth / 3 * 2,
                            offsetY.toFloat()
                        )
                        lineTo(offsetX.toFloat() + pieceWidth, offsetY.toFloat())
                    }
                    if (col == columns - 1) {
                        lineTo(offsetX.toFloat() + pieceWidth, offsetY.toFloat() + pieceHeight)
                    } else {
                        lineTo(offsetX.toFloat() + pieceWidth, offsetY.toFloat() + pieceHeight / 3)
                        cubicTo(
                            offsetX.toFloat() + pieceWidth - bumpSize,
                            offsetY.toFloat() + pieceHeight / 6,
                            offsetX.toFloat() + pieceWidth - bumpSize,
                            offsetY.toFloat() + pieceHeight / 6 * 5,
                            offsetX.toFloat() + pieceWidth,
                            offsetY.toFloat() + pieceHeight / 3 * 2
                        )
                        lineTo(offsetX.toFloat() + pieceWidth, offsetY.toFloat() + pieceHeight)
                    }
                    if (row == rows - 1) {
                        lineTo(offsetX.toFloat(), offsetY.toFloat() + pieceHeight)
                    } else {
                        lineTo(offsetX.toFloat() + pieceWidth / 3 * 2, offsetY.toFloat() + pieceHeight)
                        cubicTo(
                            offsetX.toFloat() + pieceWidth / 6 * 5,
                            offsetY.toFloat() + pieceHeight - bumpSize,
                            offsetX.toFloat() + pieceWidth / 6,
                            offsetY.toFloat() + pieceHeight - bumpSize,
                            offsetX.toFloat() + pieceWidth / 3,
                            offsetY.toFloat() + pieceHeight
                        )
                        lineTo(offsetX.toFloat(), offsetY.toFloat() + pieceHeight)
                    }
                    if (col == 0) {
                        close()
                    } else {
                        lineTo(offsetX.toFloat(), offsetY.toFloat() + pieceHeight / 3 * 2)
                        cubicTo(
                            offsetX.toFloat() - bumpSize,
                            offsetY.toFloat() + pieceHeight / 6 * 5,
                            offsetX.toFloat() - bumpSize,
                            offsetY.toFloat() + pieceHeight / 6,
                            offsetX.toFloat(),
                            offsetY.toFloat() + pieceHeight / 3
                        )
                        close()
                    }
                }

                val paint = Paint().apply {
                    style = Paint.Style.FILL
                    color = android.graphics.Color.BLACK
                }
                canvas.drawPath(clipPath, paint)
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                canvas.drawBitmap(pieceBitmap, 0f, 0f, paint)
                val whiteBorder = Paint().apply {
                    style = Paint.Style.STROKE
                    color = android.graphics.Color.WHITE
                    strokeWidth = 8.0f
                }
                canvas.drawPath(clipPath, whiteBorder)
                val blackBorder = Paint().apply {
                    style = Paint.Style.STROKE
                    color = android.graphics.Color.BLACK
                    strokeWidth = 3.0f
                }
                canvas.drawPath(clipPath, blackBorder)

                piece.image = puzzlePiece
                puzzlePieces.add(piece)

                if(imagePositionX != imagePosition.x){
                    imagePositionX += pieceWidth
                } else {
                    imagePositionX += (pieceWidth - pieceWidth/3)
                }
                xCoord += pieceWidth
            }
            if(imagePositionY != imagePosition.y){
                imagePositionY += pieceHeight
            } else {
                imagePositionY += (pieceHeight - pieceHeight/3)
            }
            yCoord += pieceHeight
        }
        return puzzlePieces
    }

    fun piecesPosition(
        pieces : List<PuzzlePiece>,
        screenWidth : Float,
        screenHeight : Float
    ){
        pieces.forEach{ piece ->
            piece.xCoord = Random.nextInt(0, ((screenWidth/1.1).toInt() - piece.pieceWidth ))
            piece.yCoord = Random.nextInt(0, ((screenHeight/3.5).toInt() - piece.pieceHeight)) ///3.5).toInt())
        }
    }
    fun isNearGoal(
        xCoord: Float,
        yCoord: Float,
        xCoordGoal: Float,
        yCoordGoal: Float,
        threshold: Float
    ): Boolean {
        val distance = sqrt((xCoord - xCoordGoal).pow(2) + (yCoord - yCoordGoal).pow(2))
        return distance <= threshold
    }
}