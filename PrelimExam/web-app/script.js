// Part 2: JavaScript (Web)
// Hardcoded CSV content (fallback if server is not running)
const csvContent = `StudentID,first_name,last_name,LAB WORK 1,LAB WORK 2,LAB WORK 3,PRELIM EXAM,ATTENDANCE GRADE
073900438,Osbourne,Wakenshaw,69,5,52,12,78
114924014,Albie,Gierardi,58,92,16,57,97
111901632,Eleen,Pentony,43,81,34,36,16`;

let records = [];
const API_URL = 'http://localhost:3000/api/records';

// Load from server
async function initData() {
    try {
        const response = await fetch(API_URL);
        if (response.ok) {
            records = await response.json();
        } else {
            // Fallback to hardcoded CSV if server is not available
            loadFromCSV();
        }
    } catch (error) {
        console.error('Server not available, using fallback data');
        loadFromCSV();
    }
}

// Fallback: Load from hardcoded CSV
function loadFromCSV() {
    const lines = csvContent.split('\n');
    const headers = lines[0].split(',');
    
    for (let i = 1; i < lines.length; i++) {
        const values = lines[i].split(',');
        if (values.length === headers.length) {
            let record = {};
            headers.forEach((header, index) => {
                record[header.trim()] = values[index].trim();
            });
            records.push(record);
        }
    }
}

// Save to server
async function saveData() {
    try {
        await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(records)
        });
    } catch (error) {
        console.error('Error saving to server:', error);
    }
}

// Render function
function render() {
    const tableBody = document.getElementById('table-body');
    tableBody.innerHTML = ''; // Clear table

    records.forEach((record, index) => {
        const row = `
            <tr>
                <td>${record.StudentID}</td>
                <td>${record.first_name}</td>
                <td>${record.last_name}</td>
                <td>${record['LAB WORK 1']}</td>
                <td>${record['LAB WORK 2']}</td>
                <td>${record['LAB WORK 3']}</td>
                <td>${record['PRELIM EXAM']}</td>
                <td>${record['ATTENDANCE GRADE']}</td>
                <td><button onclick="deleteRecord(${index})">Delete</button></td>
            </tr>
        `;
        tableBody.insertAdjacentHTML('beforeend', row);
    });
}

// Add new record
document.getElementById('record-form').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const studentID = document.getElementById('record-studentid').value;
    const firstName = document.getElementById('record-firstname').value;
    const lastName = document.getElementById('record-lastname').value;
    const lab1 = document.getElementById('record-lab1').value;
    const lab2 = document.getElementById('record-lab2').value;
    const lab3 = document.getElementById('record-lab3').value;
    const prelim = document.getElementById('record-prelim').value;
    const attendance = document.getElementById('record-attendance').value;

    if (studentID && firstName && lastName && lab1 && lab2 && lab3 && prelim && attendance) {
        records.push({ 
            StudentID: studentID, 
            first_name: firstName, 
            last_name: lastName,
            'LAB WORK 1': lab1,
            'LAB WORK 2': lab2,
            'LAB WORK 3': lab3,
            'PRELIM EXAM': prelim,
            'ATTENDANCE GRADE': attendance
        });
        await saveData();
        render();
        this.reset();
    }
});

// Delete record
async function deleteRecord(index) {
    records.splice(index, 1);
    await saveData();
    render();
}

// Initial setup
(async function() {
    await initData();
    render();
})();
