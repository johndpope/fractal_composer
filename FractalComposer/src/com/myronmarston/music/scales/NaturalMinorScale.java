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
 * A natural minor scale: WHWWHWW.  This is also known as Aeolian mode when 
 * analyzing music modally.
 * 
 * @author Myron
 */
@Root
public class NaturalMinorScale extends Scale {        
    private final static int[] SCALE_STEPS = new int[] {0, 2, 3, 5, 7, 8, 10};
    private final static int[] LETTER_NUMBERS = new int[] {0, 1, 2, 3, 4, 5, 6};

    /**
     * Constructor.
     * 
     * @param keyName the name of the tonal center
     * @throws com.myronmarston.music.scales.InvalidKeySignatureException thrown
     *         when the key is invalid
     */
    public NaturalMinorScale(NoteName keyName) throws InvalidKeySignatureException {        
        super(new KeySignature(Tonality.Minor, keyName));        
    }    
    
    /**
     * Provided to allow xml deserialization.
     * 
     * @throws com.myronmarston.music.scales.InvalidKeySignatureException thrown
     *         when the key is invalid.
     */
    public NaturalMinorScale() throws InvalidKeySignatureException {
        this(Tonality.Minor.getDefaultKey());
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
