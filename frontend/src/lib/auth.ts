import {redirect} from 'next/navigation';

export async function checkAuthentication() {
    try {
        console.log("Checking for new user...");
        const response = await fetch('http://localhost:8080/api/auth/user', {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
        });
        console.info(response.ok);

        if (!response.ok) {
            // Not authenticated
            redirect('/login');
        }

        const data = await response.json();

        // If the response indicates the user is not authenticated
        if (!data.isAuthenticated) {
            redirect('/login');
        }

        return data.user;
    } catch (error) {
        console.error('Authentication check failed:', error);
        redirect('/login');
    }
}

export async function requireAuth() {
    try {
        const user = await checkAuthentication();
        return user;
    } catch {
        redirect('/login');
    }
}

// Client-side authentication check
export function isAuthenticated(): boolean {
    // This is a simple check and should be combined with server-side validation
    const token = document.cookie.includes('JSESSIONID');
    return token;
}