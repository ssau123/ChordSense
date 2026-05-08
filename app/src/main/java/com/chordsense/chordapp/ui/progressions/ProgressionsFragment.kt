package com.chordsense.chordapp.ui.progressions


import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.ChangeBounds
import android.transition.Slide
import android.transition.TransitionManager.beginDelayedTransition
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.chordsense.chordapp.Chord
import com.chordsense.chordapp.DefaultProgressions
import com.chordsense.chordapp.FadingEdgeRecyclerView
import com.chordsense.chordapp.GlobalMethods.Companion.hide
import com.chordsense.chordapp.GlobalMethods.Companion.show
import com.chordsense.chordapp.GlobalMethods.Companion.toggleView
import com.chordsense.chordapp.MainActivity
import com.chordsense.chordapp.MainActivity.Companion.active
import com.chordsense.chordapp.MainActivity.Companion.dp
import com.chordsense.chordapp.MainActivity.Companion.fm
import com.chordsense.chordapp.MainActivity.Companion.fragment4
import com.chordsense.chordapp.MainActivity.Companion.freeSaves
import com.chordsense.chordapp.MainActivity.Companion.landscape
import com.chordsense.chordapp.MainActivity.Companion.navView
import com.chordsense.chordapp.MainActivity.Companion.premium
import com.chordsense.chordapp.MainActivity.Companion.scaleModes
import com.chordsense.chordapp.MainActivity.Companion.screenHeight
import com.chordsense.chordapp.MainActivity.Companion.screenWidth
import com.chordsense.chordapp.MainActivity.Companion.soundMenu
import com.chordsense.chordapp.MainActivity.Companion.whiteKeyHeight
import com.chordsense.chordapp.MainActivity.Companion.whiteKeyWidth
import com.chordsense.chordapp.MusicData
import com.chordsense.chordapp.MusicData.Companion.doubleVoicingOptions
import com.chordsense.chordapp.MusicData.Companion.removeOptions
import com.chordsense.chordapp.Piano
import com.chordsense.chordapp.Progression
import com.chordsense.chordapp.ProgressionAdapter
import com.chordsense.chordapp.ProgressionListAdapter
import com.chordsense.chordapp.R
import com.chordsense.chordapp.RecyclerAdapter
import com.chordsense.chordapp.Scale
import com.chordsense.chordapp.Sounds
import com.chordsense.chordapp.Sounds.playChord
import com.chordsense.chordapp.VoicingAdapter
import com.github.gzuliyujiang.wheelview.contract.OnWheelChangedListener
import com.github.gzuliyujiang.wheelview.widget.WheelView
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Collections
import java.util.stream.Collectors


class ProgressionsFragment : Fragment() {

//private var _binding: FragmentProgressionsBinding? = null
//    private val binding get() = _binding!!

    companion object {
        lateinit var activity:FragmentActivity
        lateinit var playButton:ImageButton
        lateinit var chord: Chord
        lateinit var durationSlider: SeekBar
        lateinit var progressionPanel:LinearLayout
        lateinit var chordName :TextView
        lateinit var chordName2 :TextView
        lateinit var tabMenu:TabLayout
        lateinit var progressionsLayout :ConstraintLayout
        lateinit var degreePicker: WheelView
        lateinit var typePicker: WheelView
        lateinit var extensionPicker: WheelView
        lateinit var transposeUp:ImageButton
        lateinit var transposeDown:ImageButton
        lateinit var keySelect: ConstraintLayout
        lateinit var folderButton:Button
        lateinit var saveButton :Button
        var loop = false
        lateinit var soundPreset :Button
        lateinit var cover:LinearLayout
//        lateinit var octaveBass:CheckBox
        lateinit var editChord :ImageButton
        lateinit var deleteChord:ImageButton
        lateinit var addChord:ImageButton
        lateinit var context: Context
        lateinit var savedProgressions:MutableList<String>
        lateinit var voicingEditor: RecyclerView
        lateinit var voicingAdapter: VoicingAdapter
        lateinit var imm:InputMethodManager
        lateinit var progression: Progression

        var panelHeight = 0
        var boxWidth = 0
        fun displayChord(chord: Chord) {
            activity.runOnUiThread {
                piano.displayChord(chord)
            }
        }


        fun reset() {
            activity.runOnUiThread {
                playButton.setImageResource(R.drawable.play)
                isPlaying = false
                if (progressionAdapter.selected != null) {
                    progressionAdapter.selected!!.select()
                }
                piano.clear()

            }
        }

        fun getTypes(degree:Int, mode:Int): List<String> {

            return MusicData.scaleTypes[(degree+mode)%7]
        }


        fun getExtensions(degree: Int, mode: Int): List<String> {
            return Scale.extensions[(degree + mode) % 7]
        }


        fun getSus4Extensions(options: List<String>): List<String> {
            return options.intersect(listOf("None", "7","9","maj7", "maj9", "6","6/9")).toList()
        }

        fun getSus2Extensions(options: List<String>): List<String> {
            return options.intersect(listOf("None","7","maj7","6")).toList()
        }

        var extensions = getExtensions(0, 0)
        var sus4Extensions = getSus4Extensions(extensions)
        var sus2Extensions = getSus2Extensions(extensions)
        var types = getTypes(0, 0)
        var isPlaying=false
        lateinit var piano : Piano


        var panelOverlayed=false


        fun hideKeyboard(view:View) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun showKeyboard(view:View) {
            imm.showSoftInput(view, 0)

        }
        lateinit var progressionView: RecyclerView

        lateinit var progressionAdapter: ProgressionAdapter


        fun setChordName(name:String) {
            activity.runOnUiThread {
                chordName2.text=name

            }
        }


    }

