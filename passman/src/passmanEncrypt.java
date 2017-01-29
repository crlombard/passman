/**
 * @author	Coleman R. Lombard
 * @file	passmanEncrypt.java
 *			This file contains the passmanEncrypt file, used to provide all 
 *			cryptographic methods utilized by the passman application.
 * 
 *  		All member functions are documented in the javadoc style, each comment
 *			block is preceded by a tag "(#)" for ease of searching. To view documentation,
 *			simply search by this tag sequence.
 *			
 * @date	1/4/2016
 */

package passman;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class passmanEncrypt {
	private static SecretKeySpec secretKey ;
    private static byte[] key ;
    private static byte[] salt;
	
    private static String decryptedString;
    private static String encryptedString;
	
	/***************************************************************************
	 * Accessor Methods 
	 **************************************************************************/
	
	/*(#) getSalt()
	 *	  This function returns the class variable salt. Note that salt should 
	 *	  be a 128 bit array for use in the hashing and encryption functions.
	 ***************************************************************************
	 * @param	None.
	 * @pre		None.
	 * @post	None.
	 * @return	Class byte array salt.
	 * @throws	None.
	 */
	public static byte[] getSalt()
	{
		return salt;
	}
    
	/*(#) getDecryptedString()
	 *	  This function returns the class variable decryptedString.
	 ***************************************************************************
	 * @param	None.
	 * @pre		None.
	 * @post	None.
	 * @return	Class String decryptedString.
	 * @throws	None.
	 */
    public static String getDecryptedString() {
        return passmanEncrypt.decryptedString;
    }
	
	/*(#) getEncryptedString()
	 *	  This function returns the class variable encryptedString.
	 ***************************************************************************
	 * @param	None.
	 * @pre		None.
	 * @post	None.
	 * @return	Class String encryptedString.
	 * @throws	None.
	 */
    public static String getEncryptedString() {
        return passmanEncrypt.encryptedString;
    }
	
	/***************************************************************************
	 * Modifier Methods 
	 **************************************************************************/
	
	/*(#) setDecryptedString()
	 *	  This function replaces the class variable decryptedString.
	 ***************************************************************************
	 * @param	String decryptedString, the new value to replace the class level
	 *			String decryptedString.
	 * @pre		None.
	 * @post	Class level decryptedString variable is replaced with the provided
	 *			parameter.
	 * @return	None.
	 * @throws	None.
	 */
	public static void setDecryptedString(String decryptedString) {
        passmanEncrypt.decryptedString = decryptedString;
    }
	
	/*(#) setEncryptedString()
	 *	  This function replaces the class variable encryptedString.
	 ***************************************************************************
	 * @param	String encryptedString, the new value to replace the class level
	 *			String encryptedString.
	 * @pre		None.
	 * @post	Class level encryptedString variable is replaced with the provided
	 *			parameter.
	 * @return	None.
	 * @throws	None.
	 */
    public static void setEncryptedString(String encryptedString) {
        passmanEncrypt.encryptedString = encryptedString;
    }
	
	/*(#) setKey()
	 *	  This function replaces the class variable decryptedString.
	 ***************************************************************************
	 * @param	String myKey, the new value to replace the class level String key.
	 * @pre		None.
	 * @post	Class level key variable is replaced with the provided parameter.
	 * @return	None.
	 * @throws	None.
	 */
	public static void setKey(String myKey) 
	{
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            //System.out.println(key.length);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit
            //System.out.println(key.length);
            //System.out.println(new String(key,"UTF-8"));
            secretKey = new SecretKeySpec(key, "AES");
        }catch (Exception e){
            e.printStackTrace();
		}
    }
	
	/***************************************************************************
	 * Public Methods 
	 **************************************************************************/
	
	/*(#) encrypt()
	 *	  This function encrypts a given string using AES 128 bit ECB encryption.
	 ***************************************************************************
	 * @param	String strToEncrypt which will be encrypted.
	 * @pre		None.
	 * @post	strToEncrypt is encrypted using AES 128 bit ECB encryption.
	 * @return	Encrypted data contained in strToEncrypt, or null if encryption
	 *			fails.
	 * @throws	None.
	 */
    public static String encrypt(String strToEncrypt)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			//System.out.println(secretKey);
            setEncryptedString(Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes("UTF-8"))));
			return getEncryptedString();
        }
        catch (Exception e)
        {
            System.err.println("Error while encrypting: "+e.toString());
        }
        return null;
    }
	
	/*(#) decrypt()
	 *	  This function decrypts a given string using AES 128 bit ECB encryption.
	 ***************************************************************************
	 * @param	String strToEncrypt which will be decrypted.
	 * @pre		None.
	 * @post	strToEncrypt is decrypted using AES 128 bit ECB encryption.
	 * @return	Decrypted data contained in strToEncrypt, or null if decrypted
	 *			fails.
	 * @throws	None.
	 */
    public static String decrypt(String strToDecrypt)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
			//System.out.println(secretKey);
            setDecryptedString(new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt))));
			//System.out.println(passmanEncrypt.decryptedString);
			return getDecryptedString();
        }
        catch (Exception e)
        {
            System.err.println("Error while decrypting: "+e.toString());
        }
        return null;
    }
	
	/*(#) PBKDF2Hash()
	 *	  This function hashes an input String using the PDKDF2 algorithm with SHA1.
	 ***************************************************************************
	 * @param	String strToEncrypt which will be decrypted, byte array inputSalt
	 *			which must be 16 bytes (128 bits).
	 * @pre		None.
	 * @post	password is hashed using the PDKDF2 algorithm with SHA1.
	 * @return	Hashed String of the data from password parameter String.
	 * @throws	NoSuchAlgorithmException and InvalidKeySpecException exceptions.
	 */
	public static String PBKDF2Hash(String password, byte[] inputSalt) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        salt = inputSalt;
         
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }
	
	/*(#) PBKDF2Hash()
	 *	  This function hashes an input String using the PDKDF2 algorithm with SHA1.
	 ***************************************************************************
	 * @param	String strToEncrypt which will be decrypted.
	 * @pre		None.
	 * @post	password is hashed using the PDKDF2 algorithm with SHA1.
	 * @return	Hashed String of the data from password parameter String.
	 * @throws	NoSuchAlgorithmException and InvalidKeySpecException exceptions.
	 */
	public static String PBKDF2Hash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        salt = makeSalt();
         
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }
    
	/*(#) makeSalt()
	 *	  This function generates a salt byte array with 16 byte characters, or
	 *	  128 bits of data.
	 ***************************************************************************
	 * @param	None.
	 * @pre		None.
	 * @post	Byte array of 128 bits is generated.
	 * @return	Byte array of 128 bits is returned.
	 * @throws	NoSuchAlgorithmException exception.
	 */
    public static byte[] makeSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }
     
	/***************************************************************************
	 * Private Utility Methods 
	 **************************************************************************/
	
	/*(#) toHex()
	 *	  This function converts the parameter to hexadecimal.
	 ***************************************************************************
	 * @param	16 byte array array.
	 * @pre		None.
	 * @post	None.
	 * @return	String hex format of provided byte data is returned.
	 * @throws	NoSuchAlgorithmException exception.
	 */
    private static String toHex(byte[] array) throws NoSuchAlgorithmException
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
        {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }
		else
		{
            return hex;
        }
    }
}
