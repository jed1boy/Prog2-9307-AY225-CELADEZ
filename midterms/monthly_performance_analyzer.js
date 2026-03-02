const fs = require('fs');
const path = require('path');
const readline = require('readline');

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

function normalizeHeader(headers) {
    return headers.map((h) => h.trim().toLowerCase());
}

function validateCsvFile(filePath) {
    if (!fs.existsSync(filePath)) {
        return { valid: false, error: 'File does not exist.' };
    }

    let stat;
    try {
        stat = fs.statSync(filePath);
    } catch (err) {
        return { valid: false, error: 'Unable to access file metadata.' };
    }

    if (!stat.isFile()) {
        return { valid: false, error: 'Path is not a file.' };
    }

    try {
        fs.accessSync(filePath, fs.constants.R_OK);
    } catch (err) {
        return { valid: false, error: 'File is not readable.' };
    }

    if (path.extname(filePath).toLowerCase() !== '.csv') {
        return { valid: false, error: 'File must have a .csv extension.' };
    }

    let content;
    try {
        content = fs.readFileSync(filePath, 'utf8');
    } catch (err) {
        return { valid: false, error: 'Failed to read file content.' };
    }

    const lines = content.split(/\r?\n/).filter((line) => line.trim().length > 0);
    if (lines.length < 2) {
        return { valid: false, error: 'CSV must include a header and at least one data row.' };
    }

    const headers = normalizeHeader(parseCsvLine(lines[0]));
    const requiredColumns = ['total_sales', 'release_date'];

    for (const col of requiredColumns) {
        if (!headers.includes(col)) {
            return { valid: false, error: `CSV missing required column: ${col}` };
        }
    }

    return { valid: true };
}

function toMonthKey(dateText) {
    if (!dateText) return null;

    const raw = dateText.trim();
    if (!raw) return null;

    const parts = raw.split('-');
    if (parts.length !== 3) return null;

    const [year, month] = parts;
    if (!/^\d{4}$/.test(year) || !/^\d{1,2}$/.test(month)) return null;

    const mm = month.padStart(2, '0');
    if (Number(mm) < 1 || Number(mm) > 12) return null;

    return `${year}-${mm}`;
}

function analyzeMonthlySales(filePath) {
    const content = fs.readFileSync(filePath, 'utf8');
    const lines = content.split(/\r?\n/).filter((line) => line.trim().length > 0);

    const headerCells = normalizeHeader(parseCsvLine(lines[0]));
    const totalSalesIndex = headerCells.indexOf('total_sales');
    const releaseDateIndex = headerCells.indexOf('release_date');
    const naSalesIndex = headerCells.indexOf('na_sales');
    const jpSalesIndex = headerCells.indexOf('jp_sales');
    const palSalesIndex = headerCells.indexOf('pal_sales');
    const otherSalesIndex = headerCells.indexOf('other_sales');

    const monthlyTotals = new Map();
    let processedRows = 0;
    let skippedRows = 0;

    for (let i = 1; i < lines.length; i++) {
        const cells = parseCsvLine(lines[i]);

        if (cells.length <= Math.max(totalSalesIndex, releaseDateIndex)) {
            skippedRows++;
            continue;
        }

        const monthKey = toMonthKey(cells[releaseDateIndex]) || 'Unknown';

        let sales = Number.parseFloat((cells[totalSalesIndex] || '').trim());
        if (Number.isNaN(sales)) {
            const na = Number.parseFloat((cells[naSalesIndex] || '').trim()) || 0;
            const jp = Number.parseFloat((cells[jpSalesIndex] || '').trim()) || 0;
            const pal = Number.parseFloat((cells[palSalesIndex] || '').trim()) || 0;
            const other = Number.parseFloat((cells[otherSalesIndex] || '').trim()) || 0;
            sales = na + jp + pal + other;
        }

        monthlyTotals.set(monthKey, (monthlyTotals.get(monthKey) || 0) + sales);
        processedRows++;
    }

    const sortedMonthly = [...monthlyTotals.entries()]
        .sort((a, b) => {
            if (a[0] === 'Unknown') return 1;
            if (b[0] === 'Unknown') return -1;
            return a[0].localeCompare(b[0]);
        })
        .map(([month, totalSales]) => ({ month, totalSales }));

    let bestMonth = null;
    for (const row of sortedMonthly) {
        if (!bestMonth || row.totalSales > bestMonth.totalSales) {
            bestMonth = row;
        }
    }

    return {
        sortedMonthly,
        bestMonth,
        processedRows,
        skippedRows
    };
}

function printSummary(result) {
    console.log('\n=== Monthly Performance Summary ===');

    if (result.sortedMonthly.length === 0) {
        console.log('No valid rows found for monthly computation.');
        return;
    }

    console.log('Month     | Total Sales');
    console.log('------------------------');

    for (const row of result.sortedMonthly) {
        console.log(`${row.month} | ${row.totalSales.toFixed(2)}`);
    }

    console.log('------------------------');
    console.log(`Best-performing month: ${result.bestMonth.month} (${result.bestMonth.totalSales.toFixed(2)})`);
    console.log(`Processed rows: ${result.processedRows}`);
    console.log(`Skipped rows: ${result.skippedRows}`);
}

function askFilePath(rl) {
    return new Promise((resolve) => {
        const ask = () => {
            rl.question('Enter dataset file path: ', (inputPath) => {
                const trimmed = inputPath.trim();
                const validation = validateCsvFile(trimmed);

                if (validation.valid) {
                    console.log('File found and validated. Processing...');
                    resolve(trimmed);
                } else {
                    console.log(`Invalid file path or CSV: ${validation.error}`);
                    ask();
                }
            });
        };

        ask();
    });
}

async function main() {
    const rl = readline.createInterface({
        input: process.stdin,
        output: process.stdout
    });

    try {
        const filePath = await askFilePath(rl);
        const result = analyzeMonthlySales(filePath);
        printSummary(result);
    } catch (err) {
        console.error('An error occurred while processing the dataset:', err.message);
    } finally {
        rl.close();
    }
}

main();
