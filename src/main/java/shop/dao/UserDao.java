package shop.dao;

import shop.domain.User;

public interface UserDao {
    /**
     * 根据用户名查找用户
     * @param username
     * @return
     */
    public User findByUsername(String username);

    /**
     * 用户保存
     * @param user
     */
    public void save(User user);

    /**
     * 根据激活码查用户
     * @param code
     * @return
     */
    User findByCode(String code);

    /**
     * 修改用户激活状态
     * @param user
     */
    void updateStatus(User user);

    /**
     * 根据用户名密码查询用户
     * @param username
     * @param password
     * @return
     */
    User findByUsernameAndPassword(String username, String password);
}
