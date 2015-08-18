import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author DatQuocNguyen
 * 
 */
public class InitialTagger
{
	// Sample function is used as an initial tagger for a specific language!
	public static List<WordTag> InitTagger4Sentence(
			HashMap<String, String> SAMPLEDICT, String sentence)
	{
		List<WordTag> wordtags = new ArrayList<WordTag>();

		for (String word : sentence.split(" ")) {
			String tag = "";
			if (SAMPLEDICT.containsKey(word))
				tag = SAMPLEDICT.get(word);
			else {
				/*
				 * Deal with out-of-dictionary words for your own language
				 * here... Referring to file SampleInitialTagger.py in
				 * associated Python-implementation.
				 */
				tag = SAMPLEDICT.get("DefaultTag");// Default tag
			}
			wordtags.add(new WordTag(word, tag));
		}
		return wordtags;
	}

	private static final Pattern CD = Pattern.compile("[0-9]+");
	private static final Pattern JJ1 = Pattern.compile("([0-9]+-)|(-[0-9]+)");
	private static final Pattern JJ2 = Pattern
			.compile("(^[Ii]nter.*)|(^[nN]on.*)|(^[Dd]is.*)|(^[Aa]nti.*)");
	private static final Pattern JJ3 = Pattern
			.compile("(.*ful$)|(.*ous$)|(.*ble$)|(.*ic$)|(.*ive$)|(.*est$)|(.*able$)|(.*al$)");

	private static final Pattern NN = Pattern
			.compile("(.*ness$)|(.*ment$)|(.*ship$)|(^[Ee]x-.*)|(^[Ss]elf-.*)");
	private static final Pattern NNS = Pattern.compile(".*s$");
	private static final Pattern VBG = Pattern.compile(".*ing$");
	private static final Pattern VBN = Pattern.compile(".*ed$");
	private static final Pattern RB = Pattern.compile(".*ly$");

	public static List<WordTag> EnInitTagger4Sentence(
			HashMap<String, String> ENFREQDICT, String sentence)
	{
		List<WordTag> wordtags = new ArrayList<WordTag>();

		for (String word : sentence.split(" ")) {
			String tag = "";
			String lowerW = word.toLowerCase();
			if (ENFREQDICT.containsKey(word))
				tag = ENFREQDICT.get(word);
			else if (ENFREQDICT.containsKey(lowerW))
				tag = ENFREQDICT.get(lowerW);
			else {
				if (JJ1.matcher(word).find())
					tag = "JJ";
				else if (CD.matcher(word).find())
					tag = "CD";
				else if (NN.matcher(word).find())
					tag = "NN";
				else if (NNS.matcher(word).find()
						&& Character.isLowerCase(word.charAt(0)))
					tag = "NNS";
				else if (Character.isUpperCase(word.charAt(0)))
					tag = "NNP";
				else if (JJ2.matcher(word).find())
					tag = "JJ";
				else if (VBG.matcher(word).find() && !word.contains("-"))
					tag = "VBG";
				else if (VBN.matcher(word).find() && !word.contains("-"))
					tag = "VBN";
				else if (word.contains("-") || JJ3.matcher(word).find())
					tag = "JJ";
				else if (RB.matcher(word).find())
					tag = "RB";
				else
					tag = "NN";
			}
			wordtags.add(new WordTag(word, tag));
		}
		return wordtags;
	}

	public static void EnInitTagger4Corpus(HashMap<String, String> ENFREQDICT,
			String inputRawFilePath, String outFilePath)
		throws IOException
	{
		BufferedReader buffer = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(inputRawFilePath)), "UTF-8"));

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outFilePath), "UTF-8"));

		for (String line; (line = buffer.readLine()) != null;) {
			line = line.trim();
			if (line.length() == 0) {
				bw.write("\n");
				continue;
			}
			for (WordTag st : EnInitTagger4Sentence(ENFREQDICT, line))
				bw.write(st.word + "/" + st.tag + " ");
			bw.write("\n");
		}

		buffer.close();
		bw.close();
	}

	private static final Pattern NUMP = Pattern.compile("[0-9]");

	public static HashMap<String, String> OTHERSDICT = Utils
			.getDictionary("addDicts/VNOTHERS.DICT");
	public static HashMap<String, String> VNNAMES = Utils
			.getDictionary("addDicts/VNNAMES.DICT");

	public static List<WordTag> VnInitTagger4Sentence(
			HashMap<String, String> FREQDICT, String sentence)
	{
		List<WordTag> wordtags = new ArrayList<WordTag>();

		for (String word : sentence.split(" ")) {
			String tag = "";
			if (FREQDICT.containsKey(word)) {
				tag = FREQDICT.get(word);
			}
			else if (OTHERSDICT.containsKey(word)) {
				tag = OTHERSDICT.get(word);
			}
			else if (VNNAMES.containsKey(word)) {
				tag = "Np";
			}
			else {
				if (NUMP.matcher(word).find()) {
					tag = "M";
				}
				else if (word.length() == 1
						&& Character.isUpperCase(word.charAt(0))) {
					tag = "Y";
				}
				else if (Utils.isAbbre(word)) {
					tag = "Ny";
				}
				else if (Utils.isVnProperNoun(word)) {
					tag = "Np";
				}
				else {
					tag = "N";
				}
			}
			wordtags.add(new WordTag(word, tag));
		}
		return wordtags;
	}

	public static void VnInitTagger4Corpus(HashMap<String, String> FREQDICT,
			String inputRawFilePath, String outFilePath)
		throws IOException
	{
		BufferedReader buffer = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(inputRawFilePath)), "UTF-8"));

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outFilePath), "UTF-8"));

		for (String line; (line = buffer.readLine()) != null;) {
			line = line.trim();
			if (line.length() == 0) {
				bw.write("\n");
				continue;
			}
			for (WordTag st : VnInitTagger4Sentence(FREQDICT, line))
				bw.write(st.word + "/" + st.tag + " ");
			bw.write("\n");
		}

		buffer.close();
		bw.close();
	}
}
