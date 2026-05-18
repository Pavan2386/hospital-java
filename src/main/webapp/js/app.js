// ── Global State ────────────────────────────────────
let currentUser = null;
let currentDoctorId = null;
let selectedSlotId  = null;

// ── Startup ─────────────────────────────────────────
window.addEventListener('DOMContentLoaded', async () => {
    try {
        currentUser = await API.me();
        renderNav();
        routePage();
    } catch {
        currentUser = null;
        renderNav();
        routePage();
    }
});

// ── Router ──────────────────────────────────────────
function routePage() {
    const hash = window.location.hash || '#home';
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
    const pageMap = {
        '#home':            showHome,
        '#doctors':         showDoctors,
        '#doctor-detail':   () => showDoctorDetail(currentDoctorId),
        '#my-appointments': showMyAppointments,
        '#profile':         showProfile,
        '#admin':           showAdmin,
        '#login':           showLogin,
        '#register':        showRegister,
    };
    const fn = pageMap[hash];
    if (fn) fn();
    else showHome();
}
window.addEventListener('hashchange', routePage);

function navigate(hash, extraData) {
    if (extraData) currentDoctorId = extraData;
    window.location.hash = hash;
}

// ── Nav Rendering ────────────────────────────────────
function renderNav() {
    const linksEl = document.getElementById('nav-links');
    if (!currentUser) {
        linksEl.innerHTML = `
            <a href="#doctors">Doctors</a>
            <a href="#login">Login</a>
            <a href="#register">Register</a>`;
    } else {
        linksEl.innerHTML = `
            <a href="#doctors">Doctors</a>
            <a href="#my-appointments">My Appointments</a>
            <a href="#profile">Profile</a>
            ${currentUser.role === 'admin' ? '<a href="#admin">Admin</a>' : ''}
            <button class="btn-logout" onclick="doLogout()">Logout</button>`;
    }
}

async function doLogout() {
    await API.logout().catch(() => {});
    currentUser = null;
    renderNav();
    navigate('#home');
}

// ── Helpers ──────────────────────────────────────────
function showAlert(id, msg, type = 'error') {
    const el = document.getElementById(id);
    if (!el) return;
    el.className = `alert alert-${type}`;
    el.textContent = msg;
    el.style.display = 'block';
    if (type === 'success') setTimeout(() => el.style.display = 'none', 4000);
}
function hideAlert(id) {
    const el = document.getElementById(id);
    if (el) el.style.display = 'none';
}
function loader(html) {
    return `<div class="loader"><div class="spinner"></div>${html || 'Loading...'}</div>`;
}
function empty(icon, msg) {
    return `<div class="empty"><div class="empty-icon">${icon}</div><p>${msg}</p></div>`;
}
function statusBadge(s) {
    return `<span class="badge badge-${s}">${s}</span>`;
}

// ── HOME PAGE ────────────────────────────────────────
function showHome() {
    document.getElementById('page-home').classList.add('active');
}

// ── LOGIN PAGE ───────────────────────────────────────
function showLogin() {
    document.getElementById('page-login').classList.add('active');
    hideAlert('login-alert');
}

async function doLogin(e) {
    e.preventDefault();
    const email    = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    const btn      = document.getElementById('login-btn');
    btn.disabled = true; btn.textContent = 'Logging in...';
    try {
        currentUser = await API.login(email, password);
        renderNav();
        navigate(currentUser.role === 'admin' ? '#admin' : '#home');
    } catch (err) {
        showAlert('login-alert', err.message);
    } finally {
        btn.disabled = false; btn.textContent = 'Login';
    }
}

// ── REGISTER PAGE ─────────────────────────────────────
function showRegister() {
    document.getElementById('page-register').classList.add('active');
    hideAlert('reg-alert');
}

async function doRegister(e) {
    e.preventDefault();
    const btn = document.getElementById('reg-btn');
    btn.disabled = true; btn.textContent = 'Creating account...';
    const body = {
        name:        document.getElementById('reg-name').value,
        email:       document.getElementById('reg-email').value,
        password:    document.getElementById('reg-password').value,
        phone:       document.getElementById('reg-phone').value,
        gender:      document.getElementById('reg-gender').value,
        dateOfBirth: document.getElementById('reg-dob').value,
        address:     document.getElementById('reg-address').value,
    };
    try {
        currentUser = await API.register(body);
        renderNav();
        navigate('#home');
    } catch (err) {
        showAlert('reg-alert', err.message);
    } finally {
        btn.disabled = false; btn.textContent = 'Register';
    }
}

