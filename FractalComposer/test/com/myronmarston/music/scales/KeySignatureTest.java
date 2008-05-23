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
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author Myron
 */
public class KeySignatureTest {

    public KeySignatureTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Test
    public void getKeySignatureMidiEvent() throws InvalidKeySignatureException {
        KeySignature ks = new KeySignature(Tonality.Major, NoteName.E);        
        assertKeySignatureEventEqual(ks.getKeySignatureMidiEvent(), (byte) 4, (byte) 0);
        
        ks = new KeySignature(Tonality.Major, NoteName.Bb);        
        assertKeySignatureEventEqual(ks.getKeySignatureMidiEvent(), (byte) -2, (byte) 0);
        
        ks = new KeySignature(Tonality.Minor, NoteName.Bb);        
        assertKeySignatureEventEqual(ks.getKeySignatureMidiEvent(), (byte) -5, (byte) 1);
    }
    
    @Test(expected=InvalidKeySignatureException.class)
    public void majorScaleInvalidKeySignature() throws InvalidKeySignatureException {
        MajorScale s = new MajorScale(NoteName.As);
    }
    
    @Test(expected=InvalidKeySignatureException.class)
    public void minorScaleInvalidKeySignature() throws InvalidKeySignatureException {
        MinorScale s = new MinorScale(NoteName.Gb);
    }
    
    @Test
    public void majorScaleGetKeySignature() throws InvalidMidiDataException, InvalidKeySignatureException {
        MajorScale s = new MajorScale(NoteName.D);
        assertKeySignatureEventEqual(s.getKeySignature().getKeySignatureMidiEvent(), (byte) 2, (byte) 0);
        
        s = new MajorScale(NoteName.Ab);
        assertKeySignatureEventEqual(s.getKeySignature().getKeySignatureMidiEvent(), (byte) -4, (byte) 0);
    }
    
    @Test
    public void minorScaleGetKeySignature() throws InvalidMidiDataException, InvalidKeySignatureException {
        MinorScale s = new MinorScale(NoteName.D);
        assertKeySignatureEventEqual(s.getKeySignature().getKeySignatureMidiEvent(), (byte) -1, (byte) 1);
        
        s = new MinorScale(NoteName.Gs);
        assertKeySignatureEventEqual(s.getKeySignature().getKeySignatureMidiEvent(), (byte) 5, (byte) 1);
    }

    static public void assertKeySignatureEventEqual(MidiEvent keySignatureEvent, byte byte1, byte byte2) {
        assertEquals(0L, keySignatureEvent.getTick());
        javax.sound.midi.MidiMessage msg = keySignatureEvent.getMessage();
        assertEquals(5, msg.getLength());
        assertEquals((byte) 0xFF, msg.getMessage()[0]); // 0xFF for a meta message
        assertEquals((byte) 89, msg.getMessage()[1]);   // 89 is key signature type
        assertEquals((byte) 2, msg.getMessage()[2]);    // the size of the rest of the message     
        assertEquals(byte1, msg.getMessage()[3]);        
        assertEquals(byte2, msg.getMessage()[4]);        
    }
}