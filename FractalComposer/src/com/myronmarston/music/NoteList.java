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

import com.myronmarston.music.scales.Scale;
import com.myronmarston.music.settings.VoiceSection;
import com.myronmarston.util.Fraction;

import java.util.*;

/**
 * NoteList contains a sequence of notes.
 * 
 * @author Myron
 */
public class NoteList extends ArrayList<Note> {
    // Used to serialize the class.  Change this if the class has a change significant enough to change the way the class is serialized.
    private static final long serialVersionUID = 1L;
    
    private transient Instrument instrument;
    
    /**
     * Default constructor.
     */
    public NoteList() {
        super();
    }
    
    /**
     * Constructor.
     * 
     * @param initialCapacity initial capacity for the list
     */
    public NoteList(int initialCapacity) {
        super(initialCapacity);        
    }
    
    /**
     * Constructor. Initializes the list with the given collection.
     * 
     * @param initialCollection collection of notes to put in the list
     */
    public NoteList(Collection<Note> initialCollection) {
        super(initialCollection);
    }

    /**
     * Gets the instrument for this note list.
     * 
     * @return the instrument
     */
    public Instrument getInstrument() {
        return instrument;
    }

    /**
     * Sets the instrument to use for this note list.
     *  
     * @param instrument the instrument
     */     
    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }        
    
    /**
     * Gets the first note you can hear--the first note that's not a rest.
     * 
     * @return the first note that is audible, or null
     */
    public Note getFirstAudibleNote() {
        for (Note n : this) {
            if (!n.isRest()) {
                return n;
            }                
        }
        
        return null;
    }
    
    /**
     * Gets the total length of the note list.
     * 
     * @return the duration of the note list
     */
    public Fraction getDuration() {
        Fraction duration = new Fraction(0, 1);
        for (Note n : this) duration = duration.plus(n.getDuration());        
        return duration;        
    }
    
    /**
     * Creates a new note list based on the given note list string.
     * 
     * @param noteListString string containing space-seperated notes, each of 
     *        the form 'F#4,1/4,PP'
     * @param scale the scale to use to determine the note's pitch information
     * @return the new note list
     * @throws com.myronmarston.music.NoteStringParseException thrown if the
     *         note list string is invalid
     */
    public static NoteList parseNoteListString(String noteListString, Scale scale) throws NoteStringParseException {                
        // TODO: throw exception if the note list is just a single rest
        // TODO: run some tests to figure out what kind of maximum length I should 
        // allow based on memory requirements
        Note note = null;
        Fraction defaultDuration = null;
        Integer defaultVolume = null;                        
        StringTokenizer st = new StringTokenizer(noteListString);
        NoteList list = new NoteList();
        
        while (st.hasMoreTokens()) {
            note = Note.parseNoteString(st.nextToken(), scale, defaultDuration, defaultVolume);
            
            // get our defaults for the next note from this note...
            defaultDuration = note.getDuration();            
            if (!note.isRest()) defaultVolume = note.getVolume();
            
            list.add(note);
        }
        
        if (list.size() > 0) list.get(0).setIsFirstNoteOfGermCopy(true);
        return list;
    }
    
    /**
     * Gets a list of notes with all rests normalized.  Adjacent rests are 
     * combined into one longer rest.
     * 
     * @return list of notes with normalized rests
     */
    public NoteList getListWithNormalizedRests() {
        NoteList newList = new NoteList(this.size());
        
        if (this.size() == 1) {
            // our logic below doesn't work for the case where we have a one-note
            // list, so just make it manually here
            newList.add(this.get(0));
        } else {                    
            Note thisNote, nextNote = null;
            Fraction currentRestDuration = new Fraction(0, 1);

            for (int i = 0; i < this.size() - 1; i++) {
                thisNote = this.get(i);
                nextNote = this.get(i+1);

                if (thisNote.isRest()) {
                    currentRestDuration = currentRestDuration.plus(thisNote.getDuration());

                    if (!nextNote.isRest() || thisNote.getSourceVoiceSection() != nextNote.getSourceVoiceSection()) {
                        // the next note should not be collapsed into a rest with this one,
                        // so just create a rest with our current duration
                        getListWithNormalizedRests_createRestHelper(newList, currentRestDuration, thisNote);

                        // reset the current rest duration to zero
                        currentRestDuration = new Fraction(0, 1);
                    }
                } else {
                    // we should never have a rest duration queued up when we reach here
                    assert currentRestDuration.numerator() == 0L;
                    newList.add(thisNote);
                }
            }            

            // our nextNote should be the last note of the list
            assert nextNote == this.get(this.size() - 1);
            if (currentRestDuration.numerator() > 0L) {
                getListWithNormalizedRests_createRestHelper(newList, currentRestDuration.plus(nextNote.getDuration()), nextNote);                
            } else {
                newList.add(nextNote);
            }
        }    
                
        return newList;
    }
    
    /**
     * Helper method for getListWithNormalizedRests to create the combined
     * rest note and add it to the new list.
     * 
     * @param newList new NoteList to add the rest to
     * @param currentRestDuration the rest duration of the previous notes
     * @param restNoteWithSourceVoiceSection a rest note with the desired source voice
     *        section
     */
    private static void getListWithNormalizedRests_createRestHelper(NoteList newList, Fraction currentRestDuration, Note restNoteWithSourceVoiceSection) {
        assert restNoteWithSourceVoiceSection.isRest();
        Note rest = Note.createRest(currentRestDuration);
        rest.setSourceVoiceSection(restNoteWithSourceVoiceSection.getSourceVoiceSection());
        newList.add(rest);
    }
    
    /**
     * Updates the scale on all the notes.  This should only be called when you
     * know that all the notes of this note list have the same note list.
     * 
     * @param scale the new scale for the notes
     */
    public void updateScale(Scale scale) {
        Scale originalScale = null;
        for (Note n : this) {
            if (n.isRest()) continue;
            if (originalScale == null) originalScale = n.getScale();
            
            // we should never update the scale on all the notes if they have 
            // mixed scales--in this case, this method is being used improperly
            assert n.getScale() == originalScale : n.getScale();
            n.setScale(scale);            
        }
    }
    
    /**
     * Gets the number of notes that have chromatic adjustements.
     * 
     * @return the number of notes with chromatic adjustments
     */
    public int getNumberOfAccidentals() {
        int accidentalCount = 0;
        
        for (Note n : this) {
            if (n.getChromaticAdjustment() != 0 || n.getSegmentChromaticAdjustment() != 0) {
                accidentalCount++;                        
            }
        }
        
        return accidentalCount;
    }

    /**
     * Sets the sourceVoiceSection field on all notes of this note list to
     * the given one.
     * 
     * @param source the voice section that generated the notes in this note 
     *        list
     */
    public void setSourceVoiceSectionOnAllNotes(VoiceSection source) {
        for (Note n : this) n.setSourceVoiceSection(source);
    }
    
    /**
     * Sets which notes have the firstNoteOfGermCopy flag set to true.  The
     * notes of the indices given will have this set to true; all others will
     * be set to false.  This is not intended to be used in production but is 
     * only provided as a convenience method for unit tests.
     * 
     * @param indices list of indices of the notes that should be the first
     *        notes of a germ copy
     */
    public void setfirstNotesOfGermCopy(int ... indices) {        
        for (int i = 0; i < this.size(); i++) {
            int value = Arrays.binarySearch(indices, i);
            this.get(i).setIsFirstNoteOfGermCopy(value >= 0);
        }
    }
    
    @Override
    /**
     * Clones the note list.  Each individual note is also cloned.
     */
    public NoteList clone() {
        NoteList clone = (NoteList) super.clone();
        
        for (int i = 0; i < clone.size(); i++) {
            clone.set(i, clone.get(i).clone());
        }
        
        return clone;
    }        
}
