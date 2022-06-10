package Shazam.Audio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.sound.sampled.*;

public class MakeSound {

    public static int BUFFER_SIZE = 128000;
    private URL soundFile;
    private File soundFile1;
    private AudioInputStream audioStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceLine;

    private BufferedInputStream getStream(String file) throws UnsupportedAudioFileException, IOException{
        URL url  = new URL(file);
        URLConnection conn  = url.openConnection();
        InputStream stream = conn.getInputStream();
        BufferedInputStream streamb = new BufferedInputStream(stream);

        return streamb;
    }
    public double time() throws UnsupportedAudioFileException, IOException {

        AudioFormat format = audioStream.getFormat();
        long frames = audioStream.getFrameLength();
       return (frames+0.0) / format.getFrameRate();
    }


    public int playSound(String filename,double timeStart,int delta, boolean countTime) throws UnsupportedAudioFileException, IOException {

        try {
            audioStream = AudioSystem.getAudioInputStream(new File(filename));
        } catch (Exception e){
            try {
                audioStream = AudioSystem.getAudioInputStream(getStream(filename));
            } catch (Exception er){
                er.printStackTrace();
                System.exit(1);
            }
        }

        int full = (int)time();
        double extra = (double)delta/(double)full;

        timeStart += extra;

        System.out.println("Czas: "+(timeStart*full)+"s");

        return playing((int)(timeStart*full),countTime);
    }

    private int playing(int timeStart,boolean countTime){

        audioFormat = audioStream.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        sourceLine.start();

        int time = 0;
        int nBytesRead = 0;
        byte[] abData = new byte[BUFFER_SIZE];
        while (nBytesRead != -1) {
            try {
                nBytesRead = audioStream.read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0 && time >timeStart && !countTime) {
                @SuppressWarnings("unused")
                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
            }

            time += 1;
        }


        sourceLine.drain();
        sourceLine.close();

        return time;
    }
}