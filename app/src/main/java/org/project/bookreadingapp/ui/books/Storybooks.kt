package org.project.bookreadingapp.ui.books

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import org.project.bookreadingapp.R

class Storybooks : AppCompatActivity() {

//    private var player: MediaPlayer? = null
//    private var isPlaying : Boolean = false
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


        val playStory = findViewById<ImageButton>(R.id.play_story)
        playStory.setOnClickListener {

        }
    }

    private fun playSyn(){

    }

}