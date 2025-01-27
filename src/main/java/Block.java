import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Block {

    private String hash; // it is the digital signature of the block
    private String previousHash; // previous block's hash
    private String merkleRoot;
    public List<Transaction> transactions = new ArrayList<Transaction>(); //our data will be a simple message.
    private Long timeStamp;
    private int nonce;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String calculatedHash = HashUtils.applySha256(this.previousHash + this.timeStamp.toString() + this.nonce + this.merkleRoot);
        return calculatedHash;
    }

    public void mineBlock(Integer difficulty) {
        this.merkleRoot = HashUtils.getMerkleRoot(this.transactions);
        String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0"
        while (!this.hash.substring(0, difficulty).equals(target)) {
            this.nonce++;
            this.hash = calculateHash();
        }
        System.out.println("Block mined!! :" + this.hash);
    }

    //Add transactions to this block
    public Boolean addTransaction(Transaction transaction) {
        //process transaction and check if valid, unless block is genesis block then ignore.

        if (transaction == null) {
            return false;
        }

        if (!Objects.equals(previousHash, "0")) {
            if (!transaction.processTransaction()) {
                System.out.println("Failed transaction. Discarded!");
                return false;
            }
        }

        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }
}
