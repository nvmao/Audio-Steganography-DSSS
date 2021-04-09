package com.dsss;

import com.binary.Binary;
import com.binary.BinaryTool;
import com.mao.AudioDevice;
import com.mao.AudioWriter;
import com.mao.WaveDecoder;

import javax.sound.sampled.*;
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

    public void encode() throws Exception {

        int[] message_binary = BinaryTool.convertStringToBinary(message);
        System.out.println("message: ");
        for(int i =0 ; i < message_binary.length;i++){
            System.out.print(message_binary[i]);
        }
        System.out.println();

        for(int i =0 ; i < message_binary.length;i++){
            if(message_binary[i] == 0){
                message_binary[i] = -1;
            }
        }

        ArrayList<Float> samplesWave = Common.getWaveFromAudio(originalAudioFile);

        System.out.println(samplesWave.size());
        System.out.println(message_binary);

        int[] pnSequence = Common.pnSequenceKey(key,samplesWave.size());
        System.out.println(pnSequence.length);

        int num_per_character = samplesWave.size() / message_binary.length;

        System.out.println("wave size : " + samplesWave.size());
        System.out.println("message size: " + message_binary.length);
        System.out.println("num per char: " + num_per_character);

        ArrayList<Integer> spreadSequences = new ArrayList<Integer>();

        int current_pn = 0;
        for(int i = 0; i < message_binary.length;i++){

            int data = message_binary[i];

            for(int j = 0; j < num_per_character;j++){
                int spread = data * pnSequence[current_pn];
                current_pn++;
                spreadSequences.add(spread);
            }
        }

        System.out.println("prev spread sequences size: " + spreadSequences.size());

        if(spreadSequences.size() < samplesWave.size()){
            int diff = samplesWave.size() - spreadSequences.size();
            for(int i = 0; i < diff;i++){
                spreadSequences.add(1);
            }
        }
        System.out.println("spread sequences size: " + spreadSequences.size());
        for(int i = 0; i < spreadSequences.size();i++){
            System.out.print(spreadSequences.get(i) + " ");
        }
        System.out.println();


        // embed
        for(int i =0 ; i < samplesWave.size();i++){
            if(spreadSequences.get(i) == -1){
                samplesWave.set(i,0.0f);
            }
        }

        System.out.println("sample waves: ");
        for(int i = 0; i < 10;i++){
            System.out.print(samplesWave.get(i) + " ");
        }
        System.out.println();

//        for(int i = 0 ; i < samplesWave.size();i++){
//            if(spreadSequences.get(i) == 0){
//                samplesWave.set(i,0.0f);
//            }
//        }

        this.outputAudio(samplesWave);
    }

    private void outputAudio(ArrayList<Float> samplesWave) throws Exception {
        AudioDevice device = new AudioDevice();

        float[] samples = new float[1024];
        int slice = 0;
        for (int i = 0; i < samplesWave.size(); i++)
        {
            samples[slice] = samplesWave.get(i);
//            System.out.println(samples[slice]);
            slice++;
            if(slice == 1024){
                device.writeSamples( samples );
                samples = new float[1024];
                slice = 0;
//                System.out.println(samples.length);
            }
        }
        if(slice != 0){
            device.writeSamples( samples );
        }



        final double sampleRate = 44100.0;
        final double frequency = 440;
        final double frequency2 = 90;
        final double amplitude = 1.0;
        final double seconds = 2.0;
        final double twoPiF = 2 * Math.PI * frequency;
        final double piF = Math.PI * frequency2;

        float[] buffer = new float[71680];

//        for (int sample = 0; sample < buffer.length; sample++) {
//            double time = sample / sampleRate;
//            buffer[sample] = (float)(amplitude * Math.cos(piF * time) * Math.sin(twoPiF * time));
//        }
        int t = 0;
        for(float b : samplesWave){
            buffer[t] = b;
            t++;
        }

        final byte[] byteBuffer = new byte[buffer.length * 2];

        int bufferIndex = 0;
        for (int i = 0; i < byteBuffer.length; i++) {
            final int x = (int)(buffer[bufferIndex++] * 32767.0);

            byteBuffer[i++] = (byte)x;
            byteBuffer[i] = (byte)(x >>> 8);
        }

        File out = new File("file/encode.wav");

        final boolean bigEndian = false;
        final boolean signed = true;

        final int bits = 16;
        final int channels = 1;

        AudioFormat format = new AudioFormat((float)sampleRate, bits, channels, signed, bigEndian);
        ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
        AudioInputStream audioInputStream = new AudioInputStream(bais, format, buffer.length);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, out);
        audioInputStream.close();


//        AudioFormat format = new AudioFormat( AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, false );
//
//        AudioWriter audioWriter = new AudioWriter(new File("file/encode.wav"),format,  AudioFileFormat.Type.WAVE);
//
//        for(Byte[] b : device.getBuffer_list()){
//
//            audioWriter.write(BinaryTool.toPrimitives(b));
//        }
//        audioWriter.close();


    }

}
