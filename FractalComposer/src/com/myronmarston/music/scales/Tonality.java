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
import java.util.*;

/**
 * Enumerates the two main tonalities: Major and Minor.
 *    
 * @author Myron
 */
public enum Tonality {      
    /**
     * Major tonality.
     */    
    Major((byte) 0, NoteName.C), 

    /**
     * Minor tonality.
     */    
    Minor((byte) 1, NoteName.A);
    
    /**
     * Value for a NoteName's sharps or flats that indicates it is an invalid
     * key for a given tonality.
     */
    public final static int INVALID_KEY = Integer.MAX_VALUE;  
    private List<NoteName> validKeyNames;
    private final byte midiValue;
    private final NoteName defaultKey;

    private Tonality(byte midiValue, NoteName defaultKey) {
        this.midiValue = midiValue;
        this.defaultKey = defaultKey;
    } 
    
    /**
     * Gets the midi value for this tonality.
     * 
     * @return 0 for major, 1 for minor
     */
    public byte getMidiValue() {
        return midiValue;
    }

    /**
     * Gets the key with no sharps or flats for this tonality.
     * 
     * @return the default key
     */
    public NoteName getDefaultKey() {
        return defaultKey;
    }        
    
    /**
     * Gets whether or not the given key name is valid for this tonality.  
     * A key name is invalid if it would produce a key signature with double
     * sharps or flats, such as A# major.
     * 
     * @param key the key name to test
     * @return true if the key is valid, false if it is invalid
     */
    public boolean isValidKeyName(NoteName key) {
        return this.getSharpsOrFlatsForKeyName(key) != INVALID_KEY;
    }

    /**
     * Gets a list of all valid key names for this tonality.  Key names that
     * have double sharps or flats in their key signature are note included.
     * This also sorts the list based on the circle of 5ths.
     * 
     * @return a list of valid key names
     */
    public synchronized List<NoteName> getValidKeyNames() {
        if (validKeyNames == null) {
            validKeyNames = new ArrayList<NoteName>();
            
            for (NoteName noteName : NoteName.values()) {
                if (this.isValidKeyName(noteName)) validKeyNames.add(noteName);
            }

            // sort the list...
            Collections.sort(validKeyNames, new Comparator<NoteName>() {
                    public int compare(NoteName n1, NoteName n2) {        
                        return getSharpsOrFlatsForKeyName(n1) - getSharpsOrFlatsForKeyName(n2);
                    }
                 }
            );
        }        

        return validKeyNames;
    }
    
    /**
     * Gets the number of flats or sharps used in the key signature of the 
     * given key for this tonality.
     * 
     * @param keyName the tonal center 
     * @return the number of flats (negative) or sharps (positive)
     */
    public int getSharpsOrFlatsForKeyName(NoteName keyName) {
        switch (this) {
            case Major: return keyName.getMajorKeySharpsOrFlats();
            case Minor: return keyName.getMinorKeySharpsOrFlats();
            default: throw new AssertionError("Unknown tonality.");
        }
    }
}
