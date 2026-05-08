package com.chordsense.chordapp
import android.os.SystemClock
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.chordsense.chordapp.MainActivity.Companion.dp
import com.chordsense.chordapp.Sounds.playChord
import com.chordsense.chordapp.ui.progressions.ProgressionsFragment
import com.chordsense.chordapp.ui.progressions.ProgressionsFragment.Companion.boxWidth


import com.chordsense.chordapp.ui.progressions.ProgressionsFragment.Companion.loop
import com.chordsense.chordapp.ui.progressions.ProgressionsFragment.Companion.progression
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


class Progression(private val activity: FragmentActivity) {

    var name = "Untitled"
    var scale = Scale
    var chords = mutableListOf<Chord>()
    var loops = 0
    var bpm: Int = 140
    var chordNames = mutableListOf<String>()
    var stop = false


    fun add(chord: Chord) {
        chords.add(chord)
        chordNames.add(chord.name)

    }

    fun add(index: Int, chord: Chord) {
        chords.add(index, chord)
        chordNames.add(index, chord.name)

    }

    fun sort() {
        chords.sortBy { it.position }
    }

    fun getChordNames() {
        for (i in chords) {
            chordNames.add(i.name)
        }
    }

    fun removeAt(index: Int) {
        chords.removeAt(index)
        chordNames.removeAt(index)
    }

    fun clear() {
        chords.clear()
        chordNames.clear()
    }

    fun indexOf(chord: Chord): Int {
        return chords.indexOf(chord)
    }

    fun get(index: Int): Chord {
        return chords[index]
    }

    fun getPrev(chord: Chord): Chord? {
        val i = indexOf(chord) - 1
        if (i != -1) {
            return chords[indexOf(chord) - 1]

        }
        return null
    }

    fun transposeUp(): Boolean {

        if (progression.size() == 0) {
            return false
        }
        var isTransposable = true
        for (chord in chords) {
            if ((chord.offset + chord.intervals.last()) == 60) { // intervals is sorted from least to greatest, last element is always highest note of the chord
                isTransposable = false
                break
            }
        }

        if (isTransposable) {
            for (chord in chords) {
                chord.offset += 1
                chord.root = MusicData.notes[(MusicData.notes.indexOf(chord.root) + 1) % 12]
                chord.octave = (chord.offset + 9 - MusicData.notes.indexOf(chord.root)) / 12
                chord.name = chord.root + chord.symbol

            }
        }
        return isTransposable

    }

    fun transposeDown(): Boolean {
        if (size() == 0) {
            return false
        }
        var isTransposable = true
        for (chord in chords) {
            if (chord.offset == 0) {
                isTransposable = false
                break
            }
        }

        if (isTransposable) {
            for (chord in chords) {
                chord.offset -= 1
                chord.root = MusicData.notes[(MusicData.notes.indexOf(chord.root) - 1).mod(12)]
                chord.octave = (chord.offset +12 - MusicData.notes.indexOf(chord.root)) / 12
                chord.name = chord.root + chord.symbol


            }
        }
        return isTransposable
    }


    fun size(): Int {
        return chords.size
    }


    fun play(i: Int, piano: Piano, recyclerView: RecyclerView) {
        var curr = i
        val size = size()

        try {
            while (curr < size) {
                val startTime = SystemClock.elapsedRealtime()


                playChord(
                    chords[curr],
                    (chords[curr].duration * ((60000F) / bpm.toFloat()) + 200).toLong()
                )
//                println(Sounds.streams)
                activity.runOnUiThread {
                    if (curr == 0) {
                        recyclerView.scrollBy(((boxWidth + 8*dp)*-4).toInt(), 0)

                    }
                    else if (curr==4) {
                        recyclerView.scrollBy(((boxWidth + 8*dp)*4).toInt(), 0)

                    }

                    piano.displayChord(chords[curr])
                    (recyclerView.findViewHolderForAdapterPosition(curr) as ProgressionAdapter.ViewHolder).select()

                }


                runBlocking {

                    delay(( (chords[curr].duration *60000F) / bpm.toFloat() - (SystemClock.elapsedRealtime()-startTime)).toLong())
                    (recyclerView.findViewHolderForAdapterPosition(curr) as ProgressionAdapter.ViewHolder).deselect()

                }
                curr = (curr + 1) % size

                if (curr == 0 && !loop) {
                    ProgressionsFragment.reset()
                    break
                }


            }

        } catch (e: Exception) {
            activity.runOnUiThread {
                (recyclerView.findViewHolderForAdapterPosition(curr) as ProgressionAdapter.ViewHolder).deselect()
            }

        }

    }


    fun createThread(index:Int, piano: Piano, recyclerView: RecyclerView): Thread {
        return (Thread {
            play(index, piano, recyclerView)

        })
    }
}