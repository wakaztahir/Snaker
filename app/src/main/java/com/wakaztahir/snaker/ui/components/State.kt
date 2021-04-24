package com.wakaztahir.snaker.ui.components

import androidx.annotation.ColorInt
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
                    it.snakeWidth,
                    it.snakeHeight,
                    it.foodWidth,
                    it.foodHeight
                )
            }, restore = {
                SnakerState().apply {
                    this.snakeWidth = it[0]
                    this.snakeHeight = it[1]
                    this.foodWidth = it[2]
                    this.foodHeight = it[3]
                }
            })
        }
    }

}

@Composable
fun rememberSnakerState(initialMovement: Int =  SnakeDirection.Right) =
    rememberSaveable(saver = SnakerState.Saver()) {
        SnakerState().apply {
            this.movementDirection = initialMovement
        }
    }