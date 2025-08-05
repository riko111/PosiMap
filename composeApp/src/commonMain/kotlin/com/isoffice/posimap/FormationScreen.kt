package com.isoffice.posimap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.isoffice.posimap.model.Member
import com.isoffice.posimap.model.Scene
import com.isoffice.posimap.model.StageInfo
import kotlin.math.min
import kotlin.random.Random

@Composable
fun FormationScreen(stage: StageInfo) {
    val members = remember { mutableStateListOf<Member>() }
    val scenes = remember {
        mutableStateListOf(
            Scene(Random.nextLong().toString(), "", mutableMapOf())
        )
    }
    var currentSceneIndex by remember { mutableStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTablet = maxWidth > 600.dp
        var memoVisible by remember { mutableStateOf(isTablet) }

        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                StageView(stage, members, scenes[currentSceneIndex])

                FloatingActionButton(
                    onClick = { if (members.size < 15) showDialog = true },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                ) {
                    Text("+")
                }
            }

            SceneControls(
                scenes = scenes,
                currentIndex = currentSceneIndex,
                onSelect = { index ->
                    loadMembersFromScene(scenes[index], members, stage)
                    currentSceneIndex = index
                },
                onAddBefore = {
                    val newPositions = members.associate { it.id to (it.x to it.y) }.toMutableMap()
                    val insertIndex = currentSceneIndex
                    scenes.add(insertIndex, Scene(Random.nextLong().toString(), "", newPositions))
                    currentSceneIndex = insertIndex
                },
                onAddAfter = {
                    val newPositions = members.associate { it.id to (it.x to it.y) }.toMutableMap()
                    val insertIndex = currentSceneIndex + 1
                    scenes.add(insertIndex, Scene(Random.nextLong().toString(), "", newPositions))
                    currentSceneIndex = insertIndex
                },
                onRemove = {
                    if (scenes.size > 1) {
                        scenes.removeAt(currentSceneIndex)
                        currentSceneIndex = currentSceneIndex.coerceAtMost(scenes.lastIndex)
                        loadMembersFromScene(scenes[currentSceneIndex], members, stage)
                    }
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { /* TODO 保存処理 */ }, modifier = Modifier.weight(1f)) {
                    Text("保存")
                }
                Button(onClick = { /* TODO 共有処理 */ }, modifier = Modifier.weight(1f)) {
                    Text("共有")
                }
                if (!isTablet) {
                    TextButton(onClick = { memoVisible = !memoVisible }) {
                        Text("メモ")
                    }
                }
            }

            if (memoVisible) {
                OutlinedTextField(
                    value = scenes[currentSceneIndex].memo,
                    onValueChange = { scenes[currentSceneIndex].memo = it },
                    label = { Text("メモ") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }

        if (showDialog) {
            MemberDialog(
                onAdd = { name, displayChar, color ->
                    val centerX = stage.width / 2f
                    val centerY = stage.depth / 2f
                    val member = Member(
                        id = Random.nextLong().toString(),
                        name = name,
                        displayChar = displayChar,
                        color = color,
                        x = centerX,
                        y = centerY
                    )
                    members.add(member)
                    scenes.forEach { it.positions[member.id] = centerX to centerY }
                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }
    }
}


@Composable
private fun StageView(stage: StageInfo, members: List<Member>, scene: Scene) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val grid = 0.9f
        val widthDp = maxWidth
        val heightDp = maxHeight
        val scale = min(widthDp / (stage.width + grid * 2f), heightDp / stage.depth)
        val density = LocalDensity.current
        val scalePx = with(density) { scale.toPx() }
        val gridPx = grid * scalePx
        val stageWidthPx = stage.width * scalePx
        val stageHeightPx = stage.depth * scalePx
        val totalWidthPx = stageWidthPx + gridPx * 2f
        val widthPx = with(density) { widthDp.toPx() }
        val heightPx = with(density) { heightDp.toPx() }
        val offsetXPx = (widthPx - totalWidthPx) / 2f
        val offsetYPx = (heightPx - stageHeightPx) / 2f
        val offsetXDp = with(density) { offsetXPx.toDp() }
        val offsetYDp = with(density) { offsetYPx.toDp() }
        val gridDp = scale * grid

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                Color.White,
                topLeft = Offset(offsetXPx, offsetYPx),
                size = Size(totalWidthPx, stageHeightPx)
            )
            var x = offsetXPx
            while (x <= offsetXPx + totalWidthPx + 0.5f) {
                drawLine(
                    Color.LightGray,
                    Offset(x, offsetYPx),
                    Offset(x, offsetYPx + stageHeightPx)
                )
                x += gridPx
            }
            var y = offsetYPx
            while (y <= offsetYPx + stageHeightPx + 0.5f) {
                drawLine(
                    Color.LightGray,
                    Offset(offsetXPx, y),
                    Offset(offsetXPx + totalWidthPx, y)
                )
                y += gridPx
            }
        }

        val memberOffsetX = offsetXDp + gridDp
        val memberOffsetY = offsetYDp
        members.forEach { member ->
            MemberItem(member, scale, scalePx, memberOffsetX, memberOffsetY, stage, scene)
        }
    }
}

