package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import java.io.File;
import static java.lang.Math.random;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class PathService {

	final static Path BASE_PATH = Paths.get(PathService.class.
			getProtectionDomain().getCodeSource().getLocation().getPath());

	final static Path INC_PATH = Paths.get(BASE_PATH.getParent().toString(), "inc");

	final static String DEFAULT_ATTACHMENT_PATH = "attachments";
	final static String DEFAULT_AVATAR_PATH = "photos";
	final static String DEFAULT_DICT_PATH = "/dicts/";

	static String getRandomEmailAttachmentPath() {
		return getPathFromIncOrDefault(DEFAULT_ATTACHMENT_PATH);
	}

	static String getRandomAvatarPath() {
		return getPathFromIncOrDefault(DEFAULT_AVATAR_PATH);
	}

	private static String getPathFromIncOrDefault(String path) {
		Optional<String> randomFile = getRandomFile(INC_PATH.toString(), path);
		if (randomFile.isPresent()) {
			return randomFile.get();
		}

		//return defaults
		return getFromResources(path).get();
	}

	private static Optional<String> getRandomFile(String basePath, String subPath) {
		File baseFolder = new File(Paths.get(basePath, subPath).toString());
		File[] files = baseFolder.listFiles();

		if (files.length > 0) {
			return Optional.of(files[(int) (random() * files.length)].getAbsolutePath());
		}

		return Optional.empty();
	}

	private static Optional<String> getFromResources(String subPath) {
		return getRandomFile(PathService.class.getResource("/").getPath(), subPath);
	}

	static Path getDictFirstnamesPath() {
		return getDictPath("firstnames.txt");
	}

	static Path getDictLastnamesPath() {
		return getDictPath("lastnames.txt");
	}

	private static Path getDictPath(String dictName) {
		File file = new File(INC_PATH.toString(), DEFAULT_DICT_PATH + dictName);
		if (file.exists()) {
			return Paths.get(file.getAbsolutePath());
		} else {
			return Paths.get(PathService.class.getResource(DEFAULT_DICT_PATH + dictName).getPath());
		}
	}

}
