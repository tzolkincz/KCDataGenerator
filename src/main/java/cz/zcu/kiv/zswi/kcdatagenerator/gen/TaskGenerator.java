package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import java.io.IOException;
import static java.lang.Math.random;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.Importance;
import microsoft.exchange.webservices.data.core.enumeration.service.TaskStatus;
import microsoft.exchange.webservices.data.core.service.item.Task;
import org.joda.time.DateTime;

public class TaskGenerator {

	private final String exchangeUrl;
	private final List<GeneratedUser> users;
	private final String domain;
	private static DateTime nowRounded;
	private final List<String> subjects = new ArrayList<>();

	private static final int DUE_DATE_HISTORY_MAX_DAYS = 100;
	private static final int DUE_DATE_FUTURE_MAX_DAYS = 100;
	private static final double STATUS_PROBABILITY = 0.2;
	private static final double IMPORNTANCE_PROBABILITY = 0.1;

	public TaskGenerator(String exchangeUrl, List<GeneratedUser> users, String domain) throws IOException {
		this.exchangeUrl = exchangeUrl;
		this.users = users;
		this.domain = domain;

		DateTime now = DateTime.now();
		nowRounded = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), now.getHourOfDay(), 0);
		initSubjects();
	}

	public void generateAndSave(int count) throws URISyntaxException, Exception {
		for (GeneratedUser user : users) {
			try (ExchangeService service = ExchangeServiceFactory.create(exchangeUrl, user, domain)) {
				for (int i = 0; i < count; i++) {
					Task t = new Task(service);
					t.setDueDate(nowRounded.plusDays(-DUE_DATE_HISTORY_MAX_DAYS
							+ (int) (random() * DUE_DATE_FUTURE_MAX_DAYS)).toDate());

					if (random() < STATUS_PROBABILITY) {
						t.setStatus(TaskStatus.InProgress);
						t.setPercentComplete(random() * 100);
					} else if (random() < STATUS_PROBABILITY) {
						t.setStatus(TaskStatus.Completed);
						t.setPercentComplete(100.0);
					} else if (random() < STATUS_PROBABILITY) {
						t.setStatus(TaskStatus.WaitingOnOthers);
						t.setPercentComplete(random() * 100);
					} else {
						t.setStatus(TaskStatus.NotStarted);
					}

					if (random() < IMPORNTANCE_PROBABILITY) {
						t.setImportance(Importance.High);
					} else if (random() < IMPORNTANCE_PROBABILITY) {
						t.setImportance(Importance.Low);
					}

					t.setSubject(getSubject());
					t.setReminderDueBy(new DateTime(t.getDueDate()).minusDays(1).toDate());

					t.save();
				}
			}
		}
	}

	private String getSubject() {
		return subjects.get((int) (random() * subjects.size()));
	}

	private void initSubjects() {
		subjects.add("Buy milk");
		subjects.add("Buy car");
		subjects.add("Buy house");
		subjects.add("Buy eggs");
		subjects.add("Buy tickets");
		subjects.add("Buy new suit");
		subjects.add("Speak with boss about promotion");
		subjects.add("Water the plants");
		subjects.add("Pay bills");
		subjects.add("Sell old car");
		subjects.add("Plan vacation");
		subjects.add("Book fly");
	}

}
