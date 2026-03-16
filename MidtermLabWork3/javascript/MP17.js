const fs = require('fs');
const readline = require('readline');

/**
 * MP17 – Find the longest text entry in dataset.
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
        
        let longestEntry = "";
        let longestRow = -1;
        let longestHeader = "";
        let headers = [];
        let headerFound = false;
        let currentRow = 0;

        for (let line of lines) {
            // Skip empty rows and rows that don't contain the anchor "Candidate"
            if (line.trim() === "" || (!headerFound && !line.includes("Candidate"))) {
                continue;
            }

            const cells = parseCsvLine(line);

            if (!headerFound) {
                headers = cells;
                headerFound = true;
                continue;
            }

            currentRow++;
            cells.forEach((cell, index) => {
                const trimmedCell = cell.trim();
                if (trimmedCell.length > longestEntry.length) {
                    longestEntry = trimmedCell;
                    longestRow = currentRow;
                    longestHeader = headers[index] || `Column ${index}`;
                }
            });
        }

        if (longestRow !== -1) {
            console.log("\n--- Longest Text Entry Found ---");
            console.log("Text: ", longestEntry);
            console.log("Length: ", longestEntry.length, " characters");
            console.log("Location: Row ", longestRow, ", Column: ", longestHeader);
        } else {
            console.log("No valid data found.");
        }

    } catch (err) {
        console.error('Error processing file:', err.message);
    } finally {
        rl.close();
    }
});

/**
 * Parses a CSV line handling quoted strings and commas.
 */
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
