package com.example.springserver.dao;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
// import java.util.List;

import com.example.springserver.pojo.Asset;
import com.example.springserver.pojo.AssetGo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.hyperledger.fabric.client.CallOption;
import org.hyperledger.fabric.client.CommitException;
import org.hyperledger.fabric.client.CommitStatusException;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.EndorseException;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.GatewayException;
import org.hyperledger.fabric.client.Network;
import org.hyperledger.fabric.client.SubmitException;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Identity;
import org.hyperledger.fabric.client.identity.X509Identity;
import org.hyperledger.fabric.client.identity.Signer;
import org.hyperledger.fabric.client.identity.Signers;
// import org.hyperledger.fabric.protos.gateway.ErrorDetail;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class AssetDao {


	private static final String mspID = "Org1MSP";
	private static final String channelName = "mychannel";
	private static final String chaincodeName = "mychaincode";

	// Path to crypto materials.
	private static final Path cryptoPath = Paths.get("/home/fab/fablab", "fabric-samples", "test-network", "organizations", "peerOrganizations", "org1.example.com");
	// Path to user certificate.
	private static final Path certPath = cryptoPath.resolve(Paths.get("users", "User1@org1.example.com", "msp", "signcerts", "cert.pem"));
	// Path to user private key directory.
	private static final Path keyDirPath = cryptoPath.resolve(Paths.get("users", "User1@org1.example.com", "msp", "keystore"));
	// Path to peer tls certificate.
	private static final Path tlsCertPath = cryptoPath.resolve(Paths.get("peers", "peer0.org1.example.com", "tls", "ca.crt"));

	// Gateway peer end point.
	private static final String peerEndpoint = "localhost:7051";
	private static final String overrideAuth = "peer0.org1.example.com";

	private Contract contract;
	private ManagedChannel channel;

	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public AssetDao() {
		try {
			channel = newGrpcConnection();
			Gateway.Builder builder = Gateway.newInstance().identity(newIdentity()).signer(newSigner()).connection(channel)
					// Default timeouts for different gRPC calls
					.evaluateOptions(CallOption.deadlineAfter(5, TimeUnit.SECONDS))
					.endorseOptions(CallOption.deadlineAfter(15, TimeUnit.SECONDS))
					.submitOptions(CallOption.deadlineAfter(5, TimeUnit.SECONDS))
					.commitStatusOptions(CallOption.deadlineAfter(1, TimeUnit.MINUTES));
			Gateway gateway=builder.connect();
			// Get a network instance representing the channel where the smart contract is
			// deployed.
			Network network = gateway.getNetwork(channelName);

			// Get the smart contract from the network.
			contract = network.getContract(chaincodeName);
			log.info("new assetdao");
			initLedger();
		} catch(Exception e)
		{
			e.printStackTrace();
		}
    }

	protected void finalize() {
		if (channel!=null) {
			try {
				channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static ManagedChannel newGrpcConnection() throws IOException, CertificateException {
		Reader tlsCertReader = Files.newBufferedReader(tlsCertPath);
		X509Certificate tlsCert = Identities.readX509Certificate(tlsCertReader);

		return NettyChannelBuilder.forTarget(peerEndpoint)
				.sslContext(GrpcSslContexts.forClient().trustManager(tlsCert).build()).overrideAuthority(overrideAuth)
				.build();
	}

	private static Identity newIdentity() throws IOException, CertificateException {
		Reader certReader = Files.newBufferedReader(certPath);
		X509Certificate certificate = Identities.readX509Certificate(certReader);

		return new X509Identity(mspID, certificate);
	}


	private static Signer newSigner() throws IOException, InvalidKeyException {
		Path keyPath = Files.list(keyDirPath)
				.findFirst()
				.orElseThrow();
		Reader keyReader = Files.newBufferedReader(keyPath);
		PrivateKey privateKey = Identities.readPrivateKey(keyReader);

		return Signers.newPrivateKeySigner(privateKey);
	}

	/**
	 * This type of transaction would typically only be run once by an application
	 * the first time it was started after its initial deployment. A new version of
	 * the chaincode deployed later would likely not need to run an "init" function.
	 */
	private void initLedger() throws EndorseException, SubmitException, CommitStatusException, CommitException {
		log.info("--> Submit Transaction: InitLedger, function creates the initial set of assets on the ledger");

		contract.submitTransaction("InitLedger");

		log.info("*** Transaction committed successfully");
	}

	public Asset[] getAllAssets(){
		String result=null;
		try{
			log.info("--> Evaluate Transaction: GetAllAssets, function returns all the current assets on the ledger");
			
			byte[] resultB = contract.evaluateTransaction("GetAllAssets");
			result = new String(resultB,StandardCharsets.UTF_8);
		} catch(GatewayException ge) {
			log.error("error get assets");
			ge.printStackTrace();
		}
		if(result==null) {
			return new Asset[0];
		}
		AssetGo[] assetGos = gson.fromJson(result,AssetGo[].class);
		Asset[] assets = new Asset[assetGos.length];
		for(int i=0;i<assetGos.length;i++) {
			assets[i]=assetGos[i].toAsset();
		}
		return assets;
	}

	public Boolean createNewAsset(Asset asset){
		try{
			log.info("--> Submit Transaction: CreateAsset, creates new asset with ID, Color, Size, Owner and AppraisedValue arguments");
			contract.submitTransaction("CreateAsset", asset.getId(), asset.getName(), asset.getCount(), asset.getOwner());
			//contract.submitTransaction("createAsset",asset.getId());
		} catch(GatewayException|CommitException ge) {
			log.error("error create asset");
			ge.printStackTrace();
			return false;
		} 
		return true;
	}

	public Asset readAssetById(String assetId){
		try {
			log.info("\n--> Evaluate Transaction: ReadAsset, function returns asset attributes");
		
			byte[] evaluateResult = contract.evaluateTransaction("ReadAsset", assetId);
			String stringResult = new String(evaluateResult,StandardCharsets.UTF_8);
			AssetGo resultGo = gson.fromJson(stringResult, AssetGo.class);
			Asset result = resultGo.toAsset();
		
			log.info("*** Result:" + prettyJson(evaluateResult));

			return result;
		} catch (Exception e) {
			log.error("error read asset");
			e.printStackTrace();
			return null;
		}
	}

	public Boolean updateAsset(Asset asset) {
		try {
			// log.info("\n--> Submit Transaction: UpdateAsset asset70, asset70 does not exist and should return an error");
			
			contract.submitTransaction("UpdateAsset", asset.getId(), asset.getName(), asset.getCount(), asset.getOwner());
			// log.info("******** FAILED to return an error");
		} catch (EndorseException | SubmitException | CommitStatusException e) {
			// log.info("*** Successfully caught the error: ");
			e.printStackTrace(System.out);
			// log.info("Transaction ID: " + e.getTransactionId());

			// List<ErrorDetail> details = e.getDetails();
			// if (!details.isEmpty()) {
			// 	System.out.println("Error Details:");
			// 	for (ErrorDetail detail : details) {
			// 		System.out.println("- address: " + detail.getAddress() + ", mspId: " + detail.getMspId()
			// 				+ ", message: " + detail.getMessage());
			// 	}
			// }
			return false;
		} catch (CommitException e) {
			// log.info("*** Successfully caught the error: " + e);
			e.printStackTrace(System.out);
			// log.info("Transaction ID: " + e.getTransactionId());
			// log.info("Status code: " + e.getCode());
			return false;
		}
		return true;
	}

	public Boolean deleteAssetById(String assetId){
		try {
			// log.info("\n--> Evaluate Transaction: DeleteAsset, function returns asset attributes");
		
		    byte[] evaluateResult = contract.evaluateTransaction("DeleteAsset", assetId);
			
			log.info("*** Result:" + prettyJson(evaluateResult));
		} catch (Exception e) {
			// log.error("error delete asset");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private String prettyJson(final byte[] json) {
		return prettyJson(new String(json, StandardCharsets.UTF_8));
	}

	private String prettyJson(final String json) {
		JsonElement parsedJson = JsonParser.parseString(json);
		return gson.toJson(parsedJson);
	}
}
