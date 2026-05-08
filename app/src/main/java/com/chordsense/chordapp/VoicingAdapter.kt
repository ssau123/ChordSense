package com.chordsense.chordapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.chordsense.chordapp.MusicData.Companion.doubleVoicingOptions
import com.chordsense.chordapp.MusicData.Companion.removeOptions

class VoicingAdapter(private val list: List<String>, val chord:Chord, val invertUp: Button, val invertDown:Button, val addVoicing:Button, val removeVoicing:Button) : RecyclerView.Adapter<VoicingAdapter.ViewHolder>() {
    var selected: ViewHolder? = null
    var interval = ""


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.voicing_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.textView.text = list[position]
        holder.container.setBackgroundResource(R.drawable.rect_12dp)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val container: ConstraintLayout = itemView.findViewById(R.id.container)
        val textView: TextView = itemView.findViewById(R.id.voicing)


        init {
            itemView.setOnClickListener {
                selected?.deselect()


                if (this == selected) {
                    selected = null
                    addVoicing.alpha = 0.5f
                    removeVoicing.alpha = 0.5f
                    invertUp.alpha = 0.5f
                    invertDown.alpha = 0.5f
                } else {
                    selected = this
                    interval = selected!!.textView.text.toString()
                    val index = selected!!.layoutPosition


                    if (interval in doubleVoicingOptions && chord.voicingList.count { it == interval } == 1) {
                        addVoicing.alpha = 1.0f

                    } else {
                        addVoicing.alpha = 0.5f

                    }

                    if (interval in removeOptions || chord.voicingList.count { it == interval } == 2) {
                        removeVoicing.alpha = 1.0f

                    }else {
                        removeVoicing.alpha = 0.5f

                    }

                    if ((index != (chord.intervals.size-1) && (chord.intervals[index]+chord.offset+12)<=60) && !(chord.intervals.contains(chord.intervals[index]+12))) {
                        invertUp.alpha = 1.0f

                    }else {
                        invertUp.alpha = 0.5f

                    }

                    if (chord.intervals[index]>=0 && (chord.intervals[index]+chord.offset-12)>=0 && !(chord.intervals.contains(chord.intervals[index]-12))) {
                        invertDown.alpha = 1.0f
                    }else {
                        invertDown.alpha = 0.5f

                    }


                    selected!!.select()
                }

                println(list)
                println(layoutPosition)


            }
        }

        private fun select() {
            container.setBackgroundResource(R.drawable.box_outlined)

        }

        private fun deselect() {
            container.setBackgroundResource(R.drawable.rect_12dp)
        }

        override fun onClick(view: View) {
            Toast.makeText(view.context, list[adapterPosition], Toast.LENGTH_SHORT).show()
        }
    }
}