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

import org.simpleframework.xml.*;

/**
 * Contains some settings that apply to an arbitrary group of notes.  For example,
 * when pitch self-similarity is applied to a germ with an accidental, the segment
 * generated for the accidental will be assigned a SegmentSettings object with
 * a chromatic adjustment for the whole segment.
 * 
 * @author Myron
 */
@Root
public class SegmentSettings {
    @Attribute
    private int chromaticAdjustment;        
    
    /**
     * Gets the chromatic adjustment for this segment.
     * 
     * @return the chromatic adjustment for this segment; positive is sharps, 
     *         negative is flats
     */
    public int getChromaticAdjustment() {
        return chromaticAdjustment;
    }

    /**
     * Sets the chromatic adjustment for this segment.
     * 
     * @param chromaticAdjustment the chromatic adjustment for this segment; 
     *        positive is sharps, negative is flats 
     */
    public void setChromaticAdjustment(int chromaticAdjustment) {
        this.chromaticAdjustment = chromaticAdjustment;
    }    

    /**
     * Constructor.
     * 
     * @param chromaticAdjustment the chromatic adjustment for this segment; 
     *        positive is sharps, negative is flats 
     */
    public SegmentSettings(int chromaticAdjustment) {
        this.chromaticAdjustment = chromaticAdjustment;
    }

    // equals and hashcode have been generated by Netbeans IDE.
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SegmentSettings other = (SegmentSettings) obj;
        if (this.chromaticAdjustment != other.chromaticAdjustment) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.chromaticAdjustment;
        return hash;
    }        
}
