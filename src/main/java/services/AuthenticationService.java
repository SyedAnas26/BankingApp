package services;

import connectors.DbConnector;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Base64;

@Path("/auth")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)

public class AuthenticationService {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    @Path("/login")
    @POST
    public Response authenticateUser(@FormParam("email") String email,
                                     @FormParam("password") String password) throws Exception {
        if(isAuthenticated(email,password)){
            String token = generateToken();
            String updateQuery = "update user set token ='"+token+"' where email='"+email+"'";
            DbConnector.update(updateQuery);
            String adminQuery = "select is_admin from user where email='"+email+"' and password='"+password+"';";
            Boolean isAdmin = (Boolean) DbConnector.get(adminQuery, rs -> rs.getBoolean("is_admin"));
            return Response.ok().entity("{\"token\":\""+token +"\"},\"isAdmin\":\""+isAdmin +"\"}").build();
        }else
            return Response.status(Response.Status.FORBIDDEN).build();
    }


    @Path("/register")
    @POST
    public Response registerUser(@FormParam("email") String email,
                                 @FormParam("password") String password,
                                 @FormParam("firstName") String firstName,
                                 @FormParam("lastName") String lastName) throws Exception {

        String query = "insert into user(email,firstName,lastName,password) values ( '"+email+"','"+firstName+"','"+
                lastName+"','"+password+"')";
        try {
            int userId = DbConnector.insert(query);
            return Response.ok(userId).build();
        }catch(SQLIntegrityConstraintViolationException e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    private boolean isAuthenticated(String email, String password) throws Exception {
        String query = "select 1 from user where email='"+email+"' and password='"+password+"';";
        return (Boolean) DbConnector.get(query, ResultSet::next);
    }

    private String generateToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }


}
