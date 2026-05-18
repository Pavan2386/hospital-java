// ── API Helper ──────────────────────────────────────
const BASE = '';   // Same origin — Tomcat serves both

async function api(method, path, body) {
    const opts = {
        method,
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
    };
    if (body) opts.body = JSON.stringify(body);
    const res = await fetch(BASE + path, opts);
    const data = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(data.message || 'Request failed');
    return data;
}

const API = {
    // Auth
    login:    (email, password) => api('POST', '/hospital/api/auth/login',    { email, password }),
    register: (body)            => api('POST', '/hospital/api/auth/register',  body),
    logout:   ()                => api('POST', '/hospital/api/auth/logout'),
    me:       ()                => api('GET',  '/hospital/api/auth/logout'),   // GET on same servlet

    // Doctors
    getDoctors: (specialty, search) => {
        let qs = '?';
        if (specialty) qs += `specialty=${encodeURIComponent(specialty)}&`;
        if (search)    qs += `search=${encodeURIComponent(search)}&`;
        return api('GET', '/hospital/api/doctors' + qs);
    },
    getDoctor:  (id) => api('GET', `/hospital/api/doctors/${id}`),

    // Appointments
    book:           (body)          => api('POST', '/hospital/api/appointments',                body),
    myAppointments: ()              => api('GET',  '/hospital/api/appointments/my'),
    cancel:         (id)            => api('PUT',  `/hospital/api/appointments/${id}/cancel`),

    // Admin
    allAppointments: ()             => api('GET', '/hospital/api/admin/appointments'),
    allUsers:        ()             => api('GET', '/hospital/api/admin/users'),
    updateStatus: (id, status, notes, prescription) =>
        api('PUT', `/hospital/api/admin/appointments/${id}`, { status, notes, prescription }),

    // Profile
    getProfile:    ()    => api('GET', '/hospital/api/users/profile'),
    updateProfile: (body)=> api('PUT', '/hospital/api/users/profile', body),
};
