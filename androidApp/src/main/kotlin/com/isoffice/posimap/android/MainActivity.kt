package com.isoffice.posimap.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.isoffice.posimap.model.FormationScene
import com.isoffice.posimap.model.Member
import com.isoffice.posimap.model.Performance
import com.isoffice.posimap.model.Position
import com.isoffice.posimap.model.Stage
import com.isoffice.posimap.repository.PerformanceRepository

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ダミーデータ作成
        val performance = Performance(
            title = "Demo Performance",
            stage = Stage(10, 5),
            members = listOf(Member("m1", "Anne", "A", "#FF0000")),
            scenes = listOf(
                FormationScene(
                    id = "scene-001",
                    title = "Opening",
                    positions = listOf(Position("m1", 2, 1))
                )
            )
        )

        // JSON共有
        val repo = PerformanceRepository()
        val bytes = repo.export(performance)
        ShareGateway(this).share(bytes, "demo.posimap.json")
    }
}
