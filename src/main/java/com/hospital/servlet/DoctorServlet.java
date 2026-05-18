package com.hospital.servlet;

import com.hospital.dao.*;
import com.hospital.model.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/api/doctors/*")
public class DoctorServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            String[] parts = getPathParts(req);
            if (parts.length==0) {
                sendJson(res,200,new DoctorDAO().findAll(req.getParameter("specialty"),req.getParameter("search")));
            } else {
                Doctor d = new DoctorDAO().findById(Integer.parseInt(parts[0]));
                if (d==null) sendError(res,404,"Doctor not found"); else sendJson(res,200,d);
            }
        } catch(Exception e){ sendError(res,500,e.getMessage()); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!isAdmin(req)) { sendError(res,403,"Admins only"); return; }
        try {
            String[] parts = getPathParts(req);
            if (parts.length>=2 && parts[1].equals("slots")) {
                int doctorId = Integer.parseInt(parts[0]);
                Map<?,?> body = gson.fromJson(readBody(req), Map.class);
                List<?> rawSlots = (List<?>)body.get("slots");
                List<Slot> slots = new ArrayList<>();
                for (Object o : rawSlots) {
                    Map<?,?> m = (Map<?,?>)o; Slot s = new Slot();
                    s.setDate((String)m.get("date")); s.setTime((String)m.get("time")); slots.add(s);
                }
                new SlotDAO().addSlots(doctorId, slots);
                sendJson(res,201,Map.of("message","Slots added"));
            } else {
                Doctor d = gson.fromJson(readBody(req), Doctor.class);
                sendJson(res,201,new DoctorDAO().create(d));
            }
        } catch(Exception e){ sendError(res,500,e.getMessage()); }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!isAdmin(req)) { sendError(res,403,"Admins only"); return; }
        try {
            String[] parts = getPathParts(req);
            Doctor d = gson.fromJson(readBody(req), Doctor.class);
            d.setId(Integer.parseInt(parts[0])); new DoctorDAO().update(d);
            sendJson(res,200,Map.of("message","Doctor updated"));
        } catch(Exception e){ sendError(res,500,e.getMessage()); }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (!isAdmin(req)) { sendError(res,403,"Admins only"); return; }
        try {
            new DoctorDAO().deactivate(Integer.parseInt(getPathParts(req)[0]));
            sendJson(res,200,Map.of("message","Doctor deactivated"));
        } catch(Exception e){ sendError(res,500,e.getMessage()); }
    }
}
