package cz.zcu.kiv.zswi.kcdatagenerator.gen;

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

	public static final String DEFAULT_DICT_PATH = "/dicts/";

	/**
	 *
	 * @param firstNamesFile - can be null
	 * @param lastNamesFile - can be null
	 * @throws IOException
	 */
	public NameGenerator(Path firstNamesFile, Path lastNamesFile) throws IOException {
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

	public String getLastName() {
		lastNameOffset = (int) ((skip + lastNameOffset) % Integer.MAX_VALUE);
		return lastNames.get(lastNameOffset % lastNames.size());
	}

	public String getFirstName() {
		firstNameOffset = (int) ((skip + firstNameOffset) % Integer.MAX_VALUE);
		return firstNames.get(firstNameOffset % firstNames.size());
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

	public String getRandomLogin() {
		return getLogin(getFirstName() + " " + getLastName());
	}


}
