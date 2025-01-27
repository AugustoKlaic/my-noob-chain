import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
    }

    //Applies ECDSA Signature and returns the result ( as bytes ).
    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output = new byte[0];
        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    //Verifies a String signature
    public static Boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    //Tacks in array of transactions and returns a merkle root.
    public static String getMerkleRoot(List<Transaction> transactions) {
        Integer count = transactions.size();
        List<String> previousTreeLayer = new ArrayList<>();

        for (Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.getTransactionId());
        }

        List<String> treeLayer = previousTreeLayer;
        while (count > 1) {
            treeLayer = new ArrayList<>();
            for (int i = 1; i < previousTreeLayer.size(); i++ ) {
                treeLayer.add(applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }

            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }
        String merkleRoot = treeLayer.size() == 1 ? treeLayer.getFirst() : "";
        return merkleRoot;
    }
}
