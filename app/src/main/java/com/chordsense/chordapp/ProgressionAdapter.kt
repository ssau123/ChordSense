package com.chordsense.chordapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chordsense.chordapp.MainActivity.Companion.dp
import com.chordsense.chordapp.Sounds.playChord
import com.chordsense.chordapp.ui.progressions.ProgressionsFragment
import com.chordsense.chordapp.ui.progressions.ProgressionsFragment.Companion.boxWidth
import com.chordsense.chordapp.ui.progressions.ProgressionsFragment.Companion.deleteChord
import com.chordsense.chordapp.ui.progressions.ProgressionsFragment.Companion.editChord
import com.chordsense.chordapp.ui.progressions.ProgressionsFragment.Companion.panelHeight
import com.chordsense.chordapp.ui.progressions.ProgressionsFragment.Companion.piano


class ProgressionAdapter(private val list: List<Chord>, val updateMenu: ()-> Unit) : RecyclerView.Adapter<ProgressionAdapter.ViewHolder>() {
    var selected: ViewHolder? = null
    var index = 0
    lateinit var selectedChord: Chord

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.chord_box, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.chord = list[position]
        holder.setRoot()
        holder.setNumeral()
        holder.setExtension()
        holder.box.setBackgroundResource(R.drawable.box)


    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        lateinit var chord: Chord
        var box:LinearLayout = itemView.findViewById(R.id.chord_box)
        var button:Button = itemView.findViewById(R.id.button)
        val params = LinearLayout.LayoutParams(boxWidth, (panelHeight-20*dp).toInt())

        init {

            params.setMargins((4 * dp).toInt(), 0, (4 * dp).toInt(), 0)
            itemView.layoutParams = params
            itemView.background= null
            button.background=null
            itemView.setOnClickListener {
                selected?.deselect()
                piano.clear()
                if (this == selected) {
                    selected = null
                    deleteChord.drawable.alpha=100
                    editChord.drawable.alpha=100
                } else {
                    selectedChord = chord
                    updateMenu
                    selected = this
                    selected!!.select()
                    deleteChord.drawable.alpha=255
                    editChord.drawable.alpha=255
                    ProgressionsFragment.chord = chord
                    piano.displayChord(chord)
                    playChord(chord)

                }
            }
        }


        fun setRoot() {
            (box.getChildAt(0) as TextView).text = chord.root
        }


        fun setExtension() {
            (box.getChildAt(1) as TextView).text = chord.symbol
        }


        fun setNumeral() {
            (box.getChildAt(2) as TextView).text = chord.numeral
        }


        fun select() {
            box.setBackgroundResource(R.drawable.box_outlined)
            box.elevation=10F

            itemView.animate()
                .scaleX(1.05f).duration = 100
                itemView.animate()
                .scaleY(1.05f).duration = 100
            println(adapterPosition)
        }

        fun deselect() {
            box.setBackgroundResource(R.drawable.box)
            box.elevation=1*dp

            Sounds.stop()
            itemView.animate()
                .scaleX(1.0f).duration = 100
            itemView.animate()
                .scaleY(1.0f).duration = 100
        }


//        private fun getBackgroundWithGlow(view: View, glowColor: Int,
//                                          cornerRadius: Int,
//                                          pressedGlowSize: Int
//        ): Drawable {
//            val outerRadius = FloatArray(12)
//            Arrays.fill(outerRadius, cornerRadius.toFloat())
//            val shapeDrawablePadding = Rect()
//            val shapeDrawable = ShapeDrawable()
//            shapeDrawable.setPadding(shapeDrawablePadding)
//            shapeDrawable.paint.color = Color.TRANSPARENT
//            shapeDrawable.paint.setShadowLayer(pressedGlowSize.toFloat(), 0f, 0f, glowColor)
//            shapeDrawable.shape = RoundRectShape(outerRadius, null, null)
//            view.setLayerType(LAYER_TYPE_SOFTWARE, shapeDrawable.paint)
//
//            val drawable = LayerDrawable(arrayOf<Drawable>(shapeDrawable))
//            drawable.setLayerInset(
//                0,
//                0,
//                0,
//                0,
//                0
//            )
//            return drawable
//        }

        override fun onClick(view: View) {

        }

    }
}
