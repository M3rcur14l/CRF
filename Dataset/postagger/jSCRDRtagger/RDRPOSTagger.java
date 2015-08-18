import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;

/**
 * @author DatQuocNguyen
 * 
 */
public class RDRPOSTagger
{
	public Node root;

	public RDRPOSTagger()
	{
	}

	public RDRPOSTagger(Node node)
	{
		root = node;
	}

	// Build an scrdr-based tree for pos tagging from a learned model file
	// containing rules
	public void constructTreeFromRulesFile(String rulesFilePath)
		throws IOException
	{
		BufferedReader buffer = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(rulesFilePath)), "UTF-8"));
		String line = buffer.readLine();

		this.root = new Node(new FWObject(false), "NN", null, null, null, 0);

		Node currentNode = this.root;
		int currentDepth = 0;

		for (; (line = buffer.readLine()) != null;) {
			int depth = 0;
			for (int i = 0; i <= 5; i++) { // supposed that the maximum
											// exception level is up to 5.
				if (line.charAt(i) == '\t')
					depth += 1;
				else
					break;
			}

			line = line.trim();
			if (line.length() == 0)
				continue;

			if (line.contains("cc:"))
				continue;

			// System.out.println(line);
			FWObject condition = Utils
					.getCondition(line.split(" : ")[0].trim());
			String conclusion = Utils
					.getConclusion(line.split(" : ")[1].trim());

			Node node = new Node(condition, conclusion, null, null, null, depth);

			if (depth > currentDepth) {
				currentNode.setExceptNode(node);
			}
			else if (depth == currentDepth) {
				currentNode.setIfnotNode(node);
			}
			else {
				while (currentNode.depth != depth)
					currentNode = currentNode.fatherNode;
				currentNode.setIfnotNode(node);
			}
			node.setFatherNode(currentNode);

			currentNode = node;
			currentDepth = depth;
		}
		buffer.close();
	}

	public Node findFiredNode(FWObject object)
	{
		Node currentN = root;
		Node firedN = null;
		while (true) {
			if (currentN.satisfy(object)) {
				firedN = currentN;
				if (currentN.exceptNode == null) {
					break;
				}
				else {
					currentN = currentN.exceptNode;
				}
			}
			else {
				if (currentN.ifnotNode == null) {
					break;
				}
				else {
					currentN = currentN.ifnotNode;
				}
			}

		}

		return firedN;
	}

	public String tagInitializedSentence(String inInitializedSentence)
	{
		StringBuilder sb = new StringBuilder();
		// List of pair (word, tag) in the input initialized sentence
		List<WordTag> wordtags = Utils.getWordTagList(inInitializedSentence);
		int size = wordtags.size();
		for (int i = 0; i < size; i++) {
			FWObject object = Utils.getObject(wordtags, size, i);
			Node firedNode = findFiredNode(object);
			if (firedNode.depth > 0)
				sb.append(wordtags.get(i).word + "/" + firedNode.conclusion
						+ " ");
			else {
				// Fired at root, return initialized tag.
				sb.append(wordtags.get(i).word + "/" + wordtags.get(i).tag
						+ " ");
			}
		}
		return sb.toString();
	}

	// Tag an initialized corpus. The initialized corpus are already generated
	// (before) by using an external initial tagger.
	public void tagInitializedCorpus(String inInitializedFilePath)
		throws IOException
	{
		BufferedReader buffer = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(inInitializedFilePath)), "UTF-8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(inInitializedFilePath + ".TAGGED"),
				"UTF-8"));

		for (String line; (line = buffer.readLine()) != null;) {
			line = line.trim();
			if (line.length() == 0) {
				bw.write("\n");
				continue;
			}
			bw.write(tagInitializedSentence(line) + "\n");
		}
		buffer.close();
		bw.close();
	}

	public String tagVnSentence(HashMap<String, String> FREQDICT,
			String sentence)
		throws IOException
	{
		StringBuilder sb = new StringBuilder();

		String line = sentence.trim();
		if (line.length() == 0) {
			return "\n";
		}

		line = line.replace("“", "''").replace("”", "''").replace("\"", "''");
		// Call initial tagger for Vietnamese
		List<WordTag> wordtags = InitialTagger.VnInitTagger4Sentence(FREQDICT,
				line);

		int size = wordtags.size();
		for (int i = 0; i < size; i++) {
			FWObject object = Utils.getObject(wordtags, size, i);
			Node firedNode = findFiredNode(object);
			sb.append(wordtags.get(i).word + "/" + firedNode.conclusion + " ");
		}

		return sb.toString();
	}

	public void tagVnCorpus(HashMap<String, String> FREQDICT,
			String inRawFilePath)
		throws IOException
	{
		BufferedReader buffer = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(inRawFilePath)), "UTF-8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(inRawFilePath + ".TAGGED"), "UTF-8"));
		for (String line; (line = buffer.readLine()) != null;) {
			bw.write(tagVnSentence(FREQDICT, line) + "\n");
		}
		buffer.close();
		bw.close();
	}

	public String tagEnSentence(HashMap<String, String> FREQDICT,
			String sentence)
		throws IOException
	{
		StringBuilder sb = new StringBuilder();

		String line = sentence.trim();
		if (line.length() == 0) {
			return "\n";
		}

		line = line.replace("“", "''").replace("”", "''").replace("\"", "''");
		// Call initial tagger for English
		List<WordTag> wordtags = InitialTagger.EnInitTagger4Sentence(FREQDICT,
				line);

		int size = wordtags.size();
		for (int i = 0; i < size; i++) {
			FWObject object = Utils.getObject(wordtags, size, i);
			Node firedNode = findFiredNode(object);
			sb.append(wordtags.get(i).word + "/" + firedNode.conclusion + " ");
		}
		return sb.toString();
	}

	public void tagEnCorpus(HashMap<String, String> FREQDICT,
			String inRawFilePath)
		throws IOException
	{
		BufferedReader buffer = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(inRawFilePath)), "UTF-8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(inRawFilePath + ".TAGGED"), "UTF-8"));
		for (String line; (line = buffer.readLine()) != null;) {
			bw.write(tagEnSentence(FREQDICT, line) + "\n");
		}
		buffer.close();
		bw.close();
	}

	public String tagSentence(HashMap<String, String> FREQDICT, String sentence)
		throws IOException
	{
		StringBuilder sb = new StringBuilder();
		String line = sentence.trim();
		if (line.length() == 0) {
			return "\n";
		}

		line = line.replace("“", "''").replace("”", "''").replace("\"", "''");
		// Calling the initial tagger for a particular language
		List<WordTag> wordtags = InitialTagger.InitTagger4Sentence(FREQDICT,
				line);
		int size = wordtags.size();
		for (int i = 0; i < size; i++) {
			FWObject object = Utils.getObject(wordtags, size, i);
			Node firedNode = findFiredNode(object);
			if (firedNode.depth > 0)
				sb.append(wordtags.get(i).word + "/" + firedNode.conclusion
						+ " ");
			else {
				// Fired at root, return initialized tag.
				sb.append(wordtags.get(i).word + "/" + wordtags.get(i).tag
						+ " ");
			}
		}
		return sb.toString();
	}

	public void tagCorpus(HashMap<String, String> FREQDICT, String inRawFilePath)
		throws IOException
	{
		BufferedReader buffer = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(inRawFilePath)), "UTF-8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(inRawFilePath + ".TAGGED"), "UTF-8"));
		for (String line; (line = buffer.readLine()) != null;) {
			bw.write(tagSentence(FREQDICT, line) + "\n");
		}
		buffer.close();
		bw.close();

	}

	public static void printInstructions()
	{
		System.out.println("Run RDRPOSTagger for tagging a corpus:");
		System.out
				.println("java RDRPOSTagger OPTION PATH-TO-LEARNED-MODEL [PATH-TO-LEXICON] PATH-TO-CORPUS");
		System.out
				.println("in which OPTION gets one of the 4 values 'en', 'vn', 'other', 'init' "
						+ "corresponding to a tagging process on English, Vietnamese, other languages"
						+ "and on an initialized corpus, respectively.");
		System.out
				.println("Example1: java RDRPOSTagger other ../Sample/En/T3-2/postagging.rdr ../Sample/En/fullDict ../Sample/En/rawTest");
		System.out
				.println("Example2: java RDRPOSTagger en ../Models/EN.RDR ../Dicts/EN.DICT ../Sample/En/rawTest");
		System.out
				.println("Example3: java RDRPOSTagger vn ../Models/VN.RDR ../Dicts/VN.DICT ../Sample/Vn/rawTest");
		System.out
				.println("Example4: java RDRPOSTagger init ../Sample/En/T3-2/postagging.rdr ../Sample/En/initTest");
		System.out
				.println("Having a look at http://rdrpostagger.sourceforge.net to find more information!");

	}

	public static void runRDRPOSTagging(String args[])
		throws IOException
	{
		if (args.length <= 1)
			printInstructions();
		else {
			RDRPOSTagger tree = new RDRPOSTagger();
			System.out
					.println("\nBuilding SCRDR-based tree for POS tagging from file: "
							+ args[1]);
			tree.constructTreeFromRulesFile(args[1]);

			if (args[0].equals("en")) {
				System.out.println("Reading lexicon: " + args[2]);
				HashMap<String, String> FREQDICT = Utils.getDictionary(args[2]);
				System.out
						.println("Tagging raw word-segmented corpus in English:"
								+ args[3]);
				tree.tagEnCorpus(FREQDICT, args[3]);
			}
			else if (args[0].equals("vn")) {
				System.out.println("Reading lexicon: " + args[2]);
				HashMap<String, String> FREQDICT = Utils.getDictionary(args[2]);
				System.out
						.println("Tagging raw word-segmented corpus in Vietnamese:"
								+ args[3]);
				tree.tagVnCorpus(FREQDICT, args[3]);
			}
			else if (args[0].equals("other")) {
				System.out.println("Reading lexicon: " + args[2]);
				HashMap<String, String> FREQDICT = Utils.getDictionary(args[2]);
				System.out.println("Tagging raw word-segmented corpus:"
						+ args[3]);
				tree.tagCorpus(FREQDICT, args[3]);
			}
			else if (args[0].equals("init")) {
				System.out.println("Tagging initialized corpus:" + args[2]);
				tree.tagInitializedCorpus(args[2]);
			}
			else {
				printInstructions();
			}
		}
	}

	public static void main(String[] args)
		throws IOException
	{
		runRDRPOSTagging(args);
	}
}
