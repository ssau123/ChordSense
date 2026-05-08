package com.chordsense.chordapp.ui.chords


import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chordsense.chordapp.Chord
import com.chordsense.chordapp.GlobalMethods.Companion.hide
import com.chordsense.chordapp.GlobalMethods.Companion.show
import com.chordsense.chordapp.LinearLayoutManagerWrapper
import com.chordsense.chordapp.MainActivity.Companion.dp
import com.chordsense.chordapp.MainActivity.Companion.landscape
import com.chordsense.chordapp.MainActivity.Companion.screenHeight
import com.chordsense.chordapp.MainActivity.Companion.soundMenu
import com.chordsense.chordapp.MainActivity.Companion.whiteKeyHeight
import com.chordsense.chordapp.MainActivity.Companion.whiteKeyWidth
import com.chordsense.chordapp.MusicData
import com.chordsense.chordapp.MusicData.Companion.doubleVoicingOptions
import com.chordsense.chordapp.MusicData.Companion.removeOptions
import com.chordsense.chordapp.Piano
import com.chordsense.chordapp.R
import com.chordsense.chordapp.Sounds.playChord
import com.chordsense.chordapp.VoicingAdapter
import com.chordsense.chordapp.databinding.FragmentChordsBinding
import com.github.gzuliyujiang.wheelview.contract.OnWheelChangedListener
import com.github.gzuliyujiang.wheelview.widget.WheelView
import com.google.android.material.tabs.TabLayout
import java.util.Collections


class ChordsFragment : Fragment() {

private var _binding: FragmentChordsBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!
    lateinit var rootPicker: WheelView
    lateinit var typePicker: WheelView
    lateinit var extensionPicker: WheelView

    lateinit var invertUp:Button
    lateinit var invertDown:Button
    lateinit var addVoicing:Button
    lateinit var removeVoicing:Button
    lateinit var voicingAdapter:VoicingAdapter
    lateinit var chord:Chord

    companion object {

    }


        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_chords, container, false)



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dur = (4* ((60000F) / 120.toFloat())).toLong()

        val types = listOf("maj", "min", "sus2", "sus4", "dim")
        // for Chord Builder
        val majExtensions = listOf("None", "maj7", "maj9", "maj#11", "maj13", "maj7#11", "maj7(13)","add9","6" , "6/9", "6/9(#11)", "7","9","11","13","13b9", "7b13", "9#11","7#5", "7#9", "7b9#5", "7#5#9")
        val minExtensions = listOf("None", "7", "9","11","13", "7(11)", "7(13)", "add9", "6","6/9")
        val sus2Extensions = listOf("None","7","maj7","6")
        val sus4Extensions = listOf("None", "7","9","maj7", "maj9", "6","6/9")
        val dimExtensions= listOf("None","7", "7(11)", "dim7", "dim9")

        val extensionsMap = mapOf("maj" to majExtensions, "min" to minExtensions, "sus2" to sus2Extensions, "sus4" to sus4Extensions, "dim" to dimExtensions)

        chord = Chord(root = "C", type = "maj", octave=4)
        chord.setIntervals(editingVoicing = false)
        val increaseOctaveButton = view.findViewById<ImageButton>(R.id.octave_up)
        val decreaseOctaveButton = view.findViewById<ImageButton>(R.id.octave_down)
        val chordName= view.findViewById<TextView>(R.id.chord_name)
         rootPicker= view.findViewById(R.id.root_picker)
        typePicker= view.findViewById(R.id.type_picker)
        extensionPicker= view.findViewById(R.id.extension_picker)
        val soundPreset = view.findViewById<Button>(R.id.sound_preset)
        val tabMenu = view.findViewById<TabLayout>(R.id.chord_menu_tabs)
        val chordMenuTab = view.findViewById<LinearLayout>(R.id.chord_menu)
        val tonesTab = view.findViewById<ConstraintLayout>(R.id.tones_tab)
        val voicingTab = view.findViewById<ConstraintLayout>(R.id.voicing_tab)
        val voicingEditor=view.findViewById<RecyclerView>(R.id.voicing_editor)
        addVoicing = view.findViewById(R.id.voicing_add)
        removeVoicing = view.findViewById(R.id.voicing_remove)
        invertUp= view.findViewById(R.id.invert_up)
        invertDown = view.findViewById(R.id.invert_down)
        voicingAdapter = VoicingAdapter(chord.voicingList, chord,invertUp, invertDown, addVoicing, removeVoicing)
        val keyboard:ConstraintLayout =view.findViewById(R.id.keyboard)
