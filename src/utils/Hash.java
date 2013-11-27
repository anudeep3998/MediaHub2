package utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Phoenix on 11/28/13.
 * Source : http://stackoverflow.com/questions/415953/generate-md5-hash-in-java
 */
public class Hash {

    private static MessageDigest m;

    Hash(){
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String getHash(String plainText){
        String hashText;
        try {
            //m.reset();
            m.update(plainText.getBytes("UTF-8"));
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            hashText = bigInt.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while(hashText.length() < 32 ){
                hashText = "0"+hashText;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            hashText=null;
        }
        return hashText;
    }
}
