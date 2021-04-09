package com.dsss;

import com.binary.Binary;
import com.binary.BinaryTool;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class Decoder {

    private File encodeAudioFile;
    private long key;

    public Decoder(File encodeAudioFile, long key) {
        this.encodeAudioFile = encodeAudioFile;
        this.key = key;
    }


    public void decode() throws Exception {
        ArrayList<Float> samplesWave = Common.getWaveFromAudio(encodeAudioFile);
        int[] pnSequence = Common.pnSequenceKey(key,samplesWave.size());
        System.out.println(pnSequence.length);

        float lowest_value = 0;
        Map<Float ,Integer> map = new Hashtable<>();
        for(int i = 0 ;i < samplesWave.size() ;i ++){
            if(map.containsKey(samplesWave.get(i))){
                map.put(samplesWave.get(i),map.get(samplesWave.get(i)) + 1);
                if(map.get(samplesWave.get(i)) > 10){
                    lowest_value = samplesWave.get(i);
                    break;
                }
            }
            else{
                map.put(samplesWave.get(i),1);
            }

        }

        System.out.println("low: " + lowest_value);

        System.out.println("decode wave: ");
        for(int i = 0; i < 10;i++){
            System.out.print(samplesWave.get(i) + " ");
        }
        System.out.println();

        int[] spreadSequences = new int[samplesWave.size()];
        for(int i =0; i < spreadSequences.length;i++){
            spreadSequences[i] = samplesWave.get(i) == lowest_value ? -1 : 1;
//            System.out.println(samplesWave.get(i));
        }

        // decode
        int[] data = new int[samplesWave.size()];
        for(int i = 0; i < spreadSequences.length ; i ++){
            data[i] = spreadSequences[i] * pnSequence[i];
            System.out.print(data[i] +" ") ;
        }
        System.out.println();

        int first_char = data[0];
        int number_per_byte = 0;
        for(int i = 0 ; i < data.length;i++){
            if(data[i] == first_char){
                number_per_byte++;
            }
            else{
                break;
            }
        }


        System.out.println("message count: " + samplesWave.size() / number_per_byte);
        System.out.println("number per pyte: " + number_per_byte);

        int message_byte[] = new int[data.length / number_per_byte];
        int m = 0;
        for(int i = 0 ;i < data.length;i++){
            if(i % number_per_byte == 0){
                message_byte[m] = data[i] == -1 ? 0 : 1 ;
                System.out.print(message_byte[m]);
                m++;
            }
        }
        System.out.println();

        System.out.println("decoded message: " + BinaryTool.binaryToString(message_byte));


//        for(int i = 0; i < message_binary.getIntArray().length;i+=8){
//            int iValue = 0;
//            for (int j = 0, pow = 7 ; j < 8 ; j++, pow--){
//                iValue += message_binary.getIntArray()[j]*Math.pow(2, pow);
//                System.out.println(iValue);
//            }
//            char cValue = (char)iValue;
//            System.out.println(cValue);
//        }

    }

}
