package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import static cz.zcu.kiv.zswi.kcdatagenerator.gen.EmailGenerator.DEFAULT_ATTACHMENT_PATH;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import static java.lang.Math.random;
import microsoft.exchange.webservices.data.core.enumeration.property.Sensitivity;
import microsoft.exchange.webservices.data.core.enumeration.property.time.DayOfTheWeek;
import microsoft.exchange.webservices.data.core.enumeration.service.ConflictResolutionMode;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence.WeeklyPattern;
import org.joda.time.DateTime;

public class EventGenerator {

	private final String exchangeUrl;
	private final List<GeneratedUser> users;
	private final String domain;
	private static DateTime nowRounded;
	public static final double FLAG_PROBABILITY = 0.1;
	public static final double RECURRENT_EVENT_PROBABILITY = 0.02;
	public static final int MIN_HOUR_EVENT_FROM = 5;
	public static final int MAX_HOUR_EVENT_FROM = 22 - MIN_HOUR_EVENT_FROM;
	public static final int MAX_EVENT_DURATION_HOURS = 5;
	public static final int REMINDER_MINS_BEFORE = 15;
	public static final int MAX_EVENT_ATTENDEES = 5;

	public EventGenerator(String exchangeUrl, List<GeneratedUser> users, String domain) throws IOException {
		this.exchangeUrl = exchangeUrl;
		this.users = users;
		this.domain = domain;

		DateTime now = DateTime.now();
		nowRounded = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), now.getHourOfDay(), 0);
	}

	/**
	 *
	 * @param count count of events per user
	 * @param allDay create all day events?
	 * @param multiDay	create multi days events?
	 * @param recurrent create recurrent events?
	 * @param isPrivate create private events?
	 * @param attachement @TODO
	 * @param invitation create events with multiple attendants?
	 * @throws URISyntaxException
	 * @throws Exception
	 */
	public void generateAndSave(int count, boolean allDay, boolean multiDay, boolean recurrent,
			boolean isPrivate, boolean attachement, boolean invitation) throws URISyntaxException, Exception {

		for (GeneratedUser user : users) {
			try (ExchangeService service = ExchangeServiceFactory.create(exchangeUrl, user, domain)) {
				for (int i = 0; i < count; i++) {
					Appointment appointment = new Appointment(service);
					appointment.setSubject(getSubject());

					setStartAndEnd(appointment, allDay, multiDay);

					if (recurrent && random() < RECURRENT_EVENT_PROBABILITY) {
						setRecurrent(appointment);
					}

					if (isPrivate && random() < FLAG_PROBABILITY) {
						appointment.setSensitivity(Sensitivity.Private);
					}

					if (attachement && random() < FLAG_PROBABILITY) {
						String location = getClass().getResource(DEFAULT_ATTACHMENT_PATH + "img.png").getPath();
						appointment.getAttachments().addFileAttachment(location);
					}

					appointment.save();

					//attendees must be set after save
					if (invitation && random() < FLAG_PROBABILITY) {
						for (int j = 0; j < random() * MAX_EVENT_ATTENDEES; j++) {
							appointment.getRequiredAttendees().add(getInvitee());
						}
						appointment.update(ConflictResolutionMode.AutoResolve);
					}
				}
			}
		}

	}

	private String getSubject() {
		List<String> subjects = new ArrayList<>();
		subjects.add("Call");
		subjects.add("Meeting");
		subjects.add("Retrospective");
		subjects.add("Monthly Assesment");
		subjects.add("Job Inteview");
		subjects.add("Money Raise Talk");
		subjects.add("Language Lesson");
		subjects.add("Language Home Work");
		subjects.add("Piano Lesson");
		subjects.add("Shopping with wife");
		subjects.add("Beer session");
		subjects.add("Secret Project");
		subjects.add("PR Meeting");
		subjects.add("HR Meeting");
		subjects.add("Marketing Meeting");
		subjects.add("Sales Meeting");
		subjects.add("Board Meeting");
		subjects.add("Sales Focus Group Meeting");
		subjects.add("MRR Evaluation");
		subjects.add("Presentation");
		subjects.add("Kickoff project xyz");
		subjects.add("Tea session");
		subjects.add("Golf");
		subjects.add("Tennis");
		subjects.add("Visit cassino");
		subjects.add("Online poker match");

		return subjects.get((int) (random() * subjects.size()));
	}

	private void setStartAndEnd(Appointment appointment, boolean allDay, boolean multiDay) throws Exception {

		DateTime start;
		DateTime end;

		if (random() > 1.0 / 12) {
			start = nowRounded.minusDays((int) (random() * 365));
		} else {
			start = nowRounded.plusDays((int) (random() * 30));
		}

		if (allDay && random() < FLAG_PROBABILITY) {
			start = start.withMillisOfDay(0);
			end = start;
			appointment.setIsAllDayEvent(true);
		} else if (multiDay && random() < FLAG_PROBABILITY) {
			start = start.withMillisOfDay(0);
			end = start.plusDays((int) (random() * 10));
		} else {
			start = start.withHourOfDay(MIN_HOUR_EVENT_FROM + (int) (random() * MAX_HOUR_EVENT_FROM));
			end = start.plusHours((int) (random() * MAX_EVENT_DURATION_HOURS));
		}

		appointment.setStart(start.toDate());
		appointment.setEnd(end.toDate());

		if (start.isAfterNow()) {
			appointment.setReminderMinutesBeforeStart(REMINDER_MINS_BEFORE);
		}
	}

	private void setRecurrent(Appointment appointment) throws ServiceLocalException, Exception {
		WeeklyPattern weeklyPattern = new WeeklyPattern();
		weeklyPattern.setStartDate(appointment.getStart());
		weeklyPattern.getDaysOfTheWeek().add(
				DayOfTheWeek.values()[new DateTime(appointment.getStart()).dayOfWeek().get()]);
		weeklyPattern.setEndDate(DateTime.now().plusMonths(1).toDate());
		appointment.setRecurrence(weeklyPattern);
	}

	private String getInvitee() {
		int offset = (int) (random() * users.size());
		return users.get(offset).getUserAddr(domain);
	}

}
