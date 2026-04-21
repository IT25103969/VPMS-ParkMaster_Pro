<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkMaster Pro | Dashboard</title>
    <script src="https://cdn.jsdelivr.net/npm/animejs@3.2.1/lib/anime.min.js"></script>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <script src="https://unpkg.com/lucide@latest"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf-autotable/3.5.28/jspdf.plugin.autotable.min.js"></script>
    <script>
        const role = sessionStorage.getItem('role');
        const userId = sessionStorage.getItem('userId');
        const userName = sessionStorage.getItem('userName');
        if (!role) window.location.href = 'login';

        tailwind.config = {
            darkMode: 'class',
            theme: {
                extend: {
                    colors: {
                        dark: {
                            bg: '#0f172a',
                            card: '#1e293b',
                            border: '#334155'
                        }
                    }
                }
            }
        }
    </script>
    <style>
        body { font-family: 'Inter', sans-serif; transition: background-color 0.3s, color 0.3s; }
        .bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 {
            background: rgba(255, 255, 255, 0.7);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
        }
        .dark .bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 {
            background: rgba(30, 41, 59, 0.7);
            border: 1px solid rgba(255, 255, 255, 0.1);
        }
        .gradient-bg {
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
        }
        .dark .gradient-bg {
            background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
        }
        .sidebar-item { cursor: pointer; transition: all 0.3s; }
        .sidebar-item:hover {
            background: rgba(59, 130, 246, 0.1);
            color: #3b82f6;
        }
        .sidebar-item.active {
            background: #3b82f6;
            color: white;
        }
        .security-tab {
            cursor: pointer;
            padding: 8px 16px;
            border-radius: 8px;
            font-weight: bold;
            font-size: 12px;
            transition: all 0.2s;
            border: 1px solid rgba(255, 255, 255, 0.1);
        }
        .security-tab.active.low { background: #10b981; color: white; }
        .security-tab.active.med { background: #f59e0b; color: white; }
        .security-tab.active.high { background: #ef4444; color: white; }
        .parking-slot { transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); }
        .tab-content { display: none; }
        .tab-content.active { display: block; }
        
        /* SweetAlert Dark Mode Fixes */
        .dark .swal2-popup {
            background-color: #1e293b !important;
            color: #f1f5f9 !important;
        }
        .dark .swal2-input, .dark .swal2-select, .dark .swal2-textarea {
            background-color: #334155 !important;
            color: #f1f5f9 !important;
            border-color: #475569 !important;
        }
        .dark .swal2-input:focus {
            border-color: #3b82f6 !important;
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2) !important;
        }
        .dark .swal2-input::placeholder {
            color: #94a3b8 !important;
        }
        .dark select.swal2-input option {
            background-color: #1e293b;
            color: #f1f5f9;
        }
    </style>
</head>
<body class="gradient-bg min-h-screen flex dark:text-slate-200">

    <script>
        if (localStorage.getItem('theme') === 'dark' || (!('theme' in localStorage) && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
            document.documentElement.classList.add('dark');
        } else {
            document.documentElement.classList.remove('dark');
        }
    </script>

    <!-- Sidebar -->
    <aside class="w-64 bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 border-r h-screen sticky top-0 flex flex-col p-6 space-y-8 hidden md:flex dark:border-slate-800">
        <div class="flex items-center space-x-3 px-2">
            <div class="bg-blue-600 p-2 rounded-lg text-white">
                <i data-lucide="car"></i>
            </div>
            <h1 class="text-xl font-bold text-gray-800 dark:text-white tracking-tight">ParkMaster</h1>
        </div>

        <nav class="flex-1 space-y-1">
            <div id="nav-dashboard-container">
                <div onclick="switchTab('dashboard')" id="nav-dashboard" class="cursor-pointer transition-all duration-300 hover:bg-blue-500/10 hover:text-blue-600 flex items-center space-x-3 p-3 rounded-xl text-gray-500 dark:text-slate-400 bg-blue-600 text-white">
                    <i data-lucide="layout-dashboard" class="w-5 h-5"></i>
                    <span class="font-medium">Dashboard</span>
                </div>
            </div>
            <div id="nav-slots-container">
                <div onclick="switchTab('slots')" id="nav-slots" class="cursor-pointer transition-all duration-300 hover:bg-blue-500/10 hover:text-blue-600 flex items-center space-x-3 p-3 rounded-xl text-gray-500 dark:text-slate-400">
                    <i data-lucide="parking-circle" class="w-5 h-5"></i>
                    <span class="font-medium">Parking Slots</span>
                </div>
            </div>
            <div id="nav-history-container">
                <div onclick="switchTab('history')" id="nav-history" class="cursor-pointer transition-all duration-300 hover:bg-blue-500/10 hover:text-blue-600 flex items-center space-x-3 p-3 rounded-xl text-gray-500 dark:text-slate-400">
                    <i data-lucide="bar-chart-3" class="w-5 h-5"></i>
                    <span class="font-medium">Analytics</span>
                </div>
            </div>
            <div id="nav-membership-container">
                <div onclick="switchTab('membership')" id="nav-membership" class="cursor-pointer transition-all duration-300 hover:bg-blue-500/10 hover:text-blue-600 flex items-center space-x-3 p-3 rounded-xl text-gray-500 dark:text-slate-400">
                    <i data-lucide="users" class="w-5 h-5"></i>
                    <span class="font-medium">Staff Management</span>
                </div>
            </div>
            <div id="nav-settings-container">
                <div onclick="switchTab('settings')" id="nav-settings" class="cursor-pointer transition-all duration-300 hover:bg-blue-500/10 hover:text-blue-600 flex items-center space-x-3 p-3 rounded-xl text-gray-500 dark:text-slate-400">
                    <i data-lucide="settings" class="w-5 h-5"></i>
                    <span class="font-medium">Settings</span>
                </div>
            </div>
            <div class="pt-4 border-t dark:border-slate-800 mt-4">
                <div onclick="switchTab('report')" id="nav-report" class="cursor-pointer transition-all duration-300 hover:bg-blue-500/10 hover:text-blue-600 flex items-center space-x-3 p-3 rounded-xl text-gray-500 dark:text-slate-400">
                    <i data-lucide="alert-triangle" class="w-5 h-5"></i>
                    <span class="font-medium">Report a Problem</span>
                </div>
            </div>
        </nav>

        <div class="bg-blue-50 dark:bg-slate-800/50 p-4 rounded-2xl border border-blue-100 dark:border-slate-700">
            <p class="text-xs font-semibold text-blue-800 dark:text-blue-400 mb-1" id="user-name-display">User</p>
            <button onclick="toggleDarkMode()" class="w-full flex items-center justify-center space-x-2 bg-white dark:bg-slate-700 py-2 rounded-lg text-[10px] font-bold shadow-sm border dark:border-slate-600 mb-2 transition-all">
                <i data-lucide="sun" class="hidden dark:block w-3 h-3"></i>
                <i data-lucide="moon" class="block dark:hidden w-3 h-3"></i>
                <span class="dark:hidden">Dark Mode</span>
                <span class="hidden dark:block">Light Mode</span>
            </button>
            <button onclick="logout()" class="w-full bg-gray-200 dark:bg-slate-700 text-gray-800 dark:text-slate-200 py-2 rounded-lg text-[10px] font-bold hover:bg-gray-300 dark:hover:bg-slate-600 transition-colors">Sign Out</button>
        </div>
    </aside>

    <!-- Main Content -->
    <main class="flex-1 p-4 md:p-8 space-y-8 max-w-7xl mx-auto overflow-y-auto">
        
        <header class="flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div>
                <h2 id="page-title" class="text-2xl font-bold text-gray-800 dark:text-white">Overview Dashboard</h2>
                <p id="page-subtitle" class="text-gray-500 dark:text-slate-400">Welcome back! Manage your parking facility with ease.</p>
            </div>
            <div class="flex items-center space-x-4">
                <button id="btn-new-entry" onclick="handleEntry()" class="bg-blue-600 text-white px-6 py-2.5 rounded-xl font-bold hover:bg-blue-700 transition-all shadow-lg shadow-blue-200 dark:shadow-none flex items-center space-x-2">
                    <i data-lucide="plus-circle" class="w-5 h-5"></i>
                    <span>New Entry</span>
                </button>
                <div class="flex items-center space-x-3 bg-white dark:bg-slate-800 p-1 pr-4 rounded-full border dark:border-slate-700 shadow-sm">
                    <div class="w-8 h-8 rounded-full bg-blue-100 dark:bg-blue-900 flex items-center justify-center text-blue-600 dark:text-blue-300 font-bold" id="user-initials">AD</div>
                    <span class="text-sm font-semibold text-gray-700 dark:text-slate-200" id="user-role-display">Admin</span>
                </div>
            </div>
        </header>

        <div id="tab-dashboard" class="tab-content active space-y-8">
            <section class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
                <div class="bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 p-6 rounded-3xl shadow-sm border border-white">
                    <div class="text-blue-600 dark:text-blue-400 bg-blue-100 dark:bg-blue-900/30 w-fit p-3 rounded-2xl mb-4"><i data-lucide="parking-circle"></i></div>
                    <h3 class="text-gray-500 dark:text-slate-400 text-sm font-medium">Total Slots</h3>
                    <p id="stat-total-slots" class="text-2xl font-bold text-gray-800 dark:text-white">0</p>
                </div>
                <div class="bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 p-6 rounded-3xl shadow-sm border border-white">
                    <div class="text-orange-600 dark:text-orange-400 bg-orange-100 dark:bg-orange-900/30 w-fit p-3 rounded-2xl mb-4"><i data-lucide="user-check"></i></div>
                    <h3 class="text-gray-500 dark:text-slate-400 text-sm font-medium">Occupied</h3>
                    <p id="stat-occupied" class="text-2xl font-bold text-gray-800 dark:text-white">0 <span class="text-sm font-normal text-gray-400 dark:text-slate-500">/ 0</span></p>
                </div>
                <div class="bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 p-6 rounded-3xl shadow-sm border border-white">
                    <div class="text-green-600 dark:text-green-400 bg-green-100 dark:bg-green-900/30 w-fit p-3 rounded-2xl mb-4"><i data-lucide="dollar-sign"></i></div>
                    <h3 class="text-gray-500 dark:text-slate-400 text-sm font-medium">Daily Revenue</h3>
                    <p id="stat-revenue" class="text-2xl font-bold text-gray-800 dark:text-white">$0.00</p>
                </div>
                <div class="bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 p-6 rounded-3xl shadow-sm border border-white">
                    <div class="text-purple-600 dark:text-purple-400 bg-purple-100 dark:bg-purple-900/30 w-fit p-3 rounded-2xl mb-4"><i data-lucide="clock"></i></div>
                    <h3 class="text-gray-500 dark:text-slate-400 text-sm font-medium">Hourly Rate</h3>
                    <p id="stat-rate" class="text-2xl font-bold text-gray-800 dark:text-white">$0.00</p>
                </div>
            </section>
            <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
                <div class="lg:col-span-2 bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 p-6 rounded-3xl shadow-sm border border-white">
                    <h3 class="text-lg font-bold text-gray-800 dark:text-white mb-6">Live Parking Map</h3>
                    <div id="parking-grid" class="grid grid-cols-5 sm:grid-cols-8 gap-4"></div>
                    <div id="staff-legend" class="mt-4 flex space-x-4 text-xs font-bold hidden">
                        <div class="flex items-center space-x-1"><div class="w-3 h-3 bg-green-500 rounded"></div><span>Available</span></div>
                        <div class="flex items-center space-x-1"><div class="w-3 h-3 bg-red-500 rounded"></div><span>Occupied</span></div>
                        <div class="flex items-center space-x-1"><div class="w-3 h-3 bg-yellow-500 rounded"></div><span>Reserved</span></div>
                        <div class="flex items-center space-x-1"><div class="w-3 h-3 bg-yellow-600 ring-2 ring-yellow-300 rounded"></div><span>Yours</span></div>
                    </div>
                </div>
                <div class="bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 p-6 rounded-3xl shadow-sm border border-white">
                    <h3 class="text-lg font-bold text-gray-800 dark:text-white mb-6">Live Entry Feed</h3>
                    <div id="entry-feed" class="space-y-6"></div>
                </div>
            </div>
        </div>

        <div id="tab-slots" class="tab-content space-y-8">
            <div class="bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 p-8 rounded-3xl shadow-sm border border-white">
                <h3 class="text-lg font-bold text-gray-800 dark:text-white mb-6">Slot Management</h3>
                <div id="slots-management-grid" class="grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-6 gap-6"></div>
            </div>
        </div>

        <div id="tab-history" class="tab-content space-y-8">
            <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
                <div class="lg:col-span-2 bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 p-6 rounded-3xl shadow-sm border border-white">
                    <h3 class="text-lg font-bold text-gray-800 dark:text-white mb-6">Revenue Analytics</h3>
                    <div class="h-[300px]">
                        <canvas id="revenueChart"></canvas>
                    </div>
                </div>
                <div class="bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 p-6 rounded-3xl shadow-sm border border-white flex flex-col justify-center items-center text-center">
                    <div class="bg-blue-100 dark:bg-blue-900/30 p-4 rounded-full mb-4">
                        <i data-lucide="trending-up" class="w-8 h-8 text-blue-600 dark:text-blue-400"></i>
                    </div>
                    <h3 class="text-xl font-bold text-gray-800 dark:text-white mb-2">Growth Overview</h3>
                    <p class="text-gray-500 dark:text-slate-400 text-sm">Real-time revenue tracking based on completed sessions.</p>
                </div>
            </div>
            <section class="bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 p-6 rounded-3xl shadow-sm border border-white">
                <h3 class="text-lg font-bold text-gray-800 dark:text-white mb-6">Transaction History</h3>
                <div class="overflow-x-auto">
                    <table class="w-full text-left">
                        <thead>
                            <tr class="text-gray-400 dark:text-slate-500 text-sm font-medium border-b border-gray-100 dark:border-slate-700">
                                <th class="pb-4">Vehicle ID</th>
                                <th class="pb-4">Entry</th>
                                <th class="pb-4">Exit</th>
                                <th class="pb-4">Slot</th>
                                <th class="pb-4">Amount</th>
                                <th class="pb-4">Status</th>
                            </tr>
                        </thead>
                        <tbody id="history-body" class="divide-y divide-gray-50 dark:divide-slate-700"></tbody>
                    </table>
                </div>
            </section>
        </div>

        <div id="tab-membership" class="tab-content space-y-8">
            <div class="bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 p-8 rounded-3xl shadow-sm border border-white">
                <div class="flex justify-between items-center mb-6">
                    <h3 class="text-lg font-bold text-gray-800 dark:text-white">Staff Management</h3>
                    <button onclick="showAddMemberModal()" class="bg-blue-600 text-white px-4 py-2 rounded-xl text-sm font-bold flex items-center space-x-2">
                        <i data-lucide="user-plus" class="w-4 h-4"></i>
                        <span>Register Staff</span>
                    </button>
                </div>
                <div class="overflow-x-auto">
                    <table class="w-full text-left text-sm">
                        <thead>
                            <tr class="text-gray-400 dark:text-slate-500 font-medium border-b border-gray-100 dark:border-slate-700">
                                <th class="pb-4">Staff Name</th>
                                <th class="pb-4">Department</th>
                                <th class="pb-4">Username</th>
                                <th class="pb-4">Access Permissions</th>
                                <th class="pb-4">Action</th>
                            </tr>
                        </thead>
                        <tbody id="membership-body" class="divide-y divide-gray-50 dark:divide-slate-700"></tbody>
                    </table>
                </div>
            </div>
        </div>

        <div id="tab-settings" class="tab-content space-y-8">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
                <div class="bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 p-8 rounded-3xl shadow-sm border border-white">
                    <h3 class="text-lg font-bold text-gray-800 dark:text-white mb-6">Pricing Configuration</h3>
                    <div class="space-y-4">
                        <div>
                            <label class="block text-xs font-bold text-gray-400 uppercase mb-2">Hourly Rate ($)</label>
                            <input type="number" id="input-hourly-rate" step="0.5" class="w-full bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 px-4 py-3 rounded-xl outline-none dark:text-white">
                        </div>
                        <button onclick="updateRate()" class="w-full bg-blue-600 text-white py-3 rounded-xl font-bold hover:bg-blue-700">Update Rate</button>
                    </div>
                </div>
                <div class="bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 p-8 rounded-3xl shadow-sm border border-white">
                    <h3 class="text-lg font-bold text-gray-800 dark:text-white mb-6">Monthly Membership Fee</h3>
                    <div class="space-y-4">
                        <div>
                            <label class="block text-xs font-bold text-gray-400 uppercase mb-2">30-Day Cycle Fee ($)</label>
                            <input type="number" id="input-member-fee" step="1" class="w-full bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 px-4 py-3 rounded-xl outline-none dark:text-white">
                        </div>
                        <button onclick="updateMemberFee()" class="w-full bg-purple-600 text-white py-3 rounded-xl font-bold hover:purple-700">Update Membership Fee</button>
                    </div>
                </div>
            </div>

            <div class="bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 p-8 rounded-3xl shadow-sm border border-white">
                <div class="flex justify-between items-center mb-6">
                    <h3 class="text-lg font-bold text-gray-800 dark:text-white">Active Monthly Members</h3>
                    <button onclick="showAddMonthlyMemberModal()" class="bg-indigo-600 text-white px-4 py-2 rounded-xl text-sm font-bold flex items-center space-x-2">
                        <i data-lucide="user-plus" class="w-4 h-4"></i>
                        <span>Add New Member</span>
                    </button>
                </div>
                <div class="overflow-x-auto">
                    <table class="w-full text-left text-sm">
                        <thead>
                            <tr class="text-gray-400 dark:text-slate-500 font-medium border-b border-gray-100 dark:border-slate-700">
                                <th class="pb-4">Name</th>
                                <th class="pb-4">Email</th>
                                <th class="pb-4">Join Date</th>
                                <th class="pb-4">Status</th>
                                <th class="pb-4">Action</th>
                            </tr>
                        </thead>
                        <tbody id="monthly-members-body" class="divide-y divide-gray-50 dark:divide-slate-700"></tbody>
                    </table>
                </div>
            </div>

            <div class="bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 p-8 rounded-3xl shadow-sm border border-white">
                <h3 class="text-lg font-bold text-gray-800 dark:text-white mb-6">Operational Reports</h3>
                <div class="flex items-center justify-between p-4 bg-blue-50 dark:bg-blue-900/20 rounded-2xl border border-blue-100 dark:border-blue-800">
                    <div>
                        <p class="font-bold text-blue-900 dark:text-blue-300">Daily PDF Activity Report</p>
                        <p class="text-xs text-blue-700 dark:text-blue-400">Includes revenue, vehicle statistics, and staff login activity for today.</p>
                    </div>
                    <button onclick="downloadReport()" class="bg-blue-600 text-white px-6 py-2.5 rounded-xl font-bold hover:bg-blue-700 flex items-center space-x-2">
                        <i data-lucide="download" class="w-4 h-4"></i>
                        <span>Download PDF</span>
                    </button>
                </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
                <div class="bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 p-8 rounded-3xl shadow-sm border border-white">
                    <h3 class="text-lg font-bold text-gray-800 dark:text-white mb-6">Capacity Management</h3>
                    <div class="space-y-4">
                        <input type="number" id="input-slot-count" min="1" class="w-full bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 px-4 py-3 rounded-xl outline-none dark:text-white">
                        <button onclick="updateSlotCount()" class="w-full bg-orange-600 text-white py-3 rounded-xl font-bold hover:bg-orange-700">Adjust Capacity</button>
                    </div>
                </div>
                <div class="bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 p-8 rounded-3xl shadow-sm border border-white">
                    <h3 class="text-lg font-bold text-gray-800 dark:text-white mb-6">Admin Security</h3>
                    <button onclick="changeAdminPassword()" class="w-full bg-gray-800 dark:bg-slate-700 text-white py-3 rounded-xl font-bold hover:bg-black dark:hover:bg-slate-600 transition-all">Change Admin Password</button>
                </div>
            </div>
        </div>

        <div id="tab-report" class="tab-content space-y-8">
            <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
                <!-- Reporting Form -->
                <div class="bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 p-8 rounded-3xl shadow-sm border border-white">
                    <h3 class="text-xl font-bold text-gray-800 dark:text-white mb-6">Report a Problem</h3>
                    <div class="space-y-6">
                        <div>
                            <label class="block text-xs font-bold text-gray-400 uppercase mb-2">Problem Type</label>
                            <select id="report-type" class="w-full bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 px-4 py-3 rounded-xl outline-none dark:text-white">
                                <option value="Slot Issue">Slot Issue</option>
                                <option value="Vehicle Damage">Vehicle Damage</option>
                                <option value="Payment Error">Payment Error</option>
                                <option value="Security Concern">Security Concern</option>
                                <option value="Other">Other</option>
                            </select>
                        </div>
                        <div>
                            <label class="block text-xs font-bold text-gray-400 uppercase mb-2">Security Level</label>
                            <div class="flex bg-black/5 dark:bg-white/5 p-1 rounded-xl">
                                <button onclick="selectSecurityLevel('LOW')" id="sec-low" class="security-tab active low flex-1">LOW</button>
                                <button onclick="selectSecurityLevel('MED')" id="sec-med" class="security-tab flex-1">MED</button>
                                <button onclick="selectSecurityLevel('HIGH')" id="sec-high" class="security-tab flex-1">HIGH</button>
                            </div>
                            <input type="hidden" id="report-level" value="LOW">
                        </div>
                        <div>
                            <label class="block text-xs font-bold text-gray-400 uppercase mb-2">Affected Spot / Area</label>
                            <input type="text" id="report-spot" placeholder="e.g. A12 or Entrance" class="w-full bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 px-4 py-3 rounded-xl outline-none dark:text-white">
                        </div>
                        <div>
                            <label class="block text-xs font-bold text-gray-400 uppercase mb-2">Detailed Description</label>
                            <textarea id="report-desc" rows="4" placeholder="Describe the issue in detail..." class="w-full bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 px-4 py-3 rounded-xl outline-none dark:text-white resize-none"></textarea>
                        </div>
                        <button onclick="submitReport()" class="w-full bg-blue-600 text-white py-4 rounded-xl font-bold hover:bg-blue-700 transition-all shadow-lg active:scale-[0.98]">
                            Submit Report
                        </button>
                    </div>
                </div>

                <!-- Recent Reports Feed -->
                <div class="bg-white/70 dark:bg-slate-800/70 backdrop-blur-md border border-white/20 dark:border-slate-700/50 p-8 rounded-3xl shadow-sm border border-white">
                    <h3 class="text-xl font-bold text-gray-800 dark:text-white mb-6">Recent Reports</h3>
                    <div id="reports-list" class="space-y-4 max-h-[600px] overflow-y-auto pr-2">
                        <!-- Reports will be injected here -->
                        <div class="text-center py-8 text-gray-400">Loading reports...</div>
                    </div>
                </div>
            </div>
        </div>

    </main>

    <script>
        const API_BASE = '/api';
        let currentTab = 'dashboard';
        let selectedSecurityLevel = 'LOW';

        // Initialize user info
        document.getElementById('user-name-display').textContent = userName || 'User';
        document.getElementById('user-role-display').textContent = role || 'Role';
        document.getElementById('user-initials').textContent = (userName || 'U').substring(0, 2).toUpperCase();

        const accessibleTabs = JSON.parse(sessionStorage.getItem('accessibleTabs') || '[]');

        function applyPermissions() {
            if (role === 'ADMIN') return;

            const effectiveTabs = accessibleTabs;
            const allTabs = ['dashboard', 'slots', 'history', 'membership', 'settings'];
            
            allTabs.forEach(tab => {
                const navContainer = document.getElementById('nav-' + tab + '-container');
                const navItem = document.getElementById('nav-' + tab);
                const isAccessible = effectiveTabs.includes(tab);
                
                if (navContainer) {
                    navContainer.style.display = isAccessible ? 'block' : 'none';
                } else if (navItem) {
                    navItem.style.display = isAccessible ? 'flex' : 'none';
                }
            });

            if (role === 'STAFF') {
                document.getElementById('staff-legend').classList.remove('hidden');
            }
        }

        function switchTab(tabId) {
            // Permission check
            if (tabId !== 'report' && role !== 'ADMIN' && !accessibleTabs.includes(tabId)) {
                if (accessibleTabs.length > 0) {
                    const firstTab = accessibleTabs[0];
                    if (tabId !== firstTab) return switchTab(firstTab);
                }
                return;
            }

            // Anime.js transition
            const content = document.getElementById('tab-' + tabId);
            if (content && !content.classList.contains('active')) {
                anime({
                    targets: '.tab-content.active',
                    opacity: [1, 0],
                    translateY: [0, 20],
                    duration: 200,
                    easing: 'easeOutQuad',
                    complete: () => {
                        document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
                        content.classList.add('active');
                        anime({
                            targets: content,
                            opacity: [0, 1],
                            translateY: [20, 0],
                            duration: 300,
                            easing: 'easeOutQuad'
                        });
                    }
                });
            }

            currentTab = tabId;
            
            // Deactivate all nav items
            document.querySelectorAll('nav > div > div, nav > div > div > div').forEach(i => {
                i.classList.remove('bg-blue-600', 'text-white');
                i.classList.add('text-gray-500', 'dark:text-slate-400');
            });
            
            // Activate target nav item
            const nav = document.getElementById('nav-' + tabId);
            if (nav) {
                nav.classList.remove('text-gray-500', 'dark:text-slate-400');
                nav.classList.add('bg-blue-600', 'text-white');
            }
            
            const titles = {
                dashboard: ['Overview Dashboard', 'Manage your parking facility with ease.'],
                slots: ['Parking Slots', 'Monitor and manage specific parking locations.'],
                history: ['Analytics & History', 'Review all past transactions and revenue data.'],
                membership: ['Staff Management', 'Administer staff accounts.'],
                settings: ['System Settings', 'Configure rates and system capacity.'],
                report: ['Problem Reporting', 'Notify management about issues or concerns.']
            };
            
            if (titles[tabId]) {
                document.getElementById('page-title').textContent = titles[tabId][0];
                document.getElementById('page-subtitle').textContent = titles[tabId][1];
            }
            
            loadData();
        }

        function selectSecurityLevel(level) {
            selectedSecurityLevel = level;
            document.getElementById('report-level').value = level;
            document.querySelectorAll('.security-tab').forEach(btn => btn.classList.remove('active', 'low', 'med', 'high'));
            const activeBtn = document.getElementById('sec-' + level.toLowerCase());
            activeBtn.classList.add('active', level.toLowerCase());
        }

        async function submitReport() {
            const type = document.getElementById('report-type').value;
            const spot = document.getElementById('report-spot').value.trim();
            const desc = document.getElementById('report-desc').value.trim();
            const swalConfig = { background: document.documentElement.classList.contains('dark') ? '#1e293b' : '#fff', color: document.documentElement.classList.contains('dark') ? '#fff' : '#000' };

            if (!spot || !desc) {
                Swal.fire({ ...swalConfig, icon: 'error', title: 'Missing Info', text: 'Please provide the spot and description.' });
                return;
            }

            try {
                const res = await fetch(`\${API_BASE}/reports`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        problemType: type,
                        securityLevel: selectedSecurityLevel,
                        spot: spot,
                        description: desc
                    })
                });

                if (res.ok) {
                    Swal.fire({ ...swalConfig, icon: 'success', title: 'Report Submitted', text: 'Thank you for your feedback.', timer: 1500, showConfirmButton: false });
                    document.getElementById('report-spot').value = '';
                    document.getElementById('report-desc').value = '';
                    loadReports();
                }
            } catch (err) {
                Swal.fire({ ...swalConfig, icon: 'error', title: 'Error', text: 'Failed to submit report.' });
            }
        }

        async function loadReports() {
            try {
                const res = await fetch(`\${API_BASE}/reports`);
                const reports = await res.json();
                renderReports(reports);
            } catch (err) { console.error(err); }
        }

        function renderReports(reports) {
            const list = document.getElementById('reports-list');
            if (reports.length === 0) {
                list.innerHTML = '<div class="text-center py-8 text-gray-400">No reports yet.</div>';
                return;
            }

            list.innerHTML = reports.slice().reverse().map(r => {
                const levelColor = r.securityLevel === 'HIGH' ? 'text-red-500 bg-red-100 dark:bg-red-900/30' : (r.securityLevel === 'MED' ? 'text-orange-500 bg-orange-100 dark:bg-orange-900/30' : 'text-green-500 bg-green-100 dark:bg-green-900/30');
                return `
                    <div class="p-4 rounded-2xl bg-white dark:bg-slate-800/50 border border-gray-100 dark:border-slate-700 shadow-sm">
                        <div class="flex justify-between items-start mb-2">
                            <span class="text-sm font-bold text-gray-800 dark:text-white">\${r.problemType}</span>
                            <span class="px-2 py-0.5 rounded-full text-[10px] font-bold \${levelColor}">\${r.securityLevel}</span>
                        </div>
                        <p class="text-xs text-gray-500 dark:text-slate-400 mb-2 font-medium">Spot: \${r.spot}</p>
                        <p class="text-sm text-gray-600 dark:text-slate-300 mb-3 leading-relaxed">\${r.description}</p>
                        <div class="text-[10px] text-gray-400 dark:text-slate-500 font-mono">
                            \${new Date(r.reportTime).toLocaleString()}
                        </div>
                    </div>
                `;
            }).join('');
        }

        async function loadData() {
            try {
                const [slotsRes, statsRes, rateRes, historyRes, countRes] = await Promise.all([
                    fetch(`\${API_BASE}/slots`), fetch(`\${API_BASE}/admin/revenue/stats`),
                    fetch(`\${API_BASE}/admin/rate`), fetch(`\${API_BASE}/tickets/history`),
                    fetch(`\${API_BASE}/admin/slots/count`)
                ]);

                const slots = await slotsRes.json();
                const stats = await statsRes.json();
                const rate = await rateRes.json();
                const history = await historyRes.json();
                const count = await countRes.json();

                if (currentTab === 'dashboard') {
                    renderStats(slots, stats, rate);
                    renderGrid(slots, 'parking-grid');
                    renderFeed(history);
                } else if (currentTab === 'slots') {
                    renderGrid(slots, 'slots-management-grid', true);
                } else if (currentTab === 'history') {
                    renderHistory(history);
                    renderChart(history);
                } else if (currentTab === 'membership') {
                    renderMembers();
                } else if (currentTab === 'settings') {
                    document.getElementById('input-hourly-rate').value = rate.rate;
                    document.getElementById('input-slot-count').value = count.count;
                    loadMonthlyMemberData();
                } else if (currentTab === 'report') {
                    loadReports();
                }
                lucide.createIcons();
            } catch (err) { console.error(err); }
        }

        async function loadMonthlyMemberData() {
            const [feeRes, membersRes] = await Promise.all([
                fetch(`\${API_BASE}/admin/members/fee`),
                fetch(`\${API_BASE}/admin/members`)
            ]);
            const feeData = await feeRes.json();
            const members = await membersRes.json();
            
            document.getElementById('input-member-fee').value = feeData.fee;
            renderMonthlyMembers(members);
        }

        function renderMonthlyMembers(members) {
            const body = document.getElementById('monthly-members-body');
            body.innerHTML = members.map(m => `
                <tr class="hover:bg-white dark:hover:bg-slate-800/50 transition-colors">
                    <td class="py-4 font-bold text-gray-700 dark:text-slate-200">\${m.name}</td>
                    <td class="py-4 text-gray-500 dark:text-slate-400 font-mono">\${m.email}</td>
                    <td class="py-4 text-gray-500 dark:text-slate-400">\${m.joinDate}</td>
                    <td class="py-4"><span class="px-2 py-1 rounded-full text-[10px] font-bold \${m.active ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}">\${m.active ? 'ACTIVE' : 'EXPIRED'}</span></td>
                    <td class="py-4"><button onclick="deleteMonthlyMember(\${m.id})" class="text-red-500 hover:text-red-700"><i data-lucide="trash-2" class="w-4 h-4"></i></button></td>
                </tr>
            `).join('');
            lucide.createIcons();
        }

        async function showAddMonthlyMemberModal() {
            const { value: v } = await Swal.fire({
                title: 'Register Monthly Member',
                html: '<input id="mn" class="swal2-input" placeholder="Full Name"><input id="me" class="swal2-input" placeholder="Email Address">',
                preConfirm: () => ({ name: document.getElementById('mn').value, email: document.getElementById('me').value }),
                background: document.documentElement.classList.contains('dark') ? '#1e293b' : '#fff',
                color: document.documentElement.classList.contains('dark') ? '#fff' : '#000'
            });
            if (v && v.name && v.email) {
                const res = await fetch(`\${API_BASE}/admin/members`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(v) });
                if (res.ok) { Swal.fire('Success', 'Member registered!', 'success'); loadMonthlyMemberData(); }
            }
        }

        async function deleteMonthlyMember(id) {
            if ((await Swal.fire({ title: 'Remove Member?', icon: 'warning', showCancelButton: true, background: document.documentElement.classList.contains('dark') ? '#1e293b' : '#fff', color: document.documentElement.classList.contains('dark') ? '#fff' : '#000' })).isConfirmed) {
                await fetch(`\${API_BASE}/admin/members/\${id}`, { method: 'DELETE' });
                loadMonthlyMemberData();
            }
        }

        async function updateMemberFee() {
            const f = document.getElementById('input-member-fee').value;
            const res = await fetch(`\${API_BASE}/admin/members/fee`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ fee: parseFloat(f) }) });
            if (res.ok) Swal.fire({ title: 'Updated', icon: 'success', background: document.documentElement.classList.contains('dark') ? '#1e293b' : '#fff', color: document.documentElement.classList.contains('dark') ? '#fff' : '#000' });
        }

        let revenueChart = null;
        function renderChart(history) {
            const ctx = document.getElementById('revenueChart').getContext('2d');
            const dailyData = {};
            history.filter(t => t.status === 'COMPLETED').forEach(t => {
                const date = new Date(t.exitTime).toLocaleDateString();
                dailyData[date] = (dailyData[date] || 0) + t.amount;
            });

            const labels = Object.keys(dailyData).sort((a, b) => new Date(a) - new Date(b));
            const data = labels.map(l => dailyData[l]);

            if (revenueChart) revenueChart.destroy();
            const isDark = document.documentElement.classList.contains('dark');
            const color = isDark ? '#3b82f6' : '#2563eb';
            const gridColor = isDark ? 'rgba(255, 255, 255, 0.1)' : 'rgba(0, 0, 0, 0.05)';

            revenueChart = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Daily Revenue',
                        data: data,
                        borderColor: color,
                        backgroundColor: color + '20',
                        fill: true,
                        tension: 0.4,
                        borderWidth: 3,
                        pointBackgroundColor: color,
                        pointRadius: 4
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { display: false }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            grid: { color: gridColor },
                            ticks: { color: isDark ? '#94a3b8' : '#64748b' }
                        },
                        x: {
                            grid: { display: false },
                            ticks: { color: isDark ? '#94a3b8' : '#64748b' }
                        }
                    }
                }
            });
        }

        function renderStats(slots, stats, rate) {
            document.getElementById('stat-total-slots').textContent = slots.length;
            document.getElementById('stat-occupied').innerHTML = `\${stats.activeCount} <span class="text-sm font-normal text-gray-400 dark:text-slate-500">/ \${slots.length}</span>`;
            document.getElementById('stat-revenue').textContent = `$\${stats.today.toFixed(2)}`;
            document.getElementById('stat-rate').textContent = `$\${rate.rate.toFixed(2)}`;
        }

        function renderGrid(slots, elementId, isDetailed = false) {
            const grid = document.getElementById(elementId);
            grid.innerHTML = slots.map(slot => {
                const isMine = role === 'STAFF' && slot.staffId == userId;
                const statusColor = slot.occupied ? 'bg-red-500' : (slot.bookedByStaff ? (isMine ? 'bg-yellow-600 ring-4 ring-yellow-300' : 'bg-yellow-500') : 'bg-green-500');
                
                return `
                <div onclick="handleSlotAction(\${slot.id}, '\${slot.slotNumber}', \${slot.occupied}, \${slot.bookedByStaff}, \${slot.staffId})" 
                     class="parking-slot \${isDetailed ? 'p-6' : 'h-12'} \${statusColor} rounded-2xl flex flex-col items-center justify-center cursor-pointer shadow-sm text-white">
                    \${slot.occupied ? '<i data-lucide="car" class="w-5 h-5"></i>' : (slot.bookedByStaff ? (isMine ? '<i data-lucide="check-circle" class="w-5 h-5"></i>' : '<i data-lucide="bookmark" class="w-4 h-4"></i>') : `<span class="text-sm font-bold">\${slot.slotNumber}</span>`)}
                    \${isDetailed ? `<span class="text-[10px] mt-2 opacity-80 uppercase font-bold">\${slot.occupied ? 'Occupied' : (slot.bookedByStaff ? (isMine ? 'Your Slot' : 'Reserved') : 'Free')}</span>` : ''}
                </div>
            `}).join('');
        }

        function renderFeed(history) {
            const feed = document.getElementById('entry-feed');
            const active = history.filter(t => t.status === 'ACTIVE').slice(-5).reverse();
            feed.innerHTML = active.map(t => `<div class="border-l-2 border-green-500 pl-4"><b>\${t.vehicleNumber}</b><br><span class="text-xs text-gray-500 dark:text-slate-400">\${new Date(t.entryTime).toLocaleTimeString()}</span></div>`).join('') || 'No active sessions';
        }

        function renderHistory(history) {
            const body = document.getElementById('history-body');
            body.innerHTML = history.slice().reverse().map(t => `
                <tr class="hover:bg-white dark:hover:bg-slate-800/50 text-sm">
                    <td class="py-4 font-bold text-gray-700 dark:text-slate-200">
                        <div class="flex flex-col">
                            <span>\${t.vehicleNumber}</span>
                            <span class="text-[10px] text-blue-500 font-bold uppercase">\${t.vehicleType || 'Unknown'}</span>
                        </div>
                    </td>
                    <td class="py-4 text-gray-500 dark:text-slate-400">\${new Date(t.entryTime).toLocaleString()}</td>
                    <td class="py-4 text-gray-500 dark:text-slate-400">\${t.exitTime ? new Date(t.exitTime).toLocaleString() : '-'}</td>
                    <td class="py-4 text-gray-500 dark:text-slate-400 font-mono">\${t.slot ? t.slot.slotNumber : '-'}</td>
                    <td class="py-4 font-bold text-gray-800 dark:text-white">\${t.amount ? '$' + t.amount.toFixed(2) : '-'}</td>
                    <td class="py-4"><span class="px-2 py-1 rounded-full text-xs font-bold \${t.status === 'COMPLETED' ? 'bg-green-100 text-green-700' : 'bg-blue-100 text-blue-700'}">\${t.status}</span></td>
                </tr>
            `).join('');
        }

        async function renderMembers() {
            const res = await fetch(`\${API_BASE}/staff`);
            const members = await res.json();
            const body = document.getElementById('membership-body');
            body.innerHTML = members.map(m => `
                <tr class="hover:bg-white dark:hover:bg-slate-800/50 transition-colors">
                    <td class="py-4 font-bold text-gray-700 dark:text-slate-200">\${m.name}</td>
                    <td class="py-4 text-gray-500 dark:text-slate-400">\${m.department}</td>
                    <td class="py-4 text-gray-500 dark:text-slate-400 font-mono">\${m.username}</td>
                    <td class="py-4 text-gray-500 dark:text-slate-400">
                        <div class="flex flex-wrap gap-1">
                            \${(m.accessibleTabs || []).map(t => `<span class="px-1.5 py-0.5 bg-blue-100 dark:bg-blue-900/50 text-blue-700 dark:text-blue-300 rounded text-[10px] uppercase font-bold border border-blue-200 dark:border-blue-800">\${t}</span>`).join('') || '<span class="text-gray-400">None</span>'}
                        </div>
                    </td>
                    <td class="py-4"><button onclick="deleteMember(\${m.id})" class="text-red-500 hover:text-red-700"><i data-lucide="trash-2" class="w-4 h-4"></i></button></td>
                </tr>
            `).join('');
            lucide.createIcons();
        }

        async function showAddMemberModal() {
            const { value: v } = await Swal.fire({
                title: 'Add Staff Member',
                html: `
                    <input id="n" class="swal2-input" placeholder="Full Name">
                    <input id="s" class="swal2-input" placeholder="Department">
                    <input id="u" class="swal2-input" placeholder="Username">
                    <input id="p" type="password" class="swal2-input" placeholder="Password">
                    <div class="mt-4 text-left">
                        <p class="text-sm font-bold mb-2">Accessible Tabs:</p>
                        <div class="grid grid-cols-2 gap-2 text-sm">
                            <label><input type="checkbox" name="tabs" value="dashboard" checked> Dashboard</label>
                            <label><input type="checkbox" name="tabs" value="slots" checked> Slots</label>
                            <label><input type="checkbox" name="tabs" value="history" checked> Analytics</label>
                            <label><input type="checkbox" name="tabs" value="membership"> Staff Management</label>
                            <label><input type="checkbox" name="tabs" value="settings"> Settings</label>
                        </div>
                    </div>
                `,
                preConfirm: () => {
                    const popup = Swal.getPopup();
                    const name = popup.querySelector('#n').value.trim();
                    const department = popup.querySelector('#s').value.trim();
                    const username = popup.querySelector('#u').value.trim();
                    const password = popup.querySelector('#p').value.trim();
                    const selectedTabs = Array.from(popup.querySelectorAll('input[name="tabs"]:checked')).map(cb => cb.value);

                    if (!name || !department || !username || !password) {
                        Swal.showValidationMessage('Please fill in all staff details');
                        return false;
                    }

                    return { 
                        name, 
                        department, 
                        username, 
                        password,
                        accessibleTabs: selectedTabs
                    }
                },
                background: document.documentElement.classList.contains('dark') ? '#1e293b' : '#fff',
                color: document.documentElement.classList.contains('dark') ? '#fff' : '#000'
            });
            if (v) {
                const res = await fetch(`\${API_BASE}/staff`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(v) });
                if (res.ok) { Swal.fire('Success', '', 'success'); renderMembers(); }
                else Swal.fire('Error', await res.text(), 'error');
            }
        }

        async function deleteMember(id) {
            if ((await Swal.fire({ title: 'Delete?', icon: 'warning', showCancelButton: true, background: document.documentElement.classList.contains('dark') ? '#1e293b' : '#fff', color: document.documentElement.classList.contains('dark') ? '#fff' : '#000' })).isConfirmed) {
                await fetch(`\${API_BASE}/staff/\${id}`, { method: 'DELETE' });
                renderMembers();
            }
        }

        function handleSlotAction(id, number, occupied, booked, staffId) {
            if (role === 'ADMIN') {
                if (occupied) handleExit(id, number);
                else if (booked) {
                    Swal.fire({
                        title: `Reserved Slot: \${number}`,
                        text: 'This slot is reserved by a staff member.',
                        icon: 'info',
                        showCancelButton: true,
                        showDenyButton: true,
                        confirmButtonText: 'Release Reservation',
                        denyButtonText: 'Force Park Vehicle',
                        cancelButtonText: 'Cancel',
                        confirmButtonColor: '#ef4444',
                        denyButtonColor: '#3b82f6',
                        background: document.documentElement.classList.contains('dark') ? '#1e293b' : '#fff',
                        color: document.documentElement.classList.contains('dark') ? '#fff' : '#000'
                    }).then((result) => {
                        if (result.isConfirmed) {
                            fetch(`\${API_BASE}/staff/unbook/\${id}/0`, { method: 'POST' }).then(res => { if (res.ok) { loadData(); Swal.fire('Released', '', 'success'); } });
                        } else if (result.isDenied) handleEntrySpecific(id, number);
                    });
                } else {
                    Swal.fire({
                        title: `Slot \${number}`,
                        text: 'Choose an action:',
                        icon: 'question',
                        showCancelButton: true,
                        showDenyButton: true,
                        confirmButtonText: 'Reserve for Member',
                        denyButtonText: 'Park a Vehicle',
                        cancelButtonText: 'Cancel',
                        confirmButtonColor: '#eab308',
                        denyButtonColor: '#10b981',
                        background: document.documentElement.classList.contains('dark') ? '#1e293b' : '#fff',
                        color: document.documentElement.classList.contains('dark') ? '#fff' : '#000'
                    }).then((result) => {
                        if (result.isConfirmed) handleAdminReserve(id, number);
                        else if (result.isDenied) handleEntrySpecific(id, number);
                    });
                }
            } else if (role === 'STAFF') {
                if (occupied) handleExit(id, number);
                else if (booked) {
                    if (staffId == userId) {
                        Swal.fire({
                            title: `Your Reserved Slot: \${number}`,
                            text: 'What would you like to do?',
                            icon: 'question',
                            showCancelButton: true,
                            showDenyButton: true,
                            confirmButtonText: 'Release Reservation',
                            denyButtonText: 'Park a Vehicle',
                            cancelButtonText: 'Cancel',
                            confirmButtonColor: '#ef4444',
                            denyButtonColor: '#10b981',
                            background: document.documentElement.classList.contains('dark') ? '#1e293b' : '#fff',
                            color: document.documentElement.classList.contains('dark') ? '#fff' : '#000'
                        }).then((result) => {
                            if (result.isConfirmed) toggleBooking(id, true);
                            else if (result.isDenied) handleEntrySpecific(id, number);
                        });
                    } else Swal.fire({ title: 'Reserved', text: 'Slot reserved by another staff.', icon: 'info', background: document.documentElement.classList.contains('dark') ? '#1e293b' : '#fff', color: document.documentElement.classList.contains('dark') ? '#fff' : '#000' });
                } else {
                    Swal.fire({
                        title: `Slot \${number}`,
                        text: 'Choose an action:',
                        icon: 'question',
                        showCancelButton: true,
                        showDenyButton: true,
                        confirmButtonText: 'Reserve for Member',
                        denyButtonText: 'Park a Vehicle',
                        cancelButtonText: 'Cancel',
                        confirmButtonColor: '#eab308',
                        denyButtonColor: '#10b981',
                        background: document.documentElement.classList.contains('dark') ? '#1e293b' : '#fff',
                        color: document.documentElement.classList.contains('dark') ? '#fff' : '#000'
                    }).then((result) => {
                        if (result.isConfirmed) handleAdminReserve(id, number);
                        else if (result.isDenied) handleEntrySpecific(id, number);
                    });
                }
            }
        }

        async function askReservationType(slotId, slotNumber) {
            const { value: type } = await Swal.fire({
                title: 'Reservation Type',
                text: 'Reserve this slot for:',
                input: 'select',
                inputOptions: {
                    'STAFF': 'Staff Member',
                    'MEMBER': 'Monthly Member'
                },
                showCancelButton: true,
                confirmButtonColor: '#eab308',
                background: document.documentElement.classList.contains('dark') ? '#1e293b' : '#fff',
                color: document.documentElement.classList.contains('dark') ? '#fff' : '#000'
            });

            if (type) {
                handleAdminReserve(slotId, slotNumber, type);
            }
        }

        async function handleAdminReserve(slotId, slotNumber, forceType = null) {
            try {
                // Determine type if not provided
                let type = forceType;
                if (!type) {
                    const result = await Swal.fire({
                        title: 'Select User Type',
                        input: 'select',
                        inputOptions: { 'STAFF': 'Staff Member', 'MEMBER': 'Monthly Member' },
                        confirmButtonColor: '#eab308'
                    });
                    if (!result.value) return;
                    type = result.value;
                }

                const [staffRes, membersRes] = await Promise.all([
                    fetch(`\${API_BASE}/staff`),
                    fetch(`\${API_BASE}/admin/members`)
                ]);
                const staffList = await staffRes.json();
                const membersList = await membersRes.json();
                
                const list = type === 'STAFF' ? staffList : membersList;
                if (list.length === 0) {
                    Swal.fire({ title: 'No Users', text: `No \${type.toLowerCase()}s found.`, icon: 'info' });
                    return;
                }

                const options = {};
                list.forEach(u => options[u.id] = u.name);

                const { value: id } = await Swal.fire({
                    title: `Reserve for \${type}`,
                    input: 'select',
                    inputOptions: options,
                    confirmButtonColor: '#eab308'
                });

                if (id) {
                    const bookRes = await fetch(`\${API_BASE}/book/\${slotId}/\${id}/\${type}`, { method: 'POST' });
                    if (bookRes.ok) {
                        Swal.fire({ icon: 'success', title: 'Reserved!', timer: 1500 });
                        loadData();
                    } else {
                        Swal.fire({ title: 'Error', text: await bookRes.text(), icon: 'error' });
                    }
                }
            } catch (err) {
                console.error(err);
                Swal.fire({ title: 'Error', text: 'Operation failed', icon: 'error' });
            }
        }

        async function handleEntrySpecific(slotId, slotNumber) {
            const { value: v } = await Swal.fire({
                title: 'Vehicle Entry',
                text: `Parking into Slot \${slotNumber}`,
                html: `
                    <div class="space-y-4">
                        <input id="vn" class="swal2-input !m-0 !w-full" placeholder="Number Plate (e.g. ABC1234)">
                        <select id="vt" class="swal2-input !m-0 !w-full">
                            <option value="CAR">Car</option>
                            <option value="BIKE">Bike</option>
                            <option value="VAN">Van</option>
                        </select>
                    </div>
                `,
                preConfirm: () => {
                    const vn = document.getElementById('vn').value.toUpperCase();
                    const vt = document.getElementById('vt').value;
                    if (!vn) {
                        Swal.showValidationMessage('Vehicle number is required');
                        return false;
                    }
                    return { vehicleNumber: vn, vehicleType: vt };
                },
                showCancelButton: true,
                background: document.documentElement.classList.contains('dark') ? '#1e293b' : '#fff',
                color: document.documentElement.classList.contains('dark') ? '#fff' : '#000'
            });

            if (v) {
                const res = await fetch(`\${API_BASE}/tickets/entry`, { 
                    method: 'POST', 
                    headers: { 'Content-Type': 'application/json' }, 
                    body: JSON.stringify({ ...v, preferredSlotId: slotId }) 
                });
                if (res.ok) { Swal.fire({ icon: 'success', title: 'Parked!', timer: 1000, showConfirmButton: false }); loadData(); }
                else Swal.fire('Error', await res.text(), 'error');
            }
        }

        async function toggleBooking(slotId, isCurrentlyBooked) {
            const endpoint = isCurrentlyBooked ? `\${API_BASE}/staff/unbook/\${slotId}/\${userId}` : `\${API_BASE}/staff/book/\${slotId}/\${userId}`;
            const res = await fetch(endpoint, { method: 'POST' });
            if (res.ok) { Swal.fire({ icon: 'success', title: isCurrentlyBooked ? 'Released' : 'Reserved', timer: 1000, showConfirmButton: false, background: document.documentElement.classList.contains('dark') ? '#1e293b' : '#fff', color: document.documentElement.classList.contains('dark') ? '#fff' : '#000' }); loadData(); }
        }

        async function handleEntry() {
            const { value: v } = await Swal.fire({
                title: 'Vehicle Entry',
                html: `
                    <div class="space-y-4">
                        <input id="vn" class="swal2-input !m-0 !w-full" placeholder="Number Plate (e.g. ABC1234)">
                        <select id="vt" class="swal2-input !m-0 !w-full">
                            <option value="CAR">Car</option>
                            <option value="BIKE">Bike</option>
                            <option value="VAN">Van</option>
                        </select>
                    </div>
                `,
                preConfirm: () => {
                    const vn = document.getElementById('vn').value.toUpperCase();
                    const vt = document.getElementById('vt').value;
                    if (!vn) {
                        Swal.showValidationMessage('Vehicle number is required');
                        return false;
                    }
                    return { vehicleNumber: vn, vehicleType: vt };
                },
                showCancelButton: true,
                background: document.documentElement.classList.contains('dark') ? '#1e293b' : '#fff',
                color: document.documentElement.classList.contains('dark') ? '#fff' : '#000'
            });

            if (v) {
                const res = await fetch(`\${API_BASE}/tickets/entry`, { 
                    method: 'POST', 
                    headers: { 'Content-Type': 'application/json' }, 
                    body: JSON.stringify(v) 
                });
                if (res.ok) { Swal.fire({ icon: 'success', title: 'Parked!', timer: 1000, showConfirmButton: false }); loadData(); }
                else Swal.fire('Error', await res.text(), 'error');
            }
        }

        async function handleExit(id, number) {
            if ((await Swal.fire({ title: 'Process Exit?', text: `Slot \${number}`, showCancelButton: true, background: document.documentElement.classList.contains('dark') ? '#1e293b' : '#fff', color: document.documentElement.classList.contains('dark') ? '#fff' : '#000' })).isConfirmed) {
                const res = await fetch(`\${API_BASE}/tickets/exit/\${id}`, { method: 'POST' });
                if (res.ok) { 
                    const t = await res.json(); 
                    loadData();
                    
                    Swal.fire({
                        title: 'Exit Processed',
                        text: `Total Fee: $\${t.amount.toFixed(2)}`,
                        icon: 'success',
                        showCancelButton: true,
                        confirmButtonText: 'Print Receipt',
                        cancelButtonText: 'Close',
                        background: document.documentElement.classList.contains('dark') ? '#1e293b' : '#fff',
                        color: document.documentElement.classList.contains('dark') ? '#fff' : '#000'
                    }).then((result) => {
                        if (result.isConfirmed) {
                            generateReceiptPDF(t);
                        }
                    });
                }
            }
        }

        function generateReceiptPDF(t) {
            const { jsPDF } = window.jspdf;
            const doc = new jsPDF({
                unit: 'mm',
                format: [80, 150] // Receipt size
            });

            doc.setFontSize(16);
            doc.setTextColor(37, 99, 235);
            doc.text("ParkMaster Pro", 40, 15, { align: 'center' });
            
            doc.setFontSize(10);
            doc.setTextColor(0, 0, 0);
            doc.text("PARKING RECEIPT", 40, 22, { align: 'center' });
            doc.line(10, 25, 70, 25);

            let y = 35;
            const line = (label, val) => {
                doc.setFont("helvetica", "bold");
                doc.text(label + ":", 12, y);
                doc.setFont("helvetica", "normal");
                doc.text(String(val), 40, y);
                y += 8;
            };

            line("Vehicle No", t.vehicleNumber);
            line("Type", t.vehicleType);
            line("Slot", t.slot ? t.slot.slotNumber : '-');
            line("Entry", new Date(t.entryTime).toLocaleString());
            line("Exit", new Date(t.exitTime).toLocaleString());
            
            y += 2;
            doc.line(10, y, 70, y);
            y += 10;
            
            doc.setFontSize(14);
            doc.setFont("helvetica", "bold");
            doc.text(`TOTAL FEE: $\${t.amount.toFixed(2)}`, 40, y, { align: 'center' });
            
            y += 15;
            doc.setFontSize(12);
            doc.setTextColor(100, 116, 139);
            doc.text("Thank you", 40, y, { align: 'center' });
            
            doc.save(`receipt_\${t.vehicleNumber}_\${Date.now()}.pdf`);
        }

        async function updateRate() {
            const r = document.getElementById('input-hourly-rate').value;
            await fetch(`\${API_BASE}/admin/rate`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ rate: parseFloat(r) }) });
            Swal.fire({ title: 'Updated', icon: 'success', background: document.documentElement.classList.contains('dark') ? '#1e293b' : '#fff', color: document.documentElement.classList.contains('dark') ? '#fff' : '#000' });
        }

        async function updateSlotCount() {
            const c = document.getElementById('input-slot-count').value;
            const res = await fetch(`\${API_BASE}/admin/slots/count`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ count: parseInt(c) }) });
            if (res.ok) { Swal.fire({ title: 'Updated', icon: 'success', background: document.documentElement.classList.contains('dark') ? '#1e293b' : '#fff', color: document.documentElement.classList.contains('dark') ? '#fff' : '#000' }); loadData(); }
        }

        async function changeAdminPassword() {
            const { value: p } = await Swal.fire({ title: 'Change Admin Password', input: 'password', showCancelButton: true, inputPlaceholder: 'Enter new password', background: document.documentElement.classList.contains('dark') ? '#1e293b' : '#fff', color: document.documentElement.classList.contains('dark') ? '#fff' : '#000' });
            if (p) {
                const res = await fetch(`\${API_BASE}/admin/password`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ password: p }) });
                if (res.ok) Swal.fire({ title: 'Success', icon: 'success', background: document.documentElement.classList.contains('dark') ? '#1e293b' : '#fff', color: document.documentElement.classList.contains('dark') ? '#fff' : '#000' });
            }
        }

        async function downloadReport() {
            try {
                const response = await fetch(`\${API_BASE}/admin/report/daily`);
                const data = await response.json();
                const { jsPDF } = window.jspdf;
                const doc = new jsPDF();
                doc.setFontSize(22);
                doc.setTextColor(37, 99, 235);
                doc.text("ParkMaster Pro | Daily Report", 105, 20, { align: 'center' });
                doc.autoTable({
                    startY: 50,
                    head: [['Metric', 'Value']],
                    body: [['Total Revenue', `$\${data.revenue.toFixed(2)}`], ['Vehicle Entries', data.entries.toString()], ['Vehicle Exits', data.exits.toString()], ['Staff Logins', data.logins.length.toString()]],
                    theme: 'striped',
                    headStyles: { fillColor: [37, 99, 235] }
                });
                doc.save(`daily_report_\${data.date}.pdf`);
            } catch (err) { console.error(err); }
        }

        function toggleDarkMode() {
            document.documentElement.classList.toggle('dark');
            localStorage.setItem('theme', document.documentElement.classList.contains('dark') ? 'dark' : 'light');
            lucide.createIcons();
        }

        function logout() { sessionStorage.clear(); window.location.href = 'login'; }

        // Initial tab load based on permissions
        applyPermissions();
        if (role === 'ADMIN') {
            switchTab('dashboard');
        } else if (accessibleTabs && accessibleTabs.length > 0) {
            switchTab(accessibleTabs[0]);
        } else {
            switchTab('dashboard'); // Fallback
        }

        setInterval(loadData, 30000);
        lucide.createIcons();
    </script>
</body>
</html>
