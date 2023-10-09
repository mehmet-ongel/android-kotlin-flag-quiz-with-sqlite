package com.techmania.flagquiz.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.techmania.flagquiz.R
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Thread.sleep(3000L)
        setContentView(R.layout.activity_main)
    }
}