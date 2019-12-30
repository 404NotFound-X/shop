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

@WebServlet("/registerServlet")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //实例化返回消息
        ResultInfo resultInfo = new ResultInfo();

        //验证码校验
        String check = request.getParameter("check");

        HttpSession session = request.getSession();
        String checkcode_server = (String) session.getAttribute("CHECKCODE_SERVER");
        session.removeAttribute("CHECKCODE_SERVER");//保证验证码只能使用一次

        if( checkcode_server == null||!checkcode_server.equalsIgnoreCase(check)){
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("验证码错误");
        }else{
            //获取数据
            Map<String, String[]> map = request.getParameterMap();

            //封装对象
            User user = new User();
            try {
                BeanUtils.populate(user,map);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            //调用service进行注册
            UserService userService = new UserServiceImpl();
            boolean flag = userService.regist(user);

            //判断是否成功
            if(flag){//注册成功
                resultInfo.setFlag(true);
            }else {//注册失败
                resultInfo.setFlag(false);
                resultInfo.setErrorMsg("注册失败,用户名已存在");
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
