package com.chordsense.chordapp.ui.settings

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.chordsense.chordapp.MainActivity
import com.chordsense.chordapp.MainActivity.Companion.scaleModes
import com.chordsense.chordapp.R
import com.chordsense.chordapp.ui.progressions.ProgressionsFragment


class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)





//        val sharedPref = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
//        val listener: SharedPreferences.OnSharedPreferenceChangeListener =
//            SharedPreferences.OnSharedPreferenceChangeListener { sharedPref, s ->
//                if (s.equals("theme")) {
//                    if (sharedPref.getString("theme", "Light") == "Light") {
//                        activity?.setTheme(R.style.Light)
//                        println("fej")
//                    } else {
//                        activity?.setTheme(R.style.Theme_ChordSense)
//                        println("feefwj")
//
//                    }
//                }
//
//            }
//
//        if (sharedPref != null) {
//            sharedPref.registerOnSharedPreferenceChangeListener(listener)
//        }


    }

    // PreferenceFragment class
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val rv = listView
//            rv.setPadding(300, 0, 300, 0)
        }


//        val myPref = findPreference("Restart") as Preference?
//        myPref!!.setOnPreferenceClickListener {
//
//            ProcessPhoenix.triggerRebirth(context)
//
//            true
//        }

        val myPref2 = findPreference("intervalLabels") as SwitchPreference?
        myPref2!!.setOnPreferenceClickListener {
            MainActivity.intervalLabels = !(MainActivity.intervalLabels)
            ProgressionsFragment.piano.displayChord(ProgressionsFragment.chord)

            true
        }

//        val myPref3 = findPreference("scrolling") as SwitchPreference?
//        myPref3!!.setOnPreferenceClickListener {
//            MainActivity.scrolling = !(MainActivity.scrolling)
//            true
//        }

        val myPref4 = findPreference("modes") as SwitchPreference?



        myPref4!!.setOnPreferenceClickListener {

            scaleModes = !(scaleModes)
//            if (scaleModes) {
//                ProgressionsFragment.modePicker.data = MusicData.modes
//            } else {
//                ProgressionsFragment.keySelectText.text="C Major"
//                ProgressionsFragment.modePicker.data = MusicData.scales
//            }
            true
        }

        for (i in 0 until preferenceScreen.preferenceCount) {
            initializeSummary(preferenceScreen.getPreference(i))
        }



//        val myPref5 = findPreference("premium") as SwitchPreference?
//        myPref5!!.setOnPreferenceClickListener {
//            if (sharedPref.getBoolean("premium", false)) {
//                premium = true
//                show(restart)
//
//            }
//
//
//            true
//        }
    }


    private fun initializeSummary(p: Preference) {
        if (p is SwitchPreference) {
            val listPref: SwitchPreference= p as SwitchPreference
            p.setSummary(listPref.summary)
        }
    }



}
