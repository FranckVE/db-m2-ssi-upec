package test;

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
 * Servlet implementation class Echotest
 */

@Path( "/echotest" )
public class Echotest {
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Echotest() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Context ServletContext context;

	// This method is called if request is TEXT_PLAIN
	@GET
	@Produces( MediaType.TEXT_PLAIN )
	public String sayPlainTextHello(   @DefaultValue("test") @QueryParam("login") String  log,
            						   @DefaultValue("test") @QueryParam("password") String pass	){
	// (we don't really want to use ServletContext, just show that we could:)
	ServletContext ctx = context;
	String cle="";
	String host = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
	String port = System.getenv("OPENSHIFT_MYSQL_DB_PORT");
	String url = String.format("jdbc:mysql://%s:%s/wsbdd", host, port);
  
  try {
		Class.forName("com.mysql.jdbc.Driver");			
		Connection Connexion = DriverManager.getConnection(url,"adminRf3g7If","RPGjUx1GUsiX");
		Statement State = Connexion.createStatement();
		String query = "SELECT login, pubKey FROM credentials WHERE login ='" + log +"' and mdp ='" +pass+"'";
		try{ 
			ResultSet resultat = State.executeQuery(query);
			
			boolean passe = false;
			// we wait for one answer
			while (resultat.next()) {
				log = resultat.getString("login");
				cle = resultat.getString("pubKey");
				passe = true;
			}
			if(!passe) return "rejet";
		} catch(Exception e) {
			return "rejet";
		}
		

	} catch (SQLException | ClassNotFoundException e1) {
		//pw.println("erreur de Requete"+
		return e1.getLocalizedMessage();
	}


return "login =" +log +"   cle = "+cle ;

//return "Hello Jersey in plain text";
}

// This method is called if request is HTML
//@GET
//@Produces( MediaType.TEXT_HTML )
//public String sayHtmlHello()
//{
//return "<html> "
//+ "<title>" + "Hello Jersey" + "</title>"
//+ "<body><h1>"
//+ "Hello Jersey in HTML"
//+ "</body></h1>"
//+ "</html> ";
//}
//
//// This method is called if request is XML
//@GET
//@Produces( MediaType.TEXT_XML )
//public String sayXMLHello()
//{
//return "<?xml version=\"1.0\"?>" + "<hello> Hello Jersey in XML" + "</hello>";
//}
//	

}
