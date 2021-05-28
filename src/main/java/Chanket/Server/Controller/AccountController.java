package Chanket.Server.Controller;

import Chanket.Server.Data.Entities.AccountEntity;
import Chanket.Server.Data.WrapperMapper.AccountMapper;
import Chanket.Server.Websocket.Controller.ChanketEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    AccountMapper mapper;

    // Encryption function for password
    public static String password(String password) {
        String data = password;
        for(int i = 0; i < 10; i++) {
            data = DigestUtils.md5DigestAsHex((data + "utils-chanket-encryption").getBytes());
        }

        return data;
    }

    @GetMapping("/auth")
    public String auth(@RequestParam String username, @RequestParam String password, @RequestParam boolean auto, HttpServletRequest request) {
        log.info("LOGIN: " + username + ":" + password + "(" + auto + ")");

        AccountEntity entity = mapper.username2entity(username);

        // Check user didn't login
        if(entity != null && ChanketEndpoint.OnlineUsers.containsKey(entity.getUuid())) {
            return new JSONObject()
                    .put("status", "Warning")
                    .put("reason", "Have client already use this account to login").toString();
        }

        // Register logic
        if(auto && entity == null) {
            AccountEntity insert = new AccountEntity();
            insert.setUuid(UUID.randomUUID().toString().replace("-", "").toUpperCase());
            insert.setPassword(AccountController.password(password));
            insert.setCrtime(System.currentTimeMillis() / 1000);
            insert.setUsername(username);
            insert.setState(0);                                         // 0 is available; 1 is mute; 2 is banned

            mapper.insert(insert);

            insert = mapper.username2entity(username);
            ChanketEndpoint.OnlineUsers.put(insert.getUuid(), insert);

            return new JSONObject()
                    .put("status", "Successes")
                    .put("reason", "Auto registered")
                    .put("result", insert.getUuid()).toString();
        }

        // Normal login logic
        else if(!auto && entity == null) {
            return new JSONObject()
                    .put("status", "Failed")
                    .put("reason", "Username wrong").toString();
        }

        else if(!auto && !entity.getPassword().equals(AccountController.password(password))) {
            return new JSONObject()
                    .put("status", "Failed")
                    .put("reason", "Password wrong").toString();
        }

        else {
            ChanketEndpoint.OnlineUsers.put(entity.getUuid(), entity);

            return new JSONObject()
                    .put("status", "Successes")
                    .put("reason", "You're login")
                    .put("result", entity.getUuid()).toString();
        }
    }
}
