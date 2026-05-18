package com.hospital.servlet;

import com.hospital.dao.UserDAO;
import com.hospital.model.User;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/api/users/*")
public class UserServlet extends BaseServlet {

    // GET /api/users/profile
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User user = getSessionUser(req);
        if (user==null) { sendError(res,401,"Not logged in"); return; }
        try {
            sendJson(res,200,new UserDAO().findById(user.getId()));
        } catch(Exception e){ sendError(res,500,e.getMessage()); }
    }

    // PUT /api/users/profile
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User sessionUser = getSessionUser(req);
        if (sessionUser==null) { sendError(res,401,"Not logged in"); return; }
        try {
            Map<?,?> body = gson.fromJson(readBody(req), Map.class);
            User u = new User();
            u.setId(sessionUser.getId());
            u.setName((String)body.get("name"));
            u.setPhone((String)body.get("phone"));
            u.setGender((String)body.get("gender"));
            u.setDateOfBirth((String)body.get("dateOfBirth"));
            u.setAddress((String)body.get("address"));
            new UserDAO().update(u);
            // Refresh session
            User updated = new UserDAO().findById(sessionUser.getId());
            req.getSession().setAttribute("user", updated);
            sendJson(res,200,Map.of("message","Profile updated"));
        } catch(Exception e){ sendError(res,500,e.getMessage()); }
    }
}
