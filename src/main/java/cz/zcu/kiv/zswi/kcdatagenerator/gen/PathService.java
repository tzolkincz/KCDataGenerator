package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Math.random;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PathService {

	final static Path BASE_PATH;

	static {
		String filePath = PathService.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		BASE_PATH = Paths.get(System.getProperty("os.name").contains("indow")
				? filePath.substring(1) : filePath);
	}

	final static Path INC_PATH = Paths.get(BASE_PATH.getParent().toString(), "inc");

	final static String DEFAULT_ATTACHMENT_PATH = "attachments";
	final static String DEFAULT_AVATAR_PATH = "photos";
	final static String DEFAULT_DICT_PATH = "/dicts/";

	private static boolean tempInitialized = false;
	private static File firstnamesFile;
	private static File lastnamesFile;
	private static List<File> attachmentFiles = new ArrayList<>();

	private static void initTemp() throws IOException {
		if (tempInitialized) {
			return;
		}

		firstnamesFile = loadTempFile(DEFAULT_DICT_PATH + "firstnames.txt", null);
		lastnamesFile = loadTempFile(DEFAULT_DICT_PATH + "lastnames.txt", null);

		attachmentFiles.add(loadTempFile("/" + DEFAULT_ATTACHMENT_PATH + "/img.png", "img.png"));
		attachmentFiles.add(loadTempFile("/" + DEFAULT_ATTACHMENT_PATH + "/files.zip", "files.zip"));
		attachmentFiles.add(loadTempFile("/" + DEFAULT_ATTACHMENT_PATH + "/example.jpg", "example.jpg"));

		tempInitialized = true;
	}

	private static File loadTempFile(String resourcePath, String suffix) throws IOException {
		File tempFile = File.createTempFile("tmpfile" + (int) (random() * 10000), suffix);
		Files.copy(PathService.class.getResourceAsStream(resourcePath),
				tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		return tempFile;
	}

	static String getRandomEmailAttachmentPath() throws IOException {
		return getPathFromIncOrDefault(DEFAULT_ATTACHMENT_PATH);
	}

	static InputStream getRandomAvatarFileStream() {
		Optional<String> randomFile = getRandomFile(INC_PATH.toString(), DEFAULT_AVATAR_PATH);
		if (randomFile.isPresent()) {
			try {
				return new FileInputStream(randomFile.get());
			} catch (FileNotFoundException e) {
				//continue to read default
			}
		}

		return PathService.class.getResourceAsStream("/" + DEFAULT_AVATAR_PATH
				+ "/avatar" + (int) (Math.random() * 5) + ".png");
	}

	private static String getPathFromIncOrDefault(String path) throws IOException {
		Optional<String> randomFile = getRandomFile(INC_PATH.toString(), path);
		if (randomFile.isPresent()) {
			return randomFile.get();
		}

		initTemp();

		//return defaults
		return attachmentFiles.get((int) (random() * attachmentFiles.size())).getAbsolutePath();
	}

	private static Optional<String> getRandomFile(String basePath, String subPath) {
		File baseFolder = new File(Paths.get(basePath, subPath).toString());
		File[] files = baseFolder.listFiles();

		if (files != null && files.length > 0) {
			return Optional.of(files[(int) (random() * files.length)].getAbsolutePath());
		}

		return Optional.empty();
	}

	private static Optional<String> getFromResources(String subPath) throws URISyntaxException {
		return getRandomFile(PathService.class.getResource("/" + subPath).toURI().toString(), subPath);
	}

	static Path getDictFirstnamesPath() throws IOException {
		initTemp();
		return getDictPath(firstnamesFile, "firstnames.txt");
	}

	static Path getDictLastnamesPath() throws IOException {
		initTemp();
		return getDictPath(lastnamesFile, "lastnames.txt");
	}

	private static Path getDictPath(File tempFile, String dictName) throws IOException {
		File file = new File(INC_PATH.toString() + DEFAULT_DICT_PATH + dictName);
		if (file.exists()) {
			return Paths.get(file.getAbsolutePath());
		}
		return Paths.get(tempFile.getAbsolutePath());
	}

}
