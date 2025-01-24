import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Transaction {

    private String transactionId; // this is also the hash of the transaction.
    private PublicKey sender; // senders address/public key.
    private PublicKey receiver; // Recipients address/public key.
    private Float value;
    private byte[] signature; // this is to prevent anybody else from spending funds in our wallet

    private List<TransactionInput> inputs = new ArrayList<>();
    private List<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence = 0; // a rough count of how many transactions have been generated.

    public Transaction(PublicKey sender, PublicKey receiver, Float value, List<TransactionInput> inputs) {
        this.sender = sender;
        this.receiver = receiver;
        this.value = value;
        this.inputs = inputs;
    }

    // This Calculates the transaction hash (which will be used as its id)
    public String calculateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash

        return HashUtils.applySha256(
                HashUtils.getStringFromKey(this.sender)
                        + HashUtils.getStringFromKey(this.receiver)
                        + this.value.toString()
                        + sequence);
    }

    //Signs all the data we don't wish to be tampered with.
    public void generateSignature(PrivateKey privateKey) {
        String data = HashUtils.getStringFromKey(this.sender) + HashUtils.getStringFromKey(this.receiver) + value.toString();
        this.signature = HashUtils.applyECDSASig(privateKey, data);
    }

    //Verifies the data we signed hasn't been tampered with
    public Boolean verifySignature() {
        String data = HashUtils.getStringFromKey(this.sender) + HashUtils.getStringFromKey(this.receiver) + value.toString();
        return HashUtils.verifyECDSASig(this.sender, data, this.signature);
    }
}
