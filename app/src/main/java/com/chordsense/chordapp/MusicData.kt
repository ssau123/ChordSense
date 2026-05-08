package com.chordsense.chordapp

class MusicData {
    companion object {

        val diatonicIntervals = listOf(2, 2, 1, 2, 2, 2, 1)

        val doubleVoicingOptions = listOf("R", "M2", "m3", "M3", "P4", "b5", "P5", "#5", "m7", "M7")
        val removeOptions = listOf("b5", "P5", "#5")
        val freePresets = listOf("Soft Piano", "Epiano 1")
        val presets = listOf("Soft Piano", "Epiano 1", "Epiano 2", "Nylon Guitar", "Synth Pluck 1")



        val presetsMap = mapOf("Soft Piano" to "grand_piano", "Epiano 1" to "epiano_1","Epiano 2" to "epiano_2", "Synth Pluck 1" to "synth_pluck_1", "Nylon Guitar" to "nylon_guitar")




//        val presets = listOf("grand_piano", "epiano_1","epiano_2", "synth_pluck_1", "synth_pluck_2", "nylon_guitar", "pad_1")


        val notes = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
        val modes = listOf("Ionian", "Dorian", "Phrygian", "Lydian", "Mixolydian", "Aeolian", "Locrian")
        val scales= listOf("Major", "Minor")

        val scaleTypes = listOf(listOf("maj", "sus2", "sus4"),
            listOf("min", "sus2", "sus4"),
            listOf("min", "sus4"),
            listOf("maj", "sus2"),
            listOf("maj", "sus2", "sus4"),
            listOf("min", "sus2", "sus4"),
            listOf("dim"))

        val chordTypes: Map<String, List<Int>> =
            mapOf("maj" to listOf(0, 4, 7),
                "min" to listOf(0, 3, 7),
                "dim" to listOf(0, 3, 6),
                "sus2" to listOf(0, 2, 7),
                "sus4" to listOf(0, 5, 7))

        val intervalNames = listOf("R", "m2", "M2", "m3", "M3", "P4", "b5", "P5", "#5", "M6", "m7", "M7", "", "b9", "9", "#9", "b11", "11", "#11", "", "b13", "13")

        val intervalMap:Map<String, Int> = mapOf("R" to 0, "M2" to 2, "m3" to 3, "M3" to 4, "P4" to 5, "b5" to 6, "P5" to 7, "#5" to 8, "M6" to 9, "m7" to 10, "M7" to 11, "9" to 14,"b9" to 13, "#9" to 15, "11" to 17, "#11" to 18, "13" to 21, "b13" to 20)

        val extensions: Map<String, List<Int>> =
            mapOf("6" to listOf(9),
                "7" to listOf(10),
                "add9" to listOf(14),
                "add11" to listOf(18),
                "maj7" to listOf(11),
                "maj9" to listOf(11, 14),
                "maj#11" to listOf(11,14,18),
                "maj13" to listOf(11, 14, 21),
                "11" to listOf(10,14,17),
                "7(11)" to listOf(10,17),
                "maj13(#11)" to listOf(11,14,18,21),
                "13" to listOf(10,14,21),
                "maj7(#11)" to listOf(11,18),
                "maj7(13)" to listOf(11,21),
                "7(13)" to listOf(10,21),
                "9" to listOf(10,14),
                "6/9" to listOf(9,14),
                "6/9(#11)" to listOf(9,14,18),
                "7#9" to listOf(10,15),
                "13b9" to listOf(10,13,21),
                "7b13" to listOf(10,14,20),
                "7#5#9" to listOf(10,15),
                "9#11" to listOf(10,14,18),
                "7b9" to listOf(10,13),
                "7b9#5" to listOf(10,13),
                "7#5" to listOf(10),
                "dim7" to listOf(9),
                "dim9" to listOf(9,14))
    }

//    val voicings:Map<String, Map<String, List<String>>> =
//        mapOf("maj" to mapOf("None" to listOf("Root Position", "1st Inversion", "2nd Inversion", "R P5 M3", "R M3 P5 R", "R M3 P5 M3"),
//            "6" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R M3 P5 M6 R", "R P5 M6 M3", "R M6 M3 P5", "R P5 M3 M6"),
//            "7" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R P5 m7 M3", "R m7 M3 P5", "R P5 M3 m7"),
//            "9" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R P5 m7 9 M3", "R P5 m7 9 M3", "R m7 9 M3 P5", "R m7 M3 P5 9", "R 9 M3 P5 m7"),
//            "11" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion","R M3 P5 m7 9 11"),
//            "13" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R m7 M3 13 9", "R m7 9 M3 13"),
//            "7(11)" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R m7 M3 11 5"),
//            "6/9" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion","R P5 M3 M6 9", "R M6 M3 P5 9"),
//            "6/9(#11)" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion","R P5 M3 M6 9 #11"),
//            "7(#9)" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion"),
//            "maj7" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R P5 M3 M7", "R M7 M3 P5"),
//            "maj9" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R P5 9 M3 M7","R P5 M3 M7 9", "R M7 M3 P5 9"),
//            "maj#11" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion","R M3 P5 M7 9 #11", "R P5 9 M3 #11 M7", "R P5 M3 #11 M7 9"),
//            "maj13(#11)" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R P5 M3 M7 9 #11 13"),
//            "maj13" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R M7 M3 13 9", "R M7 M3 P5 13 9", "R P5 M3 M7 9 13"),
//            "maj7(#11)" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R P5 M3 #11 M7"),
//            "maj7(13)" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion"),
//
//            "add9" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R 9 M3 P5", "R P5 9 M3")),
//
//            "min" to mapOf("None" to listOf("Root Position", "1st Inversion", "2nd Inversion", "R P5 m3", "R m3 P5 R", "R m3 P5 m3"),
//                "6" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R P5 M6 m3", "R M6 m3 P5", "R P5 m3 M6"),
//                "7" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R P5 m7 M3", "R m7 m3 P5", "R P5 m3 m7"),
//                "9" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R P5 m7 9 m3", "R P5 m7 9 M3", "R m7 9 m3 P5", "R m7 m3 P5 9", "R 9 m3 P5 m7"),
//                "11" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion","R m3 P5 m7 9 11"),
//                "13" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R m7 m3 13 9", "R m7 9 m3 13"),
//                "7(11)" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R m7 M3 11 5"),
//                "6/9" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion","R P5 m3 M6 9", "R M6 m3 P5 9"),
//                "6/9(#11)" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion","R P5 m3 M6 9 #11"),
//                "add9" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R 9 m3 P5")),
//
//            "sus2" to mapOf("None" to listOf("Root Position", "1st Inversion", "2nd Inversion", "R P5 M2", "R M2 P5 R", "R M2 P5 M2"),
//                "6" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R M2 P5 M6 R", "R P5 M6 M2", "R M6 M2 P5", "R P5 M2 M6"),
//                "7" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R P5 m7 M2", "R m7 M2 P5", "R P5 M2 m7"),
//                "maj7" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R P5 M2 M7", "R M7 M2 P5"),
//                "maj7(#11)" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion","R M2 P5 M7 #11")),
//
//            "sus4" to mapOf("None" to listOf("Root Position", "1st Inversion", "2nd Inversion", "R P5 P4", "R P4 P5 R", "R P4 P5 P4"),
//                "6" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R P4 P5 M6 R", "R P5 M6 P4", "R M6 P4 P5", "R P5 P4 M6"),
//                "7" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R P5 m7 M3", "R m7 P4 P5", "R P5 P4 m7"),
//                "9" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R P5 m7 9 P4", "R P5 m7 9 P4", "R m7 9 M3 P5", "R m7 P4 P5 9", "R 9 P4 P5 m7"),
//                "maj7" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R P5 P4 M7", "R M7 P4 P5"),
//                "maj9" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R P5 9 P4 M7","R P5 P4 M7 9", "R M7 P4 P5 9"),
//                "maj13" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R M7 P4 13 9", "R M7 P4 P5 13 9", "R P5 P4 M7 9 13"),
//                "13" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R m7 P4 13 9", "R m7 9 P4 13"),
//                "6/9" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion","R P5 P4 M6 9", "R M6 M3 P5 9")),
//
//            "dim" to mapOf("None" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion", "R b5 m3", "R m3 b5 R", "R m3 b5 m3"),
//                "7" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion","R m3 b5 m7", ),
//                "b9" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion","R m3 b5 m7 b9", ),
//                "11b9" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion","R m3 b5 m7 b9 11"),
//                "b13b9" to listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion","R m3 b5 m7 b9 b13"))
//        )
}