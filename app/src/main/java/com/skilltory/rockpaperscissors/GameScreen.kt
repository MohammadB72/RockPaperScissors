package com.skilltory.rockpaperscissors

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.skilltory.rockpaperscissors.model.Frame
import com.skilltory.rockpaperscissors.model.Item
import com.skilltory.rockpaperscissors.model.ItemType
import com.skilltory.rockpaperscissors.model.isPaper
import com.skilltory.rockpaperscissors.model.isRock
import com.skilltory.rockpaperscissors.model.isScissor
import com.skilltory.rockpaperscissors.util.myCos
import com.skilltory.rockpaperscissors.util.mySin
import com.skilltory.rockpaperscissors.util.randomFromZeroToThis
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

const val DISTANCE = 37
const val ITEM_SIZE = 40
const val DELAY = 50L
const val ITEM_COUNT = 5


@Composable
fun MyIdea() {

    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    var width by remember { mutableStateOf(0) }
    var height by remember { mutableStateOf(0) }

    val mainList: SnapshotStateList<Item> = remember { mutableStateListOf() }


    Column(
        Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .weight(1f)
                .border(width = 1.dp, color = Color.Black)
                .onGloballyPositioned { coordinates ->
                    width = coordinates.size.width
                    height = coordinates.size.height

                    val itemSizePx =
                        with(density) {
                            ITEM_SIZE.dp
                                .toPx()
                                .roundToInt()
                        }


                    if (mainList.isEmpty()) {
                        var id = 0
                        (1..ITEM_COUNT).forEach {
                            val x = (width - itemSizePx).randomFromZeroToThis()
                            val y = (height - itemSizePx).randomFromZeroToThis()
                            val degree = (0..360)
                                .random()
                                .toDouble()
                            mainList.add(
                                Item(
                                    id = ++id,
                                    itemType = ItemType.ROCK,
                                    offsetX = x,
                                    offsetY = y,
                                    teta = degree,
                                    size = itemSizePx,
                                )
                            )
                        }

                        (1..ITEM_COUNT).forEach {
                            val x = (width - itemSizePx).randomFromZeroToThis()
                            val y = (height - itemSizePx).randomFromZeroToThis()
                            val degree = (0..360)
                                .random()
                                .toDouble()

                            mainList.add(
                                Item(
                                    id = ++id,
                                    itemType = ItemType.PAPER,
                                    offsetX = x,
                                    offsetY = y,
                                    teta = degree,
                                    size = itemSizePx,
                                )
                            )
                        }

                        (1..ITEM_COUNT).forEach {
                            val x = (width - itemSizePx).randomFromZeroToThis()
                            val y = (height - itemSizePx).randomFromZeroToThis()
                            val degree = (0..360)
                                .random()
                                .toDouble()

                            mainList.add(
                                Item(
                                    id = ++id,
                                    itemType = ItemType.SCISSOR,
                                    offsetX = x,
                                    offsetY = y,
                                    teta = degree,
                                    size = itemSizePx,
                                )
                            )
                        }
                    }
                }
        ) {
            mainList.forEach { item ->
                Image(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                item.offsetX,
                                item.offsetY
                            )
                        }
                        .size(ITEM_SIZE.dp),
                    painter = painterResource(id = item.resId),
                    contentDescription = null
                )
            }
        }

        Button(onClick = {
            coroutineScope.launch {
                runGame(
                    itemSnapshotStateList = mainList,
                    screenSize = Frame(width = width, height = height),
                )
            }
        }) {
            Text(text = "Click Here!")
        }
    }
}

private suspend fun runGame(
    itemSnapshotStateList: SnapshotStateList<Item>,
    screenSize: Frame,
) {
    while (true) {
        val immutableList = itemSnapshotStateList.toList()
        val itemsToRemove = ArrayList<Item>()

        immutableList.forEachIndexed { index, item ->
            getRefreshedItemDirection(
                itemSnapshotStateList = itemSnapshotStateList,
                item = item,
                index = index,
                screenSize = screenSize
            )
            checkItemHit(
                itemSnapshotStateList = itemSnapshotStateList,
                immutableList = immutableList,
                itemsToRemove = itemsToRemove,
                item = item,
                index = index
            )
        }

        clearUnneccessaryItems(
            itemSnapshotStateList = itemSnapshotStateList,
            itemsToRemove = itemsToRemove
        )

        delay(DELAY)
    }
}

