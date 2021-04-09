package com.dsss;

import com.mao.WaveDecoder;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Random;

public class Common {

    public static ArrayList<Float> getWaveFromAudio(File audio) throws Exception {
        WaveDecoder decoder = new WaveDecoder( new FileInputStream( audio ));
        float[] samples = new float[1024];
        ArrayList<Float> originalWaveSamples = new ArrayList<Float>();

        int readSamples = 0;
        while( ( readSamples = decoder.readSamples( samples ) ) > 0 ){
            for (float b : samples){
                originalWaveSamples.add(b);
            }
        }
        return originalWaveSamples;
    }

    public static int[] pnSequenceKey(long key,int audioSize){
        int[] pn = new int[audioSize];
        Random random = new Random(key);

        for(int i = 0;i < audioSize ; i++){
            int rand = random.nextInt() % 2  == 0 ? 1 : -1;
            pn[i] = rand;
        }
        return pn;
    }

}
