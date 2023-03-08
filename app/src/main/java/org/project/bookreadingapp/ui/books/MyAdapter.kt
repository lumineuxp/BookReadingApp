package org.project.bookreadingapp.ui.books

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.project.bookreadingapp.R
import org.project.bookreadingapp.data.Tales



class MyAdapter(val context:Context, val TaleList:List<Tales>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    class ViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
        internal val imageView :ImageView
        internal val name :TextView

        init {
            name = itemView.findViewById(R.id.Name)
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

        var imageView:String = TaleList.get(position).cover
        Glide.with(holder.itemView.context).load(imageView).centerCrop().into(holder.imageView)

    }
}