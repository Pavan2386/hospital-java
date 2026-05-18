package com.hospital.servlet;

import com.hospital.dao.UserDAO;
import com.hospital.model.User;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/api/auth/login")
public class LoginServlet extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            Map<?,?> body = gson.fromJson(readBody(req), Map.class);
            String email = (String)body.get("email"), password = (String)body.get("password");
            if (email==null||password==null) { sendError(res,400,"Email and password required"); return; }
            UserDAO dao = new UserDAO();
            User user = dao.findByEmail(email);
            if (user==null||!dao.checkPassword(password,user.getPassword())) { sendError(res,401,"Invalid email or password"); return; }
            HttpSession session = req.getSession(true);
            user.setPassword(null);
            session.setAttribute("user",user);
            session.setMaxInactiveInterval(86400);
            // Cookie to demonstrate cookie usage (Syllabus Ex.8)
            Cookie c = new Cookie("userRole", user.getRole()); c.setMaxAge(86400); c.setPath("/"); res.addCookie(c);
            Map<String,Object> resp = new HashMap<>();
            resp.put("id",user.getId()); resp.put("name",user.getName()); resp.put("email",user.getEmail()); resp.put("role",user.getRole()); resp.put("phone",user.getPhone());
            sendJson(res,200,resp);
        } catch(Exception e){ sendError(res,500,e.getMessage()); }
    }
    // GET returns current session user
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User user = getSessionUser(req);
        if (user==null) { sendError(res,401,"Not logged in"); return; }
        Map<String,Object> resp = new HashMap<>();
        resp.put("id",user.getId()); resp.put("name",user.getName()); resp.put("email",user.getEmail()); resp.put("role",user.getRole()); resp.put("phone",user.getPhone());
        sendJson(res,200,resp);
    }
}
