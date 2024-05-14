package com.example.puzzle.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.puzzle.model.entity.PuzzlePiece
import kotlin.math.roundToInt

@Composable
fun PiecesView(
    puzzlePieces : MutableList<PuzzlePiece>,
    offsetX : SnapshotStateList<Float>,
    offsetY : SnapshotStateList<Float>,
    isGameOver : MutableState<Boolean>,
    puzzleViewModel: PuzzleViewModel = hiltViewModel()
){
    var selectedPieceIndex by remember { mutableIntStateOf(-1) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        puzzlePieces.forEachIndexed { index, puzzlePiece ->

            var zIndex = index.toFloat()
            if(!puzzlePiece.canMove){
                zIndex = 0f
            } else if(index == selectedPieceIndex){
                zIndex = Float.MAX_VALUE
            }

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
                    .zIndex(zIndex)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                selectedPieceIndex = index
                            },
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