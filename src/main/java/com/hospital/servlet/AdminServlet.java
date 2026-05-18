package com.hospital.servlet;

import com.hospital.dao.*;
import com.hospital.model.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/api/admin/*")
public class AdminServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!isAdmin(req)) { sendError(res,403,"Admins only"); return; }
        try {
            String[] parts = getPathParts(req);
            if (parts.length==0) { sendError(res,400,"Specify resource"); return; }
            if ("appointments".equals(parts[0]))
                sendJson(res,200,new AppointmentDAO().findAll());
            else if ("users".equals(parts[0]))
                sendJson(res,200,new UserDAO().findAllPatients());
            else sendError(res,404,"Not found");
        } catch(Exception e){ sendError(res,500,e.getMessage()); }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!isAdmin(req)) { sendError(res,403,"Admins only"); return; }
        try {
            String[] parts = getPathParts(req);
            // PUT /api/admin/appointments/{id}
            if (parts.length < 2 || !"appointments".equals(parts[0])) {
                sendError(res,400,"Invalid path"); return;
            }
            int id = Integer.parseInt(parts[1]);
            Map<?,?> body = gson.fromJson(readBody(req), Map.class);
            String status       = (String)body.get("status");
            String notes        = (String)body.get("notes");
            String prescription = (String)body.get("prescription");
            new AppointmentDAO().updateStatus(id, status, notes, prescription);
            sendJson(res,200,Map.of("message","Status updated"));
        } catch(Exception e){ sendError(res,500,e.getMessage()); }
    }
}
