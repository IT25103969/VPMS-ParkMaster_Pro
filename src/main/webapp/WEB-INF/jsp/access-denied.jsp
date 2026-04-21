<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Access Denied | ParkMaster Pro</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <script src="https://unpkg.com/lucide@latest"></script>
</head>
<body class="bg-slate-50 min-h-screen flex items-center justify-center p-6 font-['Inter']">
    <div class="max-w-md w-full text-center">
        <div class="mb-8 flex justify-center">
            <div class="bg-red-100 p-6 rounded-full">
                <i data-lucide="shield-alert" class="w-16 h-16 text-red-600"></i>
            </div>
        </div>
        <h1 class="text-4xl font-bold text-slate-900 mb-4">Access Denied</h1>
        <p class="text-slate-600 mb-10 text-lg">Oops! You don't have the required permissions to view this area. If you believe this is an error, please contact your administrator.</p>
        
        <div class="space-y-4">
            <a href="./" class="block w-full bg-blue-600 text-white py-4 rounded-xl font-bold text-lg hover:bg-blue-700 transition-all shadow-lg">
                Return to Dashboard
            </a>
            <button onclick="window.history.back()" class="block w-full bg-white text-slate-700 border border-slate-200 py-4 rounded-xl font-bold text-lg hover:bg-slate-50 transition-all">
                Go Back
            </button>
        </div>
        
        <div class="mt-12">
            <p class="text-slate-400 text-sm font-medium uppercase tracking-widest">ParkMaster Pro Security</p>
        </div>
    </div>
    <script>
        lucide.createIcons();
    </script>
</body>
</html>
