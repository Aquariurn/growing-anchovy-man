package com.example.growinganchovyman

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PresetActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preset)

        val presetList = listOf(
            Preset("프리셋 테스트 케이스1", "맨몸", "10분", R.drawable.activity1),
            Preset("프리셋 테스트 케이스2", "맨몸", "10분", R.drawable.activity2),
            Preset("프리셋 테스트 케이스3", "맨몸", "10분", R.drawable.activity3),
            Preset("프리셋 테스트 케이스4", "맨몸", "10분", R.drawable.activity4),
            Preset("프리셋 테스트 케이스5", "맨몸", "10분", R.drawable.activity5),

        )

        val recyclerView: RecyclerView = findViewById(R.id.presetRecycler)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val adapter = PresetAdapter(presetList)
        recyclerView.adapter = adapter
    }
}