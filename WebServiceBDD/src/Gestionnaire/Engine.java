package Gestionnaire;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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

	public String verif_banque(String nom_banque, String hash) {
		String cle_pub = null;
		String id_banque = null;

		String query = "SELECT * FROM banques WHERE nom ='" + nom_banque + "'";
		try {
			ResultSet resultat = State.executeQuery(query);

			boolean passe = false;
			// we wait for one answer
			while (resultat.next()) {
				cle_pub = resultat.getString("pubKey");
				id_banque = resultat.getString("id");
				passe = true;
			}
			if (!passe)
				return null;
		} catch (Exception e) {
			return null;
		}

		// faire verification du hash avec id_banque

		return cle_pub;
	}

	// genere cle de seesion

}
