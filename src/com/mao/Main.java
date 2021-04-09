// Omar/Omustardo - 2/23/2014
// Encodes a message from a text file or command line into a .wav file, and decoding a wav into a text file

// Remember that the audio length must be large enough to store the message. 2756 characters can be stored in every second of audio data.
// If you select the generate static option rather than selecting your own file, it will automatically make a file that's long enough.
package com.mao;

import com.binary.Binary;
import com.binary.BinaryTool;
import com.dsss.Encoder;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.Glide;
import net.beadsproject.beads.ugens.WavePlayer;
import sun.audio.AudioData;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

import javax.sound.sampled.*;

public class Main {

    private static final int NUMBER_OF_CHANNELS = 1;
    private static final int BITS_PER_SAMPLE = 16;
    private static final int SAMPLE_RATE = 8000; // 44100 if you want a really nice, clean sin wave, but then you must change FFT_SIZE to at least 16384 too

    public void soundToBinary() throws IOException {
        File fileSound = new File("file/sample3.wav");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileSound));

        int read;
        byte[] buff = new byte[1024];
        while ((read = in.read(buff)) > 0)
        {
            out.write(buff, 0, read);
        }
        out.flush();
        byte[] audioBytes = out.toByteArray();

        for(byte b : audioBytes){
            System.out.println(b);
        }
    }

    public static void main (String [] args) throws Exception {

        File file = new File("file/original.wav");
        String message = "hello mao";
        long key = 123;

        Encoder encoder = new Encoder(message,key,file);

        encoder.encode();

    }



}