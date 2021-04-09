/**
 * Class that can convert various types of data into their binary representations
 */

package com.binary;

import java.util.Arrays;
import com.binary.Binary;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;


public class BinaryTool {
	public static final int NUMBER_OF_BITS = 8;

	public static Binary charToBinary(char toConvert) {
		StringBuilder stringRepr = new StringBuilder(Integer.toBinaryString(toConvert));
		while (stringRepr.length() < NUMBER_OF_BITS) { // pad with 0's
			stringRepr.insert(0, "0"); // add a 0 to beginning
		}
		return new Binary(stringRepr.toString());
	}

	public static Binary ASCIIToBinary(String toConvert) {
		if (toConvert.length() == 0) {
			return null;
		}

		char[] composite_chars = toConvert.toCharArray();
		Binary converted = charToBinary(composite_chars[0]);
		for (int i = 1 ; i < composite_chars.length ; i++) {
			converted.append(charToBinary(composite_chars[i]));
		}
		return converted;
	}	

	public static String binaryToASCII(Binary toConvert) {
		int[] binaryData = toConvert.getIntArray();
		StringBuilder stringRepr = new StringBuilder(binaryData.length/NUMBER_OF_BITS);
		for (int i = 0 ; i < binaryData.length ; i+=NUMBER_OF_BITS) {
			Binary charBinary = new Binary(Arrays.copyOfRange(binaryData, i, i+NUMBER_OF_BITS));
			String character = Character.toString(binaryToChar(charBinary));
			System.out.println("cha: " + character);
			stringRepr.append(character);
		}
		return stringRepr.toString();
	}

	public static char binaryToChar(Binary toConvert) {
		String binaryData = toConvert.getStringRepr();
		return (char) Integer.parseInt(binaryData, 2);
	}

    public static Binary fileToBinary(File file) throws IOException {
	    Path path = Paths.get(file.getPath());    	
    	byte[] bytes = Files.readAllBytes(path);
    	StringBuilder sb = new StringBuilder();    	
	    for (byte by : bytes)
	      sb.append(Integer.toBinaryString(0xFF & by | 0x100).substring(1));
	    return new Binary(sb.toString());
    }

    public static void writeToFile(Binary toWrite, String outPath) throws IOException {
    	Path path = Paths.get(outPath);
    	byte[] out = toWrite.getByteArray();
    	Files.write(path, out);
    }

	public static Byte[] toObjects(byte[] bytesPrim) {

		Byte[] bytes = new Byte[bytesPrim.length];
		int i = 0;
		for (byte b : bytesPrim) bytes[i++] = b; //Autoboxing
		return bytes;

	}
	public static byte[] toPrimitives(Byte[] oBytes)
	{

		byte[] bytes = new byte[oBytes.length];
		for(int i = 0; i < oBytes.length; i++){
			bytes[i] = oBytes[i];
		}
		return bytes;

	}

	public static int[] convertStringToBinary(String input) {


		int c = 0;

		StringBuilder str = new StringBuilder();
		char[] chars = input.toCharArray();
		for (char aChar : chars) {

				String s =	String.format("%8s", Integer.toBinaryString(aChar))
							.replaceAll(" ", "0") ;
				str.append(s);
		}

		int result[] = new int[str.length()];
		for(int i = 0; i < result.length;i++){
			result[i] = Integer.parseInt(String.valueOf(str.charAt(i)));
		}

		return result;

	}

	public static String binaryToString(int[] binaryInput) {

		String input = "";
		for(int i = 0 ; i < binaryInput.length;i++){
			if(i % 8 == 0 && i != 0){
				input += " ";
			}else{
				input += binaryInput[i];
			}
		}
		System.out.println("input: {" + input +"}");

		// Java 8 makes life easier
		String str = "";
		String[] binaryStr = input.split(" ");

		for(int i = 0; i < binaryStr.length; i++){
			int charCode = Integer.parseInt(binaryStr[i], 2);
			String character = new Character((char)charCode).toString();
			str += character;
		}

		return str;
	}


	public static void main(String args[]) throws IOException {

		int[] binary = convertStringToBinary("hello");

		System.out.println(binaryToString(binary));

	}
}