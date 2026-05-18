package com.hospital.servlet;

import com.hospital.dao.*;
import com.hospital.model.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/api/appointments/*")
public class AppointmentServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User user = getSessionUser(req);
        if (user==null) { sendError(res,401,"Please login"); return; }
        try {
            String[] parts = getPathParts(req);
            if (parts.length>0 && "my".equals(parts[0]))
                sendJson(res,200,new AppointmentDAO().findByPatient(user.getId()));
            else sendError(res,400,"Invalid endpoint");
        } catch(Exception e){ sendError(res,500,e.getMessage()); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User user = getSessionUser(req);
        if (user==null) { sendError(res,401,"Please login"); return; }
        try {
            Map<?,?> body = gson.fromJson(readBody(req), Map.class);
            int doctorId = ((Number)body.get("doctorId")).intValue();
            int slotId   = ((Number)body.get("slotId")).intValue();
            String reason = (String)body.get("reason");
            if (reason==null||reason.trim().isEmpty()) { sendError(res,400,"Reason required"); return; }

            Slot slot = new SlotDAO().findAvailableSlot(doctorId, slotId);
            if (slot==null) { sendError(res,400,"Slot not available"); return; }

            new SlotDAO().markBooked(slotId, true);
            Appointment apt = new Appointment();
            apt.setPatientId(user.getId()); apt.setDoctorId(doctorId); apt.setSlotId(slotId);
            apt.setAppointmentDate(slot.getDate()); apt.setAppointmentTime(slot.getTime());
            apt.setReason(reason); apt.setStatus("confirmed");
            sendJson(res,201,new AppointmentDAO().create(apt));
        } catch(Exception e){ sendError(res,500,e.getMessage()); }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        User user = getSessionUser(req);
        if (user==null) { sendError(res,401,"Please login"); return; }
        try {
            String[] parts = getPathParts(req);
            int id = Integer.parseInt(parts[0]);
            AppointmentDAO dao = new AppointmentDAO();
            Appointment apt = dao.findById(id);
            if (apt==null) { sendError(res,404,"Appointment not found"); return; }
            if (apt.getPatientId()!=user.getId() && !"admin".equals(user.getRole())) { sendError(res,403,"Not authorized"); return; }
            dao.cancel(id);
            new SlotDAO().markBooked(apt.getSlotId(), false);
            sendJson(res,200,Map.of("message","Appointment cancelled"));
        } catch(Exception e){ sendError(res,500,e.getMessage()); }
    }
}
