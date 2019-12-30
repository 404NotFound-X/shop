package shop.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;
import shop.domain.ResultInfo;
import shop.domain.User;
import shop.service.UserService;
import shop.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/loginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //实例化返回消息
        ResultInfo resultInfo = new ResultInfo();

        //验证码校验
        String check = request.getParameter("check");

        HttpSession session = request.getSession();
        String checkcode = (String) session.getAttribute("CHECKCODE_SERVER");
        session.removeAttribute("CHECKCODE_SERVER");

        if(checkcode == null || !checkcode.equalsIgnoreCase(check)){//验证码不正确
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("验证码错误");
        }else { //验证码正确
            //获取数据
            Map<String, String[]> parameterMap = request.getParameterMap();

            //封装对象
            User user = new User();
            try {
                BeanUtils.populate(user,parameterMap);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            //调用service查询
            UserService userService = new UserServiceImpl();
            User u = userService.login(user);

            if(u == null){
                resultInfo.setFlag(false);
                resultInfo.setErrorMsg("用户名或密码错误");
            }
            if(u != null && !"Y".equals(u.getStatus())){
                resultInfo.setFlag(false);
                resultInfo.setErrorMsg("用户未激活");
            }
            if(u != null && "Y".equals(u.getStatus())){
                resultInfo.setFlag(true);
                request.getSession().setAttribute("user",u);
            }
        }
        //将info序列化为json
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(resultInfo);

        //将json写回客户端
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(json);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}
