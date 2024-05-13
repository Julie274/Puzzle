package com.example.puzzle.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.puzzle.R
import com.example.puzzle.model.entity.Puzzle
import com.example.puzzle.model.entity.PuzzlePiece
import java.util.stream.IntStream.range
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

@SuppressLint("CoroutineCreationDuringComposition", "RememberReturnType",
    "MutableCollectionMutableState", "UnrememberedMutableState"
)
@Composable
fun PuzzleView(
    puzzle: Puzzle,
    //navController: NavController,
    puzzleViewModel: PuzzleViewModel = hiltViewModel()
){
    var puzzlePieces : List<PuzzlePiece> = mutableListOf()

    var isGameOver by remember {
        mutableStateOf(false)
    }
    var imageBitmap: ImageBitmap? by rememberSaveable {
        mutableStateOf(null)
    }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(puzzle.image)
            .allowHardware(false)
            .build()
        val result = (loader.execute(request) as SuccessResult).drawable
        val bitmap = (result as BitmapDrawable).bitmap
        imageBitmap = bitmap.asImageBitmap()
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    if(imageBitmap != null){
        puzzlePieces = splitImage(puzzle, imageBitmap!!)
        piecesPosition(puzzlePieces, screenWidth, screenHeight)
    }

    val offsetX = remember { mutableStateListOf<Float>() }
    val offsetY = remember { mutableStateListOf<Float>() }

    puzzlePieces.forEach{ puzzlePiece ->
        offsetX.add(puzzlePiece.xCoord.toFloat())
        offsetY.add(puzzlePiece.yCoord.toFloat())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column {
            Box (
                modifier = Modifier.weight(0.7f)
            ){
                Column {
                    Text(
                        text = stringResource(id = R.string.description),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    AsyncImage(
                        model = puzzle.image,
                        contentDescription = "",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .conditional(!isGameOver){
                                alpha(0.4f)
                            },
                        placeholder = ColorPainter(MaterialTheme.colorScheme.secondary),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
            Box (
                modifier = Modifier
                    .weight(0.3f)
            ){
                when(isGameOver){
                    true -> {
                        VictoryMessage(
                            successContent = puzzle.successContent,
                            successLink = puzzle.successLink,
                            onClick = {
                                //navController.navigate("destination")
                            }
                        )
                    }
                    false -> {
                        if(imageBitmap != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                val shuffledPieces =
                                    puzzlePieces.shuffled(Random(System.currentTimeMillis()))
                                shuffledPieces.forEach { puzzlePiece ->
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
                                                Log.d("puzzlePiece", puzzlePiece.canMove.toString())
                                                detectDragGestures(
                                                    onDragEnd = {
                                                        if (isNearGoal(
                                                                offsetX[puzzlePiece.id],
                                                                offsetY[puzzlePiece.id],
                                                                0f,
                                                                -500f,
                                                                100f
                                                            )
                                                        ) {
                                                            offsetX[puzzlePiece.id] = 0f
                                                            offsetY[puzzlePiece.id] = -500f
                                                            puzzlePiece.canMove = false
                                                            if(puzzlePieces.isNotEmpty()){
                                                                if (puzzlePieces.all { !it.canMove }) {
                                                                    isGameOver = true
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
                    }
                }
            }
        }
    }
}

fun isNearGoal(xCoord: Float, yCoord: Float, xCoordGoal: Float, yCoordGoal: Float, threshold: Float): Boolean {
    val distance = sqrt((xCoord - xCoordGoal).pow(2) + (yCoord - yCoordGoal).pow(2))
    return distance <= threshold
}

fun piecesPosition(pieces : List<PuzzlePiece>, screenWidth : Int, screenHeight : Int){
    pieces.forEach{ piece ->
        piece.xCoord = Random.nextInt(0, (screenWidth))
        piece.yCoord = Random.nextInt(0, (screenHeight/3.5).toInt())
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
fun splitImage(puzzle: Puzzle, bitmap: ImageBitmap) : List<PuzzlePiece> {
    val rows = puzzle.rows
    val columns = puzzle.columns
    val pieceWidth = bitmap.width/columns
    val pieceHeight = bitmap.height/rows
    val puzzlePieces : MutableList<PuzzlePiece> = mutableListOf()

    var yCoord = 0
    for (row in range(0,rows)) {
        var xCoord = 0
        for (col in range(0, columns)) {
            var offsetX = 0
            var offsetY = 0
            if (col > 0) {
                offsetX = pieceWidth / 3
            }
            if (row > 0) {
                offsetY = pieceHeight / 3
            }

            val pieceBitmap = createBitmap(bitmap.asAndroidBitmap(), xCoord - offsetX, yCoord - offsetY,pieceWidth + offsetX, pieceHeight + offsetY)
            val piece = PuzzlePiece(col+(columns*row),pieceBitmap, xCoord, yCoord, -500, 200, pieceWidth, pieceHeight, true)
            piece.image = pieceBitmap
            piece.xCoord = xCoord - offsetX //+ imageView.getLeft()
            piece.yCoord = yCoord - offsetY //+ imageView.getTop()
            piece.pieceWidth = pieceWidth + offsetX
            piece.pieceHeight = pieceHeight + offsetY

            val bumpSize = pieceHeight / 4
            val puzzlePiece = createBitmap(pieceWidth + offsetX, pieceHeight + offsetY, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(puzzlePiece)

            val clipPath = android.graphics.Path().apply {
                moveTo(offsetX.toFloat(), offsetY.toFloat())
                if (row == 0) {
                    // top side piece
                    lineTo(offsetX.toFloat() + pieceWidth, offsetY.toFloat())
                } else {
                    // top bump
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
                    // right side piece
                    lineTo(offsetX.toFloat() + pieceWidth, offsetY.toFloat() + pieceHeight)
                } else {
                    // right bump
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
                    // bottom side piece
                    lineTo(offsetX.toFloat(), offsetY.toFloat() + pieceHeight)
                } else {
                    // bottom bump
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
                    // left side piece
                    close()
                } else {
                    // left bump
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
                color = Color.BLACK
            }
            canvas.drawPath(clipPath, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(pieceBitmap, 0f, 0f, paint)
            val whiteBorder = Paint().apply {
                style = Paint.Style.STROKE
                color = Color.WHITE
                strokeWidth = 8.0f
            }
            canvas.drawPath(clipPath, whiteBorder)
            val blackBorder = Paint().apply {
                style = Paint.Style.STROKE
                color = Color.BLACK
                strokeWidth = 3.0f
            }
            canvas.drawPath(clipPath, blackBorder)

            piece.image = puzzlePiece
            puzzlePieces.add(piece)
            xCoord += pieceWidth
        }
        yCoord += pieceHeight
    }
    return puzzlePieces
}

fun Modifier.conditional(condition : Boolean, modifier : Modifier.() -> Modifier) : Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}