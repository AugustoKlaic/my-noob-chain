import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class Wallet {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public Wallet() {
        generateKeyPair();
    }

    // uses Java.security.KeyPairGenerator to generate an Elliptic Curve KeyPair
    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random); //256 bytes provides an acceptable security level

            KeyPair keypair = keyGen.generateKeyPair();

            // Set the public and private keys from the keyPair
            this.privateKey = keypair.getPrivate();
            this.publicKey = keypair.getPublic();

        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }
}
