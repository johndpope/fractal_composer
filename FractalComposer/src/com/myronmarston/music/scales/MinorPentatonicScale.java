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

import com.myronmarston.music.NoteName;
import java.util.Arrays;
import org.simpleframework.xml.*;

/**
 * A 5-note scale with minor tonality.
 * 
 * @author Myron
 */
@Root
public class MinorPentatonicScale extends NaturalMinorScale {
    private final static int[] SCALE_STEPS = new int[] {0, 3, 5, 7, 10};
    private final static int[] LETTER_NUMBERS = new int[] {0, 2, 3, 4, 6};
    
   /**
     * Constructor.
     * 
     * @param keyName the name of the tonal center
     * @throws com.myronmarston.music.scales.InvalidKeySignatureException thrown
     *         when the key is invalid
     */
    public MinorPentatonicScale(NoteName keyName) throws InvalidKeySignatureException {
        super(keyName);
    }
    
    /**
     * Provided to allow xml deserialization.
     * 
     * @throws com.myronmarston.music.scales.InvalidKeySignatureException thrown
     *         when the key is invalid.
     */
    public MinorPentatonicScale() throws InvalidKeySignatureException {
        super();
    }

    @Override
    public int[] getScaleStepArray() {
        return Arrays.copyOf(SCALE_STEPS, SCALE_STEPS.length);
    }  
    
    @Override
    public int[] getLetterNumberArray() {
        return Arrays.copyOf(LETTER_NUMBERS, LETTER_NUMBERS.length);        
    } 
}
