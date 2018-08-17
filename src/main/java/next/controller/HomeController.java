package next.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.annotation.Controller;
import core.annotation.RequestMapping;
import core.mvc.ModelAndView;
import core.nmvc.AbstractNewController;
import next.dao.QuestionDao;

@Controller
public class HomeController extends AbstractNewController {
    private QuestionDao questionDao;
    
    public HomeController(QuestionDao questionDao) {
    	this.questionDao = questionDao;
	}

    @RequestMapping("/")
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return jspView("home.jsp").addObject("questions", questionDao.findAll());
    }
}
