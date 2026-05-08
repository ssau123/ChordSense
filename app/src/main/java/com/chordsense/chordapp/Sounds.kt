package com.chordsense.chordapp

import android.app.Activity
import android.content.Context
import android.media.SoundPool
import android.widget.LinearLayout
import android.widget.TextView
import com.chordsense.chordapp.GlobalMethods.Companion.hide
import com.chordsense.chordapp.GlobalMethods.Companion.show
import java.util.Timer
import kotlin.concurrent.schedule


object Sounds {

        var sp = SoundPool.Builder().setMaxStreams(100).build()
        var size = 0
        var streams: MutableList<Int> = mutableListOf()

        fun loadSounds(context:Context?, preset: String, loadingScreen: LinearLayout, loadingProgress: TextView, activity: Activity) {
            sp = SoundPool.Builder().setMaxStreams(100).build()
            size = 0
            activity.runOnUiThread {
                show(loadingScreen)
                loadingProgress.text= "Loading Sounds: 0%"
            }

            sp.setOnLoadCompleteListener { _: SoundPool, _: Int, status: Int ->
                if( status == 0) {
                    size++

                    activity.runOnUiThread {
                        loadingProgress.text= "Loading Sounds: " + ((size /61.0)*100).toInt().toString() + "%"
                        if (size >=61) {
                            hide(loadingScreen)


                        }
                    }
                }
            }

            for (i in 15 until 76) {
                if (context != null) {
                    sp.load(context, MainActivity.res.getIdentifier(preset+"_"+i, "raw", context.packageName), 1)



                }

            }


        }

        fun playNote(keyNum: Int) {
            streams.add(sp.play(keyNum+1, 1F, 1F, 1, 0, 1F))

        }

        fun playNote(keyNum: Int, dur: Long) {
            val stream = sp.play(keyNum+1, 1F, 1F, 1, 0, 1F)
            streams.add(stream)

            Timer().schedule(dur) {
                if (streams.size> 0) {
                    sp.stop(stream)
                    streams.remove(stream)

                }
            }
        }


        fun playChord(chord: Chord) {
            for (i in chord.intervals) {
                playNote(i+chord.offset)

            }
        }

        fun playChord(chord: Chord, dur:Long) {
            var prev = -1
            for (i in chord.intervals) {
                if (i!=prev) {
                    playNote(i+chord.offset, dur)
                }
                prev = i

            }
        }



        fun stop() {
            try {
                for (i in streams) {

                    sp.stop(i)
                }
                streams.clear()
            } catch (e:NullPointerException) {

            }
        }

        fun fade() {

            for (i in streams) {
                sp.setVolume(i, 0.0f, 0.0f)
            }

        }

}