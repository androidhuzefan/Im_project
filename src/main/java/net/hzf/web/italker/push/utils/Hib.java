package net.hzf.web.italker.push.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Created by qiujuer
 * on 2017/2/17.
 */
public class Hib {
    // 全局SessionFactory
    private static SessionFactory sessionFactory;

    static {
        // 静态初始化sessionFactory
        init();
    }

    private static void init() {
        // 从hibernate.cfg.xml文件初始化
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            // build 一个sessionFactory
            sessionFactory = new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            // 错误则打印输出，并销毁
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    /**
     * 获取全局的SessionFactory
     *
     * @return SessionFactory
     */
    public static SessionFactory sessionFactory() {
        return sessionFactory;
    }

    /**
     * 从SessionFactory中得到一个Session会话
     *
     * @return Session
     */
    public static Session session() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * 关闭sessionFactory
     */
    public static void closeFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }


//简化Session的操作工具方法（无返回值）
//用户实际操作的接口
public interface QueryOnly{
    void queryOnly(Session session);
}
public static void queryOnly(QueryOnly queryOnly){
    //新建一个新的会话
    Session session = sessionFactory.openSession();
    //开启一个新的事务
    final Transaction transaction = session.beginTransaction();
    try{
        //调用传递进来的接口
        //并调用接口的方法把sessio传递出去
        queryOnly.queryOnly(session);
        //提交事务
        transaction.commit();
    }catch (Exception e){
        try {
            //失败时回滚事务
            transaction.rollback();
        }catch (Exception e1){
            e1.printStackTrace();
        }finally{
            //无论是否成功，关闭会话
            session.close();
        }
    }
}

//用户实际操作的接口（有返回值）
public interface Query<T>{
    T query(Session session);
}
public static<T> T query(Query<T> query){
      Session session = sessionFactory.openSession();
      final Transaction transaction = session.beginTransaction();
      T t = null;
      try{
          t = query.query(session);
          transaction.commit();
      }catch (Exception e){
        try{
            transaction.rollback();
        }catch (Exception e1){
            e1.printStackTrace();
        }finally {
            session.close();
        }
      }
      return t;
}
}
