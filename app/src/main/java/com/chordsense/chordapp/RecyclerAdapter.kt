package com.chordsense.chordapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(private val list: List<String>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    var selected: ViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.chord_label, parent, false)
        view.clipToOutline=false
        view.layoutParams.width=parent.width
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.textView.text = list[position]

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val textView: TextView = itemView.findViewById(R.id.chord_label)

        init {
            itemView.setOnClickListener {

            }
        }

        private fun select() {

        }

        private fun deselect() {
        }

        override fun onClick(view: View) {
            Toast.makeText(view.context, list[adapterPosition], Toast.LENGTH_SHORT).show()
        }
    }
}