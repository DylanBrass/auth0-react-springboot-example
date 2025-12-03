import React from "react";
import { useNavigate } from "react-router-dom";
import { AuthProvider } from "react-oidc-context";

export const AuthProviderWithNavigate = ({ children }) => {
    const navigate = useNavigate();

    const domain = process.env.REACT_APP_DOMAIN;
    const clientId = process.env.REACT_APP_CLIENT_ID;
    const redirectUri = process.env.REACT_APP_CALLBACK_URL;
    const audience = process.env.REACT_APP_AUDIENCE;

    const authority =
        process.env.REACT_APP_AUTHORITY ||
        (domain ? `https://${domain}` : undefined);

    const onSigninCallback = () => {
        navigate(window.location.pathname);
        window.history.replaceState({}, document.title, window.location.pathname);
    };

    if (!(authority && clientId && redirectUri)) {
        return null;
    }

    const oidcConfig = {
        authority,
        client_id: clientId,
        redirect_uri: redirectUri,
        post_logout_redirect_uri: redirectUri,
        response_type: "code",
        scope: "openid profile email demo_scope",
        ...(audience ? { extraQueryParams: { audience } } : {}),
        onSigninCallback,
        automaticSilentRenew: false,
    };

    return <AuthProvider {...oidcConfig}>{children}</AuthProvider>;
};