    lateinit var chordPicker:RecyclerView
    lateinit var showPanel:ImageButton
    lateinit var invertUp:Button
    lateinit var invertDown:Button
    lateinit var addVoicing:Button
    lateinit var removeVoicing:Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_progressions, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Companion.activity = requireActivity()
        Companion.context = requireContext()
        progression = Progression(Companion.activity)
        val chordSelectDone:ImageButton = view.findViewById(R.id.chord_select_done)
        val increaseOctaveButton = view.findViewById<ImageButton>(R.id.octave_up)
        val decreaseOctaveButton = view.findViewById<ImageButton>(R.id.octave_down)
        keySelect = view.findViewById(R.id.key_select)
        val keySelectText: TextView = view.findViewById(R.id.key_select_text)
        val textSize: TextView = view.findViewById(R.id.text_size)
        folderButton = view.findViewById(R.id.folder)
        saveButton = view.findViewById(R.id.save)
        val saveMenu = view.findViewById<ConstraintLayout>(R.id.save_menu)
        val cancelSave = view.findViewById<Button>(R.id.cancel_save)
        val confirmSave = view.findViewById<ImageButton>(R.id.confirm_save)
        val progressionName = view.findViewById<EditText>(R.id.progression_name)
        val progressionListView = view.findViewById<FadingEdgeRecyclerView>(R.id.progression_list)
        val backButton = view.findViewById<Button>(R.id.back)
        val deleteProgression = view.findViewById<ImageButton>(R.id.delete_progression)
        val openProgression = view.findViewById<ImageButton>(R.id.open_progression)
        transposeUp = view.findViewById<ImageButton>(R.id.transpose_up)
        transposeDown = view.findViewById<ImageButton>(R.id.transpose_down)
        val keyMenu = view.findViewById<ConstraintLayout>(R.id.key_menu)
        val keySelectDoneButton = view.findViewById<ImageButton>(R.id.key_select_done)
        val chordMenu1 = view.findViewById<ConstraintLayout>(R.id.chord_menu_container)
//        val chordMenuDoneButton = view.findViewById<ImageButton>(R.id.chord_menu_done)
        val progressionsMenu = view.findViewById<ConstraintLayout>(R.id.progressions_menu)
        tabMenu = view.findViewById<TabLayout>(R.id.chord_menu_tabs)
        val chordMenu = view.findViewById<LinearLayout>(R.id.chord_menu)
        val exitChordMenu = view.findViewById<ImageButton>(R.id.exit_chord_menu)
        val chordMenuCurrentTab = view.findViewById<ConstraintLayout>(R.id.chord_menu_tab)
        val bpmSelect: SeekBar = view.findViewById(R.id.bpm)
        durationSlider = view.findViewById(R.id.duration_slider)
        val duration:TextView = view.findViewById(R.id.duration)
        val bpmLabel = view.findViewById<TextView>(R.id.bpm_label)
//        val clearProgression = view.findViewById<Button>(R.id.clear_progression)
        val clearProgression = view.findViewById<ImageButton>(R.id.clear_progression)

        editChord = view.findViewById<ImageButton>(R.id.edit_chord)
        deleteChord = view.findViewById<ImageButton>(R.id.delete_chord)
        val loopButton = view.findViewById<ImageButton>(R.id.loop)
        playButton = view.findViewById<ImageButton>(R.id.play)
        val options: Button = view.findViewById(R.id.options)
        val optionsMenu = view.findViewById<ConstraintLayout>(R.id.options_menu)
        val closeOptionsMenu = view.findViewById<Button>(R.id.close_options_menu)
        addChord = view.findViewById<ImageButton>(R.id.add_chord)
        progressionsLayout = view.findViewById<ConstraintLayout>(R.id.progressions_layout)
        var typedValue = TypedValue()
        context?.theme?.resolveAttribute(android.R.attr.colorBackground, typedValue, true)
        val rootPicker :WheelView = view.findViewById(R.id.key_root_picker)
        val modePicker :WheelView = view.findViewById(R.id.key_mode_picker)
//        chordPicker = view.findViewById<RecyclerView>(R.id.chord_picker)
        showPanel = view.findViewById(R.id.show_panel)
        loopButton.drawable.alpha=100
        cover = view.findViewById(R.id.cover)
        degreePicker = view.findViewById(R.id.root_picker)
        typePicker = view.findViewById(R.id.type_picker)
        extensionPicker = view.findViewById(R.id.extension_picker)
        val tonesTab = view.findViewById<ConstraintLayout>(R.id.tones_tab)
        val voicingTab = view.findViewById<ConstraintLayout>(R.id.voicing_tab)
        val articulationTab = view.findViewById<ConstraintLayout>(R.id.articulation_tab)
        val rewind:ImageButton = view.findViewById(R.id.rewind)
        chordName = view.findViewById(R.id.chord_name)
        chordName2 = view.findViewById(R.id.chord_name_2)
        soundPreset = view.findViewById(R.id.sound_preset)
        addVoicing = view.findViewById(R.id.voicing_add)
        removeVoicing = view.findViewById(R.id.voicing_remove)
        invertUp= view.findViewById(R.id.invert_up)
        invertDown = view.findViewById(R.id.invert_down)
        chord = Chord()

//        octaveBass = view.findViewById<CheckBox>(R.id.octave_bass_checkbox)
        val progressionContainer: ConstraintLayout = view.findViewById(R.id.chord_menu_container)
        voicingEditor =view.findViewById(R.id.voicing_editor)
        val dimBg = view.findViewById<View>(R.id.background)
        progressionPanel = view.findViewById(R.id.progression_panel)
        val enterTitle= view.findViewById<TextView>(R.id.enter_title)
        val closePremium = view.findViewById<Button>(R.id.close_premium)
        val closeKeyMenu = view.findViewById<Button>(R.id.close_key_menu)

        val purchasePremium = view.findViewById<View>(R.id.purchase_premium)
        imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager


        val keyboard = view.findViewById<ConstraintLayout>(R.id.keyboard)

        val kb = view.findViewById<ConstraintLayout>(R.id.keyboard)



        progressionsMenu.layoutParams.height = progressionsLayout.layoutParams.height