// ── DOCTORS PAGE ─────────────────────────────────────
async function showDoctors() {
    document.getElementById('page-doctors').classList.add('active');
    await loadDoctors();
}

async function loadDoctors(specialty, search) {
    const grid = document.getElementById('doctors-grid');
    grid.innerHTML = loader();
    try {
        const doctors = await API.getDoctors(specialty, search);
        if (!doctors.length) { grid.innerHTML = empty('🔍', 'No doctors found. Try a different filter.'); return; }
        grid.innerHTML = doctors.map(d => `
            <div class="doctor-card" onclick="navigate('#doctor-detail', ${d.id})">
                <div class="doctor-meta">
                    <div class="doctor-avatar">${d.name.charAt(0)}</div>
                    <div>
                        <h3>Dr. ${d.name}</h3>
                        <span class="specialty-badge">${d.specialty}</span>
                    </div>
                </div>
                <p>🎓 ${d.qualification}</p>
                <p>⏱ ${d.experience} years experience</p>
                <p>⭐ ${d.rating} / 5.0</p>
                <div class="fee-row">
                    <span class="fee">₹${d.consultationFee}</span>
                    <button class="btn btn-primary" style="padding:6px 14px;font-size:0.85rem"
                        onclick="event.stopPropagation();navigate('#doctor-detail',${d.id})">
                        Book Now
                    </button>
                </div>
            </div>`).join('');
    } catch (err) {
        grid.innerHTML = `<p style="color:red">${err.message}</p>`;
    }
}

function filterSpecialty(pill, specialty) {
    document.querySelectorAll('#page-doctors .pill').forEach(p => p.classList.remove('active'));
    pill.classList.add('active');
    const search = document.getElementById('doctor-search').value;
    loadDoctors(specialty === 'All' ? '' : specialty, search);
}

function searchDoctors(e) {
    if (e.key === 'Enter' || e.type === 'click') {
        const search = document.getElementById('doctor-search').value;
        const active = document.querySelector('#page-doctors .pill.active');
        const specialty = active?.dataset.specialty === 'All' ? '' : active?.dataset.specialty;
        loadDoctors(specialty, search);
    }
}

