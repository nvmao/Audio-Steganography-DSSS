package com.dsss;

import com.binary.Binary;
import com.binary.BinaryTool;
import com.mao.AudioDevice;
import com.mao.AudioWriter;
import com.mao.WaveDecoder;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

public class Encoder {

    private String message;
    private File originalAudioFile;
    private long key;

    public Encoder(String message,long key, File originalAudioFile) {
        this.message = message;
        this.key = key;
        this.originalAudioFile = originalAudioFile;
    }

    private  ArrayList<Float> getWaveFromAudio() throws Exception {
        WaveDecoder decoder = new WaveDecoder( new FileInputStream( originalAudioFile ));
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

    private int[] pnSequenceKey(int audioSize){
        int[] pn = new int[audioSize];
        Random random = new Random(key);

        for(int i = 0;i < audioSize ; i++){
            int rand = random.nextInt() % 2  == 0 ? 1 : 0;
            pn[i] = rand;
        }
        return pn;
    }

    public void encode() throws Exception {

        Binary message_binary = BinaryTool.ASCIIToBinary(message);
        ArrayList<Float> samplesWave = this.getWaveFromAudio();

        System.out.println(samplesWave.size());
        System.out.println(message_binary);

        int[] pnSequence = this.pnSequenceKey(samplesWave.size());
        System.out.println(pnSequence.length);

        int num_per_character = samplesWave.size() / message_binary.length();

        System.out.println("wave size : " + samplesWave.size());
        System.out.println("message size: " + message_binary.length());
        System.out.println("num per char: " + num_per_character);

        ArrayList<Integer> spreadSequences = new ArrayList<Integer>();

        int current_pn = 0;
        for(int i = 0; i < message_binary.length();i++){

            int data = message_binary.getIntArray()[i];

            for(int j = 0; j < num_per_character;j++){
                int spread = data ^ pnSequence[current_pn];
                current_pn++;
                spreadSequences.add(spread);
            }
        }

        if(spreadSequences.size() < samplesWave.size()){
            int diff = samplesWave.size() - spreadSequences.size();
            for(int i = 0; i < diff;i++){
                spreadSequences.add(1);
            }
        }
        System.out.println("spread sequences size: " + spreadSequences.size());


        // embed
        for(int i =0 ; i < samplesWave.size();i++){
            if(spreadSequences.get(i) == 0){
                samplesWave.set(i,0.0f);
            }
        }

        this.outputAudio(samplesWave);
    }

    private void outputAudio(ArrayList<Float> samplesWave) throws Exception {
        AudioDevice device = new AudioDevice();

        float[] samples = new float[1024];
        int slice = 0;
        for (int i = 0; i < samplesWave.size(); i++)
        {
            samples[slice] = samplesWave.get(i);
            System.out.println(samples[slice]);
            slice++;
            if(slice == 1024){
                device.writeSamples( samples );
                samples = new float[1024];
                slice = 0;
                System.out.println(samples.length);
            }
        }
        if(slice != 0){
            device.writeSamples( samples );
        }

        AudioFormat format = new AudioFormat( AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, false );

        AudioWriter audioWriter = new AudioWriter(new File("file/encode.wav"),format,  AudioFileFormat.Type.WAVE);

        for(Byte[] b : device.getBuffer_list()){

            audioWriter.write(BinaryTool.toPrimitives(b));
        }
        audioWriter.close();


    }

}
