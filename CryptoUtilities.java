import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * This class contains various utility functions for working with AES encryption
 * and decryption, and for working with HMAC-SHA1 message digests.
 * 
 * @file CryptoUtilities.java
 * @author Brian Yee
 */
public class CryptoUtilities {

	// AES key length to be used (in bytes)
	public static final int AES_KEY_LEN = 16;

	// HMAC-SHA1 digest length (in bytes)
	public static final int HMAC_SHA1_LEN = 20;

	// AES/CBC/PKCS5Padding parameter length
	public static final int AES_PARAM_LEN = 18;


	/**
	 * Constructs a AES_KEY_LEN byte AES key from a given seed
	 *
	 * @param seed (array of bytes)
	 * @return the resulting AES key
	 */
	public static SecretKeySpec key_from_seed(byte[] seed) {
		// compute SHA-1 hash of the seed
		byte[] hashval = null;
		try {
			MessageDigest sha1 = MessageDigest.getInstance("SHA1");
			hashval = sha1.digest(seed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// extract 1st AES_KEY_LEN bytes for the key material
		byte[] key = new byte[AES_KEY_LEN];
		System.arraycopy(hashval, 0, key, 0, AES_KEY_LEN);

		// initialize the key
		SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		return keySpec;
	}


	/**
	 * Computes a HMAC-SHA1 message digest of a given message, appends it to the
	 * message, returns the output.
	 *
	 * @param message the message (in bytes)
	 * @param keySpec the secret key for HMAC-SHA1
	 * @return message concatenated with the HMAC-SHA1 digest
	 */
	public static byte[] append_hash(byte[] message, SecretKeySpec keySpec) {
		byte[] ret = null;

		try {
			// Initialize the MAC with the given key
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(keySpec);

			// Compute the MAC
			byte[] m = mac.doFinal(message);

			// Append the MAC to the message
			ret = new byte[message.length + m.length];
			System.arraycopy(message, 0, ret, 0, message.length);
			System.arraycopy(m, 0, ret, message.length, m.length);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}


	/**
	 * Extracts a message from a message/digest pair.
	 *
	 * @param hash_message the message and digest (in bytes)
	 * @return message concatenated with the HMAC-SHA1 digest
	 */
	public static byte[] extract_message(byte[] hash_message) {
		byte[] plaintext = new byte[hash_message.length - HMAC_SHA1_LEN];
		System.arraycopy(hash_message, 0, plaintext, 0, plaintext.length);

		return plaintext;
	}


	/**
	 * Extracts a HMAC-SHA1 message digest form the end of the given message and
	 * deterimines whether it is valid.
	 *
	 * @param messageHash the message including digest (in bytes)
	 * @param keySpec     the secret key for HMAC-SHA1
	 * @return true if the extracted digest matches the computed digest
	 */
	public static boolean verify_hash(byte[] messageHash, SecretKeySpec keySpec) {
		boolean ret = false;

		try {
			// Split the array into the message and the digest
			byte[] message = new byte[messageHash.length - HMAC_SHA1_LEN];
			byte[] hash = new byte[HMAC_SHA1_LEN];

			System.arraycopy(messageHash, 0, message, 0, message.length);
			System.arraycopy(messageHash, message.length, hash, 0, hash.length);

			// Initialize the MAC with the given key
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(keySpec);

			// Get the MAC of the message
			byte[] m = mac.doFinal(message);

			// compare the the MAC sent and the one calculated
			ret = Arrays.equals(m, hash);

		} catch (Exception e) {
			// if there is an error, we know that hash can't be correct
			ret = false;
		}

		return ret;
	}

	/*
     * Converts a byte array to hex string
     * this code from http://java.sun.com/j2se/1.4.2/docs/guide/security/jce/JCERefGuide.html#HmacEx
     */
	public static void byte2hex(byte b, StringBuffer buf) {
		char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'A', 'B', 'C', 'D', 'E', 'F'};
		int high = ((b & 0xf0) >> 4);
		int low = (b & 0x0f);
		buf.append(hexChars[high]);
		buf.append(hexChars[low]);
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
			if (i < len - 1) {
				buf.append(":");
			}
		}
		return buf.toString();
	}

	/**
	 * Encrypts the given message using the given key with AES-CBC.
	 *
	 * @param message the message (in bytes)
	 * @param keySpec the secret key
	 * @return encrypted message (with algorithm parameters appended
	 */
	public static byte[] encrypt(byte[] message, SecretKeySpec keySpec) {
		byte[] ret = null;

		try {
			// Initialize the cipher with the given key
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);

			// encrypt the message
			byte[] cipherText = cipher.doFinal(message);
			byte[] params = cipher.getParameters().getEncoded();

			// Combine the ciphertext and cipher parameters into one byte array
			ret = new byte[cipherText.length + params.length];
			System.arraycopy(cipherText, 0, ret, 0, cipherText.length);
			System.arraycopy(params, 0, ret, cipherText.length, params.length);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}


	/**
	 * Decrypts the given message using the given key with AES-CBC.
	 *
	 * @param decrypt the message (in bytes)
	 * @param keySpec the secret key
	 * @return decrypted message
	 */
	public static byte[] decrypt(byte[] decrypt, SecretKeySpec keySpec) {
		byte[] message = null;

		try {
			// Extract the cipher parameters from the end of the input array
			byte[] cipherText = new byte[decrypt.length - AES_PARAM_LEN];
			byte[] paramsEnc = new byte[AES_PARAM_LEN];

			System.arraycopy(decrypt, 0, cipherText, 0, cipherText.length);
			System.arraycopy(decrypt, cipherText.length, paramsEnc, 0, paramsEnc.length);

			// Initialize the parameters
			AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
			params.init(paramsEnc);

			// Initialize the cipher for decryption
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, keySpec, params);

			// Decrypt the ciphertext
			message = cipher.doFinal(cipherText);

		} catch (Exception e) {
			return null;
		}

		return message;
	}

