package com.example.puzzle.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.puzzle.R
import com.example.puzzle.model.entity.Puzzle
import com.example.puzzle.model.entity.PuzzlePiece

@SuppressLint("CoroutineCreationDuringComposition", "RememberReturnType",
    "MutableCollectionMutableState", "UnrememberedMutableState"
)
@Composable
fun PuzzleView(
    puzzle: Puzzle,
    navController: NavController,
    puzzleViewModel: PuzzleViewModel = hiltViewModel()
){
    var puzzlePieces : MutableList<PuzzlePiece> = mutableListOf()

    val isGameOver = remember {
        mutableStateOf(value = false)
    }
    var imageBitmap: ImageBitmap? by remember {
        mutableStateOf(value = null)
    }
    val padding = 16.dp

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidthInPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightInPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    LaunchedEffect(Unit) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(puzzle.image)
            .allowHardware(false)
            .build()
        val result = (loader.execute(request) as SuccessResult).drawable
        val bitmap = (result as BitmapDrawable).bitmap

        val maxSize = screenWidthInPx - (padding.value * 2 * density.density)
        var width = maxSize
        var height = maxSize
        if(bitmap.width > bitmap.height){
            height = (bitmap.height * maxSize) / bitmap.width
        } else {
            width = (bitmap.width * maxSize) / bitmap.height
        }
        val resizeBitmap = Bitmap.createScaledBitmap(
            bitmap,
            width.toInt(),
            height.toInt(),
            false
        )
        imageBitmap = resizeBitmap.asImageBitmap()
    }
    val offsetX = remember { mutableStateListOf<Float>() }
    val offsetY = remember { mutableStateListOf<Float>() }

    var position by remember { mutableStateOf(Offset.Zero) }
    var height by remember { mutableIntStateOf(value = 0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        Column {
            Box (
                modifier = Modifier
                    .weight(0.7f)
            ){
                Column {
                    Text(
                        text = stringResource(id = R.string.description),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if(imageBitmap != null) {
                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier
                                .fillMaxSize()
                                .onGloballyPositioned { coordinates ->
                                    height = coordinates.size.height
                                }
                        ){
                            Image(
                                bitmap = imageBitmap!!,
                                contentDescription = "",
                                modifier = Modifier
                                    .conditional(!isGameOver.value) {
                                        alpha(0.4f)
                                    }
                                    .onGloballyPositioned { coordinates ->
                                        position = coordinates.positionInRoot()
                                    }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            if(imageBitmap != null){
                if(position.x != 0f && height != 0) {
                    val imagePosition by remember { mutableStateOf(Offset(position.x - 48, -height.toFloat())) }
                    puzzlePieces = puzzleViewModel.splitImage(puzzle, imageBitmap!!, imagePosition)
                }
                puzzleViewModel.piecesPosition(puzzlePieces, screenWidthInPx, screenHeightInPx)
                puzzlePieces.forEach{ puzzlePiece ->
                    offsetX.add(puzzlePiece.xCoord.toFloat())
                    offsetY.add(puzzlePiece.yCoord.toFloat())
                }
            }

            Box (
                modifier = Modifier
                    .weight(0.3f)
            ){
                when(isGameOver.value){
                    true -> {
                        VictoryMessage(
                            successContent = puzzle.successContent,
                            successLink = puzzle.successLink,
                            onClick = {
                                navController.navigate(puzzle.successLink)
                            }
                        )
                    }
                    false -> {
                        if(imageBitmap != null) {
                            PiecesView(
                                puzzlePieces = puzzlePieces,
                                offsetX = offsetX,
                                offsetY = offsetY,
                                isGameOver = isGameOver,
                                puzzleViewModel = puzzleViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

fun Modifier.conditional(condition : Boolean, modifier : Modifier.() -> Modifier) : Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}