package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import com.kerio.lib.json.api.connect.admin.iface.Users;
import com.kerio.lib.json.api.connect.admin.struct.User;
import com.kerio.lib.json.api.connect.admin.struct.common.Error;
import java.util.ArrayList;
import java.util.List;

public class UsersGenerator {

	private final ApiClient apiClient;
	private final String domainId;
	private final NameGenerator nameGenerator;
	private User[] users;
	private final List<String> logins = new ArrayList<>();
	private final List<GeneratedUser> generatedUsers = new ArrayList<>();


	UsersGenerator(ApiClient client, String domainId, NameGenerator nameGenerator) {
		this.apiClient = client;
		this.domainId = domainId;
		this.nameGenerator = nameGenerator;
	}

	public List<String> getUserLogins() {
		return logins;
	}

	public User[] generate(int count) {
		users = new User[count];
		for (int i = 0; i < count; i++) {
			users[i] = UsersGenerator.this.generate();
		}
		return users;
	}

	private User generate() {
		User u = new User();
		String passwd = "testTest333";
		String firstName = nameGenerator.getFirstName();
		String lastName = nameGenerator.getLastName();

		String fullName = firstName + " " + lastName;
		String login = nameGenerator.getLogin(fullName);
		logins.add(login);

		u.setFullName(fullName);
		u.setLoginName(login);
		u.setPassword(passwd);
		u.setDomainId(domainId);

		generatedUsers.add(new GeneratedUser(firstName, lastName, passwd));
		return u;
	}

	public Error[] save() {
		return apiClient.getApi(Users.class).create(users).getErrors();
	}

	List<GeneratedUser> getUsers() {
		return generatedUsers;
	}

}
