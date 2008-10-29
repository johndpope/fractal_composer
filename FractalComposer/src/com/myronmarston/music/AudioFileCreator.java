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

import com.myronmarston.util.FileHelper;

import com.sun.media.sound.*;
import java.io.*;
import javax.sound.midi.*;
import javax.sound.sampled.*;
import org.tritonus.share.sampled.*;

/**
 * This class is used to create audio files, such as WAV files and mp3's.  It
 * uses the Gervill, Tritonus and LAME open source projects.
 * 
 * @author Myron
 */
public class AudioFileCreator {
    private final OutputManager outputManager;    
    private static final AudioFormat.Encoding MPEG1L3 = Encodings.getEncoding("MPEG1L3");
    private static final AudioFileFormat.Type MP3 = AudioFileTypes.getType("MP3", "mp3");
    private static final double MICROSECONDS_PER_SECOND = 1000000.0;
        
    /**
     * Constructor.
     * 
     * @param outputManager the output manager containing the midi sequence
     * to convert to a wav or mp3 file.
     */
    public AudioFileCreator(OutputManager outputManager) {
        this.outputManager = outputManager;
    }
                   
    /**
     * Converts the output to a wav file and saves it to disk.
     * 
     * @param wavFileName the file name to save to
     * @throws javax.sound.midi.MidiUnavailableException if there is a midi 
     *         error
     * @throws java.io.IOException if there is an I/O error
     */
    protected void saveWavFile(final String wavFileName) throws MidiUnavailableException, IOException {
        AudioSynthesizer synth = null;
        AudioInputStream stream1 = null;
        AudioInputStream stream2 = null;        
        try {
            synth = AudioFileCreator.getAudioSynthesizer();
            synth.loadAllInstruments(MidiSoundbank.getCurrent().getSoundbank());
            
            // Open AudioStream from AudioSynthesizer with default values
            stream1 = synth.openStream(null, null);            

            // Play Sequence into AudioSynthesizer Receiver.
            double totalLength = this.sendOutputSequenceMidiEvents(synth.getReceiver());

            // give it an extra 2 seconds, to the reverb to fade out--otherwise it sounds unnatural
            totalLength += 2;
            // Calculate how long the WAVE file needs to be.
            long len = (long) (stream1.getFormat().getFrameRate() * totalLength);
            stream2 = new AudioInputStream(stream1, stream1.getFormat(), len);
            
            // Write the wave file to disk
            AudioSystem.write(stream2, AudioFileFormat.Type.WAVE, new File(wavFileName));            
        } finally {
            if (stream1 != null) stream1.close();
            if (stream2 != null) stream2.close();
            if (synth != null) synth.close();
        }
    }
    
    /**
     * Saves the music as a mp3 file.
     * 
     * @param mp3FileName the file name to save to
     * @throws java.lang.Exception if there is an error
     */
    protected void saveMp3File(final String mp3FileName) throws Exception {
        FileHelper.createAndUseTempFile("TempWav", ".wav", new FileHelper.TempFileUser() {
            public void useTempFile(String tempFileName) throws Exception {    
                AudioFileCreator.this.saveWavFile(tempFileName);                
                AudioFileCreator.convertWavToMp3(tempFileName, mp3FileName);
            }
        });
    }
    
    /**
     * Converts the given wav file to an mp3 file.
     * 
     * @param wavFileName the wav file
     * @param mp3FileName the mp3 file
     * @return the number of bytes written to the file
     * @throws javax.sound.sampled.UnsupportedAudioFileException if the given
     *         wav file is in an unsupported format
     * @throws java.io.IOException if there is an I/O error
     */
    private static int convertWavToMp3(String wavFileName, String mp3FileName) throws UnsupportedAudioFileException, IOException {				
        AudioInputStream streamToConvert = null;
        AudioInputStream streamThatCanConvertToMp3 = null;
        AudioInputStream mp3Stream = null;
        
        try {
            streamToConvert = AudioSystem.getAudioInputStream(new File(wavFileName));    
            streamThatCanConvertToMp3 = AudioFileCreator.getStreamThatCanConvertToMp3(streamToConvert);                
            mp3Stream =  AudioSystem.getAudioInputStream(MPEG1L3, streamToConvert);          
            return AudioSystem.write(mp3Stream, MP3, new File(mp3FileName));
        } finally {
            if (streamToConvert != null) streamToConvert.close();
            if (streamThatCanConvertToMp3 != null) streamThatCanConvertToMp3.close();
            if (mp3Stream != null) mp3Stream.close();
        }        
    }    
    
    /**
     * Gets a stream that can be converted to mp3.  This might be the given 
     * stream or an intermediary stream.
     * 
     * @param streamToConvert the input stream containing PCM data
     * @return a stream that can be converted to mp3 format
     */
    private static AudioInputStream getStreamThatCanConvertToMp3(AudioInputStream streamToConvert) {
        AudioFormat sourceFormat = streamToConvert.getFormat();
        if (AudioSystem.isConversionSupported(MPEG1L3, sourceFormat)) return streamToConvert;
        
        // direct conversion to mp3 is not possible; try an intermediate PCM format
        AudioFormat intermediateFormat = new AudioFormat(
             AudioFormat.Encoding.PCM_SIGNED,
             sourceFormat.getSampleRate(),
             16,
             sourceFormat.getChannels(),
             2 * sourceFormat.getChannels(), // frameSize
             sourceFormat.getSampleRate(),
             false);

        return AudioSystem.getAudioInputStream(intermediateFormat, streamToConvert);        
    }
    
    /**
     * Gets the audio synthesizer.
     * 
     * @return the audio synthesizer
     * @throws javax.sound.midi.MidiUnavailableException if the audio 
     *         synthesizer cannot be found
     */
    protected static AudioSynthesizer getAudioSynthesizer() throws MidiUnavailableException {
        // First check if default synthesizer is AudioSynthesizer.
        Synthesizer synth = MidiSystem.getSynthesizer();
        if (synth instanceof AudioSynthesizer) return (AudioSynthesizer) synth;

        // now check the others...        
        for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
            MidiDevice device = MidiSystem.getMidiDevice(info);
            if (device instanceof AudioSynthesizer) return (AudioSynthesizer) device;
        }

        throw new MidiUnavailableException("The AudioSynthesizer is not available.");                      
    }    
    
    /**
     * Sends the outputManager's midi sequence events to the given receiver.
     * 
     * @param receiver the receiver
     * @return the length of the audio, in seconds
     */
    private double sendOutputSequenceMidiEvents(Receiver receiver) {
        Sequence sequence = this.outputManager.getSequence();
        // this method is only designed to handle the PPQ division type.
        assert sequence.getDivisionType() == Sequence.PPQ : sequence.getDivisionType();                
                                                              
        int microsecondsPerQtrNote = Tempo.convertToMicrosecondsPerQuarterNote(this.outputManager.getFractalPiece().getTempo());
        int seqRes = sequence.getResolution();
        long totalTime = 0;
        
        for (Track track : sequence.getTracks()) {
            long lastTick = 0;
            long curTime = 0;
            
            for (int i = 0; i < track.size(); i++) {                            
                MidiEvent event = track.get(i);
                long tick = event.getTick();
                curTime += ((tick - lastTick) * microsecondsPerQtrNote) / seqRes;            
                lastTick = tick;
                MidiMessage msg = event.getMessage();
                if (!(msg instanceof MetaMessage)) {                                
                    receiver.send(msg, curTime);
                }                
            }
            
            // make the total time be the time of the langest track
            totalTime = Math.max(curTime, totalTime);
        }
        
        return totalTime / MICROSECONDS_PER_SECOND;
    }
}