        if (landscape) {
            panelHeight = (screenWidth*0.16).toInt()
            boxWidth = ((screenHeight-40*dp)/4 - 8 * dp).toInt()
            progressionPanel.layoutParams.width = (screenHeight-20*dp + boxWidth +8*dp).toInt()
            progressionPanel.layoutParams.height = panelHeight
            val progressionPanel1:LinearLayout = view.findViewById(R.id.progression_panel1)
            progressionPanel1.layoutParams.width = (screenHeight-20*dp + boxWidth +8*dp).toInt()
            progressionPanel1.layoutParams.height = panelHeight
            chordMenuCurrentTab.layoutParams.width = (screenHeight -20*dp).toInt()+66
//            chordPicker.layoutParams.width = (screenHeight -20*dp).toInt()+66
            view.findViewById<LinearLayout>(R.id.chord_menu).layoutParams.width = (screenHeight -20*dp).toInt()+66
            view.findViewById<LinearLayout>(R.id.chord_menu).invalidate()
            optionsMenu.getChildAt(0).layoutParams.width = (screenHeight * 0.96).toInt()
            saveMenu.getChildAt(0).layoutParams.width = screenHeight
        } else {

            panelHeight = (screenHeight*0.16).toInt()
            boxWidth = ((screenWidth-40*dp)/4 - 8 * dp).toInt()

        }

        kb.layoutParams.height= whiteKeyHeight+(16*dp).toInt()




        piano = Piano(kb, context, "C", 88, whiteKeyWidth, kb.layoutParams.height-(16*dp).toInt())
        context?.let { piano.showOctaveLabels(view.findViewById(R.id.keyboard), it) }
        val constraints = ConstraintSet()
        constraints.clone(chordMenu1)




        savedProgressions = mutableListOf()






        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER_VERTICAL
        }

