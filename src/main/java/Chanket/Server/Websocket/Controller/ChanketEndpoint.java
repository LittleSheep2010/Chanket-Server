package Chanket.Server.Websocket.Controller;

import Chanket.Server.Common.MessageBoxUtils;
import Chanket.Server.Data.Configure.LanguageConfigure;
import Chanket.Server.Data.Entities.AccountEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint(value = "/chanket/{uuid}")
public class ChanketEndpoint {

    public static Map<String, ChanketEndpoint> OnlineConnections = new ConcurrentHashMap<>();
    public static Map<String, Object> OnlineUsers = new HashMap<>();

    public static ChanketEndpoint self;

    public Session WebsocketSession;

    @Autowired
    LanguageConfigure language;

    @PostConstruct
    public void init() {
        self = this;
        self.language = this.language;
    }

    @OnOpen
    @SneakyThrows
    public void OnOpen(Session session, @PathParam("uuid") String uuid) {

        // Set WebsocketSession in this object
        this.WebsocketSession = session;

        // Authorization
        if(!OnlineUsers.containsKey(uuid)) {
            this.WebsocketSession.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Please login before create chat connection"));
            return;
        }

        // Save object in "OnlineConnections" container
        OnlineConnections.put(uuid, this);

        // Broadcast join message
        for(ChanketEndpoint connection : OnlineConnections.values()) {
            String message = self.language.defaultLanguage().getJSONObject("events").getJSONObject("connections").getString("join");
            connection.WebsocketSession.getBasicRemote().sendText(MessageBoxUtils.system(
                    String.format(message, ((AccountEntity)OnlineUsers.get(uuid)).getUsername()), "join"));
        }
    }

    @OnClose
    @SneakyThrows
    public void OnClose(Session session, @PathParam("uuid") String uuid) {

        // Save user before logout
        AccountEntity entity = (AccountEntity)OnlineUsers.get(uuid);

        // Remove session
        OnlineConnections.remove(uuid);
        OnlineUsers.remove(uuid);

        // Broadcast exit message
        for(ChanketEndpoint connection : OnlineConnections.values()) {
            String message = self.language.defaultLanguage().getJSONObject("events").getJSONObject("connections").getString("leave");
            connection.WebsocketSession.getBasicRemote().sendText(MessageBoxUtils.system(
                    String.format(message, entity.getUsername()), "join"));
        }
    }

    @OnMessage
    @SneakyThrows
    public void OnMessage(String message, Session session, @PathParam("uuid") String uuid) {

        // Process arguments
        String[] arguments = message.split(" ");

        // Get account entity
        AccountEntity entity = (AccountEntity)OnlineUsers.get(uuid);

        if(arguments.length == 0) {
            session.getBasicRemote().sendText(MessageBoxUtils.system("Couldn't execute command, command cannot found", "command"));
        }


    }
}