// ── DOCTOR DETAIL PAGE ───────────────────────────────
async function showDoctorDetail(id) {
    if (!id) { navigate('#doctors'); return; }
    document.getElementById('page-doctor-detail').classList.add('active');
    selectedSlotId = null;

    const content = document.getElementById('detail-content');
    content.innerHTML = loader();
    hideAlert('book-alert');

    try {
        const d = await API.getDoctor(id);
        const slots = d.slots || [];

        // Group slots by date
        const byDate = {};
        slots.forEach(s => {
            if (!byDate[s.date]) byDate[s.date] = [];
            byDate[s.date].push(s);
        });
        const dates = Object.keys(byDate).sort();

        content.innerHTML = `
            <div class="detail-layout">
                <div class="doctor-info-card">
                    <div class="doctor-info-header">
                        <div class="doctor-avatar-lg">${d.name.charAt(0)}</div>
                        <div>
                            <h2>Dr. ${d.name}</h2>
                            <span class="specialty-badge" style="margin:6px 0;display:inline-block">${d.specialty}</span>
                            <p style="color:#555;margin-top:6px">🎓 ${d.qualification}</p>
                            <p style="color:#555">⏱ ${d.experience} years experience</p>
                            <p style="color:#555">📞 ${d.phone || 'N/A'}</p>
                            <p style="color:#555">⭐ ${d.rating} / 5.0</p>
                        </div>
                    </div>
                    ${d.bio ? `<div style="background:#f0f7ff;border-radius:8px;padding:16px;margin-top:4px">
                        <h4 style="margin-bottom:8px">About</h4>
                        <p style="color:#555;line-height:1.6">${d.bio}</p>
                    </div>` : ''}
                    <div class="fee-highlight">
                        <span class="label">Consultation Fee</span>
                        <span class="amount">₹${d.consultationFee}</span>
                    </div>
                </div>

                <div class="card">
                    <h3 style="margin-bottom:20px;color:#0077b6">📅 Book Appointment</h3>
                    <div id="book-alert" class="alert" style="display:none"></div>

                    ${!currentUser ? `<div class="alert alert-info">
                        Please <a href="#login" style="color:#0077b6;font-weight:700">login</a> to book an appointment.
                    </div>` : ''}

                    <div class="form-group">
                        <label>Select Date</label>
                        <select id="slot-date" onchange="renderTimeSlots(this.value)" ${!currentUser ? 'disabled' : ''}>
                            <option value="">-- Choose a date --</option>
                            ${dates.map(d => `<option value="${d}">${new Date(d+'T12:00:00').toDateString()}</option>`).join('')}
                        </select>
                        ${!dates.length ? '<small style="color:#e63946">No available slots at this time.</small>' : ''}
                    </div>

                    <div class="form-group" id="slots-container" style="display:none">
                        <label>Select Time Slot</label>
                        <div class="slot-grid" id="slots-grid"></div>
                    </div>

                    <div class="form-group">
                        <label>Reason for Visit</label>
                        <textarea id="book-reason" rows="3" placeholder="Describe your symptoms..."
                            ${!currentUser ? 'disabled' : ''}></textarea>
                    </div>

                    <button id="book-btn" class="btn btn-primary" style="width:100%"
                        onclick="bookAppointment(${d.id}, ${d.consultationFee})"
                        ${!currentUser ? 'disabled' : ''}>
                        Confirm Booking (₹${d.consultationFee})
                    </button>
                </div>
            </div>`;

        // Store slots for JS access
        window._doctorSlots = byDate;

    } catch (err) {
        content.innerHTML = `<div class="alert alert-error">${err.message}</div>`;
    }
}

function renderTimeSlots(date) {
    if (!date) { document.getElementById('slots-container').style.display = 'none'; return; }
    const byDate = window._doctorSlots || {};
    const slots  = byDate[date] || [];
    const grid   = document.getElementById('slots-grid');
    const cont   = document.getElementById('slots-container');
    selectedSlotId = null;
    if (!slots.length) { cont.style.display = 'none'; return; }
    cont.style.display = 'block';
    grid.innerHTML = slots.map(s =>
        `<button class="slot-btn" onclick="selectSlot(this, ${s.id})">${s.time}</button>`
    ).join('');
}

function selectSlot(btn, slotId) {
    document.querySelectorAll('.slot-btn').forEach(b => b.classList.remove('selected'));
    btn.classList.add('selected');
    selectedSlotId = slotId;
}

async function bookAppointment(doctorId, fee) {
    if (!currentUser) { navigate('#login'); return; }
    if (!selectedSlotId) { showAlert('book-alert', 'Please select a time slot.'); return; }
    const reason = document.getElementById('book-reason').value.trim();
    if (!reason)  { showAlert('book-alert', 'Please enter reason for visit.'); return; }

    const btn = document.getElementById('book-btn');
    btn.disabled = true; btn.textContent = 'Booking...';
    hideAlert('book-alert');
    try {
        await API.book({ doctorId, slotId: selectedSlotId, reason });
        showAlert('book-alert', '✅ Appointment booked successfully! Check My Appointments.', 'success');
        document.getElementById('book-reason').value = '';
        selectedSlotId = null;
        // Refresh slots
        await showDoctorDetail(doctorId);
    } catch (err) {
        showAlert('book-alert', err.message);
    } finally {
        btn.disabled = false; btn.textContent = `Confirm Booking (₹${fee})`;
    }
}

// ── MY APPOINTMENTS PAGE ─────────────────────────────
async function showMyAppointments() {
    if (!currentUser) { navigate('#login'); return; }
    document.getElementById('page-my-appointments').classList.add('active');
    renderMyAppointments('all');
}