private fun getRefreshedItemDirection(
    itemSnapshotStateList: SnapshotStateList<Item>,
    item: Item,
    index: Int,
    screenSize: Frame
) {
    val offsetX = item.offsetX
    val offsetY = item.offsetY
    var teta = item.teta

    var x2 = (offsetX + DISTANCE * teta.myCos()).roundToInt()
    var y2 = (offsetY + DISTANCE * teta.mySin()).roundToInt()

    val bound = 5

    if (x2 + item.size >= screenSize.width) {
        if (teta in (0.0..90.0)) {
            teta = (90 + bound..180 - bound).random().toDouble()
        } else if (teta in (270.0..360.0)) {
            teta = (180 + bound..270 - bound).random().toDouble()
        }
        x2 = (offsetX + DISTANCE * teta.myCos()).roundToInt()
        y2 = (offsetY + DISTANCE * teta.mySin()).roundToInt()
    }

    if (y2 + item.size >= screenSize.height) {
        if (teta in (90.0..180.0)) {
            teta = (180 + bound..270 - bound).random().toDouble()
        } else if (teta in (0.0..90.0)) {
            teta = (270 + bound..360 - bound).random().toDouble()
        }
        x2 = (offsetX + DISTANCE * teta.myCos()).roundToInt()
        y2 = (offsetY + DISTANCE * teta.mySin()).roundToInt()
    }

    if (x2 <= 0) {
        if (teta in (180.0..270.0)) {
            teta = (270 + bound..360 - bound).random().toDouble()
        } else if (teta in (90.0..180.0)) {
            teta = (0 + bound..90 - bound).random().toDouble()
        }
        x2 = (offsetX + DISTANCE * teta.myCos()).roundToInt()
        y2 = (offsetY + DISTANCE * teta.mySin()).roundToInt()
    }

    if (y2 <= 0) {
        if (teta in (270.0..360.0)) {
            teta = (0 + bound..90 - bound).random().toDouble()
        } else if (teta in (180.0..270.0)) {
            teta = (90 + bound..180 - bound).random().toDouble()
        }
        x2 = (offsetX + DISTANCE * teta.myCos()).roundToInt()
        y2 = (offsetY + DISTANCE * teta.mySin()).roundToInt()
    }

    itemSnapshotStateList[index] = item.copy(offsetX = x2, offsetY = y2, teta = teta)
}

private fun checkItemHit(
    itemSnapshotStateList: SnapshotStateList<Item>,
    immutableList: List<Item>,
    itemsToRemove: ArrayList<Item>,
    item: Item,
    index: Int
) {
    for (i in immutableList.indices) {
        val tmp = immutableList[i]
        if (item != tmp) {
            val isTopLefHit =
                item.offsetX in (tmp.offsetX..tmp.offsetX + item.size) &&
                        item.offsetY in (tmp.offsetY..tmp.offsetY + item.size)

            val isTopRightHit =
                item.offsetX + item.size in (tmp.offsetX..tmp.offsetX + item.size) &&
                        item.offsetY in (tmp.offsetY..tmp.offsetY + item.size)

            val isBottomLeftHit =
                item.offsetX in (tmp.offsetX..tmp.offsetX + item.size) &&
                        item.offsetY + item.size in (tmp.offsetY..tmp.offsetY + item.size)

            val isBottomRightHit =
                item.offsetX + item.size in (tmp.offsetX..tmp.offsetX + item.size) &&
                        item.offsetY + item.size in (tmp.offsetY..tmp.offsetY + item.size)
            if (
                isTopLefHit || isTopRightHit || isBottomLeftHit || isBottomRightHit
            ) {
                if (item.itemType == tmp.itemType) {
                    itemSnapshotStateList[index] = mirrorDirection(item)
                    itemSnapshotStateList[i] = mirrorDirection(tmp)
                } else {
                    var removedItem: Item? = null
                    if (item.isRock()) {
                        if (tmp.isPaper()) {
                            removedItem = item
                        } else if (tmp.isScissor()) {
                            removedItem = tmp
                        }
                    } else if (item.isPaper()) {
                        if (tmp.isRock()) {
                            removedItem = tmp
                        } else if (tmp.isScissor()) {
                            removedItem = item
                        }
                    } else if (item.isScissor()) {
                        if (tmp.isRock()) {
                            removedItem = item
                        } else if (tmp.isPaper()) {
                            removedItem = tmp
                        }
                    }

                    if (removedItem != null) {
                        itemsToRemove.add(removedItem)
                    }
                }
            }
        }
    }
}

private fun clearUnneccessaryItems(
    itemSnapshotStateList: SnapshotStateList<Item>,
    itemsToRemove: ArrayList<Item>
) {
    itemsToRemove.forEach { removedItem ->
        itemSnapshotStateList.removeIf { it.id == removedItem.id }
    }
    itemsToRemove.clear()
}

private fun mirrorDirection(item: Item): Item {
    val offsetX = item.offsetX
    val offsetY = item.offsetY
    var teta = item.teta

    when (teta) {
        in (0.0..90.0) -> {
            teta += 180
        }

        in (90.0..180.0) -> {
            teta += 180
        }

        in (180.0..270.0) -> {
            teta -= 180
        }

        in (270.0..360.0) -> {
            teta -= 180
        }
    }

    val x2 = (offsetX + DISTANCE * teta.myCos()).roundToInt()
    val y2 = (offsetY + DISTANCE * teta.mySin()).roundToInt()

    return item.copy(offsetX = x2, offsetY = y2, teta = teta)
}