package Chanket.Server.Websocket;

import org.springframework.stereotype.Component;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

@Component
@WebListener
public class RequestListener implements ServletRequestListener {

    public void requestInitialized(ServletRequestEvent sre)  {

        // Let all request with HTTPSession
        ((HttpServletRequest) sre.getServletRequest()).getSession();
    }

    public RequestListener() {}

    public void requestDestroyed(ServletRequestEvent arg0)  {}
}
