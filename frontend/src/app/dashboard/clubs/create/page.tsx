'use client';

import { useState, FormEvent } from 'react';
import { useRouter } from 'next/navigation';

interface ClubFormData {
    name: string;
    description?: string;
    logoUrl?: string;
}

export default function CreateClubPage() {
    const router = useRouter();
    const [formData, setFormData] = useState<ClubFormData>({
        name: '',
        description: '',
        logoUrl: ''
    });
    const [errors, setErrors] = useState<Partial<ClubFormData>>({});
    const [isLoading, setIsLoading] = useState(false);

    const validateForm = (): boolean => {
        const newErrors: Partial<ClubFormData> = {};

        if (!formData.name.trim()) {
            newErrors.name = 'Club name is required';
        } else if (formData.name.length < 2 || formData.name.length > 100) {
            newErrors.name = 'Club name must be between 2 and 100 characters';
        }

        if (formData.description && formData.description.length > 1000) {
            newErrors.description = 'Description cannot exceed 1000 characters';
        }

        if (formData.logoUrl && formData.logoUrl.length > 500) {
            newErrors.logoUrl = 'Logo URL cannot exceed 500 characters';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        if (!validateForm()) return;

        setIsLoading(true);

        try {
            const response = await fetch('http://localhost:8080/api/clubs', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify({
                    name: formData.name,
                    description: formData.description || null,
                    logoUrl: formData.logoUrl || null
                })
            });

            if (!response.ok) {
                throw new Error('Failed to create club');
            }

            const createdClub = await response.json();
            router.push(`/dashboard/clubs/${createdClub.id}`);
        } catch (error) {
            console.error('Error creating club:', error);
        } finally {
            setIsLoading(false);
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    return (
        <div className="min-h-screen bg-gray-100 flex items-center justify-center px-4 py-12">
            <div className="max-w-md w-full bg-white shadow-md rounded-lg p-8">
                <h2 className="text-2xl font-bold text-center mb-6">Create a New Club</h2>
                <form onSubmit={handleSubmit} className="space-y-6">
                    <div>
                        <label htmlFor="name" className="block text-sm font-medium text-gray-700">
                            Club Name
                        </label>
                        <input
                            type="text"
                            id="name"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            className={`mt-1 block w-full rounded-md border ${
                                errors.name ? 'border-red-500' : 'border-gray-300'
                            } shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500`}
                            placeholder="Enter club name"
                        />
                        {errors.name && (
                            <p className="mt-1 text-sm text-red-500">{errors.name}</p>
                        )}
                    </div>

                    <div>
                        <label htmlFor="description" className="block text-sm font-medium text-gray-700">
                            Description (Optional)
                        </label>
                        <textarea
                            id="description"
                            name="description"
                            value={formData.description}
                            onChange={handleChange}
                            rows={4}
                            className={`mt-1 block w-full rounded-md border ${
                                errors.description ? 'border-red-500' : 'border-gray-300'
                            } shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500`}
                            placeholder="Club description"
                        />
                        {errors.description && (
                            <p className="mt-1 text-sm text-red-500">{errors.description}</p>
                        )}
                    </div>

                    <div>
                        <label htmlFor="logoUrl" className="block text-sm font-medium text-gray-700">
                            Logo URL (Optional)
                        </label>
                        <input
                            type="url"
                            id="logoUrl"
                            name="logoUrl"
                            value={formData.logoUrl}
                            onChange={handleChange}
                            className={`mt-1 block w-full rounded-md border ${
                                errors.logoUrl ? 'border-red-500' : 'border-gray-300'
                            } shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500`}
                            placeholder="https://example.com/logo.png"
                        />
                        {errors.logoUrl && (
                            <p className="mt-1 text-sm text-red-500">{errors.logoUrl}</p>
                        )}
                    </div>

                    <div>
                        <button
                            type="submit"
                            disabled={isLoading}
                            className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
                        >
                            {isLoading ? 'Creating Club...' : 'Create Club'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}