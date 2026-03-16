const fs = require('fs');
const readline = require('readline');

/**
 * MP18 – Remove rows with empty fields.
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
        let headerRow = null;
        let cleanedRows = [];
        let rowsRemoved = 0;

        for (let line of lines) {
            if (line.trim() === "" || (!headerFound && !line.includes("Candidate"))) {
                continue;
            }

            const cells = parseCsvLine(line);

            if (!headerFound) {
                headerRow = cells;
                headerFound = true;
                continue;
            }

            // Check for empty or whitespace-only fields
            const hasEmpty = cells.some(cell => cell.trim().length === 0);

            if (!hasEmpty) {
                cleanedRows.push(cells);
            } else {
                rowsRemoved++;
            }
        }

        console.log(`\n--- Dataset Cleanup Summary ---`);
        console.log(`Removed ${rowsRemoved} rows containing empty fields.`);
        console.log(`Remaining valid records: ${cleanedRows.length}`);
        console.log(`\n--- Sample Records (First 5) ---`);

        if (cleanedRows.length === 0) {
            console.log('No rows remain after cleanup.');
        } else {
            if (headerRow) {
                console.log(headerRow.join(' | '));
            }

            cleanedRows.slice(0, 5).forEach(row => {
                console.log(row.join(' | '));
            });
        }

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
