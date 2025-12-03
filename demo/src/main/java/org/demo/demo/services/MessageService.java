package org.demo.demo.services;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.demo.demo.models.AccessTokenRequest;
import org.demo.demo.models.AddRole;
import org.demo.demo.models.Message;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;


@Service
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);
    @Value("${auth.audience}")
    private String audience;

    @Value("${auth.clientId}")
    private String clientId;

    @Value("${auth.clientSecret}")
    private String clientSecret;

    @Value("${auth.grantType}")
    private String grantType;

    public Message getPublicMessage() {
        final var text = "This is a public message.";

        return Message.from(text);
    }

    public Message getProtectedMessage() {
        final var text = "This is a protected message.";

        return Message.from(text);
    }

    public Message getCustomerMessage(Jwt user) {
        final String text = "This is a customer message for " + user.getSubject() + ".";

        return Message.from(text);
    }

    public Message getAdminMessage() {
        final var text = "This is an admin message.";

        return Message.from(text);
    }

    public String getAccessToken() throws UnirestException, JSONException {

        AccessTokenRequest accessTokenRequest = new AccessTokenRequest(clientId, clientSecret, audience, grantType);


        HttpResponse<JsonNode> response = Unirest.post(audience + "/oauth/token")
                .header("content-type", "application/json")
                .body(accessTokenRequest.toString())
                .asJson();

        log.info("Access token response: {}", response.getBody().toString());
        return response.getBody().getObject().getString("access_token");
    }

    public Message addRole(AddRole addRole) throws UnirestException, JSONException {
        String accessToken = getAccessToken();
        String urlCompliant = addRole.getUserId().replace("|", "%7C");

        HttpResponse<String> response = Unirest.post(audience + "/api/v2/users/" + urlCompliant + "/roles")
                .header("content-type", "application/json")
                .header("authorization", "Bearer " + accessToken)
                .header("cache-control", "no-cache")
                // TODO: Make the role ID dynamic as per the request
                .body("{ \"roles\": [ \"rol_ateA49X4oBWvfywq\" ] }")
                .asString();

        log.info("response: {}", response.getBody());

        if (response.getBody() != null) {
            return Message.from("Failed to add role for user: " + addRole.getUserId());
        }

        final var text = "Role added for user: " + addRole.getUserId();

        return Message.from(text);
    }
}
