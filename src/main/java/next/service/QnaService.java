package next.service;

import java.util.List;

import next.CannotDeleteException;
import next.dao.AnswerDao;
import next.dao.QuestionDao;
import next.model.Answer;
import next.model.Question;
import next.model.User;

public class QnaService {
    private static QnaService qnaService;

    private QuestionDao questionDao;
    private AnswerDao answerDao;

    public QnaService(QuestionDao questionDao, AnswerDao answerDao) {
    	this.questionDao = questionDao;
    	this.answerDao = answerDao;
    }

    public static QnaService getInstance(QuestionDao questionDao, AnswerDao answerDao) {
        if (qnaService == null) {
            qnaService = new QnaService(questionDao, answerDao);
        }
        return qnaService;
    }

    public Question findById(long questionId) {
        return questionDao.findById(questionId);
    }

    public List<Answer> findAllByQuestionId(long questionId) {
        return answerDao.findAllByQuestionId(questionId);
    }

    /**
     * deleteQuestion 메소드는 삭제할 questionId와 Session의 User정보를 통해
     * questionId에 해당하는 글을 삭제해도 되는지 여부에 따라 진행하는 기능을 한다.
     *  ** QuestionDao에 접근해서 questionId로 게시글 존재 여부 파악
     *  * 1. 존재하지 않는 질문이라면 런타임 에러를 반환함
     *  * 2. 세션의 User와 삭제를 시도하는 글의 Writer가 다르면 런타임 에러를 반환함
     *  ** AnswerDao에 접근해서 다른사람이 댓글을 추가했는지 확인함
     *  * 1. 만약 추가 되어 있다면 글을 삭제할 수 없는 런타임 에러를 반환함
     *  ** QuestionDao의 Writer와 Session의 UserId
     *  ** 그리고 AnswerDao의 댓글이 없거나 자신이 남긴 댓글만 있는 경우 모두 삭제한다.
     * @param questionId
     * @param user
     * @throws CannotDeleteException
     */
    public void deleteQuestion(long questionId, User user) throws CannotDeleteException {
        Question question = questionDao.findById(questionId);
        if (question == null) {
            throw new CannotDeleteException("존재하지 않는 질문입니다.");
        }

        List<Answer> answers = answerDao.findAllByQuestionId(questionId);
        if (question.canDelete(user, answers)) {
            questionDao.delete(questionId);
        }
    }
}
