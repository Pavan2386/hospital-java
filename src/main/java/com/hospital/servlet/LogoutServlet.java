package com.hospital.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/api/auth/logout")
public class LogoutServlet extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession s = req.getSession(false);
        if (s!=null) s.invalidate();
        Cookie c = new Cookie("userRole",""); c.setMaxAge(0); c.setPath("/"); res.addCookie(c);
        Map<String,String> r = new HashMap<>(); r.put("message","Logged out");
        sendJson(res,200,r);
    }
}
