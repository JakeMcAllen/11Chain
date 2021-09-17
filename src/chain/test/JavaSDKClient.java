package chain.test;

/*

Simple Java Client for Hyperlegder Fabric

- sets and gets a value from the Ledger

*/

import java.io.*;

import java.security.*;
import java.security.spec.*;

import java.util.*;
import java.util.concurrent.*;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.exception.*;
import org.hyperledger.fabric.sdk.security.*;


public class JavaSDKClient {

    final static String PATH_CRYPTO_CONFIG = "$$$PATH_CRYPTO_CONFIG$$$"; // change this line to the correct path!

    public static void main(String[] args) {
        try {
        
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

            HFClient client = getClient();
            Channel channel = getChannel(client);

            query(client, channel);

            invoke(client, channel);

            query(client, channel);

        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    static void query(HFClient client, Channel channel) throws ProposalException, InvalidArgumentException {

        QueryByChaincodeRequest qpr = client.newQueryProposalRequest();
        ChaincodeID id = ChaincodeID.newBuilder().setName("chaincode1").build();
        qpr.setChaincodeID(id);

        qpr.setFcn("query");
        qpr.setArgs(new String[]{"a"});
        Collection<ProposalResponse> res = channel.queryByChaincode(qpr);

        for (ProposalResponse pres : res) {
            String s = new String(pres.getChaincodeActionResponsePayload());
            System.out.println(s);
        }
    }

    static void invoke(HFClient client, Channel channel) throws InvalidArgumentException, ProposalException, ChaincodeEndorsementPolicyParseException, IOException {

        TransactionProposalRequest req = client.newTransactionProposalRequest();
        ChaincodeID cid = ChaincodeID.newBuilder().setName("chaincode1").build();
        req.setChaincodeID(cid);
        req.setFcn("set");
        req.setArgs(new String[]{"a", "77"});
        final Collection responses = channel.sendTransactionProposal(req, channel.getPeers());

        CompletableFuture txFuture = channel.sendTransaction(responses, client.getUserContext());

        BlockEvent.TransactionEvent event;
        try {
            event = (TransactionEvent) txFuture.get();
            System.out.println(event.toString());
        } catch (InterruptedException ex) {
            System.out.println(ex.toString());
        } catch (ExecutionException ex) {
            System.out.println(ex.toString());
        }

    }

    static HFClient getClient() {
        HFClient client = null;
        try {
            client = getHfClient();

            client.setUserContext(new User() {

			    public String getName() {
			        return "PeerAdmin";
			    }

			    public Set getRoles() {
			        return null;
			    }

			    public String getAccount() {
			        return null;
			    }

			    public String getAffiliation() {
			        return null;
			    }

			    public Enrollment getEnrollment() {
			        return new Enrollment() {
			            public PrivateKey getKey() { // load admin private key
			                PrivateKey privateKey = null;
			                try {
			                    String k = validFile(PATH_CRYPTO_CONFIG + "/peerOrganizations/org1.chaincoder.org/users/Admin@org1.chaincoder.org/msp/keystore");
			                    File privateKeyFile = findFileSk(k);
			                    privateKey = getPrivateKeyFromBytes(toByteArray(new FileInputStream(privateKeyFile)));
			                } catch (FileNotFoundException ex) {
			                    System.out.println(ex.toString());
			                } catch (IOException ex) {
			                    System.out.println(ex.toString());
			                } catch (Exception ex) {
			                    System.out.println(ex.toString());
			                }
			                return privateKey;
			            }

			            public String getCert() {// read admin client certificate

			                String certificate = null;
			                try {
			                    String k = validFile(PATH_CRYPTO_CONFIG + "/peerOrganizations/org1.chaincoder.org/users/Admin@org1.chaincoder.org/msp/signcerts/Admin@org1.chaincoder.org-cert.pem");
			                    File certificateFile = new File(k);
			                    certificate = new String(toByteArray(new FileInputStream(certificateFile)), "UTF-8");
			                } catch (UnsupportedEncodingException ex) {
			                    System.out.println(ex.toString());
			                } catch (FileNotFoundException ex) {
			                    System.out.println(ex.toString());
			                } catch (IOException ex) {
			                    System.out.println(ex.toString());
			                }
			                return certificate;
			            }
			        };
			    }

			    public String getMspId() {
			        return "Org1MSP";
			    }
			});
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        return client;
    }

    static Channel getChannel(HFClient client) throws InvalidArgumentException, TransactionException, ProposalException {
        Channel channel = client.newChannel("channel1x2");
        Properties ordererProperties = new Properties();
        ordererProperties.setProperty("pemFile", validFile(PATH_CRYPTO_CONFIG + "/ordererOrganizations/chaincoder.org/orderers/orderer.chaincoder.org/tls/server.crt"));
        ordererProperties.setProperty("trustServerCertificate", "true"); // testing
        // environment
        // only
        // NOT
        // FOR
        // PRODUCTION!
        ordererProperties.setProperty("hostnameOverride", "orderer.chaincoder.org");
        ordererProperties.setProperty("sslProvider", "openSSL");
        ordererProperties.setProperty("negotiationType", "TLS");
        ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[]{5L, TimeUnit.MINUTES});
        ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[]{8L, TimeUnit.SECONDS});
        channel.addOrderer(client.newOrderer("orderer.chaincoder.org", "grpcs://localhost:7050", ordererProperties));  // use the network orderer container URL

        Properties peerProperties;
        peerProperties = new Properties();
        peerProperties.setProperty("pemFile", validFile(PATH_CRYPTO_CONFIG + "/peerOrganizations/org1.chaincoder.org/peers/peer0.org1.chaincoder.org/tls/server.crt"));
        peerProperties.setProperty("trustServerCertificate", "true"); // testing                                                                                                                       
        peerProperties.setProperty("hostnameOverride", "peer0.org1.chaincoder.org");
        peerProperties.setProperty("sslProvider", "openSSL");
        peerProperties.setProperty("negotiationType", "TLS");
        peerProperties.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 9000000);

        channel.addPeer(client.newPeer("peer0.org1.chaincoder.org", "grpcs://127.0.0.1:7051", peerProperties));

        channel.initialize();

        return channel;
    }

