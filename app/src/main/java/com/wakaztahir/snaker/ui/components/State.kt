package com.wakaztahir.snaker.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue


object SnakeDirection {
    const val Right = 1
    const val Left = 2
    const val Up = 3
    const val Down = 4
}

class SnakerState {

    //Snake Head Width Height
    var snakeWidth by mutableStateOf(50f)
    var snakeHeight by mutableStateOf(50f)

    //Movement
    var movementDelay by mutableStateOf((0).toLong())
    var movementStep by mutableStateOf(5f)
    var movementDirection by mutableStateOf(SnakeDirection.Right)

    //Tail
    var snakeTail by mutableStateOf(0f)

    //Food
    var foodWidth by mutableStateOf(50f)
    var foodHeight by mutableStateOf(50f)
    var foodTolerance by mutableStateOf(10f)
    var foodValue by mutableStateOf(50f)

    companion object {
        fun Saver(): Saver<SnakerState, *> {
            return listSaver(save = {
                listOf(
                    //Snake
                    it.snakeWidth,
                    it.snakeHeight,
                    //Movement
                    it.movementDelay.toFloat(),
                    it.movementStep,
                    it.movementDirection.toFloat(),
                    //Tail
                    it.snakeTail,
                    //Food
                    it.foodWidth,
                    it.foodHeight,
                    it.foodTolerance,
                    it.foodValue,

                    )
            }, restore = {
                SnakerState().apply {
                    //Snake
                    this.snakeWidth = it[0]
                    this.snakeHeight = it[1]
                    //Movement
                    this.movementDelay = it[2].toLong()
                    this.movementStep = it[3]
                    this.movementDirection = it[4].toInt()
                    //Tail
                    this.snakeTail = it[5]
                    //Food
                    this.foodWidth = it[6]
                    this.foodHeight = it[7]
                    this.foodTolerance = it[8]
                    this.foodValue = it[9]
                }
            })
        }
    }

}

@Composable
fun rememberSnakerState(initialMovement: Int = SnakeDirection.Right) =
    rememberSaveable(saver = SnakerState.Saver()) {
        SnakerState().apply {
            this.movementDirection = initialMovement
        }
    }