
import java.io.*;
import java.util.*;
import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.interfaces.*;
import java.security.SecureRandom;
import java.math.*;
import java.io.DataInputStream;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtilities {

	private static int HASH_LENGTH = 160;
	private static KeyGenerator key_gen = null;
	private static SecretKey sec_key = null;
	
	FileInputStream in_file = null;
	FileOutputStream out_file = null;
    byte[] sha_hash = null;
	byte[] hmac_hash = null;
    byte[] seed = null;
	byte[] aes_ciphertext = null;
	SecureRandom RNG = null;
	String decrypted_str = null;	

	public static byte[] receiveAndDecrypt(SecretKeySpec key, DataInputStream in) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static String aes_decrypt(byte[] data_in, SecretKeySpec sec_key_spec) {
		byte[] decrypted = null;
		String dec_str = null;
		try{
			//set cipher to decrypt mode
			Cipher sec_cipher = Cipher.getInstance("AES");
			sec_cipher.init(Cipher.DECRYPT_MODE, sec_key_spec);

			//do decryption
			decrypted = sec_cipher.doFinal(data_in);

			//convert to string
			dec_str = new String(decrypted);
		}
		catch(Exception e){
			System.out.println(e);
		}
		return dec_str;
	}			
	
	public static byte[] sha1_hash(byte[] input_data) {
		byte[] hashval = null;
		try{
			//create message digest object
			MessageDigest sha1 = MessageDigest.getInstance("SHA1");
			
			//make message digest
			hashval = sha1.digest(input_data);
		}
		catch(NoSuchAlgorithmException nsae){
			System.out.println(nsae);
		}
		return hashval;
	}

	public static byte[] hmac_sha1(byte[] in_data) {
		byte[] result = null;

		try{
			//generate the HMAC key		
			KeyGenerator theKey = KeyGenerator.getInstance("HMACSHA1");
			SecretKey secretKey = theKey.generateKey();

			Mac theMac = Mac.getInstance("HMACSHA1");
			theMac.init(secretKey);

			//create the hash
			result = theMac.doFinal(in_data);
		}
		catch(Exception e){
			System.out.println(e);
		}
		return result;
	}
	
	public static byte[] aes_encrypt(byte[] data_in, SecretKeySpec sec_key_spec) {
		byte[] out_bytes = null;
		try{
			//set cipher object to encrypt mode
			Cipher sec_cipher = Cipher.getInstance("AES");
			sec_cipher.init(Cipher.ENCRYPT_MODE, sec_key_spec);

			//create ciphertext
			out_bytes = sec_cipher.doFinal(data_in);
		}
		catch(Exception e){
			System.out.println(e);
		}
		return out_bytes;
	}
	
    /*
     * Converts a byte array to hex string
     * this code from http://java.sun.com/j2se/1.4.2/docs/guide/security/jce/JCERefGuide.html#HmacEx
     */
    public static String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();

        int len = block.length;

        for (int i = 0; i < len; i++) {
             byte2hex(block[i], buf);
             if (i < len-1) {
                 buf.append(":");
             }
        } 
        return buf.toString();
    }
    /*
     * Converts a byte to hex digit and writes to the supplied buffer
     * this code from http://java.sun.com/j2se/1.4.2/docs/guide/security/jce/JCERefGuide.html#HmacEx
     */
    public static void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                            '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }

	public static SecretKeySpec key_from_seed(byte[] bytes) {
		byte[] raw = null;
		try 
		{
			//generate 128-bit key using seed
			KeyGenerator key_gen;
			key_gen = KeyGenerator.getInstance("AES");
			SecureRandom RNG = SecureRandom.getInstance("SHA1PRNG");
			RNG.setSeed(bytes);
			key_gen.init(128, RNG);
			sec_key = key_gen.generateKey();
	
			//get key material in raw form
			raw = sec_key.getEncoded();
		} 
		catch (NoSuchAlgorithmException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return new SecretKeySpec(raw, "AES");
	}

	public static byte[] extract_message(byte[] hashed_plaintext) {
		byte[] result = new byte[hashed_plaintext.length - HASH_LENGTH];
		
		System.arraycopy(hashed_plaintext,0,result,0, result.length);
		return result;
	}

	public static boolean verify_hash(byte[] hashed_plaintext, SecretKeySpec key) {
		byte[] msg = new byte[hashed_plaintext.length - HASH_LENGTH];
		byte[] hash = new byte[HASH_LENGTH];
		byte[] sha_hash;

		try 
		{
			System.arraycopy(hashed_plaintext, 0, msg, 0, msg.length);
			System.arraycopy(hashed_plaintext, msg.length, hash, 0, hash.length);
			
			sha_hash = sha1_hash(msg);
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return sha_hash.equals(hash);
	}

	public static void encryptAndSend(byte[] bytes, SecretKeySpec key, DataOutputStream out) {
		byte[] encrypted = aes_encrypt(bytes, key);
		try 
		{
			out.write(bytes);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	public static byte[] append_hash(byte[] msg, SecretKeySpec key) {
		byte[] result = new byte[msg.length + HASH_LENGTH];        
		byte[] sha_hash;
		
		try 
		{
			sha_hash = sha1_hash(msg);
			System.arraycopy(msg,0,result,0, msg.length);
			System.arraycopy(sha_hash,0,result, msg.length, sha_hash.length);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}		
		return result;
	}
}
