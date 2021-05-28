package Chanket.Server.Common;

import Chanket.Server.Data.Entities.AccountEntity;
import Chanket.Server.Websocket.Controller.ChanketEndpoint;
import org.json.JSONObject;

public class MessageBoxUtils {

    public static String system(String message, String side) {

        JSONObject object = new JSONObject();

        object.put("display", side);
        object.put("message", message);
        object.put("type", "system");

        return object.toString();
    }

    public static String user(String message, String uuid, String[] receives, String side) {

        AccountEntity entity = (AccountEntity)ChanketEndpoint.OnlineUsers.get(uuid);
        JSONObject object = new JSONObject();

        if(entity == null) return null;

        object.put("display", side);
        object.put("message", message);
        object.put("sender", entity.toString());
        object.put("receives", String.join("[|]", receives));
        object.put("type", "user");

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
