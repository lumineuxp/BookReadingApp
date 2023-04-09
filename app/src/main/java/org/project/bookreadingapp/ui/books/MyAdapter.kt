package org.project.bookreadingapp.ui.books

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.project.bookreadingapp.R
import org.project.bookreadingapp.data.Tales



class MyAdapter(val context: Context, val TaleList:List<Tales>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    inner class ViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
        internal val imageView :ImageView
        internal val name :TextView

        init {
            name = itemView.findViewById(R.id.titleTxt)
            imageView = itemView.findViewById(R.id.imgView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var itemView = LayoutInflater.from(context).inflate(R.layout.items_book,parent,false)
        return ViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        return TaleList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var name:String = TaleList.get(position).title
        holder.name.text = name

        var story:String = TaleList.get(position).story

        var imageView:String = TaleList.get(position).cover
        Glide.with(holder.itemView.context).load(imageView).centerCrop().into(holder.imageView)

        holder.itemView.setOnClickListener {
            // create dialog box
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.activity_dialogbooks)

            // customize dialog box as needed
            val dialog_img = dialog.findViewById<ImageView>(R.id.dialog_image)
            Glide.with(holder.itemView.context).load(imageView).centerCrop().into(dialog_img)

            val dialog_btn = dialog.findViewById<Button>(R.id.dialog_Btn)
            dialog_btn.setOnClickListener {
                var t = Intent(context, Storybooks::class.java)
                    t.putExtra("name",name)
                    t.putExtra("story",story)
                    t.putExtra("image",imageView)
                    context.startActivity(t)
            }

            // show dialog box
            dialog.show()

        }

        //send data to storybooks activity on click
//        holder.itemView.setOnClickListener {
//            var t:Intent = Intent(context,Storybooks::class.java)
//            t.putExtra("name",name)
//            t.putExtra("story",story)
//            t.putExtra("image",imageView)
//            context.startActivity(t)
//        }


    }
}