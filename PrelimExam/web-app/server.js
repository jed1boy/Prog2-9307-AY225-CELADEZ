const express = require('express');
const fs = require('fs');
const path = require('path');
const cors = require('cors');

const app = express();
const PORT = 3000;
const CSV_FILE = path.join(__dirname, '..', 'data', 'class_records.csv');
const { exec } = require('child_process');

app.use(cors());
app.use(express.json({ limit: '50mb' }));
app.use(express.urlencoded({ limit: '50mb', extended: true }));
app.use(express.static(__dirname));

// Get all records
app.get('/api/records', (req, res) => {
    try {
        const data = fs.readFileSync(CSV_FILE, 'utf-8');
        const lines = data.split('\n').filter(line => line.trim());
        const headers = lines[0].split(',');
        
        const records = [];
        for (let i = 1; i < lines.length; i++) {
            const values = lines[i].split(',');
            if (values.length === headers.length) {
                const record = {};
                headers.forEach((header, index) => {
                    record[header.trim()] = values[index].trim();
                });
                records.push(record);
            }
        }
        
        res.json(records);
    } catch (error) {
        res.status(500).json({ error: 'Error reading CSV file' });
    }
});

// Save all records
app.post('/api/records', (req, res) => {
    try {
        const records = req.body;
        let csvContent = 'StudentID,first_name,last_name,LAB WORK 1,LAB WORK 2,LAB WORK 3,PRELIM EXAM,ATTENDANCE GRADE\n';
        
        records.forEach(record => {
            csvContent += `${record.StudentID},${record.first_name},${record.last_name},${record['LAB WORK 1']},${record['LAB WORK 2']},${record['LAB WORK 3']},${record['PRELIM EXAM']},${record['ATTENDANCE GRADE']}\n`;
        });
        
        fs.writeFileSync(CSV_FILE, csvContent, 'utf-8');
        res.json({ success: true });
    } catch (error) {
        res.status(500).json({ error: 'Error writing CSV file' });
    }
});

app.listen(PORT, () => {
    const url = `http://localhost:${PORT}`;
    console.log(`Server running at ${url}`);
    
    // Automatically open browser
    const start = (process.platform === 'darwin' ? 'open' : process.platform === 'win32' ? 'start' : 'xdg-open');
    exec(`${start} ${url}`);
});