//        voicingEditor.setLayoutManager(LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false))
        voicingEditor.setAdapter(voicingAdapter)

        val mLayoutManager: RecyclerView.LayoutManager =
            LinearLayoutManagerWrapper(context, LinearLayoutManager.HORIZONTAL, false)


        voicingEditor.setLayoutManager(mLayoutManager)
        keyboard.layoutParams.height= whiteKeyHeight+(16*dp).toInt()

        val piano = Piano(keyboard, context, "C", 61, whiteKeyWidth, keyboard.layoutParams.height-(16*dp).toInt())
        context?.let { piano.showOctaveLabels(view.findViewById(R.id.keyboard), it) }



        if (landscape) {
            chordMenuTab.layoutParams.width = (screenHeight -20*dp).toInt()+66
        }

        soundPreset.setOnClickListener {
            show(soundMenu)
        }


        chordName.setOnClickListener {
            piano.displayChord(chord)
            playChord(chord)
        }

//        play.setOnClickListener {
//            piano.displayChord(chord)
//            playChord(chord, dur)
//        }


        val simpleCallback = object : ItemTouchHelper.SimpleCallback( ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,  0) {
            var fromPosition = 0



            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ACTION_STATE_DRAG) {
                    fromPosition = viewHolder?.adapterPosition!!
                }
            }


            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val prevVoicing = chord.voicingList
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition

                println(fromPosition)
                println(toPosition)
                Collections.swap(chord.voicingList, fromPosition, toPosition)
                chord.voicing = chord.voicingList.joinToString(" ")
                voicingAdapter.notifyItemMoved(fromPosition, toPosition)
