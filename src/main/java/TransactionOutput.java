import java.security.PublicKey;

public class TransactionOutput {

    private String id;
    private PublicKey receiver; //also known as the new owner of these coins.
    private Float value; //the amount of coins they own
    private String parentTransactionId; //the id of the transaction this output was created in

    public TransactionOutput(PublicKey receiver, Float value, String parentTransactionId) {
        this.receiver = receiver;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = HashUtils.applySha256(
                HashUtils.getStringFromKey(receiver)
                        + value.toString()
                        + parentTransactionId);
    }

    //Check if coin belongs to you
    public Boolean isMine(PublicKey publicKey) {
        return publicKey == this.receiver;
    }

    public Float getValue() {
        return value;
    }

    public String getId() {
        return id;
    }
}
