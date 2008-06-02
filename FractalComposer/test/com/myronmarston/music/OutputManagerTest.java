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

import com.myronmarston.music.scales.*;
import com.myronmarston.music.settings.*;
import com.myronmarston.util.*;
import java.io.*;
import java.util.*;
import javax.sound.midi.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Myron
 */
public class OutputManagerTest {

    private OutputManager outputManager;
    
    @Before
    public void setUp() throws Exception {
        FractalPiece fp = new FractalPiece();
        // set the germ string so that we don't get a GermIsEmptyException...
        fp.setGermString("C4");
        NoteList nl = NoteList.parseNoteListString("D4 E4 C4", Scale.DEFAULT);
        
        outputManager = new OutputManager(fp, Arrays.asList(nl));
    } 
    
    @Test
    public void getGuidoNotation() throws Exception {
        String guidoNotationFormatString = "{[ \\pageFormat<\"A4\",10pt,10pt,10pt,10pt> %s\\key<\"C\"> \\meter<\"C\"> %s d1/4  e1/4  c1/4  ]}";
        String instrumentString = "\\instr<\"Piano\", \"MIDI 0\"> ";
        String tempoString = "\\tempo<\"Andante\",\"1/4=90\"> ";
        String fullGuidoString = String.format(guidoNotationFormatString, instrumentString, tempoString);
        String noInstrOrTempoGuidoString = String.format(guidoNotationFormatString, "", "");
                   
        assertEquals(fullGuidoString, outputManager.getGuidoNotation());
                       
        OutputManager om = new OutputManager(this.outputManager.getFractalPiece(), this.outputManager.getNoteLists(), false, false);        
        assertEquals(noInstrOrTempoGuidoString, om.getGuidoNotation());
    }

    @Test
    public void getSequence() throws Exception {                
        // We'll just check that we have a sequence object, since
        // other tests check the actual contents of the midi sequence to make 
        // sure it's correct.
        assertNotNull(this.outputManager.getSequence());
    }

    @Test
    public void getSheetMusicCreator() throws Exception {
        // we have other tests to test that the sheet music creator works properly...
        assertNotNull(this.outputManager.getSheetMusicCreator());
    }

    @Test
    public void saveMidiFile() throws Exception {
        FileHelper.createAndUseTempFile("TestMidiFile", ".mid", new FileHelper.TempFileUser() {
            public void useTempFile(String tempFileName) throws Exception {
                OutputManagerTest.this.outputManager.saveMidiFile(tempFileName);
                
                // this will throw an exception if a valid midi file was not saved...
                Sequence seq = MidiSystem.getSequence(new File(tempFileName)); 
            }
        });
    }
    
    @Test(expected=GermIsEmptyException.class)
    public void errorIfGermIsEmpty() throws Exception {
        FractalPiece fp = new FractalPiece();        
        OutputManager om = new OutputManager(fp, this.outputManager.getNoteLists());
    }

    @Test
    public void multipleKeySignature() throws Exception {
        FractalPiece fp = new FractalPiece();     
        fp.setScale(new MajorScale(NoteName.C));
        fp.setGermString("C4");        
        fp.createVoice();
        Section s1 = fp.createSection();
        Section s2 = fp.createSection();
        Section s3 = fp.createSection();
        Section s4 = fp.createSection();
        
        s1.setScale(new MinorScale(NoteName.G));
        s2.setScale(new MajorScale(NoteName.G));
        s3.setScale(new MajorPentatonicScale(NoteName.G));
        Sequence seq = fp.createPieceResultOutputManager().getSequence();
        Track track0 = seq.getTracks()[0];
        // Note: a section w/o a scale that comes after a section w/ a scale 
        // should also have a key sig event to restore the key sig to the piece default...
        KeySignatureTest.assertKeySignatureEventEqual(KeySignatureTest.getIndexedKeySigEvent(track0, 0), (byte) 0, (byte) 0, 0L);
        KeySignatureTest.assertKeySignatureEventEqual(KeySignatureTest.getIndexedKeySigEvent(track0, 1), (byte) -2, (byte) 1, 4L);        
        KeySignatureTest.assertKeySignatureEventEqual(KeySignatureTest.getIndexedKeySigEvent(track0, 2), (byte) 1, (byte) 0, 8L);
        KeySignatureTest.assertKeySignatureEventEqual(KeySignatureTest.getIndexedKeySigEvent(track0, 3), (byte) 0, (byte) 0, 16L);                
    }
    
    @Test
    public void getMidiTickResolution() throws Exception {
        NoteList nl = NoteList.parseNoteListString("C4,1/8 C4,1/4 C4,1/16 C4,3/8 C4,1/3", new MajorScale(NoteName.C));
        OutputManager om = new OutputManager(this.outputManager.getFractalPiece(), Arrays.asList(nl));
        assertEquals(48, om.getMidiTickResolution());
    }        
}