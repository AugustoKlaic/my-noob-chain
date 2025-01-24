import java.util.Date;

public class Block {

    private String hash; // it is the digital signature of the block
    private String previousHash; // previous block's hash
    private String data; // holds block's data, in this case will be a simple string
    private Long timeStamp;

    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = this.calculateHash();
    }

    public String calculateHash() {
        String calculatedHash = HashUtils.applySha256(this.previousHash + this.timeStamp.toString() + this.data);
        return calculatedHash;
    }

    public String getHash() {
        return hash;
    }
}
