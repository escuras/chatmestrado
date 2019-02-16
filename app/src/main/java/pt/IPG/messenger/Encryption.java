package pt.IPG.messenger;

import android.util.Log;

import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author JDinis
 */
public class Encryption {
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
}
