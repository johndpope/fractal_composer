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

package com.myronmarston.music.scales;

import com.myronmarston.music.Note;
import com.myronmarston.music.NoteName;

import java.util.*;
import org.simpleframework.xml.*;

/**
 * Chromatic Scale--a scale containing all 12 pitches.  This scale does not have
 * a true tonal center; instead, the key signature is set to C major just so we
 * don't have any flats or sharps in our key signature and all notes will have
 * their own accidentals.
 * 
 * @author Myron
 */
@Root
public class ChromaticScale extends Scale {
    private final static int[] SCALE_STEPS = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};        
    private final static List<NoteName> VALID_KEY_NAMES = Collections.unmodifiableList(new ArrayList<NoteName>());
    
    /**
     * Constructor.       
     *    
     * @throws InvalidKeySignatureException should never be thrown but must be
     *         declared because the super class constructor can throw it
     */
    public ChromaticScale() throws InvalidKeySignatureException {           
        super(new KeySignature(Tonality.Major, Tonality.Major.getDefaultKey()));                
    }
    
    /**
     * We have some code that uses reflection to instantiate scales, passing
     * the key name.  All other scales have this constructor, so to make this
     * one conform, we provide this one as well.  
     * 
     * @param keyName ignored
     * @throws com.myronmarston.music.scales.InvalidKeySignatureException 
     *         should never be thrown
     */
    public ChromaticScale(NoteName keyName) throws InvalidKeySignatureException {
        this();
    }
        
    @Override
    public int[] getScaleStepArray() {
        return Arrays.copyOf(SCALE_STEPS, SCALE_STEPS.length);
    }

    /**
     * Note supported by Chromatic Scale.
     * 
     * @return throws an UnsupportedOperationException
     */
    @Override
    public int[] getLetterNumberArray() {
        throw new UnsupportedOperationException("The chromatic scale does not support the letter number array.");
    }
    
    @Override    
    public NoteName getLetterNameForScaleStep(int scaleStep) throws IllegalArgumentException {
        // idea: figure this out based on the entered germ, so as to conform it to that...
        switch (scaleStep) {
            case 0: case 1:   return NoteName.C; // C, C#
            case 2:           return NoteName.D; // D
            case 3: case 4:   return NoteName.E; // Eb, E
            case 5: case 6:   return NoteName.F; // F, F#
            case 7: case 8:   return NoteName.G; // G, G#
            case 9:           return NoteName.A; // A
            case 10: case 11: return NoteName.B; // Bb, B
            default: throw new IllegalArgumentException("The scale step must be between 0 and 11.");
        }        
    }        

    @Override
    public void setNotePitchValues(Note note, NoteName noteName) {        
        // the super class setNotePitchValues() uses getLetterNumberArray(), 
        // which won't work in this case, and it's actually much easier for a
        // chromatic scale...
        this.setNotePitchValues_Helper(note, noteName, noteName.getNoteNumber());
    }

    @Override
    public List<NoteName> getValidKeyNames() {
        return VALID_KEY_NAMES;
    }        
}
