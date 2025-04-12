'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { fetchUserClubs, deleteClub } from '@/services/clubService';
import { UserClubRoleType } from '@/types/club';

export default function MyClubsPage() {
    const [clubs, setClubs] = useState<UserClubRoleType[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [deletingClubId, setDeletingClubId] = useState<string | null>(null);

    useEffect(() => {
        async function loadClubs() {
            try {
                setIsLoading(true);
                const userClubs = await fetchUserClubs();
                setClubs(userClubs);
            } catch (err) {
                setError('Failed to load clubs. Please try again.');
                console.error(err);
            } finally {
                setIsLoading(false);
            }
        }

        loadClubs();
    }, []);

    const handleDeleteClub = async (clubId: string) => {
        if (!window.confirm('Are you sure you want to delete this club? This action cannot be undone.')) {
            return;
        }

        try {
            setDeletingClubId(clubId);
            await deleteClub(clubId);

            // Remove the deleted club from the list
            setClubs(prevClubs =>
                prevClubs.filter(userClubRole => userClubRole.club.id !== clubId)
            );
        } catch (err) {
            setError('Failed to delete club. Please try again.');
            console.error(err);
        } finally {
            setDeletingClubId(null);
        }
    };

    const renderClubCard = (userClubRole: UserClubRoleType) => {
        const { club, role, joinedAt } = userClubRole;

        return (
            <div
                key={club.id}
                className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-300"
            >
                {/* Club Logo or Placeholder */}
                <div className="h-48 bg-gray-100 flex items-center justify-center">
                    {club.logoUrl ? (
                        <img
                            src={club.logoUrl}
                            alt={`${club.name} logo`}
                            className="max-h-full max-w-full object-contain"
                        />
                    ) : (
                        <div className="text-gray-400 text-6xl font-bold">
                            {club.name.charAt(0)}
                        </div>
                    )}
                </div>

                {/* Club Details */}
                <div className="p-6">
                    <div className="flex justify-between items-start mb-4">
                        <div>
                            <h2 className="text-xl font-bold text-gray-900">{club.name}</h2>
                            <div className="flex items-center mt-2">
                <span
                    className={`px-2 py-1 rounded-full text-xs font-medium ${
                        role === 'ADMIN'
                            ? 'bg-blue-100 text-blue-800'
                            : role === 'COACH'
                                ? 'bg-green-100 text-green-800'
                                : 'bg-gray-100 text-gray-800'
                    }`}
                >
                  {role}
                </span>
                                <span className="ml-2 text-sm text-gray-500">
                  Joined {new Date(joinedAt).toLocaleDateString()}
                </span>
                            </div>
                        </div>

                        {/* Role-based Actions */}
                        {role === 'ADMIN' && (
                            <div className="flex space-x-2">
                                <button
                                    onClick={() => handleDeleteClub(club.id)}
                                    disabled={deletingClubId === club.id}
                                    className="text-red-600 hover:text-red-800 disabled:opacity-50"
                                >
                                    {deletingClubId === club.id ? 'Deleting...' : 'Delete'}
                                </button>
                            </div>
                        )}
                    </div>

                    {/* Description */}
                    {club.description && (
                        <p className="text-gray-600 text-sm line-clamp-3">
                            {club.description}
                        </p>
                    )}

                    {/* Action Links */}
                    <div className="mt-4 flex justify-between items-center">
                        <Link
                            href={`/dashboard/clubs/${club.id}`}
                            className="text-blue-600 hover:text-blue-800 font-medium"
                        >
                            View Details →
                        </Link>
                    </div>
                </div>
            </div>
        );
    };

    // Render content based on loading and data states
    const renderContent = () => {
        if (isLoading) {
            return (
                <div className="flex justify-center items-center min-h-[50vh]">
                    <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-blue-500"></div>
                </div>
            );
        }

        if (error) {
            return (
                <div className="bg-red-50 border border-red-200 text-red-800 p-4 rounded-md">
                    {error}
                </div>
            );
        }

        if (clubs.length === 0) {
            return (
                <div className="text-center py-16 bg-gray-50 rounded-lg">
                    <h2 className="text-2xl text-gray-600 mb-4">No Clubs Found</h2>
                    <p className="text-gray-500 mb-6">
                        You haven't joined any clubs yet.
                    </p>
                    <Link
                        href="/dashboard/clubs/create"
                        className="bg-blue-600 text-white px-6 py-3 rounded-full hover:bg-blue-700 transition-colors"
                    >
                        Create a Club
                    </Link>
                </div>
            );
        }

        return (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                {clubs.map(renderClubCard)}
            </div>
        );
    };

    return (
        <div className="container mx-auto px-4 py-8">
            <div className="flex justify-between items-center mb-8">
                <h1 className="text-3xl font-bold text-gray-900">My Clubs</h1>
                <Link
                    href="/dashboard/clubs/create"
                    className="bg-blue-600 text-white px-4 py-2 rounded-full hover:bg-blue-700 transition-colors"
                >
                    Create New Club
                </Link>
            </div>

            {renderContent()}
        </div>
    );
}