package Shazam.Audio;

import javax.sound.sampled.*;
import java.io.*;

public class AudioRecorder {
    // record duration, in milliseconds
    static final long RECORD_TIME = 10 * 1000;  // 2.5 ms

    // path of the wav file


    // format of audio file
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    // the line from which audio data is captured
    TargetDataLine line;

    /**
     * Defines an audio format
     */
    AudioFormat getAudioFormat() {
        float sampleRate = 44100;
        int sampleSizeInBits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
        return format;
    }

    /**
     * Captures the sound and record into a WAV file
     */
    void start(int n) {
        try {

            File wavFile = new File("recorded"+n+".wav");

            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();   // start capturing



            AudioInputStream ais = new AudioInputStream(line);


            // start recording
            AudioSystem.write(ais, fileType, wavFile);

        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Closes the target data line to finish capturing and recording
     */
    void finish() {
        line.stop();
        line.close();
    }

    /**
     * Entry to run the program
     */


    public static void record(int n) {
        final AudioRecorder recorder = new AudioRecorder();


        // creates a new thread that waits for a specified
        // of time before stopping


        Thread stopper = new Thread(new Runnable() {

            public void run() {
                try {
                        Thread.sleep(RECORD_TIME);

                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                recorder.finish();
            }
        });

        stopper.start();

        // start recording
        recorder.start(n);

    }
}
