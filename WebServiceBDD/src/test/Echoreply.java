package test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.net.URI;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.xml.internal.ws.api.server.Container;

/**
 * Servlet implementation class Echoreply
 */
public class Echoreply{
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Echoreply() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Context ServletContext context;

   	// This method is called if request is TEXT_PLAIN
   	@GET
   	@Produces( MediaType.TEXT_HTML )
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
     
  	Client c = Client.create();
    String repurl = "http://wsbanque-projetcdai.rhcloud.com/banque/rest/recup?login="+log+"&pubKey="+cle;
    WebResource resource = c.resource(repurl);
    String response = resource.get(String.class);
  	return response;

   //return "login =" +log +"   cle = "+cle ;

   //return "Hello Jersey in plain text";
   }

}
