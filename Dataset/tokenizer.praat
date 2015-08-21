strings = Create Strings as file list: "gridlist", "*.TextGrid"
numberOfFiles = Get number of strings
writeFile: "tokens.txt", ""

for j to 4

	select Strings gridlist

	filename$ = Get string: j

	Read from file: filename$
	Rename: "myTextGrid"
	select TextGrid myTextGrid
	numberOfWords = Get number of intervals: 1

	for i to numberOfWords

		select TextGrid myTextGrid
		word$ = Get label of interval: 1, i

		if word$ = "#"
			if i <> 1 and i <> numberOfWords
        		appendFileLine: "tokens.txt", ""
        	endif
        else
        	appendFile: "tokens.txt", word$ + " "
        endif

	endfor
	appendFileLine: "tokens.txt", ""

endfor
