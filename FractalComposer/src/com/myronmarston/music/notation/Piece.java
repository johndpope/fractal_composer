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

package com.myronmarston.music.notation;

import com.myronmarston.music.scales.KeySignature;
import com.myronmarston.music.settings.TimeSignature;
import com.myronmarston.util.FileHelper;
import com.myronmarston.util.Fraction;

import java.util.*;

/**
 * This class contains the notation of an entire fractal piece.
 * 
 * @author Myron
 */
public class Piece {
    private final TimeSignature timeSignature;
    private final KeySignature keySignature;
    private final int tempo;
    private final NotationElementList parts = new NotationElementList();
    private final boolean includeTempo;
    private final boolean includeInstruments;
  
    /**
     * Constructor.
     * 
     * @param keySignature the key signature of the piece
     * @param timeSignature the time signature of the piece
     * @param tempo the tempo of the piece, in beats per minute
     * @param includeTempo whether or not to include a tempo marking on the 
     *        notation
     * @param includeInstrument whether or not to include the instrument name
     *        on the notation
     */
    public Piece(KeySignature keySignature, TimeSignature timeSignature, int tempo, boolean includeTempo, boolean includeInstrument) {
        this.keySignature = keySignature;
        this.timeSignature = timeSignature;                
        this.tempo = tempo;
        this.includeTempo = includeTempo;
        this.includeInstruments = includeInstrument;
    }   
    
    /**
     * Gets the key signature of this piece.
     * 
     * @return the key signature
     */
    public KeySignature getKeySignature() {
        return keySignature;
    }

    /**
     * Gets the time signature of this piece.
     * 
     * @return the time signature
     */
    public TimeSignature getTimeSignature() {
        return timeSignature;
    }

    /**
     * Gets the tempo, in beats per minute, of this piece.
     * 
     * @return the tempo, in beats per minute
     */
    public int getTempo() {
        return tempo;
    }

    /**
     * Gets whether or not to include the instruments in the output.
     * 
     * @return true if the instruments should be included in the output
     */
    public boolean getIncludeInstruments() {
        return includeInstruments;
    }

    /**
     * Gets whether or not the tempo should be included in the output.
     * 
     * @return true if the tempo should be included in hte output
     */
    public boolean getIncludeTempo() {
        return includeTempo;
    }
        
    /**
     * Gets the instrumental parts of this piece.
     * 
     * @return the instrumental parts
     */
    public NotationElementList getParts() {
        return parts;
    }           
            
    /**
     * Gets the lilypond notation of this piece.
     * 
     * @param title the title of the piece
     * @param composer the composer of the piece
     * @return the lilypond string
     */
    public String toLilypondString(String title, String composer) {
        this.scaleDurationsIfNecessary();        
        this.getParts().setElementSeperator(FileHelper.NEW_LINE);
        
        StringBuilder str = new StringBuilder();
        str.append("\\version \"2.11.47\"" + FileHelper.NEW_LINE + FileHelper.NEW_LINE);
        str.append("\\include \"english.ly\"" + FileHelper.NEW_LINE + FileHelper.NEW_LINE);
        str.append("\\header {" + FileHelper.NEW_LINE);
        if (title != null && !title.isEmpty()) str.append("  title = \"" + title + "\"" + FileHelper.NEW_LINE);
        if (title != null && !composer.isEmpty()) str.append("  composer = \"" + composer + "\"" + FileHelper.NEW_LINE);
        str.append("  copyright = \"Copyright " + Calendar.getInstance().get(Calendar.YEAR) + ",  fractalcomposer.com\"" + FileHelper.NEW_LINE);
        str.append("}" + FileHelper.NEW_LINE + FileHelper.NEW_LINE);                
        str.append("\\score {" + FileHelper.NEW_LINE);        
        str.append("        \\new StaffGroup <<" + FileHelper.NEW_LINE);
        str.append(this.getParts().toLilypondString());
        str.append("        >>" + FileHelper.NEW_LINE);
        str.append("   \\layout { }" + FileHelper.NEW_LINE);
        str.append("}");                        
        
        return str.toString();
    }   
    
    /**
     * Gets the GUIDO notation for this piece.  No title or composer will be
     * included in the output.
     * 
     * @return the guido notation for this piece
     */
    public String toGuidoString() {
        return toGuidoString(null, null);
    }
    
    /**
     * Gets the GUIDO notation for this piece.
     * 
     * @param title the title of the piece
     * @param composer the composer of the piece
     * @return the guido string
     */
    public String toGuidoString(String title, String composer) {                
        this.scaleDurationsIfNecessary();
        this.getParts().setElementSeperator("," + FileHelper.NEW_LINE);
        
        Part firstPart = null;
        for (NotationElement element : this.getParts()) {
            if (element instanceof Part) {
                firstPart = (Part) element;
                break;
            } 
        }
            
        if (firstPart != null) {
            firstPart.setPieceTitle(title);
            firstPart.setPieceComposer(composer);
        }
                
        StringBuilder str = new StringBuilder();
        str.append("{" + FileHelper.NEW_LINE);        
        str.append(this.getParts().toGuidoString());        
        str.append(FileHelper.NEW_LINE + "}");
        return str.toString();
    }
    
    /**
     * If there are any note durations that have denominators too large for 
     * Guido and Lilypond to handle, this will scale all the duration values
     * so that Guido and Lilypond can work properly.
     */
    protected void scaleDurationsIfNecessary() {                
        int powersOf2 = 0;
        long longestDurationDenominator = this.getParts().getLargestDurationDenominator();
        
        while (longestDurationDenominator > Fraction.MAX_ALLOWED_DURATION_DENOM) {
            longestDurationDenominator >>= 1;
            powersOf2++;
        }               
        
        if (powersOf2 > 0) {
            long scaleFactor = 1 << powersOf2;
            this.getParts().scaleDurations(scaleFactor);
        }
        
        assert this.getParts().getLargestDurationDenominator() <= Fraction.MAX_ALLOWED_DURATION_DENOM : this.getParts().getLargestDurationDenominator();
    }
        
}