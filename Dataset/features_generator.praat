Create Strings as file list: "wavlist", "*.wav"
strings = Create Strings as file list: "gridlist", "*.TextGrid"
numberOfFiles = Get number of strings
writeFile: "features.txt", ""
writeFile: "log.txt", ""

for j to 3

	select Strings gridlist
	filename$ = Get string: j
	Read from file: filename$
	Rename: "myTextGrid"
	select TextGrid myTextGrid
	numberOfWords = Get number of intervals: 1
	
	
	select Strings wavlist
	filename$ = Get string: j
	Read from file: filename$
	Rename: "myWav"


	select Sound myWav
	soundname$ = selected$ ("Sound")
	To Pitch: 0.01, 75, 400
	select Sound myWav
	intensityname$ = selected$ ("Sound")
	To Intensity: 100, 0


	for i to numberOfWords

		select TextGrid myTextGrid
		word$ = Get label of interval: 1, i

		if word$ = "#"
			if i <> 1 and i <> numberOfWords
				appendFileLine: "features.txt", ""
			endif
		else

			#Start point and end point fo the current word
			startPoint = Get start point: 1, i
			endPoint = Get end point: 1, i

			#Select the dialogue act of the current word
			dialogueAct = Get interval at time: 2, startPoint
			dialogueAct$ = Get label of interval: 2, dialogueAct


			#calcolo l'istante d'inizio e l'istante di fine dell'intervallo precedente (che mi servirà per calcolare l'average pitch e l'average intensity dell'intervallo precedente)
			if i > 1
				startPointPre = Get start point: 1, i-1
				endPointPre = Get end point: 1, i-1
			else
				startPointPre = 0
				endPointPre = 0
			endif

			#calcolo l'istante d'inizio e l'istante di fine dell'intervallo successivo (che mi servirà per calcolare l'average pitch e l'average intensity dell'intervallo successivo)
			if i < numberOfWords
				startPointPost = Get start point: 1, i+1
				endPointPost = Get end point: 1, i+1
			else
				startPointPre = Get start point: 1, i
				endPointPre = Get end point: 1, i
			endif

			select Pitch 'soundname$'

			#calculate avg pitch of the current interval
			avgPitch = Get mean: startPoint, endPoint, "Hertz"
			avgPitch$ = fixed$ (avgPitch, 2)

			#calculate avg pitch of the prev interval
			avgPitchPre = Get mean: startPointPre, endPointPre, "Hertz"

			#calculate avg pitch of the next interval
			avgPitchPost = Get mean: startPointPost, endPointPost, "Hertz"

			#calculate delta pitch
			deltaPitch = (avgPitchPost - avgPitchPre)/(endPoint - startPoint)
			deltaPitch$ = fixed$ (deltaPitch, 2)

			select Intensity 'intensityname$'

			#calculate avg intensity of the current interval
			avgIntensity = Get mean: startPoint, endPoint, "dB"
			avgIntensity$ = fixed$ (avgIntensity, 2)

			#calculate avg intensity of the prev interval
			avgIntensityPre = Get mean: startPointPre, endPointPre, "dB"

			#calculate avg pitch of the next interval
			avgIntensityPost = Get mean: startPointPost, endPointPost, "dB"

			#calculate delta intensity
			deltaIntensity = (avgIntensityPost - avgIntensityPre) / (endPoint - startPoint)
			deltaIntensity$ = fixed$ (deltaIntensity, 2)

			output$ =  filename$ + tab$
			output$ =  output$ + dialogueAct$ + tab$
			output$ = output$ + "word=" + word$ + tab$
			output$ = output$ + "avgPitch=" + avgPitch$ + tab$
			output$ = output$ + "deltaPitch=" + deltaPitch$ + tab$
			output$ = output$ + "avgIntensity=" + avgIntensity$ + tab$
			output$ = output$ + "deltaIntensity=" + deltaIntensity$

			appendFileLine: "features.txt", output$

		endif

	endfor
	appendFileLine: "features.txt", ""

endfor