//        octaveBass.setOnClickListener{ v ->
//            if ((v as CompoundButton).isChecked) {
//                chord.intervals[0] = chord.intervals[0]-12
//                chord.octaveBass = true
//            } else {
//                chord.intervals[0] = chord.intervals[0]+12
//                chord.octaveBass = false
//
//            }
//            displayChord(chord)
//
//        }


        voicingAdapter = VoicingAdapter(listOf(),chord, invertUp, invertDown, addVoicing, removeVoicing)
        voicingAdapter.setHasStableIds(true)
        voicingEditor.recycledViewPool.setMaxRecycledViews(1,0)
        voicingEditor.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false))
        voicingEditor.setAdapter(voicingAdapter)


        val simpleCallback1 = object : ItemTouchHelper.SimpleCallback( ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,  0) {
            var fromPosition = 0



            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ACTION_STATE_DRAG) {
                    fromPosition = viewHolder?.adapterPosition!!
                }
            }


            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition

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
                                voicingEditor.findViewHolderForAdapterPosition(i+1)?.setIsRecyclable(true)
                                if (voicingAdapter.selected != null && voicingAdapter.selected!!.layoutPosition == i+1) {
                                    voicingAdapter.selected =null
                                }
                                chord.voicingList.removeAt(i+1)
                                chord.intervals.removeAt(i+1)
                                voicingAdapter.notifyItemRemoved(i+1)
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
//                    Collections.swap(chord.voicingList, viewHolder.adapterPosition, fromPosition)
//                    chord.voicing = chord.voicingList.joinToString(" ")
//                    voicingAdapter.notifyItemMoved(viewHolder.adapterPosition, fromPosition)
//                    chord.setIntervals(editingVoicing = true)
//                    piano.displayChord(chord)
//                    playChord(chord)
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

        val itemTouchHelper1 = ItemTouchHelper(simpleCallback1)
        itemTouchHelper1.attachToRecyclerView(voicingEditor)


        progressionView = view.findViewById(R.id.progression_view)

        progressionAdapter = ProgressionAdapter(progression.chords) { updateMenu(chord) }
        progressionAdapter.setHasStableIds(true)




        progressionView.adapter = progressionAdapter

        progressionView.recycledViewPool.setMaxRecycledViews(1,0)
        progressionView.layoutParams = params
        val simpleCallback = object : ItemTouchHelper.SimpleCallback( 0,   0) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                Collections.swap(progression.chords, fromPosition, toPosition)
                recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)
                return false
            }

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(LEFT or RIGHT, START or END)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val fromPosition = viewHolder.adapterPosition

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

        progressionView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                return isPlaying
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                // Do nothing, effectively disabling touch scrolling when flag is false
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                // No-op
            }
        })



        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(progressionView)

        keySelect.setOnClickListener {
            if (!isPlaying) {
                show(keyMenu)
            }
        }

        closeKeyMenu.setOnClickListener {
            hide(keyMenu)
        }


        addVoicing.setOnClickListener {
            if (voicingAdapter.selected != null && voicingAdapter.interval in doubleVoicingOptions && chord.voicingList.count { it == voicingAdapter.interval } == 1) {

                val index = voicingAdapter.selected!!.layoutPosition
                val interval = chord.intervals[index]+12
                chord.intervals.add(interval)
                chord.intervals.sort()
                val i = chord.intervals.indexOf(interval)
                chord.voicingList.add(i, voicingAdapter.interval)
                chord.voicing = chord.voicingList.joinToString(" ")

                voicingAdapter.notifyItemInserted(i)
                displayChord(chord)
                playChord(chord)
                updateVoicingButtons()
            }
        }

        removeVoicing.setOnClickListener {
            if (voicingAdapter.selected != null && (voicingAdapter.interval in removeOptions || chord.voicingList.count { it == voicingAdapter.interval } == 2)) {
                voicingAdapter.selected!!.setIsRecyclable(true)
                val index = voicingAdapter.selected!!.layoutPosition
                voicingEditor
                chord.voicingList.removeAt(index)
                voicingAdapter.notifyItemRemoved(index)
                voicingAdapter.selected = null


                chord.voicing = chord.voicingList.joinToString(" ")
                chord.setVoicing()

                displayChord(chord)
                playChord(chord)
                updateVoicingButtons()

            }
        }

        invertUp.setOnClickListener {

            if (voicingAdapter.selected != null) {
                val index = voicingAdapter.selected!!.layoutPosition

                if (index != (chord.intervals.size-1) && (chord.intervals[index]+chord.offset+12)<=60 && !(chord.intervals.contains(chord.intervals[index]+12))) {
                    val pos = chord.invert(index, 1)

                    voicingAdapter.notifyItemMoved(index, pos)

                    try {
                        displayChord(chord)
                        playChord(chord)
                        updateVoicingButtons()


                    } catch(e:IndexOutOfBoundsException) {
                        val pos1 = chord.invert(pos, -1)
                        voicingAdapter.notifyItemMoved(pos, pos1)
                        displayChord(chord)
                        playChord(chord)
                        updateVoicingButtons()

                    }



                }
            }

        }

        invertDown.setOnClickListener {

            if (voicingAdapter.selected != null) {
                val index = voicingAdapter.selected!!.layoutPosition

                if (chord.intervals[index]>=0 && (chord.intervals[index]+chord.offset-12)>=0 && !(chord.intervals.contains(chord.intervals[index]-12))) {
                    val pos = chord.invert(index, -1)

                    voicingAdapter.notifyItemMoved(index, pos)

                    try {
                        displayChord(chord)
                        playChord(chord)
                        updateVoicingButtons()


                    } catch(e:IndexOutOfBoundsException) {
                        val pos1 = chord.invert(pos, 1)
                        voicingAdapter.notifyItemMoved(pos, pos1)
                        displayChord(chord)
                        playChord(chord)
                        updateVoicingButtons()

                    }
                }
            }
        }

        showPanel.setOnClickListener {

            panelOverlayed =!panelOverlayed

            if (panelOverlayed) {
                ObjectAnimator.ofFloat(showPanel, "rotation", 0f, 540f).start();

                var params = progressionPanel.layoutParams as ConstraintLayout.LayoutParams
                params.setMargins(0, (90*dp).toInt(),0,0)
                progressionPanel.layoutParams = params
                show(progressionPanel, Slide(Gravity.TOP), 150)
                show(view.findViewById<View>(R.id.dim_background))
            } else {
                ObjectAnimator.ofFloat(showPanel, "rotation", 0f, -360f).start();

                var params = progressionPanel.layoutParams as ConstraintLayout.LayoutParams
                params.setMargins(0, (56*dp).toInt(),0,0)
                progressionPanel.layoutParams = params
                hide(chordSelectDone)
                hide(progressionPanel, Slide(Gravity.TOP), 150)
                hide(view.findViewById<View>(R.id.dim_background))
            }


        }

        chordSelectDone.setOnClickListener {
            show(showPanel)
            val params = progressionPanel.layoutParams as ConstraintLayout.LayoutParams
            params.setMargins(0, (56*dp).toInt(),0,0)
            progressionPanel.layoutParams = params
            hide(chordSelectDone)
            hide(progressionPanel, Slide(Gravity.BOTTOM), 150)
            hide(view.findViewById<View>(R.id.dim_background))

        }


        val recyclerAdapter = RecyclerAdapter(progression.chordNames)


        rootPicker.data = MusicData.notes

        if (scaleModes) {
            modePicker.data = MusicData.modes
        } else {
            keySelectText.text="C Major"
            modePicker.data = MusicData.scales
        }

        deleteChord.drawable.alpha=100
        editChord.drawable.alpha=100
        rootPicker.typeface= resources.getFont(R.font.inter_light)
        modePicker.typeface= resources.getFont(R.font.inter_light)
        rootPicker.itemSpace=40
        modePicker.itemSpace=40

        keySelectDoneButton.setOnClickListener {
            Scale.root=rootPicker.getCurrentItem()
            Scale.mode=modePicker.getCurrentItem()
            Scale.modeNum = modePicker.currentPosition
            keySelectText.text = Scale.root + " " + Scale.mode
            textSize.text = "G#" + " " + Scale.mode
            Scale.setScale()

            hide(keyMenu)
            val degrees = arrayOf("i", "ii", "iii", "iv", "v", "vi", "vii")
            var i = 0
            for (chord in progression.chords) {
                var numeral = ""
                if (Scale.notes.indexOf(chord.root) != -1) {
                    numeral = degrees[Scale.notes.indexOf(chord.root)]
                    if (chord.type == "maj") {
                        numeral = numeral.uppercase()
                    } else if (chord.type == "dim") {
                        numeral = "$numeral°"
                    }
                } else {
                    numeral = degrees[Scale.notes.indexOf(MusicData.notes[(MusicData.notes.indexOf(chord.root)+1)%12])]
                    if (chord.type == "maj") {
                        numeral = numeral.uppercase()
                    } else if (chord.type == "dim") {
                        numeral = "$numeral°"
                    }
                    numeral = "b$numeral"
                }

                chord.numeral = numeral
                (progressionView.findViewHolderForAdapterPosition(i) as ProgressionAdapter.ViewHolder?)?.setNumeral()
                i++
            }


        }

        soundPreset.setOnClickListener {
            if (!isPlaying)
                show(soundMenu)
        }





        transposeUp.setOnClickListener {

            if (!isPlaying) {
                if (progression.transposeUp()) {
                    val pos = (MusicData.notes.indexOf(Scale.root)+1)%12
                    Scale.root = MusicData.notes[(MusicData.notes.indexOf(Scale.root)+1)%12]
                    rootPicker.scrollTo(pos)
                    keySelectText.text = Scale.root + " " + Scale.mode
                    Scale.setScale()
                    for (i in 0 until progression.size()) {
                        (progressionView.findViewHolderForAdapterPosition(i) as ProgressionAdapter.ViewHolder?)?.setRoot()
                    }

                    if (progressionAdapter.selected != null) {
                        displayChord((progressionAdapter).selectedChord)
                    }

                }
            }
        }

        transposeDown.setOnClickListener {
            if (!isPlaying) {
                if (progression.transposeDown()) {
                    val pos = (MusicData.notes.indexOf(Scale.root)-1).mod(12)
                    Scale.root = MusicData.notes[pos]
                    rootPicker.scrollTo(pos)
                    keySelectText.text = Scale.root + " " + Scale.mode
                    Scale.setScale()

                    for (i in 0 until progression.size()) {
                        (progressionView.findViewHolderForAdapterPosition(i) as ProgressionAdapter.ViewHolder?)?.setRoot()
                    }
                    if (progressionAdapter.selected != null) {
                        displayChord((progressionAdapter).selectedChord)
                    }

                }
            }



        }

        addChord.setOnClickListener {


            if (!isPlaying) {
                val chord = Chord(
                    root = Scale.root,
                    type = MusicData.scaleTypes[Scale.modeNum][0],
                    extension = "None",
                    numeral = Scale.degrees[0].substringBefore(" "),
                    degree = 0,
                    octave = 4,
                    voicing = "Root Position"

                )
                chord.setIntervals()
                chord.updateSymbol()
                Companion.chord = chord

                deleteChord.drawable.alpha=255
                editChord.drawable.alpha=255
                transposeUp.drawable.alpha=255
                transposeDown.drawable.alpha=255

                // ADD CHORD TO PROGRESSION
                if (progressionAdapter.selected !=null) {
                    progression.add((progressionAdapter).selected!!.layoutPosition+1, chord)
                    progressionAdapter.notifyItemInserted((progressionAdapter).selected!!.layoutPosition+1)
                    recyclerAdapter.notifyItemInserted((progressionAdapter).selected!!.layoutPosition+1)

                } else {
                    progression.add(chord)
                    progressionAdapter.notifyItemInserted(progression.size()-1)
                    recyclerAdapter.notifyItemInserted(progression.size()-1)

                }
                updateMenu(chord)
                Handler(Looper.getMainLooper()).postDelayed({ progressionView.findViewHolderForAdapterPosition(
                    progression.indexOf(chord))?.itemView?.performClick() }, 1)

                Handler(Looper.getMainLooper()).postDelayed({
                    if (landscape) {
                        show(chordMenu)
                        show(increaseOctaveButton)
                        show(decreaseOctaveButton)

                        show(showPanel)
                        show(dimBg)
                        hide(progressionPanel)
                        show(exitChordMenu)
                        val constraintSet = ConstraintSet()
                        constraintSet.clone(progressionsLayout)
                        constraintSet.clear(keyboard.id, ConstraintSet.BOTTOM)
                        constraintSet.connect(keyboard.id, ConstraintSet.BOTTOM, progressionsLayout.id, ConstraintSet.BOTTOM, (10*dp).toInt())
                        val transition = ChangeBounds()
                        transition.setDuration(200)
                        beginDelayedTransition(progressionsLayout, transition)
                        constraintSet.applyTo(progressionsLayout)


                    } else {
                        show(exitChordMenu)

                        show(chordMenu)
                        show(dimBg)
                        val constraintSet = ConstraintSet()
                        constraintSet.clone(chordMenu1)
                        constraintSet.clear(progressionPanel.id, ConstraintSet.TOP)
                        constraintSet.connect(progressionPanel.id, ConstraintSet.BOTTOM, R.id.chord_menu, ConstraintSet.TOP, 0)
                        constraintSet.clear(keyboard.id, ConstraintSet.BOTTOM)
                        constraintSet.connect(keyboard.id, ConstraintSet.TOP, R.id.chord_menu, ConstraintSet.BOTTOM, (0*dp).toInt())
                        val transition = ChangeBounds()
                        transition.setDuration(200)
                        beginDelayedTransition(chordMenu1, transition)
                        constraintSet.applyTo(chordMenu1)

                    } }, 500)


            }

        }

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



        editChord.setOnClickListener {

            if (!isPlaying) {
                if (progressionAdapter.selected !=null) {
                    updateMenu(progressionAdapter.selectedChord)

                    if (landscape) {
                        show(chordMenu)
                        show(showPanel)
                        hide(progressionPanel)
                        show(dimBg)
                        show(exitChordMenu)
                        show(increaseOctaveButton)
                        show(decreaseOctaveButton)
                        val constraintSet = ConstraintSet()
                        constraintSet.clone(progressionsLayout)
                        constraintSet.clear(keyboard.id, ConstraintSet.BOTTOM)
                        constraintSet.connect(keyboard.id, ConstraintSet.BOTTOM, progressionsLayout.id, ConstraintSet.BOTTOM, (10*dp).toInt())
                        val transition = ChangeBounds()
                        transition.setDuration(200)
                        beginDelayedTransition(progressionsLayout, transition)
                        constraintSet.applyTo(progressionsLayout)

                    } else {
                        show(chordMenu)

//                        show(chordMenu,Fade(), 300)
                        show(dimBg)
                        show(exitChordMenu)
                        val constraintSet = ConstraintSet()
                        constraintSet.clone(chordMenu1)
                        constraintSet.clear(progressionPanel.id, ConstraintSet.TOP)

                        constraintSet.clear(keyboard.id, ConstraintSet.BOTTOM)
                        constraintSet.connect(keyboard.id, ConstraintSet.TOP, R.id.chord_menu, ConstraintSet.BOTTOM, (0*dp).toInt())

                        constraintSet.connect(progressionPanel.id, ConstraintSet.BOTTOM, R.id.chord_menu, ConstraintSet.TOP, 0)
                        val transition = ChangeBounds()

                        transition.setDuration(200)
                        beginDelayedTransition(chordMenu1, transition)
                        constraintSet.applyTo(chordMenu1)

                    }


                }
            }

        }

        deleteChord.setOnClickListener {

            if (!isPlaying) {
                if (progressionAdapter.selected !=null) {
                    (progressionAdapter.selected!!)
                        .setIsRecyclable(true)

                    progression.removeAt(progressionAdapter.selected!!.layoutPosition)
                    (progressionAdapter).notifyItemRemoved(progressionAdapter.selected!!.layoutPosition)
                    (progressionAdapter).selected  = null
                    piano.clear()

                    deleteChord.drawable.alpha=100
                    editChord.drawable.alpha=100

                    if (progression.size() == 0) {
                        transposeUp.drawable.alpha=100
                        transposeDown.drawable.alpha=100
                    }
                }
            }
        }


        chordName.setOnClickListener {
            playChord(chord)

        }

        var qqq:Thread = progression.createThread(0, piano, progressionView)


        playButton.setOnClickListener {
            if (progression.size()>0) {

                if (isPlaying) {
                    playButton.setImageResource(R.drawable.play)
                    isPlaying = false
                    Sounds.sp.autoPause()
//                    Sounds.stop()
                    qqq.interrupt()
//                    showEditor()

                } else {
                    playButton.setImageResource(R.drawable.stop)
                    isPlaying = true
                    if (progressionAdapter.selected !=null) {
                        qqq = progression.createThread(chord.position, piano, progressionView)

                    } else {
                        qqq = progression.createThread(0, piano, progressionView)

                    }
//                    hideEditor()
                    Handler(Looper.getMainLooper()).postDelayed({ qqq.start() }, 5)

                }
            }
        }

        rewind.setOnClickListener {

            if (isPlaying) {
                qqq.interrupt()

                qqq = progression.createThread(0, piano, progressionView)
                Handler(Looper.getMainLooper()).postDelayed({ qqq.start() }, 10)



            }
        }

        loopButton.setOnClickListener {
            loop = !loop
            if (loop) {
                loopButton.drawable.alpha=255


            } else {
                loopButton.drawable.alpha=100

            }
        }

        options.setOnClickListener {
            if (!isPlaying) {
                show(optionsMenu)

            }
        }
        closeOptionsMenu.setOnClickListener {
            toggleView(optionsMenu)
        }

        bpmSelect.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, p1: Int, p2: Boolean) {
                if (seekBar != null) {
                    progression.bpm = seekBar.progress * 2 + 80
                }
                bpmLabel.text = "BPM: " + progression.bpm
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        durationSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, p1: Int, p2: Boolean) {
                if (seekBar != null) {
                    chord.duration = (seekBar.progress/2F)
                }
                duration.text = "" + chord.duration + " Beats"

//                duration.text = "" + chord.duration.toString().replace(".5", "½").replace(".0", "").replace("0.", "") + " Beats"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        folderButton.setOnClickListener {
            if (!isPlaying)
                show(progressionsMenu)


        }

        backButton.setOnClickListener {
            hide(progressionsMenu)


        }

        closePremium.setOnClickListener {
            hide(purchasePremium)
        }


        val continueButton: Button = view.findViewById(R.id.purchase)
        continueButton.setOnClickListener {
            active.let { fm.beginTransaction().setCustomAnimations(R.animator.enter, R.animator.fade_out).hide(it).show(fragment4!!).commit() }
            active = fragment4!!
            navView.menu.getItem(3).isChecked = true
            hide(purchasePremium)
        }


        saveButton.setOnClickListener {
            if (!isPlaying) {
                if (premium || freeSaves > 0) {
                    show(saveMenu)
                } else {
                    show(purchasePremium)
                }

            }

        }

        cancelSave.setOnClickListener {
            if (saveMenu.isVisible) {
                imm.hideSoftInputFromWindow(view.windowToken, 0)

                hide(saveMenu)
                progressionName.setText("")
                show(enterTitle)
                if (landscape) {
                    val saveLayout :ConstraintLayout = view.findViewById(R.id.save_layout)
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(saveLayout)
                    constraintSet.clear(progressionName.id, ConstraintSet.BOTTOM)
                    constraintSet.clear(progressionName.id, ConstraintSet.TOP)
                    constraintSet.connect(progressionName.id, ConstraintSet.TOP, R.id.save_layout, ConstraintSet.TOP )
                    constraintSet.connect(progressionName.id, ConstraintSet.BOTTOM, R.id.save_layout, ConstraintSet.BOTTOM )
//                    val transition = ChangeBounds()
//                    transition.setDuration(150)
//                    beginDelayedTransition(saveLayout, transition)
                    constraintSet.applyTo(saveLayout)
                }
            } else {
                hide(purchasePremium)

            }



        }


