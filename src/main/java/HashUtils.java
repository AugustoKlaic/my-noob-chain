import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8)); // applies the SHA-256 crypt to the input

            StringBuffer hexString = new StringBuffer(); // This will contain hash as hexadecimal
            unsignedByte(hexString, hash);
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static void unsignedByte(StringBuffer hexString, byte[] hash) {

        /*
         In Java, a byte is signed, meaning its value can range from -128 to 127,
         which can cause issues when converting to hexadecimal, as negative values would be represented unexpectedly.
         The AND operation with 0xff effectively clears the higher bits, leaving only the lower 8 bits of the byte.
         The result is a number between 0 and 255, equivalent to the range of an unsigned byte.
         */

        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
    }
}
