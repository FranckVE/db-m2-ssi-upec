package engine;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.*;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.SecretKey;

import org.bouncycastle.util.encoders.Base64;

/**
 * Servlet implementation class Verify
 */
public class Engine {

	private Statement state = null;

	public Engine() {
		String host = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
		String port = System.getenv("OPENSHIFT_MYSQL_DB_PORT");
		String url = String.format("jdbc:mysql://%s:%s/wsbdd", host, port);

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection Connexion = DriverManager.getConnection(url,
					"adminRf3g7If", "RPGjUx1GUsiX");
			state = Connexion.createStatement();
		} catch (SQLException | ClassNotFoundException e1) {
		}
	}

	public RSAPublicKey verifBanque(String nom_banque, String hash) {
		PublicKey cle_pub = null;
		String id_banque = null;
		PublicKey pubKey = null;

		String query = "SELECT * FROM banques WHERE nom ='" + nom_banque + "'";
		try {
			ResultSet resultat = state.executeQuery(query);

			boolean passe = false;
			// we wait for one answer
			while (resultat.next()) {
				pubKey = Engine.getPublicKeyBase64(resultat.getString("pubKey"));
				id_banque = resultat.getString("id");
				passe = true;
			}
			if(!passe) return null;
		} catch (Exception e) {
			return null;
		}
		
		// Vérification du ID et de son hash
//		if (CryptoUtils.verify(id_banque, hash, pubKey))
//			return cle_pub;
		return (RSAPublicKey)pubKey;
	}

	public boolean verifUser(String login, String mdp, String hash) {
		String result = "OK";
		PublicKey pubKey = null;

		// Premier test de login / mdp
		String query = "SELECT * FROM credentials WHERE login ='" + login
				+ "' and mdp ='" + mdp + "'";

		try {
			ResultSet resultat = state.executeQuery(query);
			boolean passe = false;

			while (resultat.next()) {
				pubKey = Engine.getPublicKeyBase64(resultat.getString("pubKey"));
				passe = true;
			}
			if (!passe)
				return false;
		} catch (Exception e) {
			return false;
		}

		// Vérification du ID et de son hash
//		if (CryptoUtils.verify(mdp, hash, pubKey))
//			return "OK";
		return true;
	}

	// Génère une clé de session
	public SecretKey sessionKeyGenerator() {
		return CryptoUtils.initAES128();
	}
	
	
	// ******************************************************************************
	//
	//						FONCTIONS D'ECHANGES DE DONNEES
	//
	// ******************************************************************************
	
	public byte[][] receiveChallenge (String chaine_recu){
		
		byte []  chaine = Base64.decode(chaine_recu);
		
		//RSAPrivateKey privKey = (RSAPrivateKey) CryptoUtils.loadPrivateKey(path, "RSA");
		RSAPrivateKey privKey = null;
		String query = "SELECT * FROM keys WHERE nom = 'private'";
		try {
			ResultSet resultat = state.executeQuery(query);

			boolean passe = false;
			// we wait for one answer
			while (resultat.next()) {
				privKey = (RSAPrivateKey) Engine.getPrivateKeyBase64(resultat.getString("key"));
				passe = true;
			}
			if(!passe) return null;
		} catch (Exception e) {
			return null;
		}
		
		byte[] dec = CryptoUtils.adecRSA(chaine, privKey);
		
		byte deconc [][] = CryptoUtils.deconcat(new String(dec));
		
		
		return deconc;
	}
	
	public String sendSessionKey (byte[] Ksession,RSAPublicKey pubKey){
		
		long time_l =  System.currentTimeMillis();
		byte [] time = ByteBuffer.allocate(8).putLong(time_l).array();
		
		
		byte [][] tab = new byte [2][];
		tab [0] = time;
		tab [1] = Ksession;
		
		String chaine_concat = CryptoUtils.concat(tab);
		
		try {
			byte[] enc = CryptoUtils.aencRSA(chaine_concat.getBytes(), pubKey);
			
			 System.out.println("sendSessionKey : --> "+new String(enc));
			 System.out.println("sendSessionKey (time) : --> "+new String(time));
			return new String(Base64.encode(enc));
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public byte[][] receiveLoginPassword (String chaine_recu,SecretKey Ksession){

		byte [] chaine = Base64.decode(chaine_recu);
		
		byte[] dec = CryptoUtils.decAES128(chaine, Ksession);
		byte deconc [][] = CryptoUtils.deconcat(new String(dec));
		 System.out.println("receiveLogin: -->"+new String(deconc[0]));
		 System.out.println("receivePassword: --> "+deconc[1]);
		return deconc;
	}
	
	public String sendOK(SecretKey Ksession){
		
		String accept = new String("ACCEPT");
		byte [] reponse = accept.getBytes();
		
		
		try {
			byte[] enc = CryptoUtils.encAES128(reponse, Ksession);
			return new String(Base64.encode(enc));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;	
	}

	public String sendFalse(SecretKey Ksession){
		
		String accept = new String("REFUSE");
		byte [] reponse = accept.getBytes();
		
		try {
			byte[] enc = CryptoUtils.encAES128(reponse, Ksession);
			return new String(Base64.encode(enc));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	
	// ******************************************************************************
	//
	//						FONCTIONS DE CHIFFREMENT ET CLES
	//
	// ******************************************************************************

	public static PublicKey getPublicKeyBase64(String keyBase64) {

		byte[] keyEncoded = Base64.decode(keyBase64.getBytes());
		return getPublicKeyEncoded(keyEncoded);
	}

	private static PublicKey getPublicKeyEncoded(byte[] publicKeyData) {

		PublicKey pk = null;
		try {
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
					publicKeyData);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
			pk = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);

			return pk;
		} catch (InvalidKeySpecException | NoSuchAlgorithmException
				| NoSuchProviderException ex) {
			//Logger.getLogger(API.class.getName()).log(Level.SEVERE, null, ex);
		}
		return pk;
	}
	
	public static PrivateKey getPrivateKeyBase64(String keyBase64) {

		byte[] keyEncoded = Base64.decode(keyBase64.getBytes());
		return getPrivateKeyEncoded(keyEncoded);
	}
	
	private static PrivateKey getPrivateKeyEncoded(byte[] encodedPrivateKey) {
		PrivateKey privateKey = null;
		try {

			// Generate KeyPair.
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
					encodedPrivateKey);
			privateKey = keyFactory.generatePrivate(privateKeySpec);
			return privateKey;
		} catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
			Logger.getLogger(CryptoUtils.class.getName()).log(Level.SEVERE,
					null, ex);
		}

		return privateKey;
	}
}
