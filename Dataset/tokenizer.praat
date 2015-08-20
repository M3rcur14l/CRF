strings = Create Strings as file list: "gridlist", "*.TextGrid"
numberOfFiles = Get number of strings
writeFile: "tokens.txt", ""

for j to 10

	select Strings gridlist

	filename$ = Get string: j

	Read from file: filename$
	Rename: "myTextGrid"
	select TextGrid myTextGrid
	int = Get number of intervals: 1

	j$ = string$(j)

	for i to int
		select TextGrid myTextGrid
		word$ = Get label of interval: 1, i

		if word$ = "#"
        	appendFileLine: "tokens.txt", ""
        else
        	appendFile: "tokens.txt", word$ + " "
        endif

	endfor

endfor
