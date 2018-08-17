package next.model;

import java.util.Date;

public class QuestionTest {
	public static Question newQuestion(long questionId, String writer) {
        return new Question(questionId, writer, "title", "contents", new Date(), 0);
    }
}
