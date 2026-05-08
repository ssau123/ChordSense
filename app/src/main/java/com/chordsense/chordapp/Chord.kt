package com.chordsense.chordapp

import com.chordsense.chordapp.ui.progressions.ProgressionsFragment.Companion.chord

class Chord constructor(
      var root: String = "",
      var type: String = "",
      var extension: String = "None",
      var octave: Int = 4,
      var intervals: MutableList<Int> = mutableListOf(),
      var symbol: String = "",
      var duration: Float = 4F,
      var numeral: String? = null,
      var degree: Int = 0,
      var name: String = "",
      var offset: Int = 0,
      var voicing: String = "",
      var voicingList: MutableList<String> = mutableListOf(),
      var position:Int = 0,
      var octaveBass: Boolean = false
      ) {


      fun setOffset() {
            offset =  MusicData.notes.indexOf(root) + (octave - 2) * 12
      }

      fun updateSymbol() {
           if (extension != "None") {
                 if (type == "min") {
                       if (extension == "add9") {
                             symbol = "min" + "(add9)"
                       } else {
                             symbol = "min" + extension
                       }
                 }else if (type == "sus4" || type == "sus2") {
                       symbol = extension+type
                 }else if (type == "dim") {
                     if (extension == "7") {
                         symbol = "min7"+"b5"

                     } else if (extension == "7(11)") {
                         symbol = "min7"+"b5(11)"


                     } else {
                         symbol = extension
                     }
                 } else if (type == "maj") {
                       symbol = extension
                 }

           } else {
                 symbol = type
           }
            name = root+symbol
      }


    fun setIntervals() {
        setOffset()
        val inversion = listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion").indexOf(voicing)
        if (inversion != -1) { // if user selects
            voicingList.clear()

            intervals = MusicData.chordTypes[type]!!.toMutableList()
            if (extension != "None") {
                MusicData.extensions[extension]?.let { intervals.addAll(it) } // intervals.addAll(extension[extension])
            }

            if (extension.contains("#5")) {
                intervals[2] +=1
            }

            for (i in 0 until intervals.size) {
                voicingList.add(MusicData.intervalNames[intervals[i]])
            }

            for (i in 0 until inversion) {
                intervals[i]+=12
            }
            val sortedIndicesArray2 = intervals.indices.sortedBy { intervals[it] } // Order of indices to arrange the voicingList by
            val sortedArray1 = sortedIndicesArray2.map { voicingList[it] }.toTypedArray()
            voicingList = sortedArray1.toMutableList()

            intervals = intervals.sorted().toMutableList()

        } else {
            voicingList = voicing.split(" ").toMutableList()
            intervals.clear()
            var prev ="R"
            var prevInterval=0
            for (i in voicingList) {
                val interval = (MusicData.intervalMap[i]!! - MusicData.intervalMap[prev]!!).mod(12) + prevInterval
                intervals.add(interval)
                prev=i
                prevInterval=interval
            }
        }
    }

    fun setIntervals(editingVoicing:Boolean) {
        setOffset()
        if (!editingVoicing) { // if user is changing chord root, type, extension, not editing voicing
            voicingList.clear()
//            val inversion = listOf("Root Position", "1st Inversion", "2nd Inversion", "3rd Inversion").indexOf(voicing)
            intervals = MusicData.chordTypes[type]!!.toMutableList()
            if (extension != "None") {
                MusicData.extensions[extension]?.let { intervals.addAll(it) } // intervals.addAll(extension[extension])
            }

            if (extension.contains("#5")) {
                intervals[2] +=1
            }

            for (i in 0 until intervals.size) {
                voicingList.add(MusicData.intervalNames[intervals[i]])
            }

            for (i in 0 until 0) {
                intervals[i]+=12
            }
            val sortedIndicesArray2 = intervals.indices.sortedBy { intervals[it] } // Order of indices to arrange the voicingList by
            val sortedArray1 = sortedIndicesArray2.map { voicingList[it] }.toTypedArray()
            voicingList = sortedArray1.toMutableList()
            intervals = intervals.sorted().toMutableList()

        } else {
//            voicingList = voicing.split(" ").toMutableList()
            intervals.clear()
            var prev ="R"
            var prevInterval=0
            for ((j, i) in voicingList.withIndex()) {
                var interval = (MusicData.intervalMap[i]!! - MusicData.intervalMap[prev]!!).mod(12) + prevInterval
                if (i == "R" && prev =="R" && j== 1) {
                    interval+= 12
                }
                intervals.add(interval)
                prev=i
                prevInterval=interval
            }
        }
    }


      fun setVoicing() {
//          voicingList = voicing.split(" ").toMutableList()
          intervals.clear()
          var prev ="R"
          var prevInterval=0
          for ((j, i) in voicingList.withIndex()) {
              var interval = (MusicData.intervalMap[i]!! - MusicData.intervalMap[prev]!!).mod(12) + prevInterval
              if (i == "R" && prev =="R" && j== 1) {
                  interval+= 12
              }
              intervals.add(interval)
              prev=i
              prevInterval=interval
          }

      }

    fun lower() {
        for (i in 0 until intervals.size) {
            intervals[i]-=12
        }
    }

    fun size(): Int {
        return intervals.size
    }

    fun invert(index:Int, dir:Int): Int {

        if (dir >0) {
            intervals[index]+=12
        } else {
            intervals[index]-=12
        }
        val interval = intervals[index]
        intervals.sort()

        val pos = intervals.indexOf(interval)



        voicingList.add(pos, voicingList.removeAt(index))

//
//
//        val interval = intervals[index]
//        val sortedIndicesArray2 =
//            intervals.indices.sortedBy { intervals[it] } // Order of indices to arrange the voicingList by
//        val sortedArray1 = sortedIndicesArray2.map { voicingList[it] }.toTypedArray()
//        voicingList = sortedArray1.toMutableList()
//        intervals = intervals.sorted().toMutableList()


        return intervals.indexOf(interval)





//            voicingList.clear()
//            intervals = MusicData.chordTypes[type]!!.toMutableList()
//            if (extension != "None") {
//                MusicData.extensions[extension]?.let { intervals.addAll(it) } // intervals.addAll(extension[extension])
//            }
//
//            if (extension.contains("#5")) {
//                intervals[2] += 1
//            }
//
//            for (i in 0 until intervals.size) {
//                voicingList.add(MusicData.intervalNames[intervals[i]])
//            }
//
//            for (i in 0 until 0) {
//                intervals[i] += 12
//            }
//            val sortedIndicesArray2 =
//                intervals.indices.sortedBy { intervals[it] } // Order of indices to arrange the voicingList by
//            val sortedArray1 = sortedIndicesArray2.map { voicingList[it] }.toTypedArray()
//            voicingList = sortedArray1.toMutableList()
//            intervals = intervals.sorted().toMutableList()
    }






}

fun main() {
      val chord = Chord(root = "C", type = "maj", extension = "maj#11")

      chord.updateSymbol()
      chord.setIntervals()
      println(chord.intervals.toString())
}



