package com.mao;

import com.binary.BinaryTool;
import com.sun.deploy.util.ArrayUtil;
import net.beadsproject.beads.data.audiofile.AudioFileType;

import java.io.*;
import java.util.ArrayList;

import javax.sound.sampled.*;
import javax.sound.sampled.AudioFormat.Encoding;


/**
 * Class that allows directly passing PCM float mono
 * data to the sound card for playback. The sampling 
 * rate of the PCM data must be 44100Hz. 
 * 
 * @author mzechner
 *
 */
public class AudioDevice 
{
	private final static int BUFFER_SIZE = 1024;

	public SourceDataLine getOut() {
		return out;
	}

	private final SourceDataLine out;

	private byte[] buffer = new byte[BUFFER_SIZE*2];
	ArrayList<Byte[]> buffer_list = new ArrayList<>();

	public ArrayList<Byte[]> getBuffer_list() {
		return buffer_list;
	}

	public void setBuffer_list(ArrayList<Byte[]> buffer_list) {
		this.buffer_list = buffer_list;
	}

	public AudioDevice( ) throws Exception
	{
		AudioFormat format = new AudioFormat( Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, false );
		out = AudioSystem.getSourceDataLine( format );
		out.open(format);	
		out.start();

	}

	public void writeSamples( float[] samples ) throws IOException {
		fillBuffer( samples );
		out.write( buffer, 0, buffer.length );

		buffer_list.add(BinaryTool.toObjects(buffer));
	}



	private void fillBuffer( float[] samples )
	{
		for( int i = 0, j = 0; i < samples.length; i++, j+=2 )
		{
			short value = (short)(samples[i] * Short.MAX_VALUE);
			buffer[j] = (byte)(value | 0xff);
			buffer[j+1] = (byte)(value >> 8 );
		}

	}
	
	public static void main( String[] argv ) throws Exception
	{
		float[] samples = new float[1024];
		WaveDecoder reader = new WaveDecoder( new FileInputStream( "file/sample2.wav" ) );
		AudioDevice device = new AudioDevice( );
		
		while( reader.readSamples( samples ) > 0 )
		{
			device.writeSamples( samples );
		}

		AudioFormat format = new AudioFormat( Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100, false );

		AudioWriter audioWriter = new AudioWriter(new File("file/test.wav"),format,  AudioFileFormat.Type.WAVE);

		for(Byte[] b : device.buffer_list){

			audioWriter.write(BinaryTool.toPrimitives(b));
		}
		audioWriter.close();

	}
}
