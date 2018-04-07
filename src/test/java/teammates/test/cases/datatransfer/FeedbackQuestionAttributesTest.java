package teammates.test.cases.datatransfer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.storage.entity.FeedbackQuestion;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link FeedbackQuestionAttributes}.
 */
public class FeedbackQuestionAttributesTest extends BaseTestCase {

    private DataBundle typicalBundle = getTypicalDataBundle();

    private static class FeedbackQuestionAttributesWithModifiableTimestamp extends FeedbackQuestionAttributes {

        void setCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
        }

        void setUpdatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
        }

    }

    @Test
    public void testValueOf() {
        /* Solves the problem of "java.lang.NullPointerException: No API environment is registered for this thread on
         Unit testing".
         https://stackoverflow.com/questions/31994264/java-lang-nullpointerexception-no-api-environment-
         is-registered-for-this-thread */
        LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
        helper.setUp();

        List<FeedbackParticipantType> participants = new ArrayList<>();
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER);

        List<FeedbackParticipantType> participantTypesAfterRemovingIrrelevantVisibilitiesOptions =
                new ArrayList<>(participants);
        participantTypesAfterRemovingIrrelevantVisibilitiesOptions.remove(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participantTypesAfterRemovingIrrelevantVisibilitiesOptions.remove(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);

        FeedbackQuestion qn = new FeedbackQuestion("test session", "cs1101", "test@example.com",
                new Text("question text"), new Text("description"), 1, FeedbackQuestionType.TEXT,
                FeedbackParticipantType.TEAMS, FeedbackParticipantType.TEAMS, 5, participants,
                participants, participants);
        qn.setId(12345678910L);

        FeedbackQuestionAttributes feedbackQuestionAttributes = FeedbackQuestionAttributes.valueOf(qn);
        assertEquals(qn.getFeedbackSessionName(), feedbackQuestionAttributes.getFeedbackSessionName());
        assertEquals(qn.getCourseId(), feedbackQuestionAttributes.getCourseId());
        assertEquals(qn.getCreatorEmail(), feedbackQuestionAttributes.getCreatorEmail());
        assertEquals(qn.getQuestionDescription(), feedbackQuestionAttributes.getQuestionDescription());
        assertEquals(feedbackQuestionAttributes.getQuestionNumber(), qn.getQuestionNumber());
        assertEquals(qn.getQuestionType(), feedbackQuestionAttributes.getQuestionType());
        assertEquals(qn.getNumberOfEntitiesToGiveFeedbackTo(),
                feedbackQuestionAttributes.getNumberOfEntitiesToGiveFeedbackTo());
        assertEquals(qn.getQuestionMetaData(), feedbackQuestionAttributes.getQuestionMetaData());
        assertEquals(qn.getGiverType(), feedbackQuestionAttributes.getGiverType());
        assertEquals(qn.getRecipientType(), feedbackQuestionAttributes.getRecipientType());

        /* .build() in valueOf() will remove irrelevant visibilities options, so the lists showResponsesTo,
        showGiverNameTo, and showRecipientNameTo are not the same as the ones in qn */
        assertEquals(participantTypesAfterRemovingIrrelevantVisibilitiesOptions,
                feedbackQuestionAttributes.getShowGiverNameTo());
        assertEquals(participantTypesAfterRemovingIrrelevantVisibilitiesOptions,
                feedbackQuestionAttributes.getShowRecipientNameTo());
        assertEquals(participantTypesAfterRemovingIrrelevantVisibilitiesOptions,
                feedbackQuestionAttributes.getShowResponsesTo());
        assertEquals(qn.getCreatedAt(), feedbackQuestionAttributes.getCreatedAt());
        assertEquals(qn.getUpdatedAt(), qn.getUpdatedAt());
        assertEquals(qn.getId(), feedbackQuestionAttributes.getFeedbackQuestionId());

        helper.tearDown();
    }

    @Test
    public void testBuilderWithPopulatedFieldValues() {
        String feedbackSession = "test session";
        String courseId = "some course";
        String creatorEmail = "test@case.com";
        Text questionMetaData = new Text("test qn from teams->none.");
        Text questionDescription = new Text("some description");
        int questionNumber = 1;
        int numOfEntities = 4;
        FeedbackQuestionType questionType = FeedbackQuestionType.TEXT;
        FeedbackParticipantType giverType = FeedbackParticipantType.TEAMS;
        FeedbackParticipantType recipientType = FeedbackParticipantType.TEAMS;

        List<FeedbackParticipantType> participants = new ArrayList<>();
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER);
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.ofEpochMilli(9876545);
        String feedbackQuestionId = "agR0ZXN0choLEhBGZWVkYmFja1F1ZXN0aW9uGL648P4tDA";

        FeedbackQuestionAttributes feedbackQuestionAttributes = FeedbackQuestionAttributes.builder()
                .withFeedbackSessionName(feedbackSession)
                .withCourseId(courseId)
                .withCreatorEmail(creatorEmail)
                .withQuestionMetaData(questionMetaData)
                .withQuestionDescription(questionDescription)
                .withQuestionNumber(questionNumber)
                .withNumOfEntitiesToGiveFeedbackTo(numOfEntities)
                .withQuestionType(questionType)
                .withGiverType(giverType)
                .withRecipientType(recipientType)
                .withShowGiverNameTo(new ArrayList<>(participants))
                .withShowRecipientNameTo(new ArrayList<>(participants))
                .withShowResponseTo(new ArrayList<>(participants))
                .withCreatedAt(createdAt)
                .withUpdatedAt(updatedAt)
                .withFeedbackQuestionId(feedbackQuestionId)
                .buildWithoutRemovingIrrelevantVisibilityOptions();

        assertEquals(feedbackSession, feedbackQuestionAttributes.getFeedbackSessionName());
        assertEquals(courseId, feedbackQuestionAttributes.getCourseId());
        assertEquals(creatorEmail, feedbackQuestionAttributes.getCreatorEmail());
        assertEquals(questionMetaData, feedbackQuestionAttributes.getQuestionMetaData());
        assertEquals(questionDescription, feedbackQuestionAttributes.questionDescription);
        assertEquals(questionNumber, feedbackQuestionAttributes.getQuestionNumber());
        assertEquals(numOfEntities, feedbackQuestionAttributes.numberOfEntitiesToGiveFeedbackTo);
        assertEquals(questionType, feedbackQuestionAttributes.getQuestionType());
        assertEquals(giverType, feedbackQuestionAttributes.getGiverType());
        assertEquals(recipientType, feedbackQuestionAttributes.getRecipientType());
        assertEquals(participants, feedbackQuestionAttributes.showGiverNameTo);
        assertEquals(participants, feedbackQuestionAttributes.showResponsesTo);
        assertEquals(participants, feedbackQuestionAttributes.showRecipientNameTo);
        assertEquals(createdAt, feedbackQuestionAttributes.getCreatedAt());
        assertEquals(updatedAt, feedbackQuestionAttributes.getUpdatedAt());
        assertEquals(feedbackQuestionId, feedbackQuestionAttributes.getFeedbackQuestionId());
    }

    @Test
    public void testBuilderSanitizeForBuild() {

        ______TS("sanitize whitespace");

        Text contentWithWhitespaces = new Text(" content to be sanitized by removing leading/trailing whitespace ");
        FeedbackQuestionAttributes feedbackQuestionAttributes = FeedbackQuestionAttributes.builder()
                .withQuestionMetaData(new Text("test qn from teams->none."))
                .withQuestionDescription(contentWithWhitespaces)
                .buildWithoutRemovingIrrelevantVisibilityOptions();

        assertEquals(new Text("content to be sanitized by removing leading/trailing whitespace"),
                feedbackQuestionAttributes.getQuestionDescription());

        ______TS("sanitize code block");

        Text codeblock = new Text("<code>System.out.println(\"Hello World\");</code>");
        feedbackQuestionAttributes = FeedbackQuestionAttributes.builder()
                .withQuestionMetaData(new Text("test qn from teams->none."))
                .withQuestionDescription(codeblock)
                .buildWithoutRemovingIrrelevantVisibilityOptions();

        assertEquals(new Text("<code>System.out.println(&#34;Hello World&#34;);</code>"),
                feedbackQuestionAttributes.getQuestionDescription());

        ______TS("sanitize superscript");

        Text superscript = new Text("f(x) = x<sup>2</sup>");

        feedbackQuestionAttributes = FeedbackQuestionAttributes.builder()
                .withQuestionMetaData(new Text("test qn from teams->none."))
                .withQuestionDescription(superscript)
                .buildWithoutRemovingIrrelevantVisibilityOptions();

        assertEquals(new Text("f(x) &#61; x<sup>2</sup>"), feedbackQuestionAttributes.getQuestionDescription());

        ______TS("sanitize chemical formula");

        Text chemicalFormula = new Text("<p>Chemical formula: C<sub>6</sub>H<sub>12</sub>O<sub>6</sub></p>");
        feedbackQuestionAttributes = FeedbackQuestionAttributes.builder()
                .withQuestionMetaData(new Text("test qn from teams->none."))
                .withQuestionDescription(chemicalFormula)
                .buildWithoutRemovingIrrelevantVisibilityOptions();

        assertEquals(new Text("<p>Chemical formula: C<sub>6</sub>H<sub>12</sub>O<sub>6</sub></p>"),
                feedbackQuestionAttributes.getQuestionDescription());
    }

    @Test
    public void testBuilderWithDefaultValues() {
        FeedbackQuestionAttributes observedFeedbackQuestionAttributes =
                FeedbackQuestionAttributes.builder().buildWithoutRemovingIrrelevantVisibilityOptions();

        assertEquals(0, observedFeedbackQuestionAttributes.questionNumber);
        assertNull(observedFeedbackQuestionAttributes.recipientType);
        assertNull(observedFeedbackQuestionAttributes.giverType);
        assertNull(observedFeedbackQuestionAttributes.courseId);
        assertNull(observedFeedbackQuestionAttributes.feedbackSessionName);
        assertNull(observedFeedbackQuestionAttributes.showResponsesTo);
        assertNull(observedFeedbackQuestionAttributes.showGiverNameTo);
        assertNull(observedFeedbackQuestionAttributes.showRecipientNameTo);
        assertNull(observedFeedbackQuestionAttributes.questionDescription);
        assertNull(observedFeedbackQuestionAttributes.questionType);
        assertNull(observedFeedbackQuestionAttributes.questionMetaData);
        assertNull(observedFeedbackQuestionAttributes.creatorEmail);
        assertEquals(0, observedFeedbackQuestionAttributes.numberOfEntitiesToGiveFeedbackTo);
    }

    @Test
    public void testGetCopy() {
        List<FeedbackParticipantType> participants = new ArrayList<>();
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        List<FeedbackParticipantType> participantsForShowResponseTo = new ArrayList<>(participants);
        participantsForShowResponseTo.add(FeedbackParticipantType.STUDENTS);

        FeedbackQuestionAttributes original = FeedbackQuestionAttributes.builder()
                .withFeedbackSessionName("test session")
                .withCourseId("some course")
                .withCreatorEmail("test@case.com")
                .withQuestionMetaData(new Text("test qn from teams->none."))
                .withQuestionNumber(1)
                .withQuestionType(FeedbackQuestionType.TEXT)
                .withGiverType(FeedbackParticipantType.TEAMS)
                .withRecipientType(FeedbackParticipantType.NONE)
                .withShowGiverNameTo(new ArrayList<>(participants))
                .withShowRecipientNameTo(new ArrayList<>(participants))
                .withShowResponseTo(new ArrayList<>(participantsForShowResponseTo))
                .withCreatedAt(Instant.now())
                .withUpdatedAt(Instant.MAX)
                .withQuestionDescription(new Text("some description"))
                .withNumOfEntitiesToGiveFeedbackTo(Const.MAX_POSSIBLE_RECIPIENTS)
                .build();

        FeedbackQuestionAttributes copy = original.getCopy();

        assertNotSame(copy, original);
        assertEquals(copy.getFeedbackSessionName(), original.getFeedbackSessionName());
        assertEquals(copy.getCourseId(), original.getCourseId());
        assertEquals(copy.getCreatorEmail(), original.getCreatorEmail());
        assertEquals(copy.getQuestionMetaData(), original.getQuestionMetaData());
        assertEquals(copy.getQuestionNumber(), original.getQuestionNumber());
        assertEquals(copy.getQuestionType(), original.getQuestionType());
        assertEquals(copy.getGiverType(), original.getGiverType());
        assertEquals(copy.getRecipientType(), original.getRecipientType());
        assertEquals(copy.getShowGiverNameTo(), original.getShowGiverNameTo());
        assertEquals(copy.getShowRecipientNameTo(), original.getShowRecipientNameTo());
        assertEquals(copy.getShowResponsesTo(), copy.getShowResponsesTo());
        assertEquals(copy.getCreatedAt(), original.getCreatedAt());
        assertEquals(copy.getUpdatedAt(), original.getUpdatedAt());
        assertEquals(copy.getNumberOfEntitiesToGiveFeedbackTo(), original.getNumberOfEntitiesToGiveFeedbackTo());
        assertEquals(copy.getFeedbackQuestionId(), original.getFeedbackQuestionId());
    }

    @Test
    public void testDefaultTimestamp() {

        FeedbackQuestionAttributesWithModifiableTimestamp fq =
                new FeedbackQuestionAttributesWithModifiableTimestamp();

        fq.setCreatedAt(null);
        fq.setUpdatedAt(null);

        Instant defaultTimeStamp = Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP;

        ______TS("success : defaultTimeStamp for createdAt date");

        assertEquals(defaultTimeStamp, fq.getCreatedAt());

        ______TS("success : defaultTimeStamp for updatedAt date");

        assertEquals(defaultTimeStamp, fq.getUpdatedAt());
    }

    @Test
    public void testValidate() throws Exception {

        List<FeedbackParticipantType> showGiverNameToList = new ArrayList<>();
        showGiverNameToList.add(FeedbackParticipantType.SELF);
        showGiverNameToList.add(FeedbackParticipantType.STUDENTS);

        List<FeedbackParticipantType> showRecipientNameToList = new ArrayList<>();
        showRecipientNameToList.add(FeedbackParticipantType.SELF);
        showRecipientNameToList.add(FeedbackParticipantType.STUDENTS);

        List<FeedbackParticipantType> showResponseToList = new ArrayList<>();
        showResponseToList.add(FeedbackParticipantType.NONE);
        showResponseToList.add(FeedbackParticipantType.SELF);

        FeedbackQuestionAttributes fq = FeedbackQuestionAttributes.builder()
                .withFeedbackSessionName("")
                .withCourseId("")
                .withCreatorEmail("")
                .withQuestionType(FeedbackQuestionType.TEXT)
                .withGiverType(FeedbackParticipantType.NONE)
                .withRecipientType(FeedbackParticipantType.RECEIVER)
                .withShowGiverNameTo(new ArrayList<>(showGiverNameToList))
                .withShowRecipientNameTo(new ArrayList<>(showRecipientNameToList))
                .withShowResponseTo(new ArrayList<>(showResponseToList))
                .buildWithoutRemovingIrrelevantVisibilityOptions();

        assertFalse(fq.isValid());

        String errorMessage = getPopulatedEmptyStringErrorMessage(
                                  FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                                  FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME,
                                  FieldValidator.FEEDBACK_SESSION_NAME_MAX_LENGTH) + System.lineSeparator()
                              + getPopulatedEmptyStringErrorMessage(
                                    FieldValidator.COURSE_ID_ERROR_MESSAGE_EMPTY_STRING,
                                    FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.COURSE_ID_MAX_LENGTH)
                              + System.lineSeparator()
                              + "Invalid creator's email: "
                              + getPopulatedEmptyStringErrorMessage(
                                    FieldValidator.EMAIL_ERROR_MESSAGE_EMPTY_STRING,
                                    FieldValidator.EMAIL_FIELD_NAME, FieldValidator.EMAIL_MAX_LENGTH)
                              + System.lineSeparator()
                              + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.giverType.toString(),
                                              FieldValidator.GIVER_TYPE_NAME) + System.lineSeparator()
                              + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.recipientType.toString(),
                                              FieldValidator.RECIPIENT_TYPE_NAME) + System.lineSeparator()
                              + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE,
                                              fq.showGiverNameTo.get(0).toString(),
                                              FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                              + "Trying to show giver name to STUDENTS without showing response first."
                              + System.lineSeparator()
                              + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE,
                                              fq.showRecipientNameTo.get(0).toString(),
                                              FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                              + "Trying to show recipient name to STUDENTS without showing response first."
                              + System.lineSeparator()
                              + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE,
                                              fq.showResponsesTo.get(0).toString(),
                                              FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                              + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE,
                                              fq.showResponsesTo.get(1).toString(),
                                              FieldValidator.VIEWER_TYPE_NAME);

        assertEquals(errorMessage, StringHelper.toString(fq.getInvalidityInfo()));

        fq.feedbackSessionName = "First Feedback Session";
        fq.courseId = "CS1101";
        fq.creatorEmail = "instructor1@course1.com";
        fq.giverType = FeedbackParticipantType.TEAMS;
        fq.recipientType = FeedbackParticipantType.OWN_TEAM;

        assertFalse(fq.isValid());

        errorMessage = String.format(FieldValidator.PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE,
                                     fq.recipientType.toDisplayRecipientName(),
                                     fq.giverType.toDisplayGiverName()) + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showGiverNameTo.get(0).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                       + "Trying to show giver name to STUDENTS without showing response first." + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE,
                                       fq.showRecipientNameTo.get(0).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                       + "Trying to show recipient name to STUDENTS without showing response first."
                       + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showResponsesTo.get(0).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showResponsesTo.get(1).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME);

        assertEquals(errorMessage, StringHelper.toString(fq.getInvalidityInfo()));

        fq.recipientType = FeedbackParticipantType.OWN_TEAM_MEMBERS;

        assertFalse(fq.isValid());

        errorMessage = String.format(FieldValidator.PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE,
                                     fq.recipientType.toDisplayRecipientName(),
                                     fq.giverType.toDisplayGiverName()) + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showGiverNameTo.get(0).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                       + "Trying to show giver name to STUDENTS without showing response first." + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE,
                                       fq.showRecipientNameTo.get(0).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                       + "Trying to show recipient name to STUDENTS without showing response first."
                       + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showResponsesTo.get(0).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showResponsesTo.get(1).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME);

        assertEquals(errorMessage, StringHelper.toString(fq.getInvalidityInfo()));

        fq.recipientType = FeedbackParticipantType.TEAMS;

        fq.showGiverNameTo = new ArrayList<>();
        fq.showGiverNameTo.add(FeedbackParticipantType.RECEIVER);

        fq.showRecipientNameTo = new ArrayList<>();
        fq.showRecipientNameTo.add(FeedbackParticipantType.RECEIVER);

        fq.showResponsesTo = new ArrayList<>();
        fq.showResponsesTo.add(FeedbackParticipantType.RECEIVER);

        assertTrue(fq.isValid());
    }

    @Test
    public void testGetQuestionDetails() {

        ______TS("Text question: new Json format");

        FeedbackQuestionAttributes fq = typicalBundle.feedbackQuestions.get("qn5InSession1InCourse1");
        FeedbackTextQuestionDetails questionDetails = new FeedbackTextQuestionDetails("New format text question");
        fq.setQuestionDetails(questionDetails);

        assertTrue(fq.isValid());
        assertEquals(fq.getQuestionDetails().getQuestionText(), "New format text question");

        ______TS("Text question: old string format");

        fq = typicalBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        assertEquals(fq.getQuestionDetails().getQuestionText(), "Rate 1 other student's product");
    }

    @Test
    public void testRemoveIrrelevantVisibilityOptions() {

        ______TS("test teams->none");

        List<FeedbackParticipantType> participants = new ArrayList<>();
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        List<FeedbackParticipantType> participantsForShowResponseTo = new ArrayList<>(participants);
        participantsForShowResponseTo.add(FeedbackParticipantType.STUDENTS);

        FeedbackQuestionAttributes question = FeedbackQuestionAttributes.builder()
                .withFeedbackSessionName("test session")
                .withCourseId("some course")
                .withCreatorEmail("test@case.com")
                .withQuestionMetaData(new Text("test qn from teams->none."))
                .withQuestionNumber(1)
                .withQuestionType(FeedbackQuestionType.TEXT)
                .withGiverType(FeedbackParticipantType.TEAMS)
                .withRecipientType(FeedbackParticipantType.NONE)
                .withShowGiverNameTo(new ArrayList<>(participants))
                .withShowRecipientNameTo(new ArrayList<>(participants))
                .withShowResponseTo(new ArrayList<>(participantsForShowResponseTo))
                .withNumOfEntitiesToGiveFeedbackTo(Const.MAX_POSSIBLE_RECIPIENTS)
                .build();

        assertTrue(question.showGiverNameTo.isEmpty());
        assertTrue(question.showRecipientNameTo.isEmpty());
        // check that other types are not removed
        assertTrue(question.showResponsesTo.contains(FeedbackParticipantType.STUDENTS));
        assertEquals(question.showResponsesTo.size(), 1);

        ______TS("test students->teams");

        question.giverType = FeedbackParticipantType.STUDENTS;
        question.recipientType = FeedbackParticipantType.TEAMS;

        participants.clear();
        participants.add(FeedbackParticipantType.INSTRUCTORS);
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        question.showGiverNameTo = new ArrayList<>(participants);
        participants.add(FeedbackParticipantType.STUDENTS);
        question.showRecipientNameTo = new ArrayList<>(participants);
        question.showResponsesTo = new ArrayList<>(participants);

        question.removeIrrelevantVisibilityOptions();

        assertEquals(question.showGiverNameTo.size(), 2);
        assertEquals(question.showRecipientNameTo.size(), 3);
        assertEquals(question.showResponsesTo.size(), 3);
        assertFalse(question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));

        ______TS("test students->team members including giver");

        question.giverType = FeedbackParticipantType.STUDENTS;
        question.recipientType = FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF;

        participants.clear();
        participants.add(FeedbackParticipantType.INSTRUCTORS);
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        question.showGiverNameTo = new ArrayList<>(participants);
        participants.add(FeedbackParticipantType.STUDENTS);
        question.showRecipientNameTo = new ArrayList<>(participants);
        question.showResponsesTo = new ArrayList<>(participants);

        question.removeIrrelevantVisibilityOptions();

        assertEquals(question.showGiverNameTo.size(), 3);
        assertEquals(question.showRecipientNameTo.size(), 4);
        assertEquals(question.showResponsesTo.size(), 4);
        assertFalse(question.showGiverNameTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF));
        assertFalse(question.showRecipientNameTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF));
        assertFalse(question.showResponsesTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF));

        ______TS("test students->instructors");

        question.giverType = FeedbackParticipantType.STUDENTS;
        question.recipientType = FeedbackParticipantType.INSTRUCTORS;

        participants.clear();
        participants.add(FeedbackParticipantType.RECEIVER);
        participants.add(FeedbackParticipantType.INSTRUCTORS);
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.STUDENTS);
        question.showGiverNameTo = new ArrayList<>(participants);
        question.showRecipientNameTo = new ArrayList<>(participants);
        question.showResponsesTo = new ArrayList<>(participants);

        question.removeIrrelevantVisibilityOptions();

        assertEquals(question.showGiverNameTo.size(), 4);
        assertEquals(question.showRecipientNameTo.size(), 4);
        assertEquals(question.showResponsesTo.size(), 4);
        assertFalse(question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));

        ______TS("test students->own team");

        question.giverType = FeedbackParticipantType.STUDENTS;
        question.recipientType = FeedbackParticipantType.OWN_TEAM;

        participants.clear();
        participants.add(FeedbackParticipantType.RECEIVER);
        participants.add(FeedbackParticipantType.INSTRUCTORS);
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.STUDENTS);
        question.showGiverNameTo = new ArrayList<>(participants);
        question.showRecipientNameTo = new ArrayList<>(participants);
        question.showResponsesTo = new ArrayList<>(participants);

        question.removeIrrelevantVisibilityOptions();

        assertEquals(question.showGiverNameTo.size(), 4);
        assertEquals(question.showRecipientNameTo.size(), 4);
        assertEquals(question.showResponsesTo.size(), 4);
        assertFalse(question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));

        ______TS("test students->own team members");

        question.giverType = FeedbackParticipantType.STUDENTS;
        question.recipientType = FeedbackParticipantType.OWN_TEAM_MEMBERS;

        participants.clear();
        participants.add(FeedbackParticipantType.RECEIVER);
        participants.add(FeedbackParticipantType.INSTRUCTORS);
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.STUDENTS);
        question.showGiverNameTo = new ArrayList<>(participants);
        question.showRecipientNameTo = new ArrayList<>(participants);
        question.showResponsesTo = new ArrayList<>(participants);

        question.removeIrrelevantVisibilityOptions();

        assertEquals(question.showGiverNameTo.size(), 4);
        assertEquals(question.showRecipientNameTo.size(), 4);
        assertEquals(question.showResponsesTo.size(), 4);
        assertFalse(question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
    }

}
