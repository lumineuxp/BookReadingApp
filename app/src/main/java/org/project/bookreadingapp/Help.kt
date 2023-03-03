package org.project.bookreadingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Help : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}