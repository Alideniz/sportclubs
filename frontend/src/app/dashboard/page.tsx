'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';

interface User {
  id: string;
  name: string;
  email: string;
  imageUrl?: string;
}

export default function Dashboard() {
  const router = useRouter();
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Fetch the current user data from the backend
    fetch('http://localhost:8080/api/auth/user', {
      credentials: 'include',
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error('Not authenticated');
        }
        return response.json();
      })
      .then((data) => {
        if (!data.isAuthenticated && !data.user) {
          // Redirect to login if not authenticated
          router.push('/login');
          return;
        }
        setUser(data.user);
        setLoading(false);
      })
      .catch((error) => {
        console.error('Failed to fetch user:', error);
        router.push('/login');
      });
  }, [router]);

  const handleLogout = () => {
    fetch('http://localhost:8080/api/auth/logout', {
      method: 'POST',
      credentials: 'include',
    })
      .then(() => {
        router.push('/login');
      })
      .catch((error) => {
        console.error('Failed to logout:', error);
      });
  };

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="text-xl">Loading...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100">
      <header className="bg-white shadow">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-6 sm:px-6 lg:px-8">
          <h1 className="text-3xl font-bold text-gray-900">Sport Clubs Dashboard</h1>
          <div className="flex items-center gap-4">
            {user?.imageUrl && (
              <img
                src={user.imageUrl}
                alt={user.name}
                className="h-10 w-10 rounded-full"
              />
            )}
            <span className="text-gray-700">Welcome, {user?.name}</span>
            <button
              onClick={handleLogout}
              className="rounded-md bg-red-600 px-4 py-2 text-sm font-medium text-white hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2"
            >
              Logout
            </button>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
          <div className="rounded-lg bg-white p-6 shadow">
            <h2 className="text-lg font-medium text-gray-900">My Clubs</h2>
            <p className="mt-1 text-gray-500">View and manage your sport clubs</p>
            <div className="mt-4">
              <Link
                href="/dashboard/clubs"
                className="text-blue-600 hover:text-blue-800"
              >
                View Clubs →
              </Link>
            </div>
          </div>

          <div className="rounded-lg bg-white p-6 shadow">
            <h2 className="text-lg font-medium text-gray-900">Upcoming Events</h2>
            <p className="mt-1 text-gray-500">View your upcoming sport events</p>
            <div className="mt-4">
              <Link
                href="/dashboard/events"
                className="text-blue-600 hover:text-blue-800"
              >
                View Events →
              </Link>
            </div>
          </div>

          <div className="rounded-lg bg-white p-6 shadow">
            <h2 className="text-lg font-medium text-gray-900">Facility Bookings</h2>
            <p className="mt-1 text-gray-500">Manage your facility bookings</p>
            <div className="mt-4">
              <Link
                href="/dashboard/bookings"
                className="text-blue-600 hover:text-blue-800"
              >
                View Bookings →
              </Link>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
} 