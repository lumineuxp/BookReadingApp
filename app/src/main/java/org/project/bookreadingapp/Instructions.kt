package org.project.bookreadingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Instructions : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructions)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}