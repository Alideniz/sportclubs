export type ClubType = {
    id: string;
    name: string;
    description?: string;
    logoUrl?: string;
    createdAt: string;
    updatedAt?: string;
};

export type UserClubRoleType = {
    club: ClubType;
    role: 'ADMIN' | 'COACH' | 'MEMBER';
    joinedAt: string;
};