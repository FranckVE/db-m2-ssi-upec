package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import com.google.gson.Gson;

public class BanqueBdd {

	public BanqueBdd() {
		Gson gson = new Gson();
		gson.toJson(1);
		System.out.println(gson);
	}

	public String getAccounts(String log) {
		// (we don't really want to use ServletContext, just show that we
		// could:)
		String cle = "";
		String num = "";
		String type = "";
		float balance = 0;
		String host = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
		String port = System.getenv("OPENSHIFT_MYSQL_DB_PORT");
		String url = String.format("jdbc:mysql://%s:%s/wsbanque", host, port);

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection Connexion = DriverManager.getConnection(url,
					"admin3JrZAdc", "dUrDdRkl6DAV");
			Statement State = Connexion.createStatement();
			String query = "SELECT * FROM comptes WHERE login ='" + log + "'";
			try {
				ResultSet resultat = State.executeQuery(query);

				boolean passe = false;
				// we wait for one answer
				while (resultat.next()) {
					num = resultat.getString("num_compte");
					type = resultat.getString("type_compte");
					balance = resultat.getFloat("argent");
					passe = true;
				}
				if (!passe)
					return "rejet";
			} catch (Exception e) {
				return "rejet";
			}

		} catch (SQLException | ClassNotFoundException e1) {
			// pw.println("erreur de Requete"+
			return e1.getLocalizedMessage();
		}

		return "login =" + log + "   cle = " + cle;

		// return "Hello Jersey in plain text";
	}

}
