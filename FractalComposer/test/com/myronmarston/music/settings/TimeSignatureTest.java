package com.myronmarston.music.settings;

import javax.sound.midi.MidiEvent;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Myron
 */
public class TimeSignatureTest {

    @Test(expected=NonPositiveTimeSignatureException.class)
    public void negativeNumerator() throws InvalidTimeSignatureException {
        TimeSignature ts = new TimeSignature(-3, 2);
    }
    
    @Test(expected=NonPositiveTimeSignatureException.class)
    public void negativeDenominator() throws InvalidTimeSignatureException {
        TimeSignature ts = new TimeSignature(3, -2);
    }
    
    @Test(expected=TimeSignatureDenominatorNotAPowerOf2Exception.class)
    public void denominatorNotAPowerOf2() throws InvalidTimeSignatureException {
        TimeSignature ts = new TimeSignature(4, 5);
    }
    
    @Test
    public void denominatorPowerOf2() throws InvalidTimeSignatureException {
        TimeSignature ts = new TimeSignature(4, 4);
        assertEquals(2, ts.getDenominatorPowerOf2());
        
        ts.setDenominator(8);
        assertEquals(3, ts.getDenominatorPowerOf2());
        
        ts.setDenominator(2);
        assertEquals(1, ts.getDenominatorPowerOf2());
    }
    
    @Test
    public void getMidiTimeSignatureEvent() throws InvalidTimeSignatureException {
        TimeSignature ts = new TimeSignature(4, 4);
        assertTimeSignatureEventEqual(ts.getMidiTimeSignatureEvent(), (byte) 4, (byte) 2);
        
        ts.setNumerator(6);        
        ts.setDenominator(8);        
        assertTimeSignatureEventEqual(ts.getMidiTimeSignatureEvent(), (byte) 6, (byte) 3);
                
        ts.setNumerator(15);
        ts.setDenominator(16);
        assertTimeSignatureEventEqual(ts.getMidiTimeSignatureEvent(), (byte) 15, (byte) 4);        
    }
    
    @Test
    public void getDefault() {
        TimeSignature ts = TimeSignature.DEFAULT;
        assertEquals(4, ts.getDenominator());
        assertEquals(4, ts.getNumerator());
    }
    
    @Test
    public void testToString() throws InvalidTimeSignatureException {
        assertEquals("6/8", (new TimeSignature(6, 8).toString()));
    }
    
    @Test
    public void stringConstructor() throws InvalidTimeSignatureException {
        TimeSignature ts = new TimeSignature("7/64");
        assertEquals(7, ts.getNumerator());
        assertEquals(64, ts.getDenominator());
        assertEquals(6, ts.getDenominatorPowerOf2());
        
        // test spacing
        ts = new TimeSignature("3 /4");
        assertEquals(3, ts.getNumerator());
        assertEquals(4, ts.getDenominator());
        assertEquals(2, ts.getDenominatorPowerOf2());
        
        ts = new TimeSignature("5/  16");
        assertEquals(5, ts.getNumerator());
        assertEquals(16, ts.getDenominator());
        assertEquals(4, ts.getDenominatorPowerOf2());
        
        ts = new TimeSignature("2   /    4");
        assertEquals(2, ts.getNumerator());
        assertEquals(4, ts.getDenominator());
        assertEquals(2, ts.getDenominatorPowerOf2());
    }
    
    @Test
    public void invaldTimeSignatureStrings() {
        // nonsense
        try {
            TimeSignature ts = new TimeSignature("foo");
            fail();
        } catch (InvalidTimeSignatureException ex) {}
        
        // using a space instead of a /
        try {
            TimeSignature ts = new TimeSignature("3 4");
            fail();
        } catch (InvalidTimeSignatureException ex) {}
        
        // only one digit
        try {
            TimeSignature ts = new TimeSignature("3");
            fail();
        } catch (InvalidTimeSignatureException ex) {}
        
        // no denominator
        try {
            TimeSignature ts = new TimeSignature("3/");
            fail();
        } catch (InvalidTimeSignatureException ex) {}
        
        // no numerator
        try {
            TimeSignature ts = new TimeSignature("/4");
            fail();
        } catch (InvalidTimeSignatureException ex) {}
        
        // bad denominator
        try {
            TimeSignature ts = new TimeSignature("3/15");
            fail();
        } catch (InvalidTimeSignatureException ex) {} 
        
        // only a space
        try {
            TimeSignature ts = new TimeSignature(" ");
            fail();
        } catch (InvalidTimeSignatureException ex) {} 
        
        // empty string
        try {
            TimeSignature ts = new TimeSignature("");
            fail();
        } catch (InvalidTimeSignatureException ex) {} 
        
        // empty string
        try {
            TimeSignature ts = new TimeSignature(null);
            fail();
        } catch (InvalidTimeSignatureException ex) {}
    }
    
    static public void assertTimeSignatureEventEqual(MidiEvent timeSignatureEvent, byte byte1, byte byte2) {
        assertEquals(0L, timeSignatureEvent.getTick());
        javax.sound.midi.MidiMessage msg = timeSignatureEvent.getMessage();
        assertEquals(7, msg.getLength());
        assertEquals((byte) 0xFF, msg.getMessage()[0]); // 0xFF for a meta message
        assertEquals((byte) 88, msg.getMessage()[1]);   // 88 is time signature type
        assertEquals((byte) 4, msg.getMessage()[2]);    // the size of the rest of the message     
        assertEquals(byte1, msg.getMessage()[3]);        
        assertEquals(byte2, msg.getMessage()[4]);
        assertEquals((byte) 24, msg.getMessage()[5]);
        assertEquals((byte) 8, msg.getMessage()[6]);
    }
}