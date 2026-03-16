## Program Logic

### MP17 – Find the longest text entry in dataset
The program reads the CSV file and parses each row into cells, handling quoted strings and commas. It iterates over every cell in the dataset (excluding the header) and compares the trimmed length of each value against the current longest. When a longer value is found, it stores the text, its length, and the row and column location. The output reports the longest text entry, its character count, and where it appears in the dataset.

### MP18 – Remove rows with empty fields
The program loads all CSV rows and checks each row for any empty or whitespace-only fields. Rows that contain at least one empty field are discarded, while rows with all fields filled are kept. It counts how many rows were removed and how many remain, then displays a summary and a sample of the first five cleaned records.

### MP19 – Generate dataset summary report
The program reads the CSV and identifies the Exam and Result columns by header name. It counts total records and columns, then builds frequency maps for exam types and pass/fail results. The summary report displays total records, column count, pass/fail distribution, and the top five most popular exams by candidate count.