	/**
	 * Encrypts the the given message with the given key and sends out over out_stream
	 * Prints message contents if debug enabled
	 */
	public static int sendEncrypted(byte[] message, SecretKeySpec key, DataOutputStream out_stream, boolean debug) {
		byte[] combined = append_hash(message, key);
		byte[] encrypted = encrypt(combined, key);

		try {
			out_stream.writeInt(encrypted.length);    // send size of the data
			out_stream.write(encrypted);    // send the data bytes

			if (debug)
				System.out.println("Sending " + getFileMessageString(message));    // nicely display message contents
		} catch (IOException e) {
			e.printStackTrace();
		}
		return encrypted.length;
	}

	/**
	 * Waits for incoming data, then receives and decrypts the incoming messages from in_stream
	 * Prints message contents if debug enabled
	 */
	public static byte[] receiveEncrypted(SecretKeySpec key, DataInputStream in_stream, boolean debug) throws Exception {
		// wait for incoming data
		while (in_stream.available() <= 0) {
		}

		// determine size of data blob
		int msg_size = in_stream.readInt();
		byte[] encrypted = new byte[msg_size];

		// read in the expected number of data bytes
		int bytesRead = 0;
		while (bytesRead < msg_size) {
			bytesRead += in_stream.read(encrypted, bytesRead, msg_size - bytesRead);
		}

		// decrypt the received encrypted bytes
		byte[] combined = decrypt(encrypted, key);

		if (combined == null)  // if there was an error during decryption
		{
			System.out.println("Decryption failed.  Wrong key specified?");
			return null;
		}
		if (!verify_hash(combined, key))    // if the hash verification failed
		{
			System.out.println("The digest does not match the message.  (The message was altered?)");
			return null;
		}
		byte[] message = extract_message(combined);    // extract the decrypted message
		if (debug)
			System.out.println("Receiving " + getFileMessageString(message));    // print the message contents

		return message;
	}

	public static String getFileMessageString(byte[] bytes) {
		ByteBuffer bb = ByteBuffer.allocate(4);

		// extract destination name length (first 4 bytes)
		bb.put(bytes, 0, 4);
		bb.flip();
		int dest_name_length = bb.getInt();

		// extract destination name
		String dest_name = new String(bytes, 4, dest_name_length);

		// extract data size
		bb.clear();
		bb.put(bytes, 4 + dest_name_length, 4);
		bb.flip();
		int data_length = bb.getInt();
		byte[] data = new byte[data_length];

		//extract data bytes
		System.arraycopy(bytes, 4 + dest_name_length + 4, data, 0, data.length);

		// return formatted string of contents
		return (String.format("[Client Encrypted File Message]:\n" +
						"Filename Length: %d\n" +
						"Filename: %s\n" +
						"Data Size: %d\n" +
						"Data Bytes (only 1000 bytes shown): %s[...]\n",
				dest_name.length(),
				dest_name,
				data_length,
				CryptoUtilities.toHexString(data).substring(0, Math.min(data_length, 1000))));
	}
}