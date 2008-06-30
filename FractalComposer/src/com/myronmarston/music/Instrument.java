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

package com.myronmarston.music;

import javax.sound.midi.*;
import java.util.*;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * Represents a midi instrument.  Cannot be instantiated directly.  Instead,
 * get an instrument using the static getInstrument() method.
 * 
 * @author Myron
 */
public class Instrument {
    private final javax.sound.midi.Instrument midiInstrument;
    private static final int REGULAR_INSTRUMENT_BANK = 0;    
    private static final Map<String, Instrument> INSTRUMENT_MAP;
    
    /**
     * The default instrument (a piano).
     */
    public static final Instrument DEFAULT;
    
    /**
     * List of possible instruments.
     */
    public static final List<String> AVAILABLE_INSTRUMENTS;
    
    private Instrument(javax.sound.midi.Instrument midiInstrument) {
        this.midiInstrument = midiInstrument;        
    }
    
    /**
     * Initializes our list and hash map of instruments.
     */
    static {
        HashMap<String, Instrument> map = new HashMap<String, Instrument>();            
        List<String> list = new ArrayList<String>();
        String trimmedName;
           
        for (javax.sound.midi.Instrument i : AudioFileCreator.SOUNDBANK.getInstruments()) {
            // there are several hundred instruments, but we only care about the
            // "regular" ones like piano, violin, cello, etc.
            if (i.getPatch().getBank() == REGULAR_INSTRUMENT_BANK) { 
                trimmedName = i.getName().trim(); // many instruments have extra spaces on their name
                map.put(trimmedName.toLowerCase(Locale.ENGLISH), new Instrument(i));
                list.add(trimmedName);
            }
        }

        INSTRUMENT_MAP = Collections.unmodifiableMap(map);            
        AVAILABLE_INSTRUMENTS = Collections.unmodifiableList(list);
        
        Instrument temp = null;
        // try to get a piano as the default instrument...
        for (String instrumentName : AVAILABLE_INSTRUMENTS) {
            if (instrumentName.contains("Piano")) {
                temp = getInstrument(instrumentName);
                break;
            }
        }
        
        // otherwise, just pick the first instrument...
        if (temp == null) temp = getInstrument(list.get(0));
        
        DEFAULT = temp;        
        assert DEFAULT != null : "The default instrument could not be found.";
    }

    /**
     * Gets the midi instrument.
     * 
     * @return the midi instrument object
     */
    protected javax.sound.midi.Instrument getMidiInstrument() {
        return midiInstrument;
    }        
    
    /**
     * Gets the instrument with the given name.
     * 
     * @param name the case-insensitive name of the instrument
     * @return the instrument, or null, if none was found with the given name
     */    
    public static Instrument getInstrument(String name) {
        return INSTRUMENT_MAP.get(name.trim().toLowerCase(Locale.ENGLISH));
    }
    
    /**
     * Gets the name of the instrument.
     * 
     * @return the name of the instrument
     */
    public String getName() {
        return this.midiInstrument.getName().trim();
    }
    
    /**
     * Gets a string representing this instrument in GUIDO notation.
     * 
     * @return the guido string
     */
    public String toGuidoString() {
        return "\\instr<\"" + this.getName() + "\", \"MIDI " + this.getMidiInstrument().getPatch().getProgram() + "\">";
    }
    
    /**
     * Creates a midi program change event on the given channel using this 
     * instrument.
     * 
     * @param midiChannel the channel to use (0-15)
     * @return the program change midi event
     */
    public MidiEvent getProgramChangeMidiEvent(int midiChannel) {
        if (midiChannel < MidiNote.MIN_CHANNEL || midiChannel > MidiNote.MAX_CHANNEL) {
            throw new IllegalArgumentException(String.format("The midi channel must be between %d and %d.", MidiNote.MIN_CHANNEL, MidiNote.MAX_CHANNEL));
        }            
        
        ShortMessage msg = new ShortMessage();
        try {
            msg.setMessage(ShortMessage.PROGRAM_CHANGE, midiChannel, this.midiInstrument.getPatch().getProgram(), midiChannel);
        } catch (InvalidMidiDataException ex) {
            throw new UndeclaredThrowableException(ex, "The program change midi event could not be created for an unknown reason.  This indicates a programming error.");
        }     
        
        return new MidiEvent(msg, 0);
    }
}