//        if (premium) {
//            for (i in DefaultProgressions.progressions) {
//                savedProgressions.add(i.key)
//            }
//        } else {
//            for (i in DefaultProgressions.freeProgressions) {
//                savedProgressions.add(i.key)
//            }
//        }

        for (i in DefaultProgressions.progressions) {
            savedProgressions.add(i.key)
        }



        for (i in MainActivity.sharedPref.all) {
            if (i.key.length>12) {
                if (i.key.substring(0, 12) == "Progression:") {
                    savedProgressions.add(i.key.toString())

                }
            }
        }

        val label:View = view.findViewById(R.id.progressions_label)

        progressionListView.addOnScrollListener(object : OnScrollListener() {
            var visible = true

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if (recyclerView.computeVerticalScrollOffset()> 36*dp) {
                    if (dy>0 && visible) {
                        visible = false
                        hide(label, Slide(Gravity.TOP), 400)

                    } else if (dy<0 && !visible){
                        visible = true
                        show(label, Slide(Gravity.TOP), 400)

                    }
                }


            }


        })

        val progressionListAdapter = ProgressionListAdapter(requireActivity(), savedProgressions, deleteProgression)
        progressionListAdapter.setHasStableIds(true)

        progressionListView.adapter = progressionListAdapter
        progressionListView.setItemViewCacheSize(4)
        progressionName.addTextChangedListener {

            hide(enterTitle)
//            if (landscape) {
//
//            }

        }

