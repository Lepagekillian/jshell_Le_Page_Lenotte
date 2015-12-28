package fr.umlv.jshellbook.jshell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JShellParser {

	private static final char CHAR_BLOCK_OPENER = '{';
	private static final char CHAR_BLOCK_CLOSER = '}';
	private static final String BLOCK_OPENER = "{";
	private static final String BLOCK_CLOSER = "}";
	private static final String SEPARATOR = ";";
	private final static List<String> blocList = Arrays.asList("if", "for", "while", "else", "else if", "class");
	private final static Pattern pattern = Pattern.compile(blocList.stream().collect(Collectors.joining("\\s*\\(|")));
	/**
	 * Create a list of string code from a string
	 * @param lines A String which contains the code to parse
	 * @return A list with the snippets ready to be evaluate with <code>JShellEvaluator</code>
	 * @see JShellEvaluator#evalSnippets(List)
	 */
	public static List<String> parseToSnippets(String lines) {

		List<String> linesList = splitLines(Objects.requireNonNull(lines), SEPARATOR);
		List<String> res = new ArrayList<>();
		StringBuilder builderBlock = new StringBuilder();
		int i = 0;

		while (i < linesList.size()) {
			String line = linesList.get(i);

			while (isBeginingOfBlock(line)) {
				String block = workOnBlock(linesList, i);
				builderBlock.append(block);
				i = jumpToBlockEnd(linesList, i);
				String endBlockLine = linesList.get(i);
				line = endBlockLine.substring(endBlockLine.lastIndexOf(BLOCK_CLOSER) + 1, endBlockLine.length());
			}
			if (builderBlock.length() > 0) {
				res.add(builderBlock.toString());
				builderBlock.setLength(0);
			}
			if (!line.isEmpty()) {
				res.add(line + SEPARATOR);
			}
			i++;
		}
		
		return res;
	}

	private static List<String> splitLines(String lines, String separator) {
		StringBuilder builderLine = new StringBuilder();
		String[] linesTmp = lines.split(separator);
		List<String> listString = new ArrayList<>();
		boolean isToReconstitued = false;
		for (int i = 0; i < linesTmp.length; i++) {
			String line = linesTmp[i];
			if (isToMuchSplit(line)) {
				builderLine.append(line);
				if (isToReconstitued) {
					listString.add(builderLine.toString());
					builderLine.setLength(0);
					isToReconstitued = false;
				} else {
					isToReconstitued = true;
					builderLine.append(separator);
				}

			} else if (isToReconstitued) {
				builderLine.append(line).append(separator);
			} else {
				listString.add(line);
			}
		}
		return listString;
	}

	private static boolean isToMuchSplit(String line) {
/*Impossible de faire un test d'impaaire avec un nombre négatif  ==> Possible d'avoir nb néga ?*/
		return countNbSymbole(line, '"') % 2 == 1 || countNbSymbole(line, '\'') % 2 == 1;
	}

	private static int jumpToBlockEnd(List<String> toCheck, int i) {
		int j = i;

		int braguetteClose = 0;
		int braguetteOpen = 0;
		while (j < toCheck.size()) {
			String line = toCheck.get(j);

			if (line.contains(BLOCK_CLOSER)) {
				braguetteClose += countNbBlockClose(line);
			}
			if (line.contains(BLOCK_OPENER)) {
				braguetteOpen += countNbBlockOpen(line);
			}

			if (braguetteClose == braguetteOpen && braguetteOpen != 0) {
				return j;
			}
			j++;

		}

		return 0;
	}

	private static String workOnBlock(final List<String> toAnalize, final int i) {

		boolean isClosed = false;
		int j = i;
		int braguetteClose = 0;
		int braguetteOpen = 0;

		StringBuilder builder = new StringBuilder();
		while (!isClosed && j < toAnalize.size()) {

			String line = toAnalize.get(j);

			int idxCloseBraguette = line.lastIndexOf(CHAR_BLOCK_CLOSER);
			if (idxCloseBraguette != -1) {
				braguetteClose += countNbBlockClose(line);
				builder.append(line.substring(0, idxCloseBraguette + 1));
				line = line.substring(idxCloseBraguette + 1);
			}
			if (line.contains(BLOCK_OPENER)) {
				braguetteOpen += countNbBlockOpen(line);
			}

			if (braguetteClose == braguetteOpen && braguetteOpen != 0) {
				isClosed = true;
			} else {
				builder.append(line).append(SEPARATOR);
			}

			j++;
		}
		return builder.toString();
	}

	private static boolean isBeginingOfBlock(String line) {

		if (line.contains(BLOCK_OPENER) || pattern.matcher(line).find()) {
			return true;
		}
		return false;
	}

	private static int countNbBlockOpen(String line) {

		return countNbSymbole(line, CHAR_BLOCK_OPENER);
	}

	private static int countNbBlockClose(String line) {

		return countNbSymbole(line, CHAR_BLOCK_CLOSER);
	}

	private static int countNbSymbole(String line, char symbole) {
		return (int) line.chars().filter(i -> i == symbole).count();
	}

}
