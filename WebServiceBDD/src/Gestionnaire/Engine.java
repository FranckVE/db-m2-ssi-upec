package Gestionnaire;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.sql.*;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.SecretKey;
import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import sun.misc.BASE64Decoder;

/**
 * Servlet implementation class Verify
 */
public class Engine {

	public String host = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
	public String port = System.getenv("OPENSHIFT_MYSQL_DB_PORT");
	public String url = String.format("jdbc:mysql://%s:%s/wsbdd", host, port);
	public Statement State = null;

	public Engine() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection Connexion = DriverManager.getConnection(url,
					"adminRf3g7If", "RPGjUx1GUsiX");
			State = Connexion.createStatement();
		} catch (SQLException | ClassNotFoundException e1) {
		}
	}

	public RSAPublicKey verif_banque(String nom_banque, String hash) {
		RSAPublicKey cle_pub = null;
		String id_banque = null;
		boolean verify = false;
		byte[] modulus = null;
		byte[] exponent = null;

		String query = "SELECT * FROM banques WHERE nom ='" + nom_banque + "'";
		try {
			ResultSet resultat = State.executeQuery(query);

			boolean passe = false;
			// we wait for one answer
			while (resultat.next()) {
				BASE64Decoder decoder = new BASE64Decoder();
				modulus =decoder.decodeBuffer(resultat.getString("pubModulus"));
				exponent = decoder.decodeBuffer(resultat.getString("pubExponent"));
				id_banque = resultat.getString("id");
				passe = true;
			}
			if (!passe)
				return null;
		} catch (Exception e) {
			return null;
		}
		
		// faire verification du hash avec id_banque
		cle_pub = CryptoUtils.getRSAPubKey(exponent, modulus);
		verify = CryptoUtils.verify(id_banque, hash, cle_pub);
		
		if (verify) {
			return cle_pub;
		} else {
			return null;
		}
	}

	// genere cle de session
	public byte[] sessionKeyGenerator(){
		
		SecretKey sessionKey = CryptoUtils.initAES128();
		return sessionKey.getEncoded();
		
	}
	
	public String packetGenerator(SecretKey sessionKey, RSAPublicKey pubKey){
		
		String packet = null;
		ByteBuffer bb = ByteBuffer.allocate(8);
		byte[] time = new byte[8];
		bb.putLong(System.currentTimeMillis()).get(time, 0, 8);
		
		
		return packet;
	}
	

}
