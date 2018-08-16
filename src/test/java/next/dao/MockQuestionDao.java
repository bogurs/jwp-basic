package next.dao;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;

import next.model.Question;

public class MockQuestionDao implements QuestionDao {
	private Map<Long, Question> questions = Maps.newHashMap();

	@Override
	public Question insert(Question question) {
		return questions.put(question.getQuestionId(), question);
	}

	@Override
	public List<Question> findAll() {
		return questions.values().stream().collect(Collectors.toList());
	}

	@Override
	public Question findById(long questionId) {
		return questions.get(questionId);
	}

	@Override
	public void update(Question question) {
		questions.put(question.getQuestionId(), question);
	}

	@Override
	public void delete(long questionId) {
		questions.remove(questionId);
	}

	@Override
	public void updateCountOfAnswer(long questionId) {
		
	}

}
