package pt.IPG.messenger;

import android.util.Base64;
import android.util.Log;

import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author JDinis
 */
public class Encryption {

    public static final String ENCRYPTION_SEPARATOR = ":";

    public enum MessageType {
        Decrypted,
        DecryptedBytes,
        Encrypted,
        EncryptedBytes
    }

    static final String TAG = "SymmetricAlgorithmAES";

    // Set up secret key spec for 128-bit AES encryption and decryption
    private static SecretKeySpec sks = null;

    /**
     * Instantiates the class Encryption by generating a secret key
     */
    public Encryption() {
            try {
                SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
                sr.setSeed("Chat-RSCM".getBytes());
                KeyGenerator kg = KeyGenerator.getInstance("AES");
                kg.init(128, sr);
                sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
            } catch (Exception e) {
                Log.e(TAG, "AES secret key spec error");
            }
    }

    /**
     * Encrypts a message string
     * @param message to be encrypted
     * @param type describes whether or not a message is to be encrypted
     * @return Encrypted message string encoded with base64 or Plain-Text message depending on MessageType specified
     */
    public String Encrypt(String message, MessageType type){
        return type.ordinal() +ENCRYPTION_SEPARATOR+ Base64.encodeToString(message.getBytes(), Base64.DEFAULT);
    }

    /**
     * Encrypts a byte array message
     * @param messageBytes can be a byte array that holds a string value, image bytes, voice byte data, etc...
     * @param type describes whether or not a message is to be encrypted
     * @return Encrypted byte array encoded with base64 depending on MessageType specified
     */
    public String Encrypt(byte[] messageBytes, MessageType type) {
        return MessageType.EncryptedBytes.ordinal() +ENCRYPTION_SEPARATOR+ Base64.encodeToString(messageBytes, Base64.DEFAULT);
    }

    /**
     * Decrypts a base64 string message
     * @param message is data that has been encoded with base64 to be decrypted
     * @return Decrypted data as String
     */
    public String Decrypt(String message){
        return Base64.encodeToString(message.getBytes(), Base64.DEFAULT);
    }
}
