package com.chordsense.chordapp

import android.animation.ObjectAnimator
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.chordsense.chordapp.MainActivity.Companion.intervalLabels
import com.chordsense.chordapp.MainActivity.Companion.landscape
import com.chordsense.chordapp.MainActivity.Companion.screenWidth
import com.chordsense.chordapp.MainActivity.Companion.scrolling
import com.chordsense.chordapp.MainActivity.Companion.whiteKeyWidth
import kotlin.math.ceil

class Piano(val layout: ConstraintLayout, private val context: Context?, startKey: String, numKeys: Int, keyWidth: Int, keyHeight: Int) {

    val keys = mutableListOf<Button>()
    private val pressed = mutableListOf<Pair<Button, TextView?>>()
    var width = 0
    var parent: View

    init {
        if (landscape) {
            width = (1*screenWidth)
        } else {
            width = (0.96*screenWidth).toInt()
        }
        parent = layout.parent as View

        val blackKeyWidth=keyWidth*5/7
        val blackKeyHeight=keyHeight*3/5
        val constraintSet = ConstraintSet()
        var button = Button(context, null, 0, R.style.white_key)

        button.id = View.generateViewId()
        button.layoutParams = ConstraintLayout.LayoutParams(keyWidth, keyHeight)
        layout.addView(button)
        constraintSet.clone(layout)
        constraintSet.connect(button.id, ConstraintSet.LEFT, layout.id, ConstraintSet.LEFT)
        constraintSet.connect(button.id, ConstraintSet.TOP, layout.id, ConstraintSet.TOP)
        constraintSet.applyTo(layout)
        var prevId = button.id
        var prevWhite = button.id
        keys.add(button)
        button.setOnClickListener {
            Sounds.playNote(0, 240000 / 140)
        }
        var j = MusicData.notes.indexOf(startKey) + 1
        button.tag = startKey
        for (i in 1 until 61) {

            if (MusicData.notes[j].last() == '#') {
                button = Button(context, null, 0, R.style.black_key)
                button.id = View.generateViewId()
                button.layoutParams = ConstraintLayout.LayoutParams(blackKeyWidth, blackKeyHeight)
                layout.addView(button)
                constraintSet.clone(layout)
                constraintSet.connect(button.id, ConstraintSet.LEFT, prevId, ConstraintSet.LEFT)

            } else {
                button = Button(context, null, 0, R.style.white_key)
                button.id = View.generateViewId()
                button.layoutParams = ConstraintLayout.LayoutParams(keyWidth, keyHeight)
                layout.addView(button)
                constraintSet.clone(layout)
                constraintSet.connect(button.id, ConstraintSet.LEFT, prevWhite, ConstraintSet.RIGHT)
                if (prevId != prevWhite) {
                    constraintSet.connect(prevId, ConstraintSet.RIGHT, button.id, ConstraintSet.RIGHT)
                }
                prevWhite = button.id
            }
            button.tag = MusicData.notes[j]
            keys.add(button)
            constraintSet.applyTo(layout)
            prevId = button.id
            keys[i].setOnClickListener{
                Sounds.playNote(i, 240000 / 140)
            }

            if (j == 11) {
                j = 0
            } else {
                j++
            }
        }

    }

    fun showOctaveLabels(layout: ConstraintLayout, context: Context) {
        for (i in 1 until 7) {
            val text = TextView(context)
            text.id = View.generateViewId()
            val label = "C" + (i+1)
            text.text = label
            text.setTextAppearance(R.style.octave_label)
            layout.addView(text)
            val constraintSet = ConstraintSet()
            constraintSet.clone(layout)
            constraintSet.connect(text.id, ConstraintSet.TOP, keys[(i-1)*12].id, ConstraintSet.BOTTOM, 0)
            constraintSet.connect(text.id, ConstraintSet.LEFT, keys[(i-1)*12].id, ConstraintSet.LEFT, 0)
            constraintSet.connect(text.id, ConstraintSet.RIGHT, keys[(i-1)*12].id, ConstraintSet.RIGHT, 0)
            constraintSet.applyTo(layout)
        }
    }

    fun clear() {
        for (i in pressed) {
            i.first.isActivated=false
            i.first.isSelected=false

            layout.removeView(i.second)

        }
        pressed.clear()
    }

    fun displayChord(chord: Chord) {

        clear()
        var j = 0
        for (i in chord.intervals) {
            val keyNum = i+chord.offset

            keys[keyNum].isActivated=true

            if (intervalLabels) {
                pressed.add(Pair(keys[keyNum],showNoteLabel(keys[keyNum], chord.voicingList[j++])))
            } else {
                pressed.add(Pair(keys[keyNum],showNoteLabel(keys[keyNum], keys[keyNum].tag.toString())))

            }
        }

        if (scrolling && !landscape && (1==0)) {
            val anim = ObjectAnimator.ofInt(parent, "scrollX", ((keys[(chord.offset+ceil(
                (((chord.intervals.min() + chord.intervals.max()).toDouble()/2))
            )).toInt()].x - parent.width/2)+whiteKeyWidth/2).toInt())
                .setDuration(500)
            anim.start()
        }




    }

    fun displayChord(chord: Chord, previousChord: Chord) {
        clear()
        displayPreviousChord(previousChord)

        var j = 0
        for (i in chord.intervals) {
            val keyNum = i+chord.offset
            keys[keyNum].isActivated=true
            if (intervalLabels) {
                pressed.add(Pair(keys[keyNum],showNoteLabel(keys[keyNum], chord.voicingList[j++])))
            } else {
                pressed.add(Pair(keys[keyNum],showNoteLabel(keys[keyNum], keys[keyNum].tag.toString())))

            }
        }

        if (scrolling) {
            val anim = ObjectAnimator.ofInt(parent, "scrollX", ((keys[(chord.offset+ceil(
                (((chord.intervals.min() + chord.intervals.max()).toDouble()/2))
            )).toInt()].x - parent.width/2)+whiteKeyWidth/2).toInt())
                .setDuration(500)
            anim.start()
        }


    }

    fun displayPreviousChord(chord: Chord) {

        clear()
        var j = 0
        for (i in chord.intervals) {
            val keyNum = i+chord.offset

            keys[keyNum].isSelected=true

            pressed.add(Pair(keys[keyNum],null))

        }

    }

    private fun showNoteLabel(key:Button, label:String): TextView {
        val text = TextView(context)
        text.height=40
        text.width=40
        text.id = View.generateViewId()
        text.text = label
        text.setBackgroundResource(R.drawable.circle_label)
        text.z = 20F
        text.gravity= Gravity.CENTER
        text.setTextAppearance(R.style.note_label)
        text.outlineProvider =null
        layout.addView(text)
        val constraintSet = ConstraintSet()
        constraintSet.clone(layout)
        key.let { constraintSet.connect(text.id, ConstraintSet.BOTTOM, it.id, ConstraintSet.BOTTOM, 20) }
        key.let { constraintSet.connect(text.id, ConstraintSet.LEFT, it.id, ConstraintSet.LEFT, 0) }
        key.let { constraintSet.connect(text.id, ConstraintSet.RIGHT, it.id, ConstraintSet.RIGHT, 0) }
        constraintSet.applyTo(layout)
        return text
    }

}