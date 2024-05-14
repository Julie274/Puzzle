package com.example.puzzle.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.puzzle.model.entity.PuzzlePiece
import kotlin.math.roundToInt

@Composable
fun PiecesView(
    puzzlePieces : List<PuzzlePiece>,
    offsetX : SnapshotStateList<Float>,
    offsetY : SnapshotStateList<Float>,
    puzzleViewModel: PuzzleViewModel = hiltViewModel(),
    isGameOver : MutableState<Boolean>
){
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        puzzlePieces.forEach { puzzlePiece ->
            Image(
                bitmap = puzzlePiece.image.asImageBitmap(),
                contentDescription = "",
                modifier = Modifier
                    .offset {
                        IntOffset(
                            offsetX[puzzlePiece.id].roundToInt(),
                            offsetY[puzzlePiece.id].roundToInt()
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                if (puzzleViewModel.isNearGoal(
                                        xCoord = offsetX[puzzlePiece.id],
                                        yCoord = offsetY[puzzlePiece.id],
                                        xCoordGoal = puzzlePiece.xCoordGoal.toFloat(),
                                        yCoordGoal = puzzlePiece.yCoordGoal.toFloat(),
                                        threshold = 50f
                                    )
                                ) {
                                    offsetX[puzzlePiece.id] = puzzlePiece.xCoordGoal.toFloat()
                                    offsetY[puzzlePiece.id] = puzzlePiece.yCoordGoal.toFloat()
                                    puzzlePiece.canMove = false
                                    if (puzzlePieces.isNotEmpty()) {
                                        if (puzzlePieces.all { !it.canMove }) {
                                            isGameOver.value = true
                                        }
                                    }
                                }
                            },
                            onDrag = { change, dragAmount ->
                                if (puzzlePiece.canMove) {
                                    change.consume()
                                    offsetX[puzzlePiece.id] += dragAmount.x
                                    offsetY[puzzlePiece.id] += dragAmount.y
                                }
                            }
                        )
                    }
            )
        }
    }
}