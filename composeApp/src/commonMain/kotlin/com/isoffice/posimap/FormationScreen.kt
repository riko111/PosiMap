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
import androidx.compose.ui.unit.min
import com.isoffice.posimap.model.Member
import com.isoffice.posimap.model.Scene
import com.isoffice.posimap.model.StageInfo
import kotlin.math.min
import kotlin.random.Random

/** フォーメーションを編集する画面 */
@Composable
fun FormationScreen(stage: StageInfo) {
    // 編集対象となるメンバー一覧
    val members = remember { mutableStateListOf<Member>() }
    // シーンごとのフォーメーション情報
    val scenes = remember {
        mutableStateListOf(
            Scene(Random.nextLong().toString(), "", mutableMapOf())
        )
    }
    // 現在選択されているシーンのインデックス
    var currentSceneIndex by remember { mutableStateOf(0) }
    // メンバー追加ダイアログの表示フラグ
    var showDialog by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        // 画面幅からタブレットかどうかを判定
        val isTablet = maxWidth > 600.dp
        // メモ欄の表示状態。タブレットでは常に表示
        var memoVisible by remember { mutableStateOf(isTablet) }

        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                // 舞台の描画とメンバー表示
                StageView(stage, members, scenes[currentSceneIndex])

                // メンバー追加用のFAB。最大15人まで追加可能
                FloatingActionButton(
                    onClick = { if (members.size < 15) showDialog = true },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                ) {
                    Text("+")
                }
            }

            // シーンの追加や切り替えを行う操作ボタン群
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

            // 保存・共有ボタンとメモ表示切り替え
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

            // メモ欄
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
            // メンバー追加ダイアログ
            MemberDialog(
                onAdd = { name, displayChar, color ->
                    // 初期位置は舞台の中央
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
    // 舞台のグリッドを描画し、メンバーを配置するビュー
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val grid = 0.9f // 90cmグリッド
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
            // 背景とグリッド線の描画
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
    // シーンごとの座標を保持する
    var x by remember(scene) { mutableStateOf(member.x) }
    var y by remember(scene) { mutableStateOf(member.y) }
    Box(
        modifier = Modifier
            .offset(offsetX + scale * x, offsetY + scale * y)
            .size(32.dp)
            .background(member.color, CircleShape)
            .pointerInput(scalePx) {
                detectDragGestures { change, dragAmount ->
                    // ドラッグ操作でメンバーを移動
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
    // 入力中のメンバー名
    var name by remember { mutableStateOf("") }
    // 表示文字
    var display by remember { mutableStateOf("") }
    // 選択された色
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
        Color(0xFFFFA500), // オレンジ
        Color(0xFF800080), // パープル
        Color(0xFF00FFFF)  // アクア
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
                    val disp = display.ifBlank { name.take(1) }
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
    // シーン番号や追加・削除ボタンを表示する行
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 現在の前にシーンを追加
        Button(onClick = onAddBefore, modifier = Modifier.padding(4.dp)) { Text("＋前") }
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(scenes) { index, _ ->
                if (index == currentIndex) {
                    // 選択中のシーン
                    Button(onClick = { onSelect(index) }) { Text((index + 1).toString()) }
                } else {
                    OutlinedButton(onClick = { onSelect(index) }) { Text((index + 1).toString()) }
                }
            }
        }
        // 現在の後にシーンを追加
        Button(onClick = onAddAfter, modifier = Modifier.padding(4.dp)) { Text("＋後") }
        // シーンを削除（最低1シーンは残す）
        Button(
            onClick = onRemove,
            enabled = scenes.size > 1,
            modifier = Modifier.padding(4.dp)
        ) { Text("削除") }
    }
}

private fun loadMembersFromScene(scene: Scene, members: List<Member>, stage: StageInfo) {
    // シーンに保存された座標をメンバーに適用する
    members.forEach { member ->
        val pos = scene.positions[member.id] ?: (stage.width / 2f to stage.depth / 2f)
        member.x = pos.first
        member.y = pos.second
    }
}