async function renderMyAppointments(filter) {
    document.querySelectorAll('#page-my-appointments .tab').forEach(t => t.classList.remove('active'));
    document.querySelector(`#page-my-appointments .tab[data-filter="${filter}"]`)?.classList.add('active');

    const list = document.getElementById('apt-list');
    list.innerHTML = loader();
    try {
        let apts = await API.myAppointments();
        if (filter !== 'all') apts = apts.filter(a => a.status === filter);
        if (!apts.length) { list.innerHTML = empty('📋', 'No appointments found.'); return; }
        list.innerHTML = apts.map(a => `
            <div class="apt-card ${a.status}">
                <div style="display:flex;gap:16px">
                    <div class="doctor-avatar">${(a.doctorName||'?').charAt(0)}</div>
                    <div>
                        <h3 style="margin-bottom:4px">Dr. ${a.doctorName}</h3>
                        <span class="specialty-badge">${a.doctorSpecialty}</span>
                        <p style="color:#555;margin-top:8px">📅 ${a.appointmentDate} &nbsp;|&nbsp; 🕐 ${a.appointmentTime}</p>
                        <p style="color:#555">📝 ${a.reason}</p>
                        ${a.notes ? `<p style="color:#2a9d8f;margin-top:4px">👨‍⚕️ <b>Doctor Notes:</b> ${a.notes}</p>` : ''}
                        ${a.prescription ? `<p style="color:#0077b6">💊 <b>Prescription:</b> ${a.prescription}</p>` : ''}
                    </div>
                </div>
                <div style="text-align:right;flex-shrink:0">
                    ${statusBadge(a.status)}
                    <p style="color:#0077b6;font-weight:700;margin:10px 0">₹${a.consultationFee}</p>
                    ${(a.status === 'confirmed' || a.status === 'pending')
                        ? `<button class="btn btn-danger" style="font-size:0.8rem;padding:6px 12px"
                               onclick="cancelAppointment(${a.id})">Cancel</button>` : ''}
                </div>
            </div>`).join('');
    } catch (err) {
        list.innerHTML = `<div class="alert alert-error">${err.message}</div>`;
    }
}

async function cancelAppointment(id) {
    if (!confirm('Cancel this appointment?')) return;
    try {
        await API.cancel(id);
        renderMyAppointments('all');
    } catch (err) {
        alert(err.message);
    }
}

// ── PROFILE PAGE ─────────────────────────────────────
async function showProfile() {
    if (!currentUser) { navigate('#login'); return; }
    document.getElementById('page-profile').classList.add('active');
    hideAlert('profile-alert');
    try {
        const u = await API.getProfile();
        document.getElementById('p-name').value    = u.name    || '';
        document.getElementById('p-phone').value   = u.phone   || '';
        document.getElementById('p-gender').value  = u.gender  || '';
        document.getElementById('p-dob').value     = u.dateOfBirth || '';
        document.getElementById('p-address').value = u.address || '';
        document.getElementById('p-email-display').textContent = u.email;
        document.getElementById('p-role-display').textContent  = u.role;
        document.getElementById('p-avatar').textContent = u.name.charAt(0).toUpperCase();
    } catch (err) {
        showAlert('profile-alert', err.message);
    }
}

async function saveProfile(e) {
    e.preventDefault();
    const btn = document.getElementById('profile-btn');
    btn.disabled = true; btn.textContent = 'Saving...';
    try {
        await API.updateProfile({
            name:        document.getElementById('p-name').value,
            phone:       document.getElementById('p-phone').value,
            gender:      document.getElementById('p-gender').value,
            dateOfBirth: document.getElementById('p-dob').value,
            address:     document.getElementById('p-address').value,
        });
        showAlert('profile-alert', 'Profile updated successfully!', 'success');
        currentUser = await API.me();
        renderNav();
    } catch (err) {
        showAlert('profile-alert', err.message);
    } finally {
        btn.disabled = false; btn.textContent = 'Save Changes';
    }
}

// ── ADMIN PAGE ───────────────────────────────────────
async function showAdmin() {
    if (!currentUser || currentUser.role !== 'admin') { navigate('#home'); return; }
    document.getElementById('page-admin').classList.add('active');
    loadAdminTab('overview');
}

