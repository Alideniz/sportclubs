import {NextResponse} from 'next/server';
import type {NextRequest} from 'next/server';

export function middleware(request: NextRequest) {
    const path = request.nextUrl.pathname;

    const isProtectedRoute = path.startsWith('/dashboard');

    const token = request.cookies.get('JSESSIONID');

    console.log('token', token);

    if (isProtectedRoute && !token) {
        return NextResponse.redirect(new URL('/login', request.url));
    }

    if (path === '/login' && token) {
        return NextResponse.redirect(new URL('/dashboard', request.url));
    }

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