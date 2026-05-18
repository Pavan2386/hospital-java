package com.hospital.servlet;

import com.hospital.dao.UserDAO;
import com.hospital.model.User;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/api/auth/register")
public class RegisterServlet extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            Map<?,?> body = gson.fromJson(readBody(req), Map.class);
            String name=(String)body.get("name"),email=(String)body.get("email"),
                   password=(String)body.get("password"),phone=(String)body.get("phone");
            if (name==null||email==null||password==null||phone==null) { sendError(res,400,"All fields required"); return; }
            UserDAO dao = new UserDAO();
            if (dao.emailExists(email)) { sendError(res,400,"Email already registered"); return; }
            User user = new User();
            user.setName(name); user.setEmail(email); user.setPassword(password); user.setPhone(phone);
            user.setRole("patient"); user.setGender((String)body.get("gender"));
            user.setDateOfBirth((String)body.get("dateOfBirth")); user.setAddress((String)body.get("address"));
            user = dao.create(user);
            HttpSession session = req.getSession(true);
            session.setAttribute("user",user); session.setMaxInactiveInterval(86400);
            Map<String,Object> resp = new HashMap<>();
            resp.put("id",user.getId()); resp.put("name",user.getName()); resp.put("email",user.getEmail()); resp.put("role",user.getRole());
            sendJson(res,201,resp);
        } catch(Exception e){ sendError(res,500,e.getMessage()); }
    }
}
