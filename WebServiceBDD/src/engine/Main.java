package engine;

import java.security.interfaces.RSAPublicKey;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServlet;
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
	private SecretKey sessionKey;

    public Main() {
        super();
        engine = new Engine();
    }
    
    @Context
	ServletContext context;
	
	@GET
	@Produces( MediaType.TEXT_PLAIN )
	public String sayPlainTextHello(@DefaultValue("error") @QueryParam("cipher") String cipher,
									@DefaultValue("error") @QueryParam("id") String id){
		
		// Premier échange : pour générer la clé de session
		if(!cipher.equals("error") && id.equals("1")) {

			byte[][] temp = engine.receiveChallenge(cipher);
			String nomBanque = new String(temp[1]);
			String hash = new String(temp[2]);
			
			RSAPublicKey pubKey = engine.verifBanque(nomBanque, hash);
			if(pubKey != null) {
				sessionKey = engine.sessionKeyGenerator();
				return engine.sendSessionKey(sessionKey.getEncoded(), pubKey);
			}
			return "null";
		} 
		
		// Second échange : données d'authentification du client
		else if(!cipher.equals("error") && id.equals("2")) {
			
			byte[][] temp = engine.receiveLoginPassword(cipher, sessionKey);
			String login = new String(temp[0]);
			String mdp = new String(temp[1]);
			
			if(engine.verifUser(login, mdp, "")) {
				return engine.sendOK(sessionKey);
			} else {
				return engine.sendFalse(sessionKey);
			}
		} else {
			return "Error";
		}
	}
}
