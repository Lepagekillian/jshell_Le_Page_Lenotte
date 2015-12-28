package fr.umlv.jshellbook.jshell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JShellParser {

	private final static List<String> blocList = Arrays.asList("if", "for", "while", "else", "else if", "class");
	private final static Pattern pattern = Pattern.compile(blocList.stream().collect(Collectors.joining("\\s?\\(?|")));

	public static List<String> parse(String lines) {
		Objects.requireNonNull(lines);
		List<String> res = new ArrayList<>();
		StringBuilder builderBlock = new StringBuilder();
		List<String> linesList = Arrays.asList(lines.split(";"));

		int i = 0;

		while (i < linesList.size()) {
			String line = linesList.get(i);

			while (isBeginingOfBlock(line)) {
				String block = workOnBlock(linesList, i);
				builderBlock.append(block);
				i = jumpToBlockEnd(linesList, i);
				String endBlockLine = linesList.get(i);
				line = endBlockLine.substring(endBlockLine.lastIndexOf("}") + 1, endBlockLine.length());
			}
			if(builderBlock.length() > 0){
				res.add(builderBlock.toString());
				builderBlock.setLength(0);
			}
			if (!line.isEmpty()) {
				res.add(line + ";");
			}
			i++;
		}

		return res;
	}

	private static int jumpToBlockEnd(List<String> toCheck, int i) {
		int j = i;

		int braguetteClose = 0;
		int braguetteOpen = 0;
		while (j < toCheck.size()) {
			String line = toCheck.get(j);

			if (line.contains("}")) {
				braguetteClose += countNbBraguetteClose(line);
			}
			if (line.contains("{")) {
				braguetteOpen += countNbBraguetteOpen(line);
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

			int idxCloseBraguette = line.lastIndexOf('}');
			if (idxCloseBraguette != -1) {
				braguetteClose += countNbBraguetteClose(line);
				builder.append(line.substring(0, idxCloseBraguette + 1));
				line = line.substring(idxCloseBraguette + 1);
			}
			if (line.contains("{")) {
				braguetteOpen += countNbBraguetteOpen(line);
			}

			if (braguetteClose == braguetteOpen && braguetteOpen != 0) {
				isClosed = true;
			} else {
				builder.append(line).append(";");
			}

			j++;
		}
		return builder.toString();
	}

	private static boolean isBeginingOfBlock(String line) {

		if (line.contains("{") || pattern.matcher(line).find()) {
			return true;
		}
		return false;
	}

	private static int countNbBraguetteOpen(String line) {

		return countNbSymbole(line, '{');
	}

	private static int countNbBraguetteClose(String line) {

		return countNbSymbole(line, '}');
	}

	private static int countNbSymbole(String line, char symbole) {
		return (int) line.chars().filter(i -> i == symbole).count();
	}

}
