import java.util.Date;

public class Block {

    private String hash; // it is the digital signature of the block
    private String previousHash; // previous block's hash
    private String data; // holds block's data, in this case will be a simple string
    private Long timeStamp;
    private int nonce;

    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String calculatedHash = HashUtils.applySha256(this.previousHash + this.timeStamp.toString() + this.nonce + this.data);
        return calculatedHash;
    }

    public void mineBlock(Integer difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0"
        while(!this.hash.substring(0, difficulty).equals(target)) {
            this.nonce++;
            this.hash = calculateHash();
        }
        System.out.println("Block mined!! :" + this.hash);
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }
}
