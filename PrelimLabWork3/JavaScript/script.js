document.getElementById('calculate-btn').addEventListener('click', () => {
    // 1. Get Elements
    const attendanceInput = document.getElementById('attendance');
    const lab1Input = document.getElementById('lab1');
    const lab2Input = document.getElementById('lab2');
    const lab3Input = document.getElementById('lab3');
    const errorMsg = document.getElementById('error-msg');

    // 2. Parse Values
    const attendance = parseFloat(attendanceInput.value);
    const lab1 = parseFloat(lab1Input.value);
    const lab2 = parseFloat(lab2Input.value);
    const lab3 = parseFloat(lab3Input.value);

    // 3. Inline Validation
    if (isNaN(attendance) || isNaN(lab1) || isNaN(lab2) || isNaN(lab3)) {
        errorMsg.classList.remove('hidden');
        return;
    }

    // Hide error if valid
    errorMsg.classList.add('hidden');

    // 4. Computation
    // Lab Average
    const labAverage = (lab1 + lab2 + lab3) / 3;

    // Class Standing: 40% Attendance + 60% Lab Avg
    const classStanding = (attendance * 0.40) + (labAverage * 0.60);

    // Required Scores Calculation
    // Formula: Exam = (Target - (CS * 0.3)) / 0.7
    const targetPass = 75;
    const targetExcellent = 100;

    const requiredPass = (targetPass - (classStanding * 0.3)) / 0.7;
    const requiredExcellent = (targetExcellent - (classStanding * 0.3)) / 0.7;

    // 5. Update UI - Input Summary
    const summaryList = document.getElementById('input-summary-list');
    summaryList.innerHTML = `
        <li><span>Attendance:</span> <strong>${attendance.toFixed(2)}</strong></li>
        <li><span>Lab 1:</span> <strong>${lab1.toFixed(2)}</strong></li>
        <li><span>Lab 2:</span> <strong>${lab2.toFixed(2)}</strong></li>
        <li><span>Lab 3:</span> <strong>${lab3.toFixed(2)}</strong></li>
    `;

    // 6. Update UI - Computed Values
    document.getElementById('res-lab-avg').textContent = labAverage.toFixed(2);
    document.getElementById('res-class-standing').textContent = classStanding.toFixed(2);
    document.getElementById('req-pass').textContent = requiredPass.toFixed(2);
    document.getElementById('req-excellent').textContent = requiredExcellent.toFixed(2);

    // 7. Remarks Logic (Matched to Java version)
    let remarks = "";

    // Pass Remarks
    if (requiredPass <= 0) {
        remarks = "You have <strong>ALREADY PASSED</strong> based on Class Standing!";
    } else if (requiredPass > 100) {
        remarks = "Passing (75) is <strong>mathematically impossible</strong> this period.";
    } else {
        remarks = `You need <strong>${requiredPass.toFixed(2)}</strong> on the exam to PASS.`;
    }

    // Excellent Remarks
    if (requiredExcellent <= 0) {
        remarks += "<br>You are guaranteed an <strong>EXCELLENT</strong> grade!";
    } else if (requiredExcellent <= 100) {
        remarks += `<br>You need <strong>${requiredExcellent.toFixed(2)}</strong> on the exam for EXCELLENT standing.`;
    } else {
        remarks += "<br>Excellent standing (100) is <strong>not reachable</strong> this period.";
    }

    const remarksElement = document.getElementById('remarks-text');
    remarksElement.innerHTML = remarks;

    // Show results with animation
    const resultsPanel = document.getElementById('results');
    resultsPanel.classList.remove('hidden');
    resultsPanel.scrollIntoView({ behavior: 'smooth' });
});

// Optional: Enter key listener
document.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
        document.getElementById('calculate-btn').click();
    }
});