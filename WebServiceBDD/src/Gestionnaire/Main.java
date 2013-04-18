package Gestionnaire;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Main
 */
public class Main extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Engine engine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	@Path("/Main")
    public Main() {
        super();
        Engine engine = new Engine();
    }
	
	@GET
	@Produces( MediaType.TEXT_PLAIN )
	public String sayPlainTextHello(@DefaultValue("error") @QueryParam("cipher") String cipher){
		
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
