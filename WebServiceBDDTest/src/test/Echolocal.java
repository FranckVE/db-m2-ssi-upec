package test;

import java.io.IOException;


import java.io.PrintWriter;
import java.sql.*;

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
 * Servlet implementation class echolocal
 */
@Path( "/echolocal" )
public class Echolocal{
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Echolocal() {
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
   	String localhost = "localhost";
   	String localurl = String.format("jdbc:mysql://%s/projet_banque", localhost);
   	System.out.println(localurl);
     try {
   		Class.forName("com.mysql.jdbc.Driver");			
   		Connection Connexion = DriverManager.getConnection(localurl,"root","riyaad");
   		Statement State = Connexion.createStatement();
   		String query = "SELECT nom, clepub FROM user WHERE prenom='" + log +"' and psswd='" +pass+"'";
   		System.out.println(query);
   		ResultSet resultat = State.executeQuery(query);
   		// we wait for one answer
   		while (resultat.next()) {
   			log = resultat.getString("nom");
   			cle = resultat.getString("clepub");
   			System.out.println(log+cle);
   		}

   	} catch (SQLException | ClassNotFoundException e1) {
   		//pw.println("erreur de Requete"+
   		e1.printStackTrace();
   	}


   return "login =" +log +"   cle = "+cle;
   
//	Client c = Client.create();
//  String repurl = "http://wsbdd-projetcdai.rhcloud.com/WebService/rest/echotest?login="+log+"&pubKey="+cle;
//  WebResource resource = c.resource(repurl);
//  String response = resource.get(String.class);
//	return response;
   

   //return "Hello Jersey in plain text";
   }
   	

   // This method is called if request is HTML
//   @GET
//   @Produces( MediaType.TEXT_HTML )
//   public String sayHtmlHello()
//   {
//	   
//	  
////   return "<html> "
////   + "<title>" + "Hello Jersey" + "</title>"
////   + "<body><h1>"
////   + "Hello Jersey in HTML"
////   + "</body></h1>"
////   + "</html> ";
//   }

   // This method is called if request is XML
 //  @GET
//   @Produces( MediaType.TEXT_XML )
//   public String sayXMLHello()
//   {
//   return "<?xml version=\"1.0\"?>" + "<hello> Hello Jersey in XML" + "</hello>";
//   }

}
