const fs = require('fs');
const readline = require('readline');

/**
 * MP19 – Generate dataset summary report.
 * Student: CELADEZ, JED CEDRIC G.
 */

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

rl.question('Enter dataset file path: ', (filePath) => {
    if (!fs.existsSync(filePath)) {
        console.error('Error: File not found.');
        rl.close();
        return;
    }

    try {
        const fileContent = fs.readFileSync(filePath, 'utf8');
        const lines = fileContent.split(/\r?\n/);
        
        let headerFound = false;
        let totalRecords = 0;
        let columnCount = 0;
        let examColIndex = -1;
        let resultColIndex = -1;

        const examCounts = {};
        const resultCounts = {};

        for (let line of lines) {
            if (line.trim() === "" || (!headerFound && !line.includes("Candidate"))) {
                continue;
            }

            const cells = parseCsvLine(line);

            if (!headerFound) {
                columnCount = cells.length;
                cells.forEach((h, i) => {
                    const header = h.trim().toLowerCase();
                    if (header === 'exam') examColIndex = i;
                    if (header === 'result') resultColIndex = i;
                });
                headerFound = true;
                continue;
            }

            totalRecords++;

            if (examColIndex !== -1 && cells[examColIndex]) {
                const exam = cells[examColIndex].trim();
                examCounts[exam] = (examCounts[exam] || 0) + 1;
            }

            if (resultColIndex !== -1 && cells[resultColIndex]) {
                const result = cells[resultColIndex].trim();
                resultCounts[result] = (resultCounts[result] || 0) + 1;
            }
        }

        // Print Summary
        console.log(`\n========= DATASET SUMMARY REPORT =========`);
        console.log(`Total Records: ${totalRecords}`);
        console.log(`Number of Columns: ${columnCount}`);
        
        console.log(`\n--- Pass/Fail Distribution ---`);
        Object.keys(resultCounts).forEach(res => {
            console.log(`${res}: ${resultCounts[res]}`);
        });

        console.log(`\n--- Most Popular Exams ---`);
        Object.entries(examCounts)
            .sort((a, b) => b[1] - a[1])
            .slice(0, 5)
            .forEach(([exam, count]) => {
                console.log(`${exam}: ${count} candidates`);
            });

    } catch (err) {
        console.error('Error processing file:', err.message);
    } finally {
        rl.close();
    }
});

function parseCsvLine(line) {
    const cells = [];
    let current = '';
    let inQuotes = false;

    for (let i = 0; i < line.length; i++) {
        const ch = line[i];

        if (ch === '"') {
            if (inQuotes && line[i + 1] === '"') {
                current += '"';
                i++;
            } else {
                inQuotes = !inQuotes;
            }
        } else if (ch === ',' && !inQuotes) {
            cells.push(current);
            current = '';
        } else {
            current += ch;
        }
    }

    cells.push(current);
    return cells;
}
