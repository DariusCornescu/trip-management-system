import React, { useState, useEffect } from 'react';
import { userAPI } from '../../services/api';

const UserManagement = () => {
    const [users, setUsers] = useState([]);
    const [showForm, setShowForm] = useState(false);
    const [editingUser, setEditingUser] = useState(null);
    const [loading, setLoading] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    const [error, setError] = useState('');

    const [formData, setFormData] = useState({ id: '', username: '', password: '' });

    useEffect(() => { loadUsers().then(r => r); }, []);

    const showMessage = (message, isError = false) => {
        if (isError) {
            setError(message);
            setTimeout(() => setError(''), 5000);
        } else {
            alert(message);
            setError('');
        }
    };

    const loadUsers = async () => {
        setLoading(true);
        try {
            const data = await userAPI.getAllUsers();
            setUsers(Array.isArray(data) ? data : []);
        } catch (error) {
            showMessage('Error loading users: ' + error.message, true);
            console.error('Error loading users:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = async () => {
        if (!searchTerm.trim()) {
            await loadUsers();
            return;
        }

        setLoading(true);
        try {
            const data = await userAPI.searchUsers(searchTerm);
            setUsers(Array.isArray(data) ? data : []);
        } catch (error) {
            showMessage('Error searching users: ' + error.message, true);
        } finally {
            setLoading(false);
        }
    };

    const handleCreateOrUpdate = async (e) => {
        e.preventDefault();

        if (!formData.username.trim() || !formData.password.trim()) {
            showMessage('Username and password are required', true);
            return;
        }

        if (!editingUser && !formData.id) {
            showMessage('User ID is required for new users', true);
            return;
        }

        setLoading(true);
        try {
            if (editingUser) {
                const response = await userAPI.updateUser(editingUser.id, formData.username, formData.password);
                if (response.success) {
                    showMessage('User updated successfully!');
                } else {
                    showMessage(response.message || 'Failed to update user', true);
                    return;
                }
            } else {
                const response = await userAPI.createUser(parseInt(formData.id), formData.username, formData.password);
                if (response.success) {
                    showMessage('User created successfully!');
                } else {
                    showMessage(response.message || 'Failed to create user', true);
                    return;
                }
            }

            setShowForm(false);
            setEditingUser(null);
            setFormData({ id: '', username: '', password: '' });
            loadUsers();
        } catch (error) {
            showMessage('Error saving user: ' + error.message, true);
            console.error('Error saving user:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleEdit = (user) => {
        setEditingUser(user);
        setFormData({ id: user.id.toString(), username: user.username, password: user.password });
        setShowForm(true);
    };

    const handleDelete = async (user) => {
        if (user.username === 'Admin') {
            showMessage('Cannot delete the Admin user', true);
            return;
        }

        if (window.confirm(`Are you sure you want to delete user "${user.username}"?`)) {
            setLoading(true);
            try {
                await userAPI.deleteUser(user.id);
                showMessage('User deleted successfully!');
                loadUsers();
            } catch (error) {
                showMessage('Error deleting user: ' + error.message, true);
                console.error('Error deleting user:', error);
            } finally {
                setLoading(false);
            }
        }
    };

    const handleCancel = () => {
        setShowForm(false);
        setEditingUser(null);
        setFormData({ id: '', username: '', password: '' });
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    if (showForm) {
        return (
            <div className="max-w-md mx-auto">
                <div className="bg-white rounded-lg shadow-md p-6">
                    <h2 className="text-2xl font-bold text-gray-800 mb-6">
                        {editingUser ? 'Edit User' : 'Create New User'}
                    </h2>

                    {error && (
                        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-4">
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleCreateOrUpdate} className="space-y-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                User ID *
                            </label>
                            <input
                                type="number"
                                name="id"
                                value={formData.id}
                                onChange={handleInputChange}
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                disabled={!!editingUser}
                                required
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Username *
                            </label>
                            <input
                                type="text"
                                name="username"
                                value={formData.username}
                                onChange={handleInputChange}
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                required
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                Password *
                            </label>
                            <input
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleInputChange}
                                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                required
                            />
                        </div>

                        <div className="flex gap-3 pt-4">
                            <button
                                type="submit"
                                disabled={loading}
                                className="flex-1 bg-blue-500 hover:bg-blue-600 text-white font-medium py-2 px-4 rounded-lg transition duration-200 disabled:opacity-50"
                            >
                                {loading ? 'Saving...' : (editingUser ? 'Update User' : 'Create User')}
                            </button>
                            <button
                                type="button"
                                onClick={handleCancel}
                                className="flex-1 bg-gray-500 hover:bg-gray-600 text-white font-medium py-2 px-4 rounded-lg transition duration-200"
                            >
                                Cancel
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        );
    }

    return (
        <div>
            {error && (
                <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
                    {error}
                </div>
            )}

            <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-bold text-gray-800">User Management</h2>
                <button
                    onClick={() => setShowForm(true)}
                    className="bg-green-500 hover:bg-green-600 text-white font-medium py-2 px-4 rounded-lg transition duration-200"
                >
                    ➕ Add User
                </button>
            </div>

            <div className="flex gap-2 mb-6 max-w-md">
                <input
                    type="text"
                    placeholder="Search users..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                    className="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
                <button
                    onClick={handleSearch}
                    className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg transition duration-200"
                >
                    🔍
                </button>
                <button
                    onClick={() => { setSearchTerm(''); loadUsers(); }}
                    className="bg-gray-500 hover:bg-gray-600 text-white px-4 py-2 rounded-lg transition duration-200"
                >
                    Clear
                </button>
            </div>

            {loading ? (
                <div className="text-center py-8">
                    <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
                    <p className="mt-2 text-gray-600">Loading users...</p>
                </div>
            ) : users.length === 0 ? (
                <div className="text-center py-8">
                    <div className="text-4xl mb-4">👥</div>
                    <h3 className="text-xl font-medium text-gray-900 mb-2">No users found</h3>
                    <p className="text-gray-600">
                        {searchTerm ? 'No users match your search criteria.' : 'No users available.'}
                    </p>
                </div>
            ) : (
                <div className="bg-white rounded-lg shadow overflow-hidden">
                    <table className="min-w-full">
                        <thead className="bg-gray-50">
                        <tr>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                ID
                            </th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Username
                            </th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Role
                            </th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Actions
                            </th>
                        </tr>
                        </thead>
                        <tbody className="bg-white divide-y divide-gray-200">
                        {users.map((user) => (
                            <tr key={user.id} className="hover:bg-gray-50">
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                    {user.id}
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                    {user.username}
                                    {user.username === 'Admin' && (
                                        <span className="ml-2 inline-block bg-blue-100 text-blue-800 text-xs font-semibold px-2 py-1 rounded">
                        Admin
                      </span>
                                    )}
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                    {user.username === 'Admin' ? 'Administrator' : 'User'}
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                                    <button
                                        onClick={() => handleEdit(user)}
                                        className="text-blue-600 hover:text-blue-900 bg-blue-100 hover:bg-blue-200 px-3 py-1 rounded transition duration-200"
                                    >
                                        Edit
                                    </button>
                                    {user.username !== 'Admin' && (
                                        <button
                                            onClick={() => handleDelete(user)}
                                            className="text-red-600 hover:text-red-900 bg-red-100 hover:bg-red-200 px-3 py-1 rounded transition duration-200"
                                        >
                                            Delete
                                        </button>
                                    )}
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};

export default UserManagement;