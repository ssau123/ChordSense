package com.chordsense.chordapp

import android.content.Context
import android.transition.Transition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import com.jakewharton.processphoenix.ProcessPhoenix


class GlobalMethods {

    companion object {

        fun hide(view: View) {
            if (view.visibility != View.GONE) {

                view.visibility = View.INVISIBLE

            }
        }

        fun show(view: View) {
            if (view.visibility != View.GONE) {
                view.visibility = View.VISIBLE

            }
        }




        fun hide(view: View, anim: Transition, dur:Long) {
            if (view.visibility != View.GONE) {


                anim.setDuration(dur)
                anim.addTarget(view)

                TransitionManager.beginDelayedTransition(view.parent as ViewGroup?, anim)
                view.visibility = View.INVISIBLE

            }
            println("r392ru39ru29")

        }

        fun show(view: View, anim: Transition, dur:Long) {
            if (view.visibility != View.GONE) {

                anim.setDuration(dur)
                anim.addTarget(view)

                TransitionManager.beginDelayedTransition(view.parent as ViewGroup?, anim)
                view.visibility = View.VISIBLE

            }
            println("r392ru39ru29")
        }


        fun isVisible(view: View): Boolean {
            return view.visibility == View.VISIBLE
        }

        fun toggleView(view: View) {
            if (view.visibility == View.INVISIBLE) {
                show(view)
            }

            else if(view.visibility == View.VISIBLE) {
                hide(view)
            }
        }

        fun restart(context: Context) {
            ProcessPhoenix.triggerRebirth(context)

        }

    }


}