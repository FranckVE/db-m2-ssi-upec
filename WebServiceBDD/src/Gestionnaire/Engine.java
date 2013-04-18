package Gestionnaire;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.sql.*;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.SecretKey;

import sun.misc.BASE64Decoder;

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
		RSAPublicKey cle_pub = null;
		String id_banque = null;
		PublicKey pubKey = null;

		String query = "SELECT * FROM banques WHERE nom ='" + nom_banque + "'";
		try {
			ResultSet resultat = state.executeQuery(query);

			boolean passe = false;
			// we wait for one answer
			while (resultat.next()) {
				BASE64Decoder decoder = new BASE64Decoder();
				pubKey = this.getPublicKeyBase64(resultat.getString("pubKey"));
				id_banque = resultat.getString("id");
				passe = true;
			}
			if (!passe)
				return null;
		} catch (Exception e) {
			return null;
		}
		
		// Vérification du ID et de son hash
		if (CryptoUtils.verify(mdp, hash, pubKey))
			return cle_pub;
		return null;
	}

	public String verifUser(String login, String mdp, String hash) {
		String result = "OK";
		PublicKey pubKey = null;

		// Premier test de login / mdp
		String query = "SELECT * FROM credentials WHERE login ='" + login
				+ "' and mdp ='" + mdp + "'";

		try {
			ResultSet resultat = state.executeQuery(query);
			boolean passe = false;

			while (resultat.next()) {
				pubKey = this.getPublicKeyBase64(resultat.getString("pubKey"));
				passe = true;
			}
			if (!passe)
				return "REJECT";
		} catch (Exception e) {
			return "REJECT";
		}

		// Vérification du ID et de son hash
		if (CryptoUtils.verify(mdp, hash, pubKey))
			return cle_pub;
		return null;
	}

	// genere cle de session
	public byte[] sessionKeyGenerator() {

		SecretKey sessionKey = CryptoUtils.initAES128();
		return sessionKey.getEncoded();
	}
	
	// ******************************************************************************

	public static PublicKey getPublicKeyBase64(String keyBase64) {

		byte[] keyEncoded = Base64.decode(keyBase64.getBytes());
		return getPublicKey1(keyEncoded);
	}

	private static PublicKey getPublicKey1(byte[] key) {
		// return getRSAPubKeyEncoded(key);
		return getPublicKeyEncoded(key);
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
			Logger.getLogger(API.class.getName()).log(Level.SEVERE, null, ex);
		}
		return pk;
	}
	
	// ******************************************************************************

	public String packetGenerator(SecretKey sessionKey, RSAPublicKey pubKey) {

		String packet = null;
		ByteBuffer bb = ByteBuffer.allocate(8);
		byte[] time = new byte[8];
		bb.putLong(System.currentTimeMillis()).get(time, 0, 8);

		return packet;
	}

}
