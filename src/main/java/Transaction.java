import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Transaction {

    private String transactionId; // this is also the hash of the transaction.
    private final PublicKey sender; // senders address/public key.
    private final PublicKey receiver; // Recipients address/public key.
    private final Float value;
    private byte[] signature; // this is to prevent anybody else from spending funds in our wallet

    private List<TransactionInput> inputs = new ArrayList<>();
    private final List<TransactionOutput> outputs = new ArrayList<>();

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

    public Boolean processTransaction() {
        if (verifySignature()) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        //gather transaction inputs (Make sure they are unspent):
        for (TransactionInput input : inputs) {
            input.setUTXO(NoobChain.UTXOs.get(input.getTransactionOutputId()));
        }

        //check if transaction is valid:
        if (getInputValues() < NoobChain.minimumTransaction) {
            System.out.println("#Transaction Inputs to small: " + getInputValues());
            return false;
        }

        //generate transaction outputs:
        Float leftOver = getInputValues() - this.value; //get value of inputs then the leftover change:
        this.transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.receiver, this.value, this.transactionId)); //send value to recipient
        outputs.add(new TransactionOutput(this.sender, leftOver, this.transactionId)); //send the leftover 'change' back to sender

        //add outputs to Unspent list
        for (TransactionOutput output : outputs) {
            NoobChain.UTXOs.put(output.getId(), output);
        }

        //remove transaction inputs from UTXO lists as spent:
        for (TransactionInput input : inputs) {
            if (input.getUTXO() == null) {
                continue;
            }
            NoobChain.UTXOs.remove(input.getUTXO().getId());
        }
        return true;
    }

    //returns sum of inputs(UTXOs) values
    public Float getInputValues() {
        Float total = 0F;
        for (TransactionInput input : inputs) {
            if (input.getUTXO() == null) {
                continue;
            }
            total += input.getUTXO().getValue();
        }
        return total;
    }

    //returns sum of outputs:
    public Float getOutputValues() {
        Float total = 0F;
        for (TransactionOutput output : outputs) {
            total += output.getValue();
        }
        return total;
    }

    public List<TransactionOutput> getOutputs() {
        return outputs;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public PublicKey getSender() {
        return sender;
    }

    public Float getValue() {
        return value;
    }

    public PublicKey getReceiver() {
        return receiver;
    }

    public List<TransactionInput> getInputs() {
        return inputs;
    }
}