async function loadAdminTab(tab) {
    document.querySelectorAll('#page-admin .tab').forEach(t => t.classList.remove('active'));
    document.querySelector(`#page-admin .tab[data-tab="${tab}"]`)?.classList.add('active');
    const body = document.getElementById('admin-body');
    body.innerHTML = loader();

    try {
        if (tab === 'overview') {
            const [apts, users] = await Promise.all([API.allAppointments(), API.allUsers()]);
            const counts = { confirmed:0, pending:0, completed:0, cancelled:0 };
            apts.forEach(a => counts[a.status] = (counts[a.status]||0) + 1);
            body.innerHTML = `
                <div class="stats-grid">
                    <div class="stat-card"><div class="icon">📋</div>
                        <div class="number" style="color:#0077b6">${apts.length}</div>
                        <div class="label">Total Appointments</div></div>
                    <div class="stat-card"><div class="icon">🧑‍🤝‍🧑</div>
                        <div class="number" style="color:#2a9d8f">${users.length}</div>
                        <div class="label">Total Patients</div></div>
                    <div class="stat-card"><div class="icon">✅</div>
                        <div class="number" style="color:#065f46">${counts.confirmed}</div>
                        <div class="label">Confirmed</div></div>
                    <div class="stat-card"><div class="icon">✔️</div>
                        <div class="number" style="color:#3730a3">${counts.completed}</div>
                        <div class="label">Completed</div></div>
                </div>
                <div class="card">
                    <h3 style="margin-bottom:16px">Recent Appointments</h3>
                    ${renderAptTable(apts.slice(0,8))}
                </div>`;
        }
        else if (tab === 'appointments') {
            const apts = await API.allAppointments();
            body.innerHTML = `<div class="card"><h3 style="margin-bottom:16px">All Appointments (${apts.length})</h3>
                ${renderAptTable(apts)}</div>`;
        }
        else if (tab === 'patients') {
            const users = await API.allUsers();
            body.innerHTML = `<div class="card">
                <h3 style="margin-bottom:16px">All Patients (${users.length})</h3>
                <div class="table-wrap"><table>
                    <thead><tr><th>Name</th><th>Email</th><th>Phone</th><th>Gender</th><th>Joined</th></tr></thead>
                    <tbody>${users.map(u => `
                        <tr>
                            <td><strong>${u.name}</strong></td>
                            <td>${u.email}</td>
                            <td>${u.phone}</td>
                            <td style="text-transform:capitalize">${u.gender || '-'}</td>
                            <td>${u.createdAt ? u.createdAt.substring(0,10) : '-'}</td>
                        </tr>`).join('')}
                    </tbody>
                </table></div></div>`;
        }
    } catch (err) {
        body.innerHTML = `<div class="alert alert-error">${err.message}</div>`;
    }
}

function renderAptTable(apts) {
    if (!apts.length) return empty('📋', 'No appointments yet.');
    return `<div class="table-wrap"><table>
        <thead><tr><th>Patient</th><th>Doctor</th><th>Date</th><th>Time</th><th>Reason</th><th>Status</th><th>Action</th></tr></thead>
        <tbody>${apts.map(a => `
            <tr>
                <td><strong>${a.patientName||'-'}</strong><br><small style="color:#888">${a.patientEmail||''}</small></td>
                <td>Dr. ${a.doctorName}<br><small>${a.doctorSpecialty}</small></td>
                <td>${a.appointmentDate}</td>
                <td>${a.appointmentTime}</td>
                <td style="max-width:140px;word-break:break-word">${a.reason}</td>
                <td>${statusBadge(a.status)}</td>
                <td>
                    <select onchange="adminUpdateStatus(${a.id},this.value)"
                        style="padding:4px 8px;border-radius:4px;border:1px solid #ddd;font-size:0.8rem">
                        <option value="pending"   ${a.status==='pending'   ?'selected':''}>Pending</option>
                        <option value="confirmed" ${a.status==='confirmed' ?'selected':''}>Confirmed</option>
                        <option value="completed" ${a.status==='completed' ?'selected':''}>Completed</option>
                        <option value="cancelled" ${a.status==='cancelled' ?'selected':''}>Cancelled</option>
                    </select>
                </td>
            </tr>`).join('')}
        </tbody>
    </table></div>`;
}

async function adminUpdateStatus(id, status) {
    try {
        await API.updateStatus(id, status, null, null);
    } catch (err) {
        alert('Update failed: ' + err.message);
    }
}
