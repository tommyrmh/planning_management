// Common JavaScript functions for Planning Management

// Check if user is authenticated
function isAuthenticated() {
    return localStorage.getItem('token') !== null;
}

// Get current user
function getCurrentUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
}

// Get auth token
function getAuthToken() {
    return localStorage.getItem('token');
}

// Logout function
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    // Use replace to prevent going back
    window.location.replace('/login');
    // Clear browser history
    window.history.pushState(null, '', window.location.href);
    window.onpopstate = function() {
        window.location.replace('/login');
    };
}

// API request helper with JWT
async function apiRequest(url, options = {}) {
    const token = getAuthToken();

    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
            ...(token && { 'Authorization': `Bearer ${token}` })
        }
    };

    const mergedOptions = {
        ...defaultOptions,
        ...options,
        headers: {
            ...defaultOptions.headers,
            ...options.headers
        }
    };

    const response = await fetch(url, mergedOptions);

    if (response.status === 401) {
        logout();
        return;
    }

    return response;
}

// Format date
function formatDate(dateString) {
    const options = { year: 'numeric', month: 'long', day: 'numeric' };
    return new Date(dateString).toLocaleDateString('fr-FR', options);
}

// Format time
function formatTime(dateString) {
    const options = { hour: '2-digit', minute: '2-digit' };
    return new Date(dateString).toLocaleTimeString('fr-FR', options);
}

// Show toast notification
function showToast(message, type = 'info') {
    const toastContainer = document.getElementById('toastContainer');
    if (!toastContainer) {
        const container = document.createElement('div');
        container.id = 'toastContainer';
        container.style.position = 'fixed';
        container.style.top = '20px';
        container.style.right = '20px';
        container.style.zIndex = '9999';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `alert alert-${type} alert-slack`;
    toast.style.minWidth = '300px';
    toast.innerHTML = `
        <i class="bi bi-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'}"></i>
        ${message}
    `;

    document.getElementById('toastContainer').appendChild(toast);

    setTimeout(() => {
        toast.remove();
    }, 5000);
}

// Initialize tooltips
document.addEventListener('DOMContentLoaded', () => {
    // Bootstrap tooltips initialization
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});
