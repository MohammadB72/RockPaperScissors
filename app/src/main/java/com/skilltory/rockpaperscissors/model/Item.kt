package com.skilltory.rockpaperscissors.model

import com.skilltory.rockpaperscissors.R

enum class ItemType {
    NONE, ROCK, PAPER, SCISSOR
}

data class Item(
    val id: Int,
    val itemType: ItemType = ItemType.NONE,
    val offsetX: Int = 0,
    val offsetY: Int = 0,
    val teta: Double = 0.0,
    val size: Int,
) {
    val resId: Int
        get() = when (itemType) {
            ItemType.NONE -> {
                0
            }

            ItemType.ROCK -> {
                R.drawable.rock
            }

            ItemType.PAPER -> {
                R.drawable.paper
            }

            ItemType.SCISSOR -> {
                R.drawable.scissor
            }
        }
}

fun Item.isRock() = this.itemType == ItemType.ROCK
fun Item.isPaper() = this.itemType == ItemType.PAPER
fun Item.isScissor() = this.itemType == ItemType.SCISSOR