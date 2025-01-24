import com.google.gson.GsonBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;

public class NoobChain {

    public static List<Block> blockchain = new ArrayList<Block>();
    public static Integer difficulty = 5;

    public static Wallet wallet1;
    public static Wallet wallet2;

    public static void main(String[] args) {
        //Setup Bouncey castle as a Security Provider
        Security.addProvider(new BouncyCastleProvider());

        wallet1 = new Wallet();
        wallet2 = new Wallet();

        System.out.println("Private and public keys:");
        System.out.println(HashUtils.getStringFromKey(wallet1.getPrivateKey()));
        System.out.println(HashUtils.getStringFromKey(wallet1.getPublicKey()));

        Transaction transaction = new Transaction(wallet1.getPublicKey(), wallet2.getPublicKey(), 5F, null);
        transaction.generateSignature(wallet1.getPrivateKey());

        //Verify the signature works and verify it from the public key
        System.out.println("Is signature verified");
        System.out.println(transaction.verifySignature());

//        Block genesisBlock = new Block("Hi im the first block", "0");
//        System.out.println("Trying to Mine block 1... \n");
//        blockchain.add(genesisBlock);
//        blockchain.get(0).mineBlock(difficulty);
//
//        Block secondBlock = new Block("Yo im the second block", genesisBlock.getHash());
//        blockchain.add(secondBlock);
//        System.out.println("Trying to Mine block 2... \n");
//        blockchain.get(1).mineBlock(difficulty);
//
//        Block thirdBlock = new Block("Hey im the third block", secondBlock.getHash());
//        blockchain.add(thirdBlock);
//        System.out.println("Trying to Mine block 3... \n");
//        blockchain.get(2).mineBlock(difficulty);
//
//        System.out.println("Blockchain is Valid: " + isChainValid() + "\n");
//
//        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
//        System.out.println(blockchainJson);
    }

    public static Boolean isChainValid() {
        Block currentBlock, previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

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

            //check if hash is solved
            if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
        }
        return true;
    }
}
