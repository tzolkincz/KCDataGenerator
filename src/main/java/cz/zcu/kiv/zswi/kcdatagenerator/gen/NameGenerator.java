package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class NameGenerator {

	List<String> firstNames = new ArrayList<>();
	List<String> lastNames = new ArrayList<>();
	int firstNameOffset = 0;
	int lastNameOffset = 0;
	long skip = 0;

	public static final String DEFAULT_DICT_PATH = File.separator + "dicts" + File.separator;

	/**
	 *
	 * @param firstNamesFile - can be null
	 * @param lastNamesFile - can be null
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public NameGenerator(Path firstNamesFile, Path lastNamesFile) throws IOException, URISyntaxException {
		if (firstNamesFile == null) {
			loadFirstnames(Paths.get(getClass().getResource(DEFAULT_DICT_PATH + "firstnames").getPath()));
		} else {
			loadFirstnames(firstNamesFile);
		}

		if (lastNamesFile == null) {
			loadLastnames(Paths.get(getClass().getResource(DEFAULT_DICT_PATH + "lastnames").getPath()));
		} else {
			loadLastnames(lastNamesFile);
		}

		if(firstNames.isEmpty()) {
			throw new IllegalStateException("firstnames are empty");
		}
		if (lastNames.isEmpty()) {
			throw new IllegalStateException("lastnames are empty");
		}

		initRand();
	}

	private void loadFirstnames(Path firstNamesFile) throws IOException {
		Files.lines(firstNamesFile).forEach(firstNames::add);
	}

	private void loadLastnames(Path lastNamesFile) throws IOException {
		Files.lines(lastNamesFile).forEach(lastNames::add);
	}

	private void initRand() {
		firstNameOffset = (int) (Math.random() * 1000);
		lastNameOffset = (int) (Math.random() * 1000);
		skip = (int) (Math.random() * 100);
	}

	public String getFullName() {
		firstNameOffset = (int) ((skip + firstNameOffset) % Integer.MAX_VALUE);
		lastNameOffset = (int) ((skip + lastNameOffset) % Integer.MAX_VALUE);
		return firstNames.get(firstNameOffset % firstNames.size())
				+ " " + lastNames.get(lastNameOffset % lastNames.size());
	}

	public String getLogin(String name) {
		StringBuilder sb = new StringBuilder();
		for (char c : name.toLowerCase().toCharArray()) {
			if (c >= 'a' && c <= 'z') {
				sb.append(c);
			}
		}
		return sb.toString() + lastNameOffset % 100;
	}

}
