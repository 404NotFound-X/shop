package shop.service.impl;

import shop.dao.UserDao;
import shop.dao.impl.UserDaoImpl;
import shop.domain.User;
import shop.service.UserService;
import shop.util.MailUtils;
import shop.util.UuidUtil;

public class UserServiceImpl implements UserService {
    private UserDao userDao =new  UserDaoImpl();

    @Override
    public boolean regist(User user) {
        User u = userDao.findByUsername(user.getUsername());
        if(u != null){//用户名存在，注册失败
            return false;
        }
        //用户名不存在，注册成功
        user.setCode(UuidUtil.getUuid());
        user.setStatus("N");
        userDao.save(user);

        //发送邮件
        String content = "<a href='http://localhost:8080/activeUserServlet?code="+user.getCode()+"'>点击激活</a>";
        MailUtils.sendMail(user.getEmail(),content,"激活邮件");
        return true;
    }

    @Override
    public boolean active(String code) {
        //根据激活码查询用户
        User user = userDao.findByCode(code);
        //修改用户激活状态
        if(user!=null){
            userDao.updateStatus(user);
            return true;
        }else {
            return false;
        }

    }

    @Override
    public User login(User user) {
        return userDao.findByUsernameAndPassword(user.getUsername(),user.getPassword());
    }
}
