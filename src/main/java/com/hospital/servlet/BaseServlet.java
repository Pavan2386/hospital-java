package com.hospital.servlet;

import com.google.gson.Gson;
import com.hospital.model.User;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

public abstract class BaseServlet extends HttpServlet {
    protected static final Gson gson = new Gson();

    protected void sendJson(HttpServletResponse res, int status, Object data) throws IOException {
        res.setStatus(status); res.setContentType("application/json"); res.setCharacterEncoding("UTF-8");
        res.getWriter().write(gson.toJson(data));
    }
    protected void sendError(HttpServletResponse res, int status, String message) throws IOException {
        Map<String,String> err = new HashMap<>(); err.put("message", message);
        sendJson(res, status, err);
    }
    protected String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) { String l; while ((l=br.readLine())!=null) sb.append(l); }
        return sb.toString();
    }
    protected User getSessionUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (User) s.getAttribute("user");
    }
    protected boolean isAdmin(HttpServletRequest req) {
        User u = getSessionUser(req); return u != null && "admin".equals(u.getRole());
    }
    protected String[] getPathParts(HttpServletRequest req) {
        String info = req.getPathInfo();
        if (info == null || info.equals("/")) return new String[0];
        return info.replaceFirst("^/","").split("/");
    }
}
