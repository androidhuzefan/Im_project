package net.hzf.web.italker.push.factory;

import com.google.common.base.Strings;
import net.hzf.web.italker.push.bean.db.User;
import net.hzf.web.italker.push.utils.Hib;
import net.hzf.web.italker.push.utils.TextUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.UUID;

/**
 * @author shaocheng
 * @version 1.0.0
 */
public class UserFactory {
/*    // 通过Token字段查询用户信息
    // 只能自己使用，查询的信息是个人信息，非他人信息
    public static User findByToken(String token) {
        return Hib.query(session -> (User) session
                .createQuery("from User where token=:token")
                .setParameter("token", token)
                .uniqueResult());
    }

    // 通过Phone找到User
    public static User findByPhone(String phone) {
        return Hib.query(session -> (User) session
                //创建一个查询
                .createQuery("from User where phone=:inPhone")
                .setParameter("inPhone", phone)
                .uniqueResult());
    }

    // 通过Name找到User
    public static User findByName(String name) {
        return Hib.query(session -> (User) session
                .createQuery("from User where name=:name")
                .setParameter("name", name)
                .uniqueResult());
    }
  */
    //通过phone找到User
public static User findByPhone(String phone){
   return Hib.query(new Hib.Query<User>() {
        @Override
        public User query(Session session) {
           User user = (User) session.createQuery("from User where phone=:inphone")
                    .setParameter("inphone",phone)
                    .uniqueResult();
            return user ;
        }
    });
}
   //通过name找到User
public static User findByName(String name){
    return Hib.query(new Hib.Query<User>() {
        @Override
        public User query(Session session) {
            User user = (User) session.createQuery("from User where name=:name")
                    .setParameter("name",name)
                    .uniqueResult();
            return user;
        }
    });
}
    /**
     * 更新用户信息到数据库
     *
     * @param user User
     * @return User
     */
    public static User update(User user) {
        return Hib.query(session -> {
            session.saveOrUpdate(user);
            return user;
        });
    }


    /**
     * 给当前的账户绑定PushId
     *
     * @param user   自己的User
     * @param pushId 自己设备的PushId
     * @return User
     */
    public static User bindPushId(User user, String pushId) {
        if (Strings.isNullOrEmpty(pushId))
            return null;

        // 第一步，查询是否有其他账户绑定了这个设备
        // 取消绑定，避免推送混乱
        // 查询的列表不能包括自己
        Hib.queryOnly(session -> {
            @SuppressWarnings("unchecked")
            List<User> userList = (List<User>) session
                    .createQuery("from User where lower(pushId)=:pushId and id!=:userId")
                    .setParameter("pushId", pushId.toLowerCase())
                    .setParameter("userId", user.getId())
                    .list();

            for (User u : userList) {
                // 更新为null
                u.setPushId(null);
                session.saveOrUpdate(u);
            }
        });

        if (pushId.equalsIgnoreCase(user.getPushId())) {
            // 如果当前需要绑定的设备Id，之前已经绑定过了
            // 那么不需要额外绑定
            return user;
        } else {
            // 如果当前账户之前的设备Id，和需要绑定的不同
            // 那么需要单点登录，让之前的设备退出账户，
            // 给之前的设备推送一条退出消息
            if (Strings.isNullOrEmpty(user.getPushId())) {
                // TODO 推送一个退出消息
            }

            // 更新新的设备Id
            user.setPushId(pushId);
            return update(user);
        }
    }

    /**
     * 使用账户和密码进行登录
     */
    public static User login(String account, String password) {
        final String accountStr = account.trim();
        // 把原文进行同样的处理，然后才能匹配
        final String encodePassword = encodePassword(password);

        // 寻找
        User user = Hib.query(session -> (User) session
                .createQuery("from User where phone=:phone and password=:password")
                .setParameter("phone", accountStr)
                .setParameter("password", encodePassword)
                .uniqueResult());

        if (user != null) {
            // 对User进行登录操作，更新Token
            user = login(user);
        }
        return user;
    }


    /**
     *  用户注册
     *  注册的操作需要写入数据库，并返回数据库中的User信息
     * @param account 账号
     * @param password 加密密码
     * @param name 用户名
     * @return
     */
   public static User register(String account ,String password , String name){
       //去除首位的空格
       account = account.trim();
       password = encodePassword(password);

       User user = new User();
       user.setName(name);
       user.setPhone(account);
       user.setPassword(password);


       return Hib.query(new Hib.Query<User>() {
           @Override
           public User query(Session session) {
               session.save(user);
               return user;
           }
       });

   }


    /**
     * 注册部分的新建用户逻辑
     *
     * @param account  手机号
     * @param password 加密后的密码
     * @param name     用户名
     * @return 返回一个用户
     */
    private static User createUser(String account, String password, String name) {
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        // 账户就是手机号
        user.setPhone(account);
        // 数据库存储
        return Hib.query(session -> {
            session.save(user);
            return user;
        });
    }


    /**
     * 把一个User进行登录操作
     * 本质上是对Token进行操作
     *
     * @param user User
     * @return User
     */
    private static User login(User user) {
        // 使用一个随机的UUID值充当Token
        String newToken = UUID.randomUUID().toString();
        // 进行一次Base64格式化
        newToken = TextUtil.encodeBase64(newToken);
        user.setToken(newToken);

        return update(user);
    }


    /**
     * 对密码进行加密
     * @param password 原密码
     * @return 加密密码
     */
    private static String encodePassword(String password){
        //去除密码首尾的空格
        password = password.trim();
        //非对称加密，只有八位
        password = TextUtil.getMD5(password);
        //进行一次对称的Base64加密，可以采取加盐的方案（当前的时间+password（原））
        return TextUtil.encodeBase64(password);
}

}
