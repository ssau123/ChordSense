package com.chordsense.chordapp

object Scale {

    //scale degree extensions
    val extensions = listOf(listOf("None","maj7", "maj9", "maj13", "maj7(13)","add9", "6","6/9"),
        listOf("None","7", "9", "11", "13", "7(11)", "7(13)", "add9","6", "6/9"),
        listOf("None","7", "7(11)"),
        listOf("None","maj7", "maj9", "maj#11", "maj13(#11)", "maj13", "maj7#11","maj7(13)", "add9", "6", "6/9","6/9(#11)"),
        listOf("None","7", "9", "9#11", "13", "13b9", "7b13", "add9", "6", "6/9","7b9","7b9#5", "7#5","7#5#9", "7#9"),
        listOf("None","7", "9", "11", "7(11)", "add9"),
        listOf("None","7","7(11)", "dim7", "dim9"))
    var root= "C"
    var mode = "Ionian"
    var modeNum = 0


    var notes = mutableListOf("C","D","E","F","G","A","B")
    var degrees = mutableListOf("I (C)", "ii (D)", "iii (E)", "IV (F)", "V (G)", "vi (A)", "vii° (B)")

    fun setScale() {

        var qqq = MusicData.modes.indexOf(mode)
        if (mode == "Major") {
            qqq = 0
        } else if (mode == "Minor") {
            qqq = 5
        }
        modeNum = qqq

        degrees.clear()
        notes.clear()
        val degrees = arrayOf("i", "ii", "iii", "iv", "v", "vi", "vii")
        var curr = MusicData.notes.indexOf(root)
        for (i in 0 until 7) {
            notes.add(MusicData.notes[curr])
            curr =  (curr + MusicData.diatonicIntervals[(i+qqq)%7])%12
            Scale.degrees.add(degrees[i] + " (" + notes[i] + ")")
            if ((i+qqq)%7 == 6) {
                Scale.degrees[i] = Scale.degrees[i].replace(" ", "° ")
            } else if ((i+qqq)%7 in listOf(0,3,4)) {
                Scale.degrees[i] = Scale.degrees[i].uppercase()
            }
        }
    }
}