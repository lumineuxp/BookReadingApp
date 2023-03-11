package org.project.bookreadingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class Storybooks : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storybooks)


        val t:Intent = intent

        //component in xml
        val name = findViewById<TextView>(R.id.story_title)
        val story = findViewById<TextView>(R.id.story_tale)
        val img = findViewById<ImageView>(R.id.story_img)

        name.text = t.getStringExtra("name")
        story.text = t.getStringExtra("story")
        Glide.with(this).load(t.getStringExtra("image")).centerCrop().into(img)


    }
}