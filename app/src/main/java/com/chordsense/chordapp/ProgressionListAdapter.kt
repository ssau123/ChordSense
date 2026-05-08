package com.chordsense.chordapp

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.chordsense.chordapp.GlobalMethods.Companion.hide
import com.chordsense.chordapp.GlobalMethods.Companion.show
import com.chordsense.chordapp.MainActivity.Companion.premium
import com.chordsense.chordapp.ui.progressions.ProgressionsFragment.Companion.savedProgressions
import com.chordsense.chordapp.ui.progressions.ProgressionsFragment.Companion.showKeyboard


class ProgressionListAdapter(val activity: FragmentActivity, private val list: List<String>, val deleteProgression: ImageButton) : RecyclerView.Adapter<ProgressionListAdapter.ViewHolder>() {
    var selected: ViewHolder? = null
    var id:String = ""
    lateinit var data :List<String>


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.progression_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position].split(":")
        holder.name.isEnabled=false
        holder.name.setText(data[1])
        holder.key.text = data[2]+" "+data[3]+" | " + data[4] + " BPM"
        holder.date.text = data[5]
        holder.name.tag = list[position]
        holder.progressionId=list[position]

        if (data[6] == "fav_1") {
            holder.favorited =true
            holder.favorite.setBackgroundResource(R.drawable.star_fill1_wght200_grad0_opsz24)

        } else {
            holder.favorited = false
            holder.favorite.setBackgroundResource(R.drawable.star)

        }
        if (data[0] == "DefaultProgression" || data[0] == "PremiumProgression") {
            println(data[0]+" " +data[1])
            holder.name.isEnabled=false
            holder.editIcon.visibility = View.GONE
            holder.date.visibility = View.INVISIBLE
            holder.logo.visibility=View.VISIBLE

        }

        if (data[0] == "PremiumProgression" && !premium) {
            holder.itemView.isClickable = false
            holder.bg.alpha=0.9F
            holder.name.alpha=0.5F
            holder.logo.alpha=0.5F
            holder.key.alpha=0.5F
            holder.favorite.alpha=0.5F

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val name: TextView = itemView.findViewById(R.id.prog_name)
        val key:TextView = itemView.findViewById(R.id.key)
        val favorite: Button = itemView.findViewById(R.id.favorite)
        val date:TextView = itemView.findViewById(R.id.date)
        var editIcon:ImageView = itemView.findViewById(R.id.edit_icon)
        var favorited = false
        var progressionId:String=""
        var bg :View = itemView.findViewById(R.id.bg)
        val logo:ImageView= itemView.findViewById(R.id.logo)
        private var mLastClickTime = SystemClock.elapsedRealtime()


        init {
            favorite.isClickable=false
            itemView.setOnClickListener {
                selected?.deselect()

                if (this == selected) {
                    selected = null
                } else {
                    selected = this
                    selected!!.select()
                    id = progressionId
                    data = progressionId.split(":")
                    if (data[0] == "DefaultProgression" || data[0] == "PremiumProgression" ) {
                        deleteProgression.drawable.alpha=100
                    } else {
                        deleteProgression.drawable.alpha=255

                    }

                }

            }

            editIcon.setOnClickListener {
//                name.requestFocus()
//                name.setSelection(name.text.length)
                showKeyboard(name)

            }

            favorite.setOnClickListener {


                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return@setOnClickListener
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                favorited = !favorited
                if (favorited) {

                    favorite.setBackgroundResource(R.drawable.star_fill1_wght200_grad0_opsz24)
                    var oldId = progressionId
                    progressionId= progressionId.replace("fav_0","fav_1")
                    with (MainActivity.sharedPref.edit()) {
                        putStringSet(progressionId, MainActivity.sharedPref.getStringSet(oldId, null))
                        remove(oldId)
                    }.apply()
                    var index = list.indexOf(oldId)
                    if (index!= -1) {
                        savedProgressions[index] = progressionId
//                        notifyItemChanged(index)
//                        Handler(Looper.getMainLooper()).postDelayed(
//                            Runnable { mRecyclerView.findViewHolderForAdapterPosition(
//                                index)?.itemView?.performClick() },
//                            0
//                        )

                    }

                } else {

                    favorite.setBackgroundResource(R.drawable.star)
                    val oldId = progressionId
                    progressionId= progressionId.replace("fav_1","fav_0")
                    with (MainActivity.sharedPref.edit()) {
                        putStringSet(progressionId, MainActivity.sharedPref.getStringSet(oldId, null))
                        remove(oldId)
                    }.apply()

                    val index = list.indexOf(oldId)
                    if (index!= -1) { // should be true
                        savedProgressions[index] = progressionId
//                        notifyItemChanged(index)
//                        Handler(Looper.getMainLooper()).postDelayed(
//                            Runnable { mRecyclerView.findViewHolderForAdapterPosition(
//                                index)?.itemView?.performClick() },
//                            0
//                        )
                    }
                }
            }
        }

        override fun onClick(view: View) {
            Toast.makeText(view.context, list[adapterPosition], Toast.LENGTH_SHORT).show()
        }

        fun select() {
//            name.isFocusableInTouchMode = true
            favorite.isClickable=true
            bg.setBackgroundResource(R.drawable.box_selected)
            show(editIcon)

        }

        fun deselect() {
//            name.focusable = View.NOT_FOCUSABLE
            favorite.isClickable=false

            bg.setBackgroundResource(R.drawable.box)
            hide(editIcon)

        }
    }
}
