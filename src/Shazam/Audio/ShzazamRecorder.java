package Shazam.Audio;

import Shazam.fingerprint.AudioFile;
import Shazam.fingerprint.hash.peak.HashedPeak;

import java.io.File;
import java.util.ArrayList;

public class ShzazamRecorder {

    ArrayList<String> hashes = new ArrayList<>();
    int k=0;

    public ShzazamRecorder(){

    }

    private void oneIter(int n) throws Exception{

        AudioRecorder.record(n);
        AudioFile  recordFile = new AudioFile(new File("recorded" + (n) + ".wav"));

        for (HashedPeak peak : recordFile.getFingerPrint().getHashes()) {
            hashes.add(peak.getHashAsHex());

        }

    }

    public void listen() throws Exception {

        oneIter(k);
    }

    public String[] getHashes(){
        String[] haS = new String[hashes.size()];

        for(int k=0;k<haS.length;k++){
            haS[k] = hashes.get(k);
        }

        return haS;
    }
}