//        progressionName.setOnClickListener {
//            if (landscape) {
//
//                val saveLayout :ConstraintLayout = view.findViewById(R.id.save_layout)
//                val constraintSet = ConstraintSet()
//                constraintSet.clone(saveLayout)
//                constraintSet.clear(progressionName.id, ConstraintSet.BOTTOM)
//                constraintSet.clear(progressionName.id, ConstraintSet.TOP)
//                constraintSet.connect(progressionName.id, ConstraintSet.TOP, R.id.top1, ConstraintSet.TOP )
//                constraintSet.connect(progressionName.id, ConstraintSet.BOTTOM, R.id.top1, ConstraintSet.BOTTOM )
//                val transition = ChangeBounds()
//                transition.setDuration(150)
//                beginDelayedTransition(saveLayout, transition)
//                constraintSet.applyTo(saveLayout)
//            }
//        }

        progressionName.setOnFocusChangeListener { _, _ ->
            if (landscape) {
                val saveLayout: ConstraintLayout = view.findViewById(R.id.save_layout)
                val constraintSet = ConstraintSet()
                constraintSet.clone(saveLayout)
                constraintSet.clear(progressionName.id, ConstraintSet.BOTTOM)
                constraintSet.clear(progressionName.id, ConstraintSet.TOP)
                constraintSet.connect(progressionName.id, ConstraintSet.TOP, R.id.top1, ConstraintSet.TOP)
                constraintSet.connect(progressionName.id, ConstraintSet.BOTTOM, R.id.top1, ConstraintSet.BOTTOM)
                val transition = ChangeBounds()
                transition.setDuration(150)
                beginDelayedTransition(saveLayout, transition)
                constraintSet.applyTo(saveLayout)
            }
        }



