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

	/**
	 * Fonction récupérant dans la BdD tous les comptes d'une personne
	 * log Login de la personne concernée
	 */
	public ArrayList<Compte> getAccounts(String log) {
		ArrayList<Compte> listComptes = new ArrayList<Compte>();
		
		String host = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
		String port = System.getenv("OPENSHIFT_MYSQL_DB_PORT");
		String url = String.format("jdbc:mysql://%s:%s/wsbanque", host, port);

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection Connexion = DriverManager.getConnection(url,"admin3JrZAdc", "dUrDdRkl6DAV");
			Statement State = Connexion.createStatement();
			String query = "SELECT * FROM comptes WHERE login ='" + log + "'";
			try {
				ResultSet resultat = State.executeQuery(query);

				boolean passe = false;
				while (resultat.next()) {
					listComptes.add(new Compte(resultat.getString("num_compte"), 
							resultat.getString("type_compte"), resultat.getFloat("argent")));
					passe = true;
				}
				if (!passe)
					return "rejet";
			} catch (Exception e) {
				return "rejet";
			}

		} catch (SQLException | ClassNotFoundException e1) {
			return e1.getLocalizedMessage();
		}

		return listComptes;
	}

}
