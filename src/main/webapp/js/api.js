const isLocal = window.location.hostname === 'localhost';
const isNgrok = window.location.hostname.includes('ngrok');
const BASE = (isLocal || isNgrok) ? '/hospital' : '';

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
    login:    (email, password) => api('POST', '/api/auth/login',    { email, password }),
    register: (body)            => api('POST', '/api/auth/register',  body),
    logout:   ()                => api('POST', '/api/auth/logout'),
    me:       ()                => api('GET',  '/api/auth/login'),

    getDoctors: (specialty, search) => {
        let qs = '?';
        if (specialty) qs += `specialty=${encodeURIComponent(specialty)}&`;
        if (search)    qs += `search=${encodeURIComponent(search)}&`;
        return api('GET', '/api/doctors' + qs);
    },
    getDoctor:  (id) => api('GET', `/api/doctors/${id}`),

    book:           (body) => api('POST', '/api/appointments',                body),
    myAppointments: ()     => api('GET',  '/api/appointments/my'),
    cancel:         (id)   => api('PUT',  `/api/appointments/${id}/cancel`),

    allAppointments: ()    => api('GET', '/api/admin/appointments'),
    allUsers:        ()    => api('GET', '/api/admin/users'),
    updateStatus: (id, status, notes, prescription) =>
        api('PUT', `/api/admin/appointments/${id}`, { status, notes, prescription }),

    getProfile:    ()     => api('GET', '/api/users/profile'),
    updateProfile: (body) => api('PUT', '/api/users/profile', body),
};
