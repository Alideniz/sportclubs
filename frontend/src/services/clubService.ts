import {UserClubRoleType} from "@/types/club";

export async function fetchUserClubs(): Promise<UserClubRoleType[]> {
    try {
        const response = await fetch('http://localhost:8080/api/clubs/user', {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error('Failed to fetch clubs');
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching user clubs:', error);
        throw error;
    }
}

export async function deleteClub(clubId: string): Promise<void> {
    try {
        const response = await fetch(`http://localhost:8080/api/clubs/${clubId}`, {
            method: 'DELETE',
            credentials: 'include',
        });

        if (!response.ok) {
            throw new Error('Failed to delete club');
        }
    } catch (error) {
        console.error('Error deleting club:', error);
        throw error;
    }
}