//        progressionName.onEditorAction(EditorInfo.IME_ACTION_DONE)

        confirmSave.setOnClickListener {
            progression.name = progressionName.text.toString().trim()
            if (progression.size() > 0) {
                val gson = Gson()

                    var list:MutableList<String> = mutableListOf()
                    for (i in 0 until progression.size()) {
                        progression.chords[i].position = i
                        val json = gson.toJson( progression.chords[i])
                        list.add(json)
                    }
                    var set = list.toSet()
                    var a = list.stream().collect(Collectors.joining("','", "'", "'"))
                    a = a.replace("\"", "%")
                    a = a.replace("'", "\"")
                    a = a.replace("%", "'")
                    println(a)



                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val current = LocalDateTime.now().format(formatter)

                val progressionId = listOf("Progression", progression.name, Scale.root,
                    Scale.mode, progression.bpm,current,"fav_0").joinToString(":")
                MainActivity.sharedPref.edit().putStringSet(progressionId, set).apply()
                var exists = false
                for (i in savedProgressions) {
                    if (i.contains(":"+ progression.name+":")) {
                        savedProgressions[savedProgressions.indexOf(i)] = progressionId

                        progressionListAdapter.notifyItemChanged(savedProgressions.indexOf(i))
                        exists = true
                        break

                    }
                }
                if(!exists) {
                    savedProgressions.add(progressionId)
                    progressionListAdapter.notifyItemInserted(savedProgressions.size-1)
                }


                hide(saveMenu)
                if (landscape) {
                    val saveLayout :ConstraintLayout = view.findViewById(R.id.save_layout)
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(saveLayout)
                    constraintSet.clear(progressionName.id, ConstraintSet.BOTTOM)
                    constraintSet.clear(progressionName.id, ConstraintSet.TOP)
                    constraintSet.connect(progressionName.id, ConstraintSet.TOP, R.id.save_layout, ConstraintSet.TOP )
                    constraintSet.connect(progressionName.id, ConstraintSet.BOTTOM, R.id.save_layout, ConstraintSet.BOTTOM )
//                    val transition = ChangeBounds()
//                    transition.setDuration(150)
//                    beginDelayedTransition(saveLayout, transition)
                    constraintSet.applyTo(saveLayout)
                }
                imm.hideSoftInputFromWindow(view.windowToken, 0)

            }

        }




        openProgression.setOnClickListener {
            if ((progressionListView.adapter as ProgressionListAdapter).selected!=null) {
                toggleView(progressionsMenu)
                val gson = Gson()

                val progressionData :List<String>
                if (progressionListAdapter.data[0] == "DefaultProgression"|| progressionListAdapter.data[0] == "PremiumProgression") {
                    progressionData = DefaultProgressions.progressions[progressionListAdapter.id]!!

                } else {
                    val strSet = MainActivity.sharedPref.getStringSet((progressionListAdapter).id,null )
                    progressionData = strSet!!.toList()

                }

                clearProgression()
                for (i in progressionData) {
                    val chord: Chord = gson.fromJson(i, Chord::class.java)
                    progression.add(chord)
                }
                progression.sort()
                progression.getChordNames()
                progressionAdapter.notifyDataSetChanged()
                recyclerAdapter.notifyDataSetChanged()
                bpmSelect.progress = (progressionListAdapter.data[4].toInt()-80)/2
                Scale.root = progressionListAdapter.data[2]
                Scale.mode = progressionListAdapter.data[3]
                Scale.setScale()
                rootPicker.scrollTo(MusicData.notes.indexOf(Scale.root))

                if (Scale.mode == "Major" && scaleModes) {
                    modePicker.scrollTo(0)

                } else if (Scale.mode == "Minor" && scaleModes) {
                    modePicker.scrollTo(5)

                } else {
                    modePicker.scrollTo(MusicData.modes.indexOf(Scale.mode))

                }

                keySelectText.text = Scale.root + " " + Scale.mode
                textSize.text = "G#" + " " + Scale.mode
                transposeUp.drawable.alpha=255
                transposeDown.drawable.alpha=255

            }
        }

        deleteProgression.setOnClickListener {
            if ((progressionListAdapter).selected != null && progressionListAdapter.data[0] != "DefaultProgression" && progressionListAdapter.data[0] != "PremiumProgression") {
                MainActivity.sharedPref.edit().remove(progressionListAdapter.id).apply()
                val removeIndex = savedProgressions.indexOf(progressionListAdapter.id)
                savedProgressions.removeAt(removeIndex)
                progressionListAdapter.notifyItemRemoved(removeIndex)
            }
        }

        degreePicker.data = Scale.degrees
        degreePicker.typeface= resources.getFont(R.font.inter_light)
        degreePicker.itemSpace=30
        degreePicker.setOnWheelChangedListener(object:OnWheelChangedListener {
            override fun onWheelScrolled(view: WheelView?, offset: Int) {
            }

            override fun onWheelSelected(view: WheelView?, position: Int) {
                val i = chord.offset
                chord.root = Scale.notes[position]
                chord.setOffset()
                chord.degree = position
                chord.numeral = (view!!.getCurrentItem() as String).substringBefore(" ")

                extensions = getExtensions(chord.degree, Scale.modeNum)
                sus4Extensions = getSus4Extensions(extensions)
                sus2Extensions = getSus2Extensions(extensions)
                types = getTypes(chord.degree, Scale.modeNum)
                typePicker.data = types
                extensionPicker.data = extensions
                chord.extension = "None"
                chord.type = types[0]
                chord.setIntervals(editingVoicing= false)
                chord.updateSymbol()
                chordName.text = chord.name
                update(progression.indexOf(chord))
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


        typePicker.data = getTypes(0,0)
        typePicker.typeface= resources.getFont(R.font.inter_light)
        typePicker.itemSpace=30
        typePicker.setOnWheelChangedListener(object:OnWheelChangedListener {
            override fun onWheelScrolled(view: WheelView?, offset: Int) {
            }

            override fun onWheelSelected(view: WheelView?, position: Int) {
                chord.type = view!!.getCurrentItem()

                if (chord.type == "sus2") {
                    extensionPicker.data = sus2Extensions
                } else if (chord.type == "sus4") {
                    extensionPicker.data = sus4Extensions
                } else {
                    extensionPicker.data = extensions
                }

                val pos = extensionPicker.data.indexOf(chord.extension)
                if (pos != -1) {
                    extensionPicker.scrollTo(pos)
                } else {
                    chord.extension = "None"
                }

                chord.setIntervals(editingVoicing= false)
                chord.updateSymbol()
                chordName.text = chord.name
                update(progression.indexOf(chord))

                try {
                    piano.displayChord(chord)
                    playChord(chord)
                } catch(e:Exception) {

                }

            }

            override fun onWheelScrollStateChanged(view: WheelView?, state: Int) {
            }

            override fun onWheelLoopFinished(view: WheelView?) {
            }

        })


        extensionPicker.data = getExtensions(0,0)
        extensionPicker.typeface= resources.getFont(R.font.inter_light)
        extensionPicker.itemSpace=30
        extensionPicker.setOnWheelChangedListener(object:OnWheelChangedListener {
            override fun onWheelScrolled(view: WheelView?, offset: Int) {
            }

            override fun onWheelSelected(view: WheelView?, position: Int) {
                chord.extension = view!!.getCurrentItem()
                chord.setIntervals(editingVoicing= false)
                chord.updateSymbol()
                chordName.text = chord.name
                update(progression.indexOf(chord))

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

                } else {
                    show(articulationTab)
                    tabMenu.tag = articulationTab

                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })


//        chordMenuDoneButton.setOnClickListener {
//
//            if (landscape) {
//                hide(chordMenu1)
//                show(progressionPanel)
//                hide(showPanel)
//
//                val params = keyboard.layoutParams as ConstraintLayout.LayoutParams
//                params.setMargins(0, 0,0,(56*dp).toInt())
//                keyboard.layoutParams = params
//
//                val transition = ChangeBounds()
//                transition.setInterpolator(AnticipateOvershootInterpolator(0.5f))
//                transition.setDuration(150)
//                beginDelayedTransition(progressionsLayout, transition)
//            } else {
//                hide(dimBg)
//                hide(chordMenu)
//                hide(chordMenuDoneButton)
//                val transition = ChangeBounds()
//                transition.setInterpolator(AnticipateOvershootInterpolator(0.5f))
//                transition.setDuration(300)
//                beginDelayedTransition(chordMenu1, transition)
//                constraints.applyTo(chordMenu1)
//
//            }
//
//        }

        exitChordMenu.setOnClickListener {
            if (landscape) {
                hide(chordMenu)
                show(progressionPanel)
                hide(showPanel)
                hide(dimBg)
                hide(exitChordMenu)
                hide(increaseOctaveButton)
                hide(decreaseOctaveButton)
                val params = keyboard.layoutParams as ConstraintLayout.LayoutParams
                params.setMargins(0, 0,0,(56*dp).toInt())
                keyboard.layoutParams = params

                val transition = ChangeBounds()
                transition.setDuration(200)
                beginDelayedTransition(progressionsLayout, transition)
            } else {
                hide(dimBg)
                hide(chordMenu)
//                hide(chordMenuDoneButton)
                hide(exitChordMenu)

                val transition = ChangeBounds()
                transition.setDuration(200)
                beginDelayedTransition(chordMenu1, transition)
                constraints.applyTo(chordMenu1)

            }
        }


        clearProgression.setOnClickListener {
            clearProgression()
        }


//        view.post {
//
//        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
//        _binding = null
    }

    fun update(index:Int) {

        (progressionView.findViewHolderForAdapterPosition(index) as ProgressionAdapter.ViewHolder?)?.setRoot()
        (progressionView.findViewHolderForAdapterPosition(index) as ProgressionAdapter.ViewHolder?)?.setNumeral()
        (progressionView.findViewHolderForAdapterPosition(index) as ProgressionAdapter.ViewHolder?)?.setExtension()
        voicingAdapter = VoicingAdapter(chord.voicingList,chord, invertUp, invertDown, addVoicing, removeVoicing)
        voicingEditor.setAdapter(voicingAdapter)

        progression.chordNames[index] = chord.name

    }

    fun getTypes(degree:Int, mode:Int): List<String> {

        return MusicData.scaleTypes[(degree+mode)%7]
    }


    fun getExtensions(degree: Int, mode: Int): List<String> {
        return Scale.extensions[(degree + mode) % 7]
    }


    fun getSus4Extensions(options: List<String>): List<String> {

        return options.intersect(listOf("None", "7","9","maj7", "maj9", "6","6/9")).toList()
    }

    fun getSus2Extensions(options: List<String>): List<String> {
        return options.intersect(listOf("None","7","maj7","6")).toList()

    }

    fun updateMenu(chord: Chord) {

            chordName.text = chord.name

            tabMenu.getTabAt(0)?.select()
            durationSlider.progress= (chord.duration*2).toInt()
            extensions = getExtensions(chord.degree, Scale.modeNum)
            sus4Extensions = getSus4Extensions(extensions)
            sus2Extensions = getSus2Extensions(extensions)
            types = getTypes(chord.degree, Scale.modeNum)
            typePicker.data = types
            if (chord.type == "sus2") {
                extensionPicker.data = sus2Extensions
            } else if (chord.type == "sus4") {
                extensionPicker.data = sus4Extensions
            } else {
                extensionPicker.data = extensions
            }
            degreePicker.scrollTo(chord.degree)
            typePicker.scrollTo(typePicker.data.indexOf(chord.type))
            extensionPicker.scrollTo(extensionPicker.data.indexOf(chord.extension))
            voicingAdapter = VoicingAdapter(chord.voicingList , chord, invertUp, invertDown, addVoicing, removeVoicing)
            voicingEditor.setAdapter(voicingAdapter)
            voicingAdapter.selected = null
//        octaveBass.isChecked = chord.octaveBass

//        val prevChord = progression.getPrev(chord)

//        }


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

    fun clearProgression() {
        progressionView.removeAllViews()
        progression.clear()
        progressionAdapter.selected = null
        piano.clear()
        deleteChord.drawable.alpha=100
        editChord.drawable.alpha=100
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



}