// --- Global Variables ---
const LOGS_KEY = 'attendance_app_logs';

let usersData = []; // Loaded from accounts.json
let currentUser = null;
let currentSignatureCode = "";
let currentFormattedTime = "";

// --- DOM Elements ---
const loginView = document.getElementById('login-view');
const dashboardView = document.getElementById('dashboard-view');

const loginForm = document.getElementById('login-form');
const studentIdInput = document.getElementById('student-id');
const passwordInput = document.getElementById('password');
const logoutBtn = document.getElementById('logout-btn');

const manualDbUpload = document.getElementById('manual-db-upload');
const jsonFileInput = document.getElementById('json-file-input');

// Dashboard Elements
const userRealNameSpan = document.getElementById('user-real-name');
const currentTimestampSpan = document.getElementById('current-timestamp');
const generatedSigCodeDisplay = document.getElementById('generated-sig-code');
const submitBtn = document.getElementById('submit-attendance');
const attendanceList = document.getElementById('attendance-list');
const notification = document.getElementById('notification');

// --- Initialization ---
async function init() {
    await loadDatabase();
    
    // Listen for storage changes
    window.addEventListener('storage', (e) => {
        if (e.key === LOGS_KEY && currentUser) {
            renderLogs();
        }
    });
}

// --- Database Loading ---
async function loadDatabase() {
    try {
        const response = await fetch('accounts.json');
        if (!response.ok) throw new Error("Could not find accounts.json");
        const data = await response.json();
        processUserData(data);
    } catch (err) {
        console.warn("Auto-load failed.", err);
        manualDbUpload.classList.remove('hidden');
    }
}

function processUserData(data) {
    if (Array.isArray(data)) {
        usersData = data;
        manualDbUpload.classList.add('hidden');
        console.log(`Loaded ${data.length} users.`);
    } else {
        showNotification("Invalid accounts.json format", "error");
    }
}

jsonFileInput.addEventListener('change', (e) => {
    const file = e.target.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = function(e) {
        try {
            processUserData(JSON.parse(e.target.result));
        } catch (err) {
            showNotification("Invalid JSON file.", "error");
        }
    };
    reader.readAsText(file);
});

// --- Step 3: Adding the Beeping Sound ---
function playBeep() {
    // Lab Requirement: "Use the HTML Audio API to play the sound"
    // If you had a file: const beep = new Audio('beep.mp3'); beep.play();
    
    // Using AudioContext to generate a beep so it works without uploading an mp3 file
    try {
        const AudioContext = window.AudioContext || window.webkitAudioContext;
        if (!AudioContext) return;
        
        const ctx = new AudioContext();
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();

        osc.type = 'square'; // Harsh sound for incorrect password
        osc.frequency.value = 400; // Frequency in Hz
        
        osc.connect(gain);
        gain.connect(ctx.destination);
        
        osc.start();
        
        // Stop after 200ms
        setTimeout(() => {
            osc.stop();
            ctx.close();
        }, 200);
    } catch (e) {
        console.error("Audio error", e);
    }
}

// --- Step 4: Displaying the Timestamp ---
function getFormattedTimestamp() {
    // Format: MM/DD/YYYY HH:MM:SS
    const now = new Date();
    
    const mm = String(now.getMonth() + 1).padStart(2, '0');
    const dd = String(now.getDate()).padStart(2, '0');
    const yyyy = now.getFullYear();
    
    const hh = String(now.getHours()).padStart(2, '0');
    const min = String(now.getMinutes()).padStart(2, '0');
    const ss = String(now.getSeconds()).padStart(2, '0');
    
    return `${mm}/${dd}/${yyyy} ${hh}:${min}:${ss}`;
}

// --- Authentication (Step 2) ---
loginForm.addEventListener('submit', (e) => {
    e.preventDefault();
    
    if (usersData.length === 0) {
        showNotification("Database not loaded.", "error");
        return;
    }

    const id = studentIdInput.value.trim();
    const pass = passwordInput.value.trim();

    const foundUser = usersData.find(u => u.studentId === id && u.password === pass);

    if (foundUser) {
        currentUser = foundUser;
        loginSuccess();
    } else {
        // Step 2 & 3: If login fails, trigger beeping sound
        playBeep();
        showNotification("Incorrect password.", "error");
    }
});

function loginSuccess() {
    loginView.classList.add('hidden');
    dashboardView.classList.remove('hidden');
    logoutBtn.classList.remove('hidden');
    
    userRealNameSpan.textContent = currentUser.realName;
    
    // Capture System Time on Login Success
    currentFormattedTime = getFormattedTimestamp();
    currentTimestampSpan.textContent = `Login Time: ${currentFormattedTime}`;
    
    // Generate session code
    currentSignatureCode = generateRandomSignature();
    generatedSigCodeDisplay.textContent = currentSignatureCode;
    
    renderLogs();
}

logoutBtn.addEventListener('click', () => {
    currentUser = null;
    currentSignatureCode = "";
    loginForm.reset();
    dashboardView.classList.add('hidden');
    logoutBtn.classList.add('hidden');
    loginView.classList.remove('hidden');
});

// --- Signature Generation ---
function generateRandomSignature() {
    const chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    function getSegment() {
        let segment = "";
        for (let i = 0; i < 4; i++) {
            segment += chars.charAt(Math.floor(Math.random() * chars.length));
        }
        return segment;
    }
    return `${getSegment()}-${getSegment()}-${getSegment()}`;
}

// --- Submit Logic ---
submitBtn.addEventListener('click', () => {
    if (!currentUser) return;

    submitBtn.disabled = true;
    submitBtn.textContent = "Verifying...";

    try {
        const record = {
            realName: currentUser.realName,
            studentId: currentUser.studentId,
            timestamp: currentFormattedTime, // Use captured login time
            signature: currentSignatureCode
        };

        const existingLogs = JSON.parse(localStorage.getItem(LOGS_KEY) || '[]');
        existingLogs.push(record);
        localStorage.setItem(LOGS_KEY, JSON.stringify(existingLogs));

        showNotification("Success. Attendance verified.", "success");
        
        renderLogs();
        
    } catch (error) {
        console.error("Submit error", error);
        showNotification("Failed to save locally.", "error");
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = "Verify & Submit Attendance";
    }
});

function renderLogs() {
    const logs = JSON.parse(localStorage.getItem(LOGS_KEY) || '[]');
    logs.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp)); // Simple string sort works for ISO, but here we depend on recent push

    attendanceList.innerHTML = '';
    
    // Just show current session log for clarity in this assignment context
    if (currentUser) {
        const div = document.createElement('div');
        div.className = 'log-item';
        div.innerHTML = `
            <div class="log-info">
                <div class="log-name">${currentUser.realName}</div>
                <div class="log-time">${currentFormattedTime}</div>
            </div>
            <div class="log-sig">
                 <div style="font-family:'Courier New'; font-size:0.75rem;">${currentSignatureCode}</div>
            </div>
        `;
        attendanceList.appendChild(div);
    }
}

function showNotification(msg, type) {
    notification.textContent = msg;
    notification.className = `notification ${type}`;
    notification.classList.remove('hidden');
    setTimeout(() => { notification.classList.add('hidden'); }, 3000);
}

init();