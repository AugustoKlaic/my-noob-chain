import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class NoobChain {

    public static List<Block> blockchain = new ArrayList<Block>();
    public static Integer difficulty = 5;

    public static void main(String[] args) {

        Block genesisBlock = new Block("Hi im the first block", "0");
        Block secondBlock = new Block("Yo im the second block", genesisBlock.getHash());
        Block thirdBlock = new Block("Hey im the third block", secondBlock.getHash());

        blockchain.add(genesisBlock);
        blockchain.add(secondBlock);
        blockchain.add(thirdBlock);

        for (int i = 1; i < blockchain.size(); i++) {
            System.out.println("Trying to Mine block " + i + "... ");
            blockchain.get(i).mineBlock(difficulty);
        }

        System.out.println("Blockchain is Valid: " + isChainValid());

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println(blockchainJson);
    }

    public static Boolean isChainValid() {
        Block currentBlock, previousBlock;

        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);

            //compare registered hash and calculated hash:
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }

            //compare previous hash and registered previous hash
            if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                System.out.println("Previous Hashes not equal");
                return false;
            }

        }

        return true;
    }
}
