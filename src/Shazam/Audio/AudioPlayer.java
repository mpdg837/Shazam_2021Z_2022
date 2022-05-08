package Shazam.Audio;

public class AudioPlayer {
    public static void main(String[] args){
        MakeSound sound = new MakeSound();
        int n = sound.playSound("Samples/sample2.wav",100,false);
    }
}
