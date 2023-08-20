package com.skilltory.rockpaperscissors.util

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

fun Int.randomFromZeroToThis(): Int = Random.nextInt(0, this)


fun Double.myCos(): Double = cos(this * (PI / 180.0))
fun Double.mySin(): Double = sin(this * (PI / 180.0))