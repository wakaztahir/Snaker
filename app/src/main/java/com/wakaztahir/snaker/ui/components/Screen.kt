package com.wakaztahir.snaker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

data class TailDiv(val x: MutableState<Float>, val y: MutableState<Float>)

@Composable
fun Snaker(
    modifier: Modifier = Modifier,
    state: SnakerState = rememberSnakerState(7),
    snakeColor: Color = MaterialTheme.colors.primary,
    foodColor: Color = MaterialTheme.colors.secondary,
    onSelfBite: () -> Unit = {}
) {
    //Snake variables
    val snakeX = remember { mutableStateOf(0f) }
    val snakeY = remember { mutableStateOf(0f) }
    var width = remember { -1 }
    var height = remember { -1 }

    //Food particle variables
    val foodX = remember {
        mutableStateOf(-1f)
    }
    val foodY = remember {
        mutableStateOf(-1f)
    }

    //Tail Variables
    var tailLength = remember {
        state.snakeTail
    }
    val tailDivisions = remember {
        mutableListOf<TailDiv>()
    }

    val placeFood: () -> Unit = {
        if (width != -1 && height != -1) {
            foodX.value = Random.nextInt(from = 0, (width - state.foodWidth).toInt()).toFloat()
            foodY.value = Random.nextInt(from = 0, (height - state.foodHeight).toInt()).toFloat()
        }
    }

    Canvas(
        modifier = modifier
            .onGloballyPositioned {
                width = it.size.width
                height = it.size.height
                if (foodX.value == -1f && foodY.value == -1f) {
                    placeFood()
                }
            }
            .fillMaxSize()

    ) {

        //Displaying the snake head
        drawRoundRect(
            topLeft = Offset(snakeX.value, snakeY.value),
            color = snakeColor,
            size = Size(state.snakeWidth, state.snakeHeight),
//            cornerRadius = CornerRadius(state.snakeWidth,state.snakeHeight)
        )

        //Displaying the snake tail
        state.snakeTail.let { tail ->
            tailDivisions.forEach { tailDiv ->
                drawRect(
                    color = snakeColor,
                    topLeft = Offset(tailDiv.x.value, tailDiv.y.value),
                    size = Size(state.snakeWidth, state.snakeHeight)
                )
            }
        }

        //Displaying the food particle
        if (foodX.value != -1f && foodY.value != -1f) {
            drawRoundRect(
                topLeft = Offset(foodX.value, foodY.value),
                color = foodColor,
                size = Size(state.foodWidth, state.foodHeight),
                cornerRadius = CornerRadius(state.foodWidth, state.foodHeight)
            )
        }
    }

    //Divide Snake Tail When Changed
    LaunchedEffect(state.snakeTail) {

        //How many tail divisions to add/remove to come near to new snake tail
        val howMany = (state.snakeTail / state.foodValue).roundToInt()

        if (state.snakeTail < tailLength) {
            //Removing tail divisions
            for (i in 0..howMany) {
                val index = tailDivisions.size - 1 - i
                if (index > -1 && index < tailDivisions.size) {
                    tailDivisions.removeAt(index)
                }
            }
        } else if (state.snakeTail > tailLength) {
            //Adding tail division out of canvas
            for (i in 0..howMany) {
                tailDivisions.add(
                    TailDiv(
                        x = mutableStateOf(-state.snakeWidth),
                        y = mutableStateOf(-state.snakeHeight)
                    )
                )
            }
        }

        //Updating tail length for next round check
        tailLength = state.snakeTail
    }

    LaunchedEffect(snakeX.value, snakeY.value, width, height) {

        var newX = snakeX.value
        var newY = snakeY.value

        if (width != -1 && height != -1) {

            //Moving Snake Head
            when (state.movementDirection) {
                SnakeDirection.Left -> {
                    val expected = snakeX.value - state.movementStep
                    newX = if (expected > 0) {
                        expected
                    } else {
                        width.toFloat() - state.snakeWidth //Minus to avoid out of screen
                    }
                }
                SnakeDirection.Up -> {
                    val expected = snakeY.value - state.movementStep
                    newY = if (expected > 0) {
                        expected
                    } else {
                        height.toFloat() - state.snakeHeight
                    }
                }
                SnakeDirection.Right -> {
                    val expected = snakeX.value + state.movementStep
                    newX = if ((expected + state.snakeWidth) < width) {
                        expected
                    } else {
                        0f
                    }
                }
                SnakeDirection.Down -> {
                    val expected = snakeY.value + state.movementStep
                    newY = if ((expected + state.snakeHeight) < height) {
                        expected
                    } else {
                        0f
                    }
                }
            }


            //Updating Snake Tail Divisions

            var prevX = newX
            var prevY = newY

            tailDivisions.forEachIndexed { index, tailDiv ->

                var curX = newX
                var curY = newY

                if (index == 0) {
                    //Current value of first tail div
                    prevX = tailDiv.x.value
                    prevY = tailDiv.y.value

                } else if (index > 0) {
                    //Assigning current value from previous tail div
                    curX = prevX
                    curY = prevY

                    //Assigning old tail div value to tail div after it
                    prevX = tailDiv.x.value //todo remove x,y state change
                    prevY = tailDiv.y.value
                }

                //Moving the tail div
                tailDiv.x.value = curX
                tailDiv.y.value = curY


                //Self Bite Detection
                if (//X
                    index>3 &&
                    newX >= curX &&
                    newX <= curX + state.snakeWidth &&
                    //Y
                    newY >= curY &&
                    newY <= curY + state.snakeHeight
                ) {
                    onSelfBite()
                }
            }


            //Food Consumption Detection
            if (//X
                (snakeX.value + state.snakeWidth) >= foodX.value - state.foodTolerance &&
                snakeX.value <= (foodX.value + state.foodWidth + state.foodTolerance) &&
                //Y
                (snakeY.value + state.snakeHeight) >= foodY.value - state.foodTolerance &&
                snakeY.value <= (foodY.value + state.foodHeight + state.foodTolerance)
            ) {
                //Increasing snake tail size
                state.snakeTail = state.snakeTail + state.foodValue
                //Changing food coordinates
                placeFood()
            }
        }

        delay(state.movementDelay)
        snakeX.value = newX
        snakeY.value = newY
    }
}