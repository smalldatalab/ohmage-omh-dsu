package org.openmhealth.dsu.service;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Service that communicate with the shims server to get authorization url or remove the previous authorization.
 * Created by Cheng-Kang Hsieh on 3/31/15.
 */
public interface ShimService {
    /**
     * This exception is thrown when the user has authorize the targeted and it is not possible to get auth url without deauthorization
     */
    public class UserIsAuthorizedException extends Exception {
    }

    /**
     * Return url that init the oauth process
     *
     * @param shim           the targeted app (e.g. moves)
     * @param auth           the user's identity
     * @param servletRequest the servlet request used to determine the user's device
     * @return the auth url
     * @throws UserIsAuthorizedException if the shim server already has the auth token for the targeted app. Need to de-authorizee first to get a new auth url.
     * @throws IOException
     */
    String getAuthorizationUrl(String shim, Authentication auth,
                               HttpServletRequest servletRequest) throws UserIsAuthorizedException, IOException;


    /**
     * De-authorize the auth token to the app
     *
     * @param shim the targeted app
     * @param auth the user's identity
     * @return if de-authorization succeed
     * @throws IOException
     */
    boolean deauthorize(String shim, Authentication auth) throws IOException;


    /**
     * Check if the shim server already has the auth token for the user and the targeted app.
     *
     * @param shim the targeted app
     * @param auth the user's identity
     * @return
     * @throws IOException
     */
    boolean isAuthorized(String shim, Authentication auth) throws IOException;

}
