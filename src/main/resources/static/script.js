const API_BASE = '/api';

const slotsContainer = document.getElementById('slots-container');
const entryModal = document.getElementById('entry-modal');
const exitModal = document.getElementById('exit-modal');
const historySection = document.getElementById('history-section');
const historyBody = document.getElementById('history-body');

let selectedSlotId = null;

document.addEventListener('DOMContentLoaded', () => {
    fetchSlots();
    
    // Close buttons
    document.querySelectorAll('.close-btn').forEach(btn => {
        btn.onclick = () => {
            entryModal.classList.add('hidden');
            exitModal.classList.add('hidden');
        };
    });

    // Refresh
    document.getElementById('refresh-btn').onclick = fetchSlots;
    
    // History
    document.getElementById('show-history-btn').onclick = () => {
        historySection.classList.toggle('hidden');
        if (!historySection.classList.contains('hidden')) {
            fetchHistory();
        }
    };
    
    // Entry Form
    document.getElementById('entry-form').onsubmit = handleEntrySubmit;
    
    // Exit Confirm
    document.getElementById('confirm-exit-btn').onclick = handleExitConfirm;
    
    // Click outside to close
    window.onclick = (event) => {
        if (event.target == entryModal) entryModal.classList.add('hidden');
        if (event.target == exitModal) exitModal.classList.add('hidden');
    };
});

async function fetchSlots() {
    try {
        const res = await fetch(`${API_BASE}/slots`);
        const slots = await res.json();
        renderSlots(slots);
    } catch (err) {
        console.error('Error fetching slots:', err);
    }
}

function renderSlots(slots) {
    slotsContainer.innerHTML = '';
    slots.forEach(slot => {
        const div = document.createElement('div');
        // Handle Jackson boolean mapping (usually 'occupied')
        const isOccupied = slot.occupied !== undefined ? slot.occupied : slot.isOccupied;
        
        div.className = `slot ${isOccupied ? 'occupied' : 'free'}`; 
        div.textContent = slot.slotNumber;
        div.onclick = () => handleSlotClick(slot, isOccupied);
        slotsContainer.appendChild(div);
    });
}

function handleSlotClick(slot, isOccupied) {
    if (!isOccupied) {
        // Open generic entry modal (since backend picks the slot)
        entryModal.classList.remove('hidden');
        document.getElementById('vehicle-number').focus();
    } else {
        selectedSlotId = slot.id;
        document.getElementById('exit-slot-number').textContent = slot.slotNumber;
        exitModal.classList.remove('hidden');
    }
}

async function handleEntrySubmit(e) {
    e.preventDefault();
    const vehicleNumber = document.getElementById('vehicle-number').value;
    
    try {
        const res = await fetch(`${API_BASE}/tickets/entry`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ vehicleNumber })
        });
        
        if (res.ok) {
            entryModal.classList.add('hidden');
            document.getElementById('vehicle-number').value = '';
            fetchSlots(); // Refresh grid
            // Optional: Show success message or toast
        } else {
            const txt = await res.text();
            alert('Error: ' + txt);
        }
    } catch (err) {
        console.error(err);
        alert('Network Error');
    }
}

async function handleExitConfirm() {
    if (!selectedSlotId) return;
    
    try {
        const res = await fetch(`${API_BASE}/tickets/exit/${selectedSlotId}`, {
            method: 'POST'
        });
        
        if (res.ok) {
            const ticket = await res.json();
            alert(`Vehicle Exited.\nDuration: ${calculateDuration(ticket.entryTime, ticket.exitTime)}\nFee: $${ticket.amount}`);
            exitModal.classList.add('hidden');
            fetchSlots();
            if (!historySection.classList.contains('hidden')) fetchHistory();
        } else {
            const txt = await res.text();
            alert('Error: ' + txt);
        }
    } catch (err) {
        console.error(err);
        alert('Network Error');
    }
}

function calculateDuration(start, end) {
    const s = new Date(start);
    const e = new Date(end);
    const diffMs = e - s;
    const diffHrs = Math.floor(diffMs / 3600000);
    const diffMins = Math.round(((diffMs % 3600000) / 60000));
    return `${diffHrs}h ${diffMins}m`;
}

async function fetchHistory() {
    try {
        const res = await fetch(`${API_BASE}/tickets/history`);
        const tickets = await res.json();
        
        historyBody.innerHTML = '';
        // Sort by id desc
        tickets.sort((a, b) => b.id - a.id);
        
        tickets.forEach(t => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${t.id}</td>
                <td>${t.vehicleNumber}</td>
                <td>${t.slot ? t.slot.slotNumber : 'Released'}</td>
                <td>${new Date(t.entryTime).toLocaleString()}</td>
                <td>${t.exitTime ? new Date(t.exitTime).toLocaleString() : '-'}</td>
                <td>${t.amount ? '$' + t.amount : '-'}</td>
                <td>${t.status}</td>
            `;
            historyBody.appendChild(tr);
        });
    } catch (err) {
        console.error(err);
    }
}
