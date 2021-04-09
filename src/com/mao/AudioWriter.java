
package com.mao;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class AudioWriter implements Runnable {
    AudioFormat format;
    File file;
    AudioFileFormat.Type targetType;

    SourceDataLine sdl;
    PipedOutputStream pos;
    PipedInputStream pis;
    AudioInputStream ais;
    byte[] bytes;

    // Write to a source data line.  The line should be open before
    // passing it in here.
    public AudioWriter(SourceDataLine sdl) {
        this.sdl = sdl;
        format = sdl.getFormat();
        sdl.start();
    }

    // Write to a file
    public AudioWriter(File file, AudioFormat format,
                       AudioFileFormat.Type targetType) throws IOException {
        //System.out.println("AudioWriter File constructor");
        this.format = format;
        this.targetType = targetType;
        this.file = file;

        // Write to the output stream
        pos = new PipedOutputStream();

        // It will then go to the file via the input streams
        pis = new PipedInputStream(pos);
        ais = new AudioInputStream(pis, format, AudioSystem.NOT_SPECIFIED);

        new Thread(this).start();
    }

    public void run() {
        try {
            AudioSystem.write(ais, targetType, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(double[] data) throws IOException {
//        write(data, data.length);
    }

//    public void write(double[] data, int length) throws IOException {
//        // Allocate a new bytes array if necessary.  If bytes is too long,
//        // don't worry about it, just use as much as is needed.
//        int numBytes = length * format.getFrameSize();
//        if ( bytes == null || numBytes > bytes.lengtbytes = new byte[numBytes];
//
//        // Limit data to [-1, 1]
//        limit(data);
//
//        // Convert doubles to bytes using format
//        doubles2bytes(data, bytes, length);
//
//        // write it
//        if (pos != null)
//            pos.write(bytes, 0, numBytes);
//        if (sdl != null)
//            sdl.write(bytes, 0, numBytes);
//    }

    // Perform memoryless limiting on the audio data to keep all samples
    // in [-1,1]
    public static void limit(double[] data) {
        double t = 0.8;
        double c = 2 * (1 - t) / Math.PI;

        for (int i = 0; i < data.length; i++) {
            if (data[i] > t) {
                data[i] = c * Math.atan((data[i] - t) / c) + t;
            } else if (data[i] < -t) {
                data[i] = c * Math.atan((data[i] + t) / c) - t;
            }
        }
    }

    public void write(byte[] bytes) throws IOException {
        if (pos != null)
            pos.write(bytes, 0, bytes.length);
        if (sdl != null)
            sdl.write(bytes, 0, bytes.length);
    }

    public void close() throws IOException {
        if (pos != null) {
            ais.close();
            pis.close();
            pos.close();
        }
        if (sdl != null)
            sdl.close();
    }

    public AudioFormat getFormat() {
        return format;
    }


    public void doubles2bytes(double[] audioData, byte[] audioBytes) {
        doubles2bytes(audioData, audioBytes, audioData.length);
    }

    public void doubles2bytes(double[] audioData, byte[] audioBytes, int length) {
        int in;
        if (format.getSampleSizeInBits() == 16) {
            if (format.isBigEndian()) {
                for (int i = 0; i < length; i++) {
                    in = (int) (audioData[i] * 32767);
                    /* First byte is MSB (high order) */
                    audioBytes[2 * i] = (byte) (in >> 8);
                    /* Second byte is LSB (low order) */
                    audioBytes[2 * i + 1] = (byte) (in & 255);
                }
            } else {
                for (int i = 0; i < length; i++) {
                    in = (int) (audioData[i] * 32767);
                    /* First byte is LSB (low order) */
                    audioBytes[2 * i] = (byte) (in & 255);
                    /* Second byte is MSB (high order) */
                    audioBytes[2 * i + 1] = (byte) (in >> 8);
                }
            }
        } else if (format.getSampleSizeInBits() == 8) {
            if (format.getEncoding().toString().startsWith("PCM_SIGN")) {
                for (int i = 0; i < length; i++) {
                    audioBytes[i] = (byte) (audioData[i] * 127);
                }
            } else {
                for (int i = 0; i < length; i++) {
                    audioBytes[i] = (byte) (audioData[i] * 127 + 127);
                }
            }
        }
    }
}
