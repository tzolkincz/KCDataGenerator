package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import java.io.IOException;
import static java.lang.Math.random;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.DefaultExtendedPropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.Importance;
import microsoft.exchange.webservices.data.core.enumeration.property.MapiPropertyType;
import microsoft.exchange.webservices.data.core.enumeration.service.TaskStatus;
import microsoft.exchange.webservices.data.core.service.item.Task;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.definition.ExtendedPropertyDefinition;
import org.joda.time.DateTime;

public class TaskGenerator {

	private final String exchangeUrl;
	private final List<GeneratedUser> users;
	private final String domain;
	private static DateTime nowRounded;
	private final List<String> subjects = new ArrayList<>();
	private final List<Task> generatedTasks;
	private boolean nationalChars = false;

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
		initSubjects(nationalChars);
		generatedTasks = new ArrayList<>();
	}

	/**
	 *
	 * @param count
	 * @return
	 * @throws URISyntaxException
	 * @throws Exception
	 */
	@Deprecated
	public List<Task> generateAndSave(int count) throws URISyntaxException, Exception {
		return generateAndSave(count, true);
	}

	/**
	 *
	 * @param count
	 * @param nationalChars
	 * @return
	 * @throws URISyntaxException
	 * @throws Exception
	 */
	public List<Task> generateAndSave(int count, boolean nationalChars) throws URISyntaxException, Exception {
		initSubjects(nationalChars);

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
					t.setBody(new MessageBody(t.getSubject() + " task"));

					setReminder(t, new DateTime(t.getDueDate()).minusDays(1).toDate());

					t.save();
					generatedTasks.add(t);
				}
			}
		}

		return generatedTasks;
	}

	private String getSubject() {
		return subjects.get((int) (random() * subjects.size()));
	}

	private void initSubjects(boolean nationalChars) {
		if (subjects.isEmpty() || this.nationalChars != nationalChars) {
			subjects.clear();
			this.nationalChars = nationalChars;

			if (nationalChars) {
				subjects.add("купить молоко (ru)");
				subjects.add("купить автомобиль (ru)");
				subjects.add("купить дом (ru)");
				subjects.add("купить яйца (ru)");
				subjects.add("Полей растения (ru)");
				subjects.add("План отпуск (ru)");
				subjects.add("Koupit Mléko");
				subjects.add("Koupit Dům");
				subjects.add("Zaplatit složenky");
				subjects.add("Prodat staré auto");
			} else {
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
	}

	private void setReminder(Task t, Date reminderDate) throws Exception {
		t.setReminderDueBy(reminderDate);
		t.setReminderMinutesBeforeStart(45);

		ExtendedPropertyDefinition propAlertTime = new ExtendedPropertyDefinition(
				DefaultExtendedPropertySet.Common, 34144, MapiPropertyType.SystemTime);

		t.setExtendedProperty(propAlertTime, reminderDate);

		ExtendedPropertyDefinition propAlertTime2 = new ExtendedPropertyDefinition(
				DefaultExtendedPropertySet.Common, 34050, MapiPropertyType.SystemTime);
		t.setExtendedProperty(propAlertTime2, reminderDate);

		ExtendedPropertyDefinition propSetAlarm = new ExtendedPropertyDefinition(
				DefaultExtendedPropertySet.Common, 34051, MapiPropertyType.Boolean);
		t.setExtendedProperty(propSetAlarm, true);
	}

}
