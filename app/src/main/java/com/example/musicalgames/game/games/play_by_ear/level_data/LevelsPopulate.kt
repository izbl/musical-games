package com.example.musicalgames.games.play_by_ear

import com.example.musicalgames.game.games.play_by_ear.PlayEarLevel
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.MusicUtil
import com.example.musicalgames.utils.MusicUtil.getScaleNotesFrom
import com.example.musicalgames.utils.MusicUtil.getScaleNotesTo
import com.example.musicalgames.utils.MusicUtil.getWhiteKeysFrom
import com.example.musicalgames.utils.MusicUtil.midi
import com.example.musicalgames.utils.MusicUtil.noteLetter
import com.example.musicalgames.utils.MusicUtil.noteName
import com.example.musicalgames.utils.Note
import com.example.musicalgames.utils.Scale

object EarPlayLevels {
    val baseLevels: List<PlayEarLevel> = generateLevels()
    val minorLevels: List<PlayEarLevel> = generateMinorLevels()
    private fun generateLevel(scale: Scale, rootNote: ChromaticNote, notes: List<Int>, notesNum: Int, interval: Int) : PlayEarLevel {
        val id =0
        val root = midi(rootNote.toString()+"4")
        val span = notes[notes.size-1]-notes[0]
        //if the span is at least an octave, we just show the note
        //if the notes end on the root, we show the octave below
        //otherwise we show the octave above
        val firstNote = Note(notes[0]).noteChromatic
        val lastNote = Note(notes[notes.size-1]).noteChromatic
        val firstRootBelow =
            if(firstNote.ordinal >=rootNote.ordinal)
                noteLetter(root)+Note(notes[0]).octave
            else
                noteLetter(root)+(Note(notes[0]).octave-1)

        val firstRootAbove =
            if(lastNote.ordinal <= rootNote.ordinal)
                noteLetter(root)+Note(notes[notes.size-1]).octave
            else
                noteLetter(root)+(Note(notes[notes.size-1]).octave+1)

        val belowDistance = notes[0] - Note(firstRootBelow).midiCode
        val aboveDistance = Note(firstRootAbove).midiCode - notes[notes.size - 1]


        val min =
            if(span<12 && (belowDistance<aboveDistance || lastNote == rootNote))
                Note(firstRootBelow).midiCode
            else notes[0]

        val max =
            if(span<12 && (belowDistance>=aboveDistance || firstNote == rootNote))
                Note(firstRootAbove).midiCode
            else notes[notes.size-1]

        return PlayEarLevel(
            id,
            min,
            max,
            ChromaticNote.fromString(noteName(root)),
            notesNum,
            interval,
            notes.toList(),
            "$rootNote $scale, ${noteName(notes[0])} to ${noteName(notes[notes.size-1])}",
            "$notesNum notes, max interval $interval semitones"
        )
    }
    private fun generateLevels(): List<PlayEarLevel> {
        val levels = mutableListOf<PlayEarLevel>()
        val rootNote = midi("C4")
        for(maxInterval in listOf(4,5,6,7,8)) {
            for (notesNum in listOf(3, 5, 7)) {
                for (sizeList in listOf(listOf(3, 4), listOf(5, 6), listOf(7), listOf(8))) {
                    //generate levels with notes above root
                    for (size in sizeList) {
                        val notes = getWhiteKeysFrom(rootNote, size)
                        levels.add(generateLevel(Scale.MAJOR, ChromaticNote.C, notes, notesNum, maxInterval))
                    }
                    //generate levels with notes below root
                    for (size in sizeList) {
                        val notes = MusicUtil.getWhiteKeysTo(rootNote, size)
                        levels.add(generateLevel(Scale.MAJOR, ChromaticNote.C, notes, notesNum, maxInterval))
                    }

                    //generate levels that concat notes below and notes above
                    for (size in sizeList) {
                        var notes = MusicUtil.getWhiteKeysTo(rootNote, size)
                        notes = notes.slice(0..(notes.size - 2)) + getWhiteKeysFrom(
                            rootNote,
                            size
                        )
                        levels.add(generateLevel(Scale.MAJOR, ChromaticNote.C, notes, notesNum, maxInterval))
                    }
                }
            }
        }

        return levels
    }

    private fun generateMinorLevels(): List<PlayEarLevel> {
        val levels = mutableListOf<PlayEarLevel>()
        val rootNote = Note("A4")

        for(maxInterval in listOf(4,5,6,7,8)) {
            for (notesNum in listOf(3, 5, 7)) {
                for (sizeList in listOf(listOf(3, 4), listOf(5, 6), listOf(7), listOf(8))) {
                    //generate levels with notes above root
                    for (size in sizeList) {
                        val notes =
                            getScaleNotesFrom(Scale.MINOR, rootNote.noteChromatic, rootNote, size)
                        levels.add(
                            generateLevel(
                                Scale.MINOR,
                                rootNote.noteChromatic,
                                notes.map { note -> note.midiCode },
                                notesNum,
                                maxInterval
                            )
                        )
                    }
                    //generate levels with notes below root
                    for (size in sizeList) {
                        val notes =
                            getScaleNotesTo(Scale.MINOR, rootNote.noteChromatic, rootNote, size)
                        levels.add(
                            generateLevel(
                                Scale.MINOR,
                                rootNote.noteChromatic,
                                notes.map { note -> note.midiCode },
                                notesNum,
                                maxInterval
                            )
                        )
                    }

                    //generate levels that concat notes below and notes above
                    for (size in sizeList) {
                        var notes =
                            getScaleNotesTo(Scale.MINOR, rootNote.noteChromatic, rootNote, size)
                        notes = notes.slice(0..(notes.size - 2)) + getScaleNotesFrom(
                            Scale.MINOR,
                            rootNote.noteChromatic,
                            rootNote,
                            size
                        )
                        levels.add(
                            generateLevel(
                                Scale.MINOR,
                                rootNote.noteChromatic,
                                notes.map { note -> note.midiCode },
                                notesNum,
                                maxInterval
                            )
                        )
                    }
                }
            }
        }

        return levels
    }
}



