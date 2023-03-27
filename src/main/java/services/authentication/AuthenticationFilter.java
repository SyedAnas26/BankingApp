package services.authentication;

import com.mysql.cj.exceptions.WrongArgumentException;
import connectors.DbConnector;
import services.Secured;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.sql.ResultSet;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final String REALM = "example";
    private static final String AUTHENTICATION_SCHEME = "Bearer";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        String uriPath = requestContext.getUriInfo().getPath();

        int userId = Integer.parseInt(uriPath.split("/")[1]);
        String userType = uriPath.split("/")[0];

        if (!isTokenBasedAuthentication(authorizationHeader)) {
            abortWithUnauthorized(requestContext);
            return;
        }

        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();

        try {
            if(!validateToken(token,userId,userType))
                abortWithUnauthorized(requestContext);
        } catch (Exception e) {
            e.printStackTrace();
            abortWithUnauthorized(requestContext);
        }
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {

        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");

    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {

        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .header(HttpHeaders.WWW_AUTHENTICATE,
                                AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"")
                        .build());

    }

    private Boolean validateToken(String token, int userId, String userType) throws Exception {


        if(userType == null || token.equals("null"))
            throw new WrongArgumentException();
        String query = "select 1 from user where id='" + userId + "' and token='" + token + "';";;
        return (Boolean) DbConnector.get(query, ResultSet::next);
    }

}
