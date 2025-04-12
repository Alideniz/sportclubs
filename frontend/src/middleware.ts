// frontend/src/middleware.ts
import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function middleware(request: NextRequest) {
    // Get the pathname of the request
    const path = request.nextUrl.pathname;

    // Check if the path is a protected route
    const isProtectedRoute = path.startsWith('/dashboard');

    // Check for authentication cookie or token
    const token = request.cookies.get('JSESSIONID');

    // If trying to access a protected route without authentication
    if (isProtectedRoute && !token) {
        // Redirect to login page
        return NextResponse.redirect(new URL('/login', request.url));
    }

    // If already authenticated and trying to access login page, redirect to dashboard
    if (path === '/login' && token) {
        return NextResponse.redirect(new URL('/dashboard', request.url));
    }

    // Allow the request to proceed
    return NextResponse.next();
}

// See "Matching Paths" below to learn more
export const config = {
    matcher: [
        // Protected routes
        '/dashboard/:path*',
        // Login page (to prevent authenticated users from accessing it)
        '/login'
    ]
};