//                if (chord.octaveBass) {
//                    chord.octaveBass = false
//                    octaveBass.isChecked = false
//                }




                chord.setIntervals(editingVoicing = true)

                //new
                if (chord.voicingList[0] == chord.voicingList[1]) {
                    chord.lower()
//                    octaveBass.alpha = 0.5f
                } else {
//                    octaveBass.alpha = 1.0f

                }


                return false
            }

            override fun canDropOver(recyclerView: RecyclerView, current: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return current.itemViewType == target.itemViewType
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                try {

                    fun check(size:Int) {
                        for (i in 1 until size-1) {
                            if (chord.voicingList[i] == chord.voicingList[i+1]) {
                                println(chord.voicingList)
                                println(chord.intervals)
                                voicingEditor.findViewHolderForAdapterPosition(i+1)?.setIsRecyclable(true)
                                if (voicingAdapter.selected != null && voicingAdapter.selected!!.layoutPosition == i+1) {
                                    voicingAdapter.selected =null
                                }
                                chord.voicingList.removeAt(i+1)
                                chord.intervals.removeAt(i+1)
                                voicingAdapter.notifyItemRemoved(i+1)
                                println(chord.voicingList)
                                println(chord.intervals)
                                print(size)
                                check(size-1)

                                break
                            }
                        }
                    }

                    check(chord.size())

                    updateVoicingButtons()


                    piano.displayChord(chord)
                    playChord(chord)


                } catch (e:IndexOutOfBoundsException) {
                    decreaseOctave(chord, piano)



                }

            }



            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // No action needed on swipe
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                val clippedDx  = clip(recyclerView.width, viewHolder.itemView.left, viewHolder.itemView.right, dX)
                val clippedDy  = clip(recyclerView.height, viewHolder.itemView.top, viewHolder.itemView.bottom, dY)
                super.onChildDraw(c, recyclerView, viewHolder, clippedDx, dY, actionState, isCurrentlyActive)
            }

            private fun clip(size: Int, start: Int, end: Int, delta: Float): Float {
                val newStart = start + delta
                val newEnd = end + delta

                val oobStart = 0 - newStart
                val oobEnd = newEnd - size

                return when {
                    oobStart > 0 -> delta + oobStart
                    oobEnd > 0 -> delta - oobEnd
                    else -> delta
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(voicingEditor)


        increaseOctaveButton.setOnClickListener {
            try {
                increaseOctave(chord, piano)
            } catch( e: IndexOutOfBoundsException) {
                decreaseOctave(chord, piano)
            }
        }

        decreaseOctaveButton.setOnClickListener {
            try {
                decreaseOctave(chord, piano)
            } catch(e: IndexOutOfBoundsException) {
                increaseOctave(chord, piano)
            }
        }

        addVoicing.setOnClickListener {
            if (voicingAdapter.selected != null && voicingAdapter.interval in doubleVoicingOptions && chord.voicingList.count { it == voicingAdapter.interval } == 1) {

                val index = voicingAdapter.selected!!.layoutPosition
                val interval = chord.intervals[index]+12
                chord.intervals.add(interval)
                chord.intervals.sort()
                println(chord.intervals)
                val i = chord.intervals.indexOf(interval)
                chord.voicingList.add(i, voicingAdapter.interval)
                chord.voicing = chord.voicingList.joinToString(" ")

                voicingAdapter.notifyItemInserted(i)
                piano.displayChord(chord)
                playChord(chord)
                updateVoicingButtons()
            }
        }

        removeVoicing.setOnClickListener {
            if (voicingAdapter.selected != null && (voicingAdapter.interval in removeOptions || chord.voicingList.count { it == voicingAdapter.interval } == 2)) {
                voicingAdapter.selected!!.setIsRecyclable(true)
                val index = voicingAdapter.selected!!.layoutPosition
                chord.voicingList.removeAt(index)
                voicingAdapter.notifyItemRemoved(index)
                voicingAdapter.selected = null


                chord.voicing = chord.voicingList.joinToString(" ")
                chord.setVoicing()

                piano.displayChord(chord)
                playChord(chord)
                updateVoicingButtons()

            }
        }

        invertUp.setOnClickListener {

            if (voicingAdapter.selected != null) {
                val index = voicingAdapter.selected!!.layoutPosition

                if (index != (chord.intervals.size-1) && (chord.intervals[index]+ chord.offset+12)<=60 && !(chord.intervals.contains(
                        chord.intervals[index]+12))) {
                    val pos = chord.invert(index, 1)

                    voicingAdapter.notifyItemMoved(index, pos)

                    try {
                        piano.displayChord(chord)
                        playChord(chord)
                        updateVoicingButtons()


                    } catch(e:IndexOutOfBoundsException) {
                        val pos1 = chord.invert(pos, -1)
                        voicingAdapter.notifyItemMoved(pos, pos1)
                        piano.displayChord(chord)
                        playChord(chord)
                        updateVoicingButtons()

                    }



                }
            }

        }

        invertDown.setOnClickListener {

            if (voicingAdapter.selected != null) {
                val index = voicingAdapter.selected!!.layoutPosition

                if (chord.intervals[index]>=0 && (chord.intervals[index]+ chord.offset-12)>=0 && !(chord.intervals.contains(
                        chord.intervals[index]-12))) {
                    val pos = chord.invert(index, -1)

                    voicingAdapter.notifyItemMoved(index, pos)

                    try {
                        piano.displayChord(chord)
                        playChord(chord)
                        updateVoicingButtons()


                    } catch(e:IndexOutOfBoundsException) {
                        val pos1 = chord.invert(pos, 1)
                        voicingAdapter.notifyItemMoved(pos, pos1)
                        piano.displayChord(chord)
                        playChord(chord)
                        updateVoicingButtons()

                    }
                }
            }
        }


        rootPicker.data = MusicData.notes
        rootPicker.typeface= resources.getFont(R.font.inter_light)
        rootPicker.itemSpace=30

        typePicker.data = types
        typePicker.typeface= resources.getFont(R.font.inter_light)
        typePicker.itemSpace=30

        extensionPicker.data = majExtensions
        extensionPicker.typeface= resources.getFont(R.font.inter_light)
        extensionPicker.itemSpace=30


        rootPicker.setOnWheelChangedListener(object: OnWheelChangedListener {
            override fun onWheelScrolled(view: WheelView?, offset: Int) {
            }

            override fun onWheelSelected(view: WheelView?, position: Int) {
                if (view != null) {
                    chord.root = view.getCurrentItem()
                }
                val i = chord.offset
                chord.setOffset()
//                chord.setIntervals(editingVoicing= false)
                chord.updateSymbol()
                chordName.text = chord.name

                try {
                    piano.displayChord(chord)
                    playChord(chord)

                } catch (e:Exception) {
                    if (chord.offset<i) {
                        increaseOctave(chord, piano)
                    } else {
                        decreaseOctave(chord, piano)
                    }


                }

            }

            override fun onWheelScrollStateChanged(view: WheelView?, state: Int) {
            }

            override fun onWheelLoopFinished(view: WheelView?) {
            }

        })


        typePicker.setOnWheelChangedListener(object: OnWheelChangedListener {
            override fun onWheelScrolled(view: WheelView?, offset: Int) {
            }

            override fun onWheelSelected(view: WheelView?, position: Int) {
                chord.type = view!!.getCurrentItem()

                extensionPicker.data  = extensionsMap[chord.type]
                chord.extension = "None"
                chord.setIntervals(editingVoicing= false)
                chord.updateSymbol()
                chordName.text = chord.name
                voicingAdapter = VoicingAdapter(chord.voicingList, chord,invertUp, invertDown, addVoicing, removeVoicing)
                voicingEditor.setAdapter(voicingAdapter)

                try {
                    piano.displayChord(chord)
                    playChord(chord)
                } catch(e:IndexOutOfBoundsException) {
                    decreaseOctave(chord, piano)
                }


            }

            override fun onWheelScrollStateChanged(view: WheelView?, state: Int) {
            }

            override fun onWheelLoopFinished(view: WheelView?) {
            }

        })


        extensionPicker.setOnWheelChangedListener(object: OnWheelChangedListener {
            override fun onWheelScrolled(view: WheelView?, offset: Int) {
            }

            override fun onWheelSelected(view: WheelView?, position: Int) {
                chord.extension = view!!.getCurrentItem()
                chord.setIntervals(editingVoicing= false)
                chord.updateSymbol()
                chordName.text = chord.name
                voicingAdapter = VoicingAdapter(chord.voicingList, chord,invertUp, invertDown, addVoicing, removeVoicing)
                voicingEditor.setAdapter(voicingAdapter)
                try {
                    piano.displayChord(chord)
                    playChord(chord)

                } catch (e:Exception) {
                    decreaseOctave(chord, piano)
                }

            }

            override fun onWheelScrollStateChanged(view: WheelView?, state: Int) {
            }

            override fun onWheelLoopFinished(view: WheelView?) {
            }

        })


        tabMenu.tag = tonesTab
        tabMenu.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

                hide(tabMenu.tag as View)


                if (tab!!.position == 0) {
                    show(tonesTab)
                    tabMenu.tag = tonesTab
                } else if (tab.position == 1) {
                    show(voicingTab)
                    tabMenu.tag = voicingTab

                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        fun updateVoicingButtons() {

            if (voicingAdapter.selected != null) {
                val index = voicingAdapter.selected!!.adapterPosition

                if (voicingAdapter.interval in doubleVoicingOptions && chord.voicingList.count { it == voicingAdapter.interval } == 1) {
                    addVoicing.alpha = 1.0f

                } else {
                    addVoicing.alpha = 0.5f

                }

                if (voicingAdapter.interval in removeOptions || chord.voicingList.count { it == voicingAdapter.interval } == 2) {
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
            } else {
                addVoicing.alpha = 0.5f
                removeVoicing.alpha = 0.5f
                invertUp.alpha = 0.5f
                invertDown.alpha = 0.5f

            }


        }

        view.post {
//            keyboard.scrollTo(((piano.keys[44].x - piano.parent.width/2+ whiteKeyWidth /2).toInt()), 0)
            piano.displayChord(chord)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun decreaseOctave(chord: Chord, piano: Piano) {

        chord.octave--
        chord.setOffset()
        piano.displayChord(chord)
        playChord(chord)


    }

    fun increaseOctave(chord: Chord, piano: Piano) {
        chord.octave++
        chord.setOffset()
        piano.displayChord(chord)
        playChord(chord)

    }

    fun updateVoicingButtons() {

        if (voicingAdapter.selected != null) {
            val index = voicingAdapter.selected!!.adapterPosition

            if (voicingAdapter.interval in doubleVoicingOptions && chord.voicingList.count { it == voicingAdapter.interval } == 1) {
                addVoicing.alpha = 1.0f

            } else {
                addVoicing.alpha = 0.5f

            }

            if (voicingAdapter.interval in removeOptions || chord.voicingList.count { it == voicingAdapter.interval } == 2) {
                removeVoicing.alpha = 1.0f

            }else {
                removeVoicing.alpha = 0.5f

            }

            if ((index != (chord.intervals.size-1) && (chord.intervals[index]+ chord.offset+12)<=60) && !(chord.intervals.contains(
                    chord.intervals[index]+12))) {
                invertUp.alpha = 1.0f

            }else {
                invertUp.alpha = 0.5f

            }

            if (chord.intervals[index]>=0 && (chord.intervals[index]+ chord.offset-12)>=0 && !(chord.intervals.contains(
                    chord.intervals[index]-12))) {
                invertDown.alpha = 1.0f
            }else {
                invertDown.alpha = 0.5f

            }
        } else {
            addVoicing.alpha = 0.5f
            removeVoicing.alpha = 0.5f
            invertUp.alpha = 0.5f
            invertDown.alpha = 0.5f

        }


    }

}