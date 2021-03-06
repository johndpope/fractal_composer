/*
 * Copyright 2008, Myron Marston <myron DOT marston AT gmail DOT com>
 *
 * This file is part of Fractal Composer.
 *
 * Fractal Composer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option any later version.
 *
 * Fractal Composer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fractal Composer.  If not, see <http://www.gnu.org/licenses/>. 
 */

package com.myronmarston.music.settings;

import com.myronmarston.music.*;
import com.myronmarston.music.scales.*;
import com.myronmarston.util.Fraction;

import javax.sound.midi.*;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Contains tests to test entire fractal pieces, checking the results.
 * 
 * @author Myron
 */
public class PieceTest {   
    
    @Test
    public void singleNoteGermChromaticTest() throws Exception {
        FractalPiece fp = new FractalPiece();
        fp.setScale(new ChromaticScale());
        fp.setGermString("G4");
        fp.createDefaultSettings();
        Sequence seq = fp.createPieceResultOutputManager().getSequence();
     
        // array of voices, notes, pitches/durations
        int[][][] noteValues = {
            // first voice...
            {   
                 // intro...
                {-1, 16}, 
                {-1, 16},                
                {79, 4}, {79, 4}, {79, 4}, {79, 4},
                
                // self-similar section...        
                {79, 4}, {79, 4}, {79, 4}, {79, 4},
                {79, 4}, {79, 4}, {79, 4}, {79, 4},
                {79, 4}, {79, 4}, {79, 4}, {79, 4},
                {79, 4}, {79, 4}, {79, 4}, {79, 4},
                        
                // outro...                
                {79, 4}, {79, 4}, {79, 4}, {79, 4},
                {-1, 16},              
                {-1, 16}
            },
                       
            // second voice...
            {
                // intro...
                {-1, 16},
                {67, 8}, {67, 8},
                {67, 8}, {67, 8},
                
                // self-similar section...        
                {67, 8}, {67, 8},
                {67, 8}, {67, 8},
                {67, 8}, {67, 8},
                {67, 8}, {67, 8},
                        
                // outro...                
                {67, 8}, {67, 8},
                {67, 8}, {67, 8},
                {-1, 16}
            },
            
            {
                // intro...
                {55, 16}, {55, 16}, {55, 16},
                
                // self-similar section...        
                {55, 16}, {55, 16}, {55, 16}, {55, 16},
                        
                // outro...                
                {55, 16}, {55, 16}, {55, 16}
            }
        };
        
        
        int MFVolume = Dynamic.MF.getMidiVolume();        
        for (int trackIndex = 0; trackIndex < noteValues.length; trackIndex++) {
            //System.out.println(String.format("  simplePieceTest track: %d", trackIndex));
            
            Track track = seq.getTracks()[trackIndex + 1]; //skip track 0, which has time signature and key signature
            int trackNoteValues[][] = noteValues[trackIndex];
            long ticksSoFar = 0;
            int channelNum = trackIndex;
            
            // make sure we have the right number of events. 
            assertTrackHasRightNumEvents(track, trackNoteValues.length);            
                        
            for (int noteIndex = 0; noteIndex < trackNoteValues.length; noteIndex++) {
                if (trackNoteValues[noteIndex][0] == -1)  { // rest
                    assertTrackMidiNoteEqual(track, noteIndex, ticksSoFar, trackNoteValues[noteIndex][1], 0, 0, channelNum);
                } else {                                        
                    assertTrackMidiNoteEqual(track, noteIndex, ticksSoFar, trackNoteValues[noteIndex][1], trackNoteValues[noteIndex][0], MFVolume, channelNum);
                }
                ticksSoFar += trackNoteValues[noteIndex][1];
            }  
        }
    }
    
    @Test
    public void simplePieceTest() throws InvalidKeySignatureException, NoteStringParseException, GermIsEmptyException {
        FractalPiece fp = new FractalPiece();
        fp.setScale(new MajorScale(NoteName.G));
        fp.setGermString("G4,1/4 A4,1/8 B4,1/8 G4,1/4");
        fp.createDefaultVoices();
        Section s = fp.createSection();
        s.setSelfSimilaritySettingsOnAllVoiceSections(true, true, true, 1);
        
        fp.getVoices().get(0).setInstrumentName("Violin");
        fp.getVoices().get(1).setInstrumentName("Viola");
        fp.getVoices().get(2).setInstrumentName("Cello");
        
        Sequence seq = fp.createPieceResultOutputManager().getSequence();
                
        // array of voices, notes, pitches/durations
        int[][][] noteValues = {
            // first voice...
            {   
                // intro...
                {-1, 192}, // -1 means rest...                
                {-1, 192}, // -1 means rest...                
                {79, 16}, {81, 8}, {83, 8}, {79, 16},
                {79, 16}, {81, 8}, {83, 8}, {79, 16},
                {79, 16}, {81, 8}, {83, 8}, {79, 16},
                {79, 16}, {81, 8}, {83, 8}, {79, 16},
                        
                //self-similar section...
                {79, 16}, {81, 8}, {83, 8}, {79, 16},
                {81, 8}, {83, 4}, {84, 4}, {81, 8},
                {83, 8}, {84, 4}, {86, 4}, {83, 8},
                {79, 16}, {81, 8}, {83, 8}, {79, 16},
                        
                {79, 16}, {81, 8}, {83, 8}, {79, 16},
                {81, 8}, {83, 4}, {84, 4}, {81, 8},
                {83, 8}, {84, 4}, {86, 4}, {83, 8},
                {79, 16}, {81, 8}, {83, 8}, {79, 16},
                        
                {79, 16}, {81, 8}, {83, 8}, {79, 16},
                {81, 8}, {83, 4}, {84, 4}, {81, 8},
                {83, 8}, {84, 4}, {86, 4}, {83, 8},
                {79, 16}, {81, 8}, {83, 8}, {79, 16},
                        
                {79, 16}, {81, 8}, {83, 8}, {79, 16},
                {81, 8}, {83, 4}, {84, 4}, {81, 8},
                {83, 8}, {84, 4}, {86, 4}, {83, 8},
                {79, 16}, {81, 8}, {83, 8}, {79, 16},
                
                //outro..
                {79, 16}, {81, 8}, {83, 8}, {79, 16},
                {79, 16}, {81, 8}, {83, 8}, {79, 16},
                {79, 16}, {81, 8}, {83, 8}, {79, 16},
                {79, 16}, {81, 8}, {83, 8}, {79, 16},
                {-1, 192},                
                {-1, 192}                                
            },
                       
            // second voice...
            {
                // intro...
                {-1, 192},                        
                {67, 32}, {69, 16}, {71, 16}, {67, 32},
                {67, 32}, {69, 16}, {71, 16}, {67, 32},
                {67, 32}, {69, 16}, {71, 16}, {67, 32},
                {67, 32}, {69, 16}, {71, 16}, {67, 32},
                        
                // self-similar section...           
                {67, 32}, {69, 16}, {71, 16}, {67, 32},
                {69, 16}, {71, 8}, {72, 8}, {69, 16},        
                {71, 16}, {72, 8}, {74, 8}, {71, 16},        
                {67, 32}, {69, 16}, {71, 16}, {67, 32},
                        
                {67, 32}, {69, 16}, {71, 16}, {67, 32},
                {69, 16}, {71, 8}, {72, 8}, {69, 16},        
                {71, 16}, {72, 8}, {74, 8}, {71, 16},        
                {67, 32}, {69, 16}, {71, 16}, {67, 32},                         
                        
                // outro...                                        
                {67, 32}, {69, 16}, {71, 16}, {67, 32},
                {67, 32}, {69, 16}, {71, 16}, {67, 32},
                {67, 32}, {69, 16}, {71, 16}, {67, 32},
                {67, 32}, {69, 16}, {71, 16}, {67, 32},
                {-1, 192}
            },
            
            {
                // intro...
                {55, 64}, {57, 32}, {59, 32}, {55, 64},
                {55, 64}, {57, 32}, {59, 32}, {55, 64},
                {55, 64}, {57, 32}, {59, 32}, {55, 64},
                
                // self-similar section...        
                {55, 64}, {57, 32}, {59, 32}, {55, 64},
                {57, 32}, {59, 16}, {60, 16}, {57, 32},
                {59, 32}, {60, 16}, {62, 16}, {59, 32},
                {55, 64}, {57, 32}, {59, 32}, {55, 64},
                        
                // outro...
                {55, 64}, {57, 32}, {59, 32}, {55, 64},
                {55, 64}, {57, 32}, {59, 32}, {55, 64},
                {55, 64}, {57, 32}, {59, 32}, {55, 64}
            }
        };
        
        int MFVolume = Dynamic.MF.getMidiVolume();
        int[] instrumentProgramNumbers = {40, 41, 42}; // the program numbers for violin, viola, cello
        for (int trackIndex = 0; trackIndex < noteValues.length; trackIndex++) {
            //System.out.println(String.format("  simplePieceTest track: %d", trackIndex));
            
            Track track = seq.getTracks()[trackIndex + 1]; //skip track 0, which has time signature and key signature
            int trackNoteValues[][] = noteValues[trackIndex];
            long ticksSoFar = 0;
            int channelNum = trackIndex;
            
            // make sure we have the right number of events. 
            assertTrackHasRightNumEvents(track, trackNoteValues.length);
            
            // test that the instrument event is correct...
            InstrumentTest.assertMidiProgramChangeEventEquals(track.get(0), ticksSoFar, channelNum, instrumentProgramNumbers[trackIndex]);            
            
            for (int noteIndex = 0; noteIndex < trackNoteValues.length; noteIndex++) {
                if (trackNoteValues[noteIndex][0] == -1)  { // rest
                    assertTrackMidiNoteEqual(track, noteIndex, ticksSoFar, trackNoteValues[noteIndex][1], 0, 0, channelNum);
                } else {                                        
                    assertTrackMidiNoteEqual(track, noteIndex, ticksSoFar, trackNoteValues[noteIndex][1], trackNoteValues[noteIndex][0], MFVolume, channelNum);
                }
                ticksSoFar += trackNoteValues[noteIndex][1];
            }  
        }
    }
    
    @Test
    public void pieceWithSectionScale() throws Exception {
        FractalPiece fp = new FractalPiece();
        fp.setGenerateLayeredIntro(false);
        fp.setGenerateLayeredOutro(false);            
        fp.setScale(new MajorScale(NoteName.G));
        fp.setGermString("G4 A4 B4 G4");
        
        // two voices, with no differences...
        Voice v1 = fp.createVoice();
        Voice v2 = fp.createVoice();
        v1.getSettings().getSelfSimilaritySettings().setApplyToPitch(true);
        v2.getSettings().getSelfSimilaritySettings().setApplyToPitch(true);
        
        // set a scale on one section and on one voice section
        Section s1 = fp.createSection();
        Section s2 = fp.createSection();   
        s1.setOverridePieceScale(true);
        s1.setScale(new NaturalMinorScale(NoteName.C));        
        
        Sequence seq = fp.createPieceResultOutputManager().getSequence();
                                
        int[] track1PitchNumbers = {
            // midi numbers taken from http://pages.cs.wisc.edu/~suan/misc/midi.html
            // self-similar based on germ in C minor...
            60, 62, 63, 60,
            62, 63, 65, 62,
            63, 65, 67, 63,
            60, 62, 63, 60,
                       
            // using G major...
            67, 69, 71, 67,
            69, 71, 72, 69,
            71, 72, 74, 71,
            67, 69, 71, 67
        };
        
        int[] track2PitchNumbers = {
            // midi numbers taken from http://pages.cs.wisc.edu/~suan/misc/midi.html
            // self-similar based on germ in C minor...
            60, 62, 63, 60,
            62, 63, 65, 62,
            63, 65, 67, 63,
            60, 62, 63, 60,                        
            
            // using G major...
            67, 69, 71, 67,
            69, 71, 72, 69,
            71, 72, 74, 71,
            67, 69, 71, 67            
        };
        
        
        Track t1 = seq.getTracks()[1];
        Track t2 = seq.getTracks()[2];
        
        // make sure we have the right number of events. 
        assertTrackHasRightNumEvents(t1, track1PitchNumbers.length);
        assertTrackHasRightNumEvents(t2, track2PitchNumbers.length);
        
        for (int i = 0; i < track1PitchNumbers.length; i++) {
            assertTrackMidiNoteEqual(t1, i, i * 4, 4, track1PitchNumbers[i], MidiNote.DEFAULT_VELOCITY, 0);
        }
        
        for (int i = 0; i < track2PitchNumbers.length; i++) {
            assertTrackMidiNoteEqual(t2, i, i * 4, 4, track2PitchNumbers[i], MidiNote.DEFAULT_VELOCITY, 1);
        }
    }
    
