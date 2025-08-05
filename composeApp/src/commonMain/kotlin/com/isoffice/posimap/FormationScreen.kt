package com.isoffice.posimap

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.platform.LocalDensity
import com.isoffice.posimap.model.Member
import com.isoffice.posimap.model.StageInfo
import java.util.UUID

@Composable
fun FormationScreen(stage: StageInfo) {
    val members = remember { mutableStateListOf<Member>() }
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        StageView(stage, members)

        FloatingActionButton(
            onClick = { if (members.size < 15) showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Text("+")
        }
    }

    if (showDialog) {
        MemberDialog(
            onAdd = { name, displayChar, color ->
                members.add(
                    Member(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        displayChar = displayChar,
                        color = color,
                        x = stage.width / 2f,
                        y = stage.depth / 2f
                    )
                )
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun StageView(stage: StageInfo, members: List<Member>) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val width = maxWidth
        val height = maxHeight
        val scaleX = width / stage.width
        val scaleY = height / stage.depth
        val density = LocalDensity.current
        val scaleXPx = with(density) { scaleX.toPx() }
        val scaleYPx = with(density) { scaleY.toPx() }

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(Color.White, size = size)
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val stepX = 0.9f * scaleXPx
            var x = centerX
            while (x <= size.width) {
                drawLine(Color.LightGray, Offset(x, 0f), Offset(x, size.height))
                x += stepX
            }
            x = centerX - stepX
            while (x >= 0f) {
                drawLine(Color.LightGray, Offset(x, 0f), Offset(x, size.height))
                x -= stepX
            }
            val stepY = 0.9f * scaleYPx
            var y = centerY
            while (y <= size.height) {
                drawLine(Color.LightGray, Offset(0f, y), Offset(size.width, y))
                y += stepY
            }
            y = centerY - stepY
            while (y >= 0f) {
                drawLine(Color.LightGray, Offset(0f, y), Offset(size.width, y))
                y -= stepY
            }
        }

        members.forEach { member ->
            MemberItem(member, scaleX, scaleY, scaleXPx, scaleYPx, stage)
        }
    }
}

@Composable
private fun MemberItem(
    member: Member,
    scaleX: Dp,
    scaleY: Dp,
    scaleXPx: Float,
    scaleYPx: Float,
    stage: StageInfo
) {
    var x by remember { mutableStateOf(member.x) }
    var y by remember { mutableStateOf(member.y) }
    Box(
        modifier = Modifier
            // scaleX and scaleY represent the dp size of one meter on the stage.
            // Multiply them on the left side so the operator extension on Dp is used
            // (Dp * Float -> Dp). This converts the member's position in meters to
            // device independent pixels for the offset modifier.
            .offset(scaleX * x, scaleY * y)
            .size(32.dp)
            .background(member.color, CircleShape)
            .pointerInput(scaleXPx, scaleYPx) {
                detectDragGestures { change, dragAmount ->
                    change.consumeAllChanges()
                    x = (x + dragAmount.x / scaleXPx).coerceIn(0f, stage.width)
                    y = (y + dragAmount.y / scaleYPx).coerceIn(0f, stage.depth)
                    member.x = x
                    member.y = y
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