@Composable
private fun MemberItem(
    member: Member,
    scale: Dp,
    scalePx: Float,
    offsetX: Dp,
    offsetY: Dp,
    stage: StageInfo,
    scene: Scene
) {
    var x by remember(scene) { mutableStateOf(member.x) }
    var y by remember(scene) { mutableStateOf(member.y) }
    Box(
        modifier = Modifier
            .offset(offsetX + scale * x, offsetY + scale * y)
            .size(32.dp)
            .background(member.color, CircleShape)
            .pointerInput(scalePx) {
                detectDragGestures { change, dragAmount ->
                    change.consumeAllChanges()
                    x = (x + dragAmount.x / scalePx).coerceIn(0f, stage.width)
                    y = (y + dragAmount.y / scalePx).coerceIn(0f, stage.depth)
                    member.x = x
                    member.y = y
                    scene.positions[member.id] = x to y
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(member.displayChar, color = Color.White)
    }
}

@Composable
private fun MemberDialog(
    onAdd: (String, String, Color) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var display by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf<Color?>(null) }
    val colors = listOf(
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Yellow,
        Color.Cyan,
        Color.Magenta,
        Color.Gray,
        Color.Black,
        Color.White,
        Color(0xFFFFA500), // Orange
        Color(0xFF800080), // Purple
        Color(0xFF00FFFF)  // Aqua
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("メンバー追加") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("名前") }
                )
                OutlinedTextField(
                    value = display,
                    onValueChange = { display = it },
                    label = { Text("表示文字") },
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colors.forEach { c ->
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(c)
                                .border(
                                    width = if (selectedColor == c) 2.dp else 1.dp,
                                    color = Color.Black
                                )
                                .clickable { selectedColor = c }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val color = selectedColor ?: Color.Gray
                    val disp = if (display.isNotBlank()) display else name.take(1)
                    onAdd(name, disp, color)
                }
            ) {
                Text("追加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("キャンセル") }
        }
    )
}

@Composable
private fun SceneControls(
    scenes: List<Scene>,
    currentIndex: Int,
    onSelect: (Int) -> Unit,
    onAddBefore: () -> Unit,
    onAddAfter: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onAddBefore, modifier = Modifier.padding(4.dp)) { Text("＋前") }
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(scenes) { index, _ ->
                if (index == currentIndex) {
                    Button(onClick = { onSelect(index) }) { Text((index + 1).toString()) }
                } else {
                    OutlinedButton(onClick = { onSelect(index) }) { Text((index + 1).toString()) }
                }
            }
        }
        Button(onClick = onAddAfter, modifier = Modifier.padding(4.dp)) { Text("＋後") }
        Button(
            onClick = onRemove,
            enabled = scenes.size > 1,
            modifier = Modifier.padding(4.dp)
        ) { Text("削除") }
    }
}

private fun loadMembersFromScene(scene: Scene, members: List<Member>, stage: StageInfo) {
    members.forEach { member ->
        val pos = scene.positions[member.id] ?: (stage.width / 2f to stage.depth / 2f)
        member.x = pos.first
        member.y = pos.second
    }
}

