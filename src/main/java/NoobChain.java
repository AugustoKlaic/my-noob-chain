import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class NoobChain {

    public static List<Block> blockchain = new ArrayList<Block>();
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>(); //list of all unspent transactions.

    public static Integer difficulty = 3;
    public static float minimumTransaction = 0.1f;

    public static Wallet wallet1;
    public static Wallet wallet2;
    public static Transaction genesisTransaction;

    public static void main(String[] args) {
        //Setup Bouncey castle as a Security Provider
        Security.addProvider(new BouncyCastleProvider());

        wallet1 = new Wallet();
        wallet2 = new Wallet();
        Wallet coinbase = new Wallet();

        //create genesis transaction, which sends 100 NoobCoin to walletA:
        genesisTransaction = new Transaction(coinbase.getPublicKey(), wallet1.getPublicKey(), 100F, null);
        genesisTransaction.generateSignature(coinbase.getPrivateKey()); //manually sign the genesis transaction
        genesisTransaction.setTransactionId("0"); //manually set the transaction id
        genesisTransaction.getOutputs().add(
                new TransactionOutput(genesisTransaction.getReceiver(),
                        genesisTransaction.getValue(),
                        genesisTransaction.getTransactionId())); //manually add the Transactions Output

        UTXOs.put(genesisTransaction.getOutputs().getFirst().getId(), genesisTransaction.getOutputs().getFirst()); //its important to store our first transaction in the UTXOs list.

        System.out.println("Creating and Mining Genesis block... ");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        //testing
        Block block1 = new Block(genesis.getHash());
        System.out.println("\nWalletA's balance is: " + wallet1.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(wallet1.sendFunds(wallet2.getPublicKey(), 40f));
        addBlock(block1);
        System.out.println("\nWalletA's balance is: " + wallet1.getBalance());
        System.out.println("WalletB's balance is: " + wallet2.getBalance());

        Block block2 = new Block(block1.getHash());
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(wallet1.sendFunds(wallet2.getPublicKey(), 1000f));
        addBlock(block2);
        System.out.println("\nWalletA's balance is: " + wallet1.getBalance());
        System.out.println("WalletB's balance is: " + wallet2.getBalance());

        Block block3 = new Block(block2.getHash());
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(wallet2.sendFunds(wallet1.getPublicKey(), 20f));
        System.out.println("\nWalletA's balance is: " + wallet1.getBalance());
        System.out.println("WalletB's balance is: " + wallet2.getBalance());

        System.out.println(isChainValid());
    }

    public static Boolean isChainValid() {
        Block currentBlock, previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
        tempUTXOs.put(genesisTransaction.getOutputs().getFirst().getId(), genesisTransaction.getOutputs().getFirst());

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

            //loop through blockchains transactions:
            TransactionOutput tempOutput;
            for (int t = 0; t < currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if (!currentTransaction.verifySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if (!Objects.equals(currentTransaction.getInputValues(), currentTransaction.getOutputValues())) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for (TransactionInput input : currentTransaction.getInputs()) {
                    tempOutput = tempUTXOs.get(input.getTransactionOutputId());

                    if (tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if (!Objects.equals(input.getUTXO().getValue(), tempOutput.getValue())) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.getTransactionOutputId());
                }

                for (TransactionOutput output : currentTransaction.getOutputs()) {
                    tempUTXOs.put(output.getId(), output);
                }

                if (currentTransaction.getOutputs().getFirst().getReceiver() != currentTransaction.getReceiver()) {
                    System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
                    return false;
                }
                if (currentTransaction.getOutputs().get(1).getReceiver() != currentTransaction.getSender()) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }

            }
        }
        return true;
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }
}


