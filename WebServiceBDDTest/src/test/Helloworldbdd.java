package test;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Helloworld
 */
public class Helloworldbdd extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Helloworldbdd() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String host = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
		String port = System.getenv("OPENSHIFT_MYSQL_DB_PORT");
		String url = String.format("jdbc:mysql://%s:%s/wsbdd", host, port);
		
	      PrintWriter pw = response.getWriter() ;
	      pw.write("Hello tout le monde !!") ;
	      
	      try {
				Class.forName("com.mysql.jdbc.Driver");			
			//	Connection Connexion = DriverManager.getConnection("jdbc:mysql://System.getenv(OPENSHIFT_MYSQL_DB_HOST):System.getenv(OPENSHIFT_MYSQL_DB_HOST)/wsbdd","adminRf3g7If","RPGjUx1GUsiX");
			//	Connection Connexion = DriverManager.getConnection("jdbc:mysql://localhost/projet_banque","root","riyaad");
				Connection Connexion = DriverManager.getConnection(url,"adminRf3g7If","RPGjUx1GUsiX");
				Statement State = Connexion.createStatement();
				ResultSet resultat = State.executeQuery("SELECT nom,prenom FROM credentials");
				while (resultat.next()) {
					pw.println("nom complet"+resultat.getString("nom")+" "+resultat.getString("prenom")+"</BR>");
				}
	 
			} catch (SQLException | ClassNotFoundException e1) {
				//pw.println("erreur de Requete"+
				e1.printStackTrace(pw);
			}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response) ;
	}

}
