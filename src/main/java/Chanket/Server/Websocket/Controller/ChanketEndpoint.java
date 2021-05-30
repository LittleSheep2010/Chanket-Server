package Chanket.Server.Websocket.Controller;

import Chanket.Server.Common.MessageBoxUtils;
import Chanket.Server.Common.Utils.CommandArgumentsProcessor;
import Chanket.Server.Data.Configure.LanguageConfigure;
import Chanket.Server.Data.Entities.AccountEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.*;
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
                    String.format(message, ((AccountEntity)OnlineUsers.get(uuid)).getUsername()), "join", "INFO"));
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
                    String.format(message, entity.getUsername()), "join", "INFO"));
        }
    }

    @OnMessage
    @SneakyThrows
    public void OnMessage(String message, Session session, @PathParam("uuid") String uuid) {

        // Process arguments
        String[] arguments = CommandArgumentsProcessor.QuotedDoubleSpaces(message.split(" "));
        String res;
        String title;

        // Unknown command process
        if(arguments.length == 0) {
            res = self.language.defaultLanguage().getJSONObject("commands").getString("failed-length");
            session.getBasicRemote().sendText(MessageBoxUtils.system(res, "command", "WARNING"));
        }

        // Message command
        else if(arguments[0].equals("message") && arguments.length == 3) {

            List<String> receives = new ArrayList<>(Arrays.asList(arguments[1].split("\\|")));

            // Special characters process
            if(arguments[1].equals("${GLOBAL}")) {
                receives.clear();
                for(Object entity : OnlineUsers.values().toArray()) { receives.add(((AccountEntity)entity).getUuid()); }

                title = "Global chatroom";
            }

            // Session title process
            // One receives title
            else if(receives.size() == 1) {
                title = receives.get(0);
            }

            // Normal title (e.g. user, user1, user2)
            else {
                title = String.join(", ", receives);
            }

            // Check users is online
            for(String receive : receives) {
               if(!OnlineUsers.containsKey(receive))  {
                   res = self.language.defaultLanguage().getJSONObject("commands").getJSONObject("message").getString("failed-offline");
                   session.getBasicRemote().sendText(MessageBoxUtils.system(res, "command-message", "WARNING"));
               }

               else {

                   // Send message to client
                   OnlineConnections.get(receive).WebsocketSession.getBasicRemote().sendText(MessageBoxUtils.user(arguments[2], uuid, title, "message"));
               }
            }
        }
    }
}