    static HFClient getHfClient() throws Exception {
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(cryptoSuite);
        return client;
    }

    // *******************************************************************************
    // ************************* helper functions ************************************
    // *******************************************************************************
    static PrivateKey getPrivateKeyFromBytes(byte[] data) throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        final Reader pemReader = new StringReader(new String(data));

        final PrivateKeyInfo pemPair;
        PEMParser pemParser = new PEMParser(pemReader);
        pemPair = (PrivateKeyInfo) pemParser.readObject();

        PrivateKey privateKey = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getPrivateKey(pemPair);

        return privateKey;
    }

    static File findFileSk(String directorys) {

        File directory = new File(directorys);

        File[] matches;
        matches = directory.listFiles();

        if (null == matches) {
            throw new RuntimeException(java.lang.String.format("Matches returned null does %s directory exist?", directory.getAbsoluteFile().getName()));
        }

        if (matches.length != 1) {
            throw new RuntimeException(java.lang.String.format("Expected in %s only 1 sk file but found %d", directory.getAbsoluteFile().getName(), matches.length));
        }

        return matches[0];
    }

    static byte[] toByteArray(InputStream input) throws IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }

    static String validFile(String s) {
        if (directoryExists(s) || fileExists(s)) {

        } else {
            System.out.println(s + " not exsting!!");
        }
        return s;
    }

    static boolean directoryExists(String filePathString) {
        File f = new File(filePathString);
        if (f.exists() && f.isDirectory()) {
            return true;
        }
        return false;
    }

    static boolean fileExists(String filePathString) {
        File f = new File(filePathString);
        if (f.exists() && !f.isDirectory()) {
            return true;
        }
        return false;
    }
}
      
           