/**
 * FREEALLA - Core Frontend Application Framework
 * Handles Authentication, Session Management, UI State, and Shared Utilities.
 */

const API_BASE = '/api';

// Current Session State
const AppState = {
    user: null,
    theme: localStorage.getItem('freealla_theme') || 'light'
};

// Initialize Application State on DOM Ready
document.addEventListener('DOMContentLoaded', () => {
    initTheme();
    loadStoredUser();
    renderNavbarUserSection();
});

// -----------------------------------------------------------------------------
// Theme Management (Light / Dark Mode)
// -----------------------------------------------------------------------------
function initTheme() {
    if (AppState.theme === 'dark') {
        document.documentElement.setAttribute('data-theme', 'dark');
    } else {
        document.documentElement.removeAttribute('data-theme');
    }
}

function toggleTheme() {
    AppState.theme = (AppState.theme === 'dark') ? 'light' : 'dark';
    localStorage.setItem('freealla_theme', AppState.theme);
    initTheme();
    showToast(`Switched to ${AppState.theme.toUpperCase()} mode`, 'info');
}

// -----------------------------------------------------------------------------
// Session & User Authentication
// -----------------------------------------------------------------------------
function loadStoredUser() {
    const raw = localStorage.getItem('freealla_user');
    if (raw) {
        try {
            AppState.user = JSON.parse(raw);
        } catch (e) {
            AppState.user = null;
            localStorage.removeItem('freealla_user');
        }
    }
}

function setCurrentUser(userData) {
    AppState.user = userData;
    localStorage.setItem('freealla_user', JSON.stringify(userData));
    renderNavbarUserSection();
}

function getCurrentUser() {
    return AppState.user;
}

function logout() {
    AppState.user = null;
    localStorage.removeItem('freealla_user');
    renderNavbarUserSection();
    showToast('Logged out successfully', 'info');
    setTimeout(() => {
        if (window.location.pathname.includes('my-bookings') || 
            window.location.pathname.includes('admin') || 
            window.location.pathname.includes('checkout')) {
            window.location.href = '/';
        }
    }, 600);
}

// -----------------------------------------------------------------------------
// Navigation Bar Rendering
// -----------------------------------------------------------------------------
function renderNavbarUserSection() {
    const container = document.getElementById('navbar-auth-container');
    if (!container) return;

    if (AppState.user) {
        container.innerHTML = `
            <div class="dropdown">
                <button class="btn btn-outline-secondary dropdown-toggle d-flex align-items-center gap-2 rounded-pill px-3 py-2" type="button" data-bs-toggle="dropdown">
                    <span class="avatar-circle bg-primary text-white d-inline-flex justify-content-center align-items-center rounded-circle" style="width:28px; height:28px; font-size:12px; font-weight:700;">
                        ${AppState.user.fullName ? AppState.user.fullName.charAt(0).toUpperCase() : 'U'}
                    </span>
                    <span class="d-none d-sm-inline font-semibold">${AppState.user.fullName}</span>
                </button>
                <ul class="dropdown-menu dropdown-menu-end shadow-lg border-0 rounded-4 p-2 mt-2">
                    <li class="px-3 py-2 border-bottom mb-2">
                        <p class="mb-0 small text-muted">Signed in as</p>
                        <p class="mb-0 font-bold">${AppState.user.email}</p>
                        <span class="badge ${AppState.user.role === 'ADMIN' ? 'bg-danger' : 'bg-primary'} rounded-pill mt-1" style="font-size:10px;">${AppState.user.role}</span>
                    </li>
                    <li><a class="dropdown-item rounded-3 py-2" href="/my-bookings"><i class="fa-solid fa-ticket me-2 text-primary"></i>My Bookings</a></li>
                    ${AppState.user.role === 'ADMIN' ? `<li><a class="dropdown-item rounded-3 py-2" href="/admin"><i class="fa-solid fa-gauge me-2 text-danger"></i>Admin Dashboard</a></li>` : ''}
                    <li><hr class="dropdown-divider"></li>
                    <li><a class="dropdown-item rounded-3 py-2 text-danger" href="javascript:void(0)" onclick="logout()"><i class="fa-solid fa-right-from-bracket me-2"></i>Sign Out</a></li>
                </ul>
            </div>
        `;
    } else {
        container.innerHTML = `
            <button class="btn btn-outline-primary rounded-pill px-3 py-2 fw-semibold" onclick="openLoginModal()">
                <i class="fa-solid fa-arrow-right-to-bracket me-1"></i> Sign In
            </button>
            <button class="btn btn-primary rounded-pill px-3 py-2 fw-semibold brand-gradient-orange border-0 text-white shadow-sm" onclick="openRegisterModal()">
                Sign Up
            </button>
        `;
    }
}

// -----------------------------------------------------------------------------
// Interactive Toast Notifications
// -----------------------------------------------------------------------------
function showToast(message, type = 'success') {
    let container = document.getElementById('freealla-toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'freealla-toast-container';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    let icon = 'circle-check';
    if (type === 'error') icon = 'circle-exclamation';
    if (type === 'info') icon = 'circle-info';

    toast.innerHTML = `<i class="fa-solid fa-${icon} fs-5"></i> <span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transition = 'opacity 0.4s ease';
        setTimeout(() => toast.remove(), 400);
    }, 3500);
}

// -----------------------------------------------------------------------------
// Global Auth Modal Actions
// -----------------------------------------------------------------------------
function openLoginModal() {
    const modalEl = document.getElementById('loginModal');
    if (modalEl) {
        const modal = bootstrap.Modal.getOrCreateInstance(modalEl);
        modal.show();
    }
}

function openRegisterModal() {
    const modalEl = document.getElementById('registerModal');
    if (modalEl) {
        const modal = bootstrap.Modal.getOrCreateInstance(modalEl);
        modal.show();
    }
}

async function handleLoginSubmit(event) {
    event.preventDefault();
    const email = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value;

    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        const data = await res.json();
        if (res.ok && data.success) {
            setCurrentUser(data);
            showToast(`Welcome back, ${data.fullName}!`, 'success');
            const modalEl = document.getElementById('loginModal');
            if (modalEl) bootstrap.Modal.getInstance(modalEl).hide();
            // If on checkout page, proceed
            if (window.onUserLoggedIn) window.onUserLoggedIn();
        } else {
            showToast(data.message || 'Login failed. Please check credentials.', 'error');
        }
    } catch (err) {
        showToast('Server unreachable. Please ensure the backend is running.', 'error');
    }
}

async function handleRegisterSubmit(event) {
    event.preventDefault();
    const fullName = document.getElementById('regName').value.trim();
    const email = document.getElementById('regEmail').value.trim();
    const password = document.getElementById('regPassword').value;
    const phone = document.getElementById('regPhone').value.trim();
    const role = document.getElementById('regRole') ? document.getElementById('regRole').value : 'USER';

    try {
        const res = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ fullName, email, password, phone, role })
        });
        const data = await res.json();
        if (res.ok && data.success) {
            setCurrentUser(data);
            showToast('Account registered successfully!', 'success');
            const modalEl = document.getElementById('registerModal');
            if (modalEl) bootstrap.Modal.getInstance(modalEl).hide();
        } else {
            showToast(data.message || 'Registration failed.', 'error');
        }
    } catch (err) {
        showToast('Network error during registration.', 'error');
    }
}

// Quick Demo Login Button for Viva/Demonstration
function fillDemoCredentials(role) {
    const emailInput = document.getElementById('loginEmail');
    const passInput = document.getElementById('loginPassword');
    if (!emailInput || !passInput) return;

    if (role === 'ADMIN') {
        emailInput.value = 'admin@busora.com';
        passInput.value = 'admin123';
    } else {
        emailInput.value = 'rahul@busora.com';
        passInput.value = 'user123';
    }
    showToast(`Filled credentials for ${role} demo`, 'info');
}

// Currency and Date Format Helpers
function formatCurrency(amount) {
    return '₹' + Number(amount || 0).toLocaleString('en-IN', { maximumFractionDigits: 2 });
}

function formatDate(dateStr) {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    return d.toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric', weekday: 'short' });
}

function formatTime(timeStr) {
    if (!timeStr) return '';
    return timeStr.substring(0, 5);
}

