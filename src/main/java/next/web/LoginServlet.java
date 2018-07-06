package next.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.db.DataBase;
import next.model.User;

@WebServlet("/user/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	User user = DataBase.findUserById(req.getParameter("userId"));
    	if(user != null) { //등록된 Id를 찾을 수 있는 경우
    		if(req.getParameter("password").equals(user.getPassword())) { //등록된 Id의 패스워드가 일치하는 경우(로그인 성공)
            	log.debug("user : {}", user);
        		HttpSession session = req.getSession();
                session.setAttribute("user", user);
                resp.sendRedirect("/index.jsp");
    		}
    	} else { //등록된 Id를 찾을 수 없는 경우(로그인 실패)
    		resp.sendRedirect("/user/login_failed.jsp");
    	}
    }
}
