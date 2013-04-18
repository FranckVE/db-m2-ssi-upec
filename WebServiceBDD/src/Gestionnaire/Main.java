package Gestionnaire;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Servlet implementation class Main
 */
@Path("/Main")
public class Main extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Engine engine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */

    public Main() {
        super();
        Engine engine = new Engine();
    }
	
	@GET
	@Produces( MediaType.TEXT_PLAIN )
	public String sayPlainTextHello(@DefaultValue("error") @QueryParam("cipher") String cipher){
		return "";
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
