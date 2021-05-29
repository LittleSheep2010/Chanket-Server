package Chanket.Server.Common;

import Chanket.Server.Data.Entities.AccountEntity;
import Chanket.Server.Websocket.Controller.ChanketEndpoint;
import org.json.JSONObject;

public class MessageBoxUtils {

    public static String system(String message, String side, String level) {

        JSONObject object = new JSONObject();

        object.put("display", side);
        object.put("message", message);
        object.put("level", level);
        object.put("type", "system");

        return object.toString();
    }

    public static String user(String message, String uuid, String title, String side) {

        AccountEntity entity = (AccountEntity)ChanketEndpoint.OnlineUsers.get(uuid);
        JSONObject object = new JSONObject();

        if(entity == null) return null;

        JSONObject sender = new JSONObject();
        sender.put("permission", entity.getPermission());
        sender.put("username", entity.getUsername());
        sender.put("uuid", entity.getUuid());

        object.put("display", side);
        object.put("message", message);
        object.put("sender", sender);
        object.put("title", title);
        object.put("type", "chat-message");
        object.put("time", System.currentTimeMillis() / 1000);

        return object.toString();
    }

    public static String complie(String message, String side, String type) {

        JSONObject object = new JSONObject();

        object.put("message", message);
        object.put("display", side);
        object.put("type", type);

        return object.toString();
    }
}
