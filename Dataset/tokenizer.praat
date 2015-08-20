strings = Create Strings as file list: "gridlist", "*.TextGrid"
numberOfFiles = Get number of strings
writeFileLine: "tokens.txt", ""

for j to numberOfFiles

	select Strings gridlist

	filename$ = Get string: j

	Read from file: filename$
	Rename: "myTextGrid"
	select TextGrid myTextGrid
	int = Get number of intervals: 1

	for i to int
		select TextGrid myTextGrid
		label$ = Get label of interval: 1, i
	
		appendFileLine: "tokens.txt", label$
	
	endfor
endfor
