package net.hzf.web.italker.push.bean.db;


import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户的Model,对应的数据库
 */
@Entity
@Table( name = "TB_USER" )
public class User {
    /**
     * 运用uuid的好处：
     * 用户id一般为int或long，id是阿拉伯数字，作用是id能自动增长，若id一旦暴露在客户端，反编译客户端的id
     * 发现数据库主键是自增，使用攻击手法提取用户信息
     *
     * uuid的运算方法：当前时间和日期，时钟序列和全局唯一的IEEE机器识别号。这就能保证生成的uuid不等，唯一性
     * 是让分布式系统中的所有元素，都能有唯一的辨识资讯
     */
    //这是一个主键
    @Id
    @PrimaryKeyJoinColumn
    //主键生成的存储数据类型uuid
    @GeneratedValue(generator = "uuid")
    //把uuid的生成器定义为uuid2，uuid2为常规的uuid
    @GenericGenerator(name="uuid" ,strategy = "uuid2")
    //不允许更新，不允许为空
    @Column(nullable = false ,updatable = false)
    private String id;

    //用户的名字，用户名唯一性
    @Column(nullable = false,length = 128 ,unique = true)
    private String name;

    //电话号码
    @Column(nullable = false,unique = true,length = 62)
    private String phone;

    //登陆密码
    @Column(nullable = false)
    private String password;

    //头像
    @Column
    private String portrait;

    //描述
    @Column
    private String description;

    //性别，初始化有值，不允许为空
    @Column(nullable = false)
    private int sex=0;

    //登陆才有token，使用token来拉取用户信息
    //三种方法拉取用户信息，用户id和phone，token
    @Column(unique = true)
    private String token;

    //用于推送的设备id
    @Column
    private String pushId;


    //创建时自动加载到数据库
    @Column(nullable = false)
    //定义创建时间戳
    @CreationTimestamp
    private LocalDateTime createAt =LocalDateTime.now();

    // 定义为更新时间戳，在创建时就已经写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    // 最后一次收到消息的时间
    @Column
    private LocalDateTime lastReceiveredAt = LocalDateTime.now();


    /**
     * 一对多双向
     */
    //我关注的人的列表
    //对应的数据库表字段为TB_USER_FOLLOW.targetId
    @JoinColumn(name = "originId")
    //加载自己用户的信息，不加载我关注的人的列表
    /**
     * LazyCollectionOption.TRUE：集合具有延迟性，只有在访问的时候才加载。
     LazyCollectionOption.EXTRA：集合具有延迟性，并且所有的操作都会尽量避免加载集合， 对于一个巨大的集合特别有用，因为这样的集合中的元素没有必要全部加载。
     LazyCollectionOption.FALSE：非延迟加载的关联。

     FetchType.LAZY：懒加载，加载一个实体时，定义懒加载的属性不会马上从数据库中加载，即从数据库读到内存。
     FetchType.EAGER：急加载，加载一个实体时，定义急加载的属性会立即从数据库中加载。也可以说成表示关联关系的从类在主类加载时同时加载。
     */
    @LazyCollection(LazyCollectionOption.EXTRA)
    //与 @LazyCollection(LazyCollectionOption.EXTRA)一起使用
    //一对多：一个用户可以被很多人关注，每一次关注都是一个记录
    //参考点：User
    /**
     * cascade(级联)
     级联在编写触发器时经常用到，触发器的作用是当 主控表信息改变时，用来保证其关联表中数据同步更新。
     若对触发器来修改或删除关联表相记录，
     必须要删除对应的关联表信息，
     否则，会存有脏数据
     */
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    //Set不重复
    private Set<UserFollow> following = new HashSet<>();


    // 关注我的人的列表
    // 对应的数据库表字段为TB_USER_FOLLOW.targetId
    //注释的是另一个表指向本表的外键
    /**
     * 关注我的人的列表
     */
    @JoinColumn(name = "targetId")
    // 定义为懒加载，默认加载User信息的时候，并不查询这个集合
    @LazyCollection(LazyCollectionOption.EXTRA)
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<UserFollow> follower = new HashSet<>();


    //我所创造的群
    // 对应的字段为：Group.ownerId
    @JoinColumn(name = "ownerId")
    /**
     * 懒加载集合
     * 尽可能不加载具体的数据，当访问groups.size()仅仅查询数量
     * 不加载具体的group信息
     * 只有当遍历集合的时候才加载具体的数据
     */
    @LazyCollection(LazyCollectionOption.EXTRA)
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<Group> groups = new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public LocalDateTime getLastReceiveredAt() {
        return lastReceiveredAt;
    }

    public void setLastReceiveredAt(LocalDateTime lastReceiveredAt) {
        this.lastReceiveredAt = lastReceiveredAt;
    }

    public Set<UserFollow> getFollowing() {
        return following;
    }

    public void setFollowing(Set<UserFollow> following) {
        this.following = following;
    }

    public Set<UserFollow> getFollower() {
        return follower;
    }

    public void setFollower(Set<UserFollow> follower) {
        this.follower = follower;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }
}
