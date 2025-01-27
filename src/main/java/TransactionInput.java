public class TransactionInput {

    //This class will be used to reference TransactionOutputs that have not yet been spent.
    // The transactionOutputId will be used to find the relevant TransactionOutput, allowing miners to check your ownership.

    private final String transactionOutputId; //Reference to TransactionOutputs -> transactionId
    private TransactionOutput UTXO; //Contains the Unspent transaction output

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }

    public String getTransactionOutputId() {
        return transactionOutputId;
    }

    public void setUTXO(TransactionOutput UTXO) {
        this.UTXO = UTXO;
    }

    public TransactionOutput getUTXO() {
        return UTXO;
    }
}
