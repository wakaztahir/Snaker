package com.wakaztahir.snaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wakaztahir.snaker.ui.components.Controls
import com.wakaztahir.snaker.ui.components.SnakeDirection
import com.wakaztahir.snaker.ui.components.Snaker
import com.wakaztahir.snaker.ui.components.rememberSnakerState
import com.wakaztahir.snaker.ui.theme.SnakerTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SnakerTheme {
                // A surface container using the 'background' color from the theme
                val snakerState = rememberSnakerState()
                val gameOver = remember { mutableStateOf(false) }
                val score = remember {
                    mutableStateOf(0f)
                }

                //Increasing snake speed everytime tail increases
                LaunchedEffect(snakerState.snakeTail) {
                    snakerState.movementStep += 0.1f
                }

                Surface(color = MaterialTheme.colors.background) {
                    if (!gameOver.value) {
                        Column() {
                            Text(
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.h6,
                                text = stringResource(id = R.string.your_score) + " : " + score.value.roundToInt()
                                    .toString()
                            )
                            Snaker(
                                modifier = Modifier.weight(1f),
                                state = snakerState,
                                snakeColor = MaterialTheme.colors.primary,
                                onSelfBite = {
                                    gameOver.value = true
                                    snakerState.snakeTail = 0f
                                },
                                onFoodConsumption = {
                                    val x = if (score.value == 0f) 1f else score.value
                                    score.value = snakerState.movementStep + x
                                }
                            )
                            Controls(
                                onLeftClick = {
                                    if (snakerState.movementDirection != SnakeDirection.Right) {
                                        snakerState.movementDirection = SnakeDirection.Left
                                    }
                                },
                                onUpClick = {
                                    if (snakerState.movementDirection != SnakeDirection.Down) {
                                        snakerState.movementDirection = SnakeDirection.Up
                                    }
                                },
                                onDownClick = {
                                    if (snakerState.movementDirection != SnakeDirection.Up) {
                                        snakerState.movementDirection = SnakeDirection.Down
                                    }
                                },
                                onRightClick = {
                                    if (snakerState.movementDirection != SnakeDirection.Left) {
                                        snakerState.movementDirection = SnakeDirection.Right
                                    }
                                }
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.gameOver),
                                style = MaterialTheme.typography.h4
                            )
                            Text(
                                text = stringResource(id = R.string.scores) + " : " + score.value.roundToInt()
                                    .toString(),
                                style = MaterialTheme.typography.h6
                            )
                            Button(
                                modifier = Modifier.padding(top = 16.dp),
                                onClick = {
                                    gameOver.value = false
                                    score.value = 0f
                                }
                            ) {
                                Text(text = stringResource(id = R.string.tryAgain))
                            }
                        }
                    }
                }
            }
        }
    }
}