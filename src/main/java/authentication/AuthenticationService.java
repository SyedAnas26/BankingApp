package authentication;

import connectors.DbConnector;
import models.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Types;
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

            String adminQuery = "select * from user where email='"+email+"' and password='"+password+"';";
            User loggedUser = (User) DbConnector.get(adminQuery, rs -> {
                if(rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setAdmin(rs.getBoolean("is_admin"));
                    return user;
                }
                return null;
            });
            return Response.ok().entity("{\"token\":\""+token +"\",\"isAdmin\":\""+loggedUser.getAdmin() +"\", \"userId\":\""+ loggedUser.getId()+"\" }").build();
        }else
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"status\":\"failed\", \"reason\":\"invalid credentials\"}").build();
    }


    @Path("/register")
    @POST
    public Response registerUser(@FormParam("email") String email,
                                 @FormParam("password") String password,
                                 @FormParam("firstName") String firstName,
                                 @FormParam("lastName") String lastName) throws Exception {

        String query = "insert into user(email,first_name,last_name,password) values ( '"+email+"','"+firstName+"','"+
                lastName+"','"+password+"')";
        try {
            int userId = DbConnector.insert(query);
            return Response.ok().entity("{\"status\":\"success\", \"userId\": \" "+userId+ "\"}").build();
        }catch(SQLIntegrityConstraintViolationException e){
            return Response.status(Response.Status.CONFLICT).entity("{\"status\":\"failed\", \"reason\":\"Email should be unique\"}").build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @Path("/logout")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Response logoutUser(User user) throws Exception {


        try {
            String updateQuery = "update user set token ='"+ Types.NULL+"' where id='"+user.getId()+"'";
            DbConnector.update(updateQuery);
            return Response.ok().entity("{\"status\":\"success\"}").build();
        }catch(SQLIntegrityConstraintViolationException e){
            return Response.status(Response.Status.CONFLICT).entity("{\"status\":\"failed\", \"reason\":\"Email should be unique\"}").build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.serverError().build();
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
