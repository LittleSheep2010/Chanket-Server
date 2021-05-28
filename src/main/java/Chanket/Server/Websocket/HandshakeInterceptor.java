package Chanket.Server.Websocket;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

public class HandshakeInterceptor extends ServerEndpointConfig.Configurator {

    @Override
    // Get HTTPSession from websocket handshake
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {

        // Get HTTPSession
        HttpSession HTTPSession = (HttpSession)request.getHttpSession();

        // Give HTTPSession to Endpoint config
        sec.getUserProperties().put(HttpSession.class.getName(), HTTPSession);
    }
}