    @Test
    public void complicatedChromaticSelfSimilarityTest() throws Exception {
        
        FractalPiece fp = new FractalPiece();
        fp.setScale(new MajorScale(NoteName.C));
        fp.setGermString("C4,1/8 G4 F#4 B4 A#4 Eb4 D4 B3");
        
        fp.setGenerateLayeredIntro(false);
        fp.setGenerateLayeredOutro(false);
        
        Voice v = fp.createVoice();
        v.getSettings().setSelfSimilaritySettings(new SelfSimilaritySettings(true, false, true, 1));
        fp.createDefaultSections();
        
        Sequence seq = fp.createPieceResultOutputManager().getSequence();
        Track t = seq.getTracks()[1];
        int[] pitchNumbers = {
            // midi numbers taken from http://pages.cs.wisc.edu/~suan/misc/midi.html
            // self-similar based on germ...
            60, 67, 66, 71, 70, 63, 62, 59,
            67, 74, 73, 77, 76, 70, 69, 65,
            66, 73, 72, 77, 76, 69, 68, 65,            
            71, 77, 76, 81, 80, 73, 72, 69,
            70, 77, 76, 80, 79, 73, 72, 68,
            63, 70, 69, 73, 72, 65, 64, 61,
            62, 69, 68, 72, 71, 65, 64, 60,
            59, 65, 64, 69, 68, 61, 60, 57,
            
            // inversion
            60, 53, 54, 50, 51, 58, 59, 62,
            53, 47, 48, 43, 44, 51, 52, 55,
            54, 47, 48, 44, 45, 51, 52, 56,
            50, 43, 44, 40, 41, 47, 48, 52,
            51, 44, 45, 40, 41, 48, 49, 52,
            58, 51, 52, 48, 49, 55, 56, 60,
            59, 52, 53, 48, 49, 56, 57, 60,
            62, 55, 56, 52, 53, 59, 60, 64,
            
            // retrograde inversion
            62, 59, 58, 51, 50, 54, 53, 60,
            59, 55, 54, 48, 47, 51, 50, 57,
            58, 54, 53, 47, 46, 50, 49, 56,
            51, 47, 46, 40, 39, 43, 42, 49,
            50, 47, 46, 39, 38, 42, 41, 48,
            54, 51, 50, 43, 42, 47, 46, 52,
            53, 50, 49, 42, 41, 46, 45, 52,
            60, 57, 56, 49, 48, 53, 52, 59,
                                   
            // retrograde
            59, 62, 63, 70, 71, 66, 67, 60,
            62, 65, 66, 73, 74, 70, 71, 64, 
            63, 66, 67, 74, 75, 70, 71, 64,
            70, 73, 74, 81, 82, 77, 78, 72,
            71, 74, 75, 82, 83, 78, 79, 72,
            66, 70, 71, 77, 78, 74, 75, 68,
            67, 71, 72, 78, 79, 75, 76, 69,
            60, 64, 65, 71, 72, 68, 69, 62
        };
        
        // make sure we have the right number of events. 
        assertTrackHasRightNumEvents(t, pitchNumbers.length);
        
        for (int i = 0; i < pitchNumbers.length; i++) {            
            assertTrackMidiNoteEqual(t, i, i * 4, 4, pitchNumbers[i], MidiNote.DEFAULT_VELOCITY, 0);
        }                
    }
    
    public static void assertTrackMidiNoteEqual(Track t, int noteIndex, long tick, long duration, int pitchNum, int velocity, int channelNum) {
        MidiEvent noteOnEvent = t.get(2 * noteIndex + 1);
        MidiEvent noteOffEvent = t.get((2 * noteIndex) + 2);
        
        byte noteOnEventByte1 = (byte) (-112 + channelNum);
        byte noteOffEventByte1 = (byte) (-128 + channelNum);        
        
        //System.out.println(String.format("assertTrackMidiNoteEqual index: %d", noteIndex));
        
        MidiNoteTest.assertNoteEventEqual(noteOnEvent, tick, noteOnEventByte1, (byte) pitchNum, (byte) velocity);
        MidiNoteTest.assertNoteEventEqual(noteOffEvent, tick + duration, noteOffEventByte1, (byte) pitchNum, (byte) 0);
    }
    
    public static void assertTrackHasRightNumEvents(Track t, int numNotes) {           
        int numNoteEvents = numNotes * 2; // note on and note off events
        // there are always two extra events - instrument event, and the end-of-track event
        assertEquals(numNoteEvents + 2, t.size());
    }
            
}