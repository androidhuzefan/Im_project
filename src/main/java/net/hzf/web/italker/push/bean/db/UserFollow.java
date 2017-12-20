package net.hzf.web.italker.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户关系的Model（好比中间介质表）
 * 运用一对多模型，用于用户直接进行友好关系的实现
 * 用户与中间表的关系
 * 由多对多转换成一对多
 *
 */
//参考点为UserFollow
@Entity
@Table(name = "TB_USER_FOLLOW")
public class UserFollow {

    @Id
    @PrimaryKeyJoinColumn
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid",strategy = "uuid2")
    @Column(nullable = false ,updatable = false)
    private String id;

    /**
     * 定义关注别人的发起者，你关注某人，主体要包括用户自己
     * 多对一：你可以关注很多人，每关注一个人都会形成一个关注记录
     * 你可以创建很多个关注的信息，所有是多对1
     * 这里的多对一是：User 对应 多个UserFollow
     *  一个user对应多个UserFollowing
     *  optional 不可选，必须存储
     */


    //参考点为UserFollow
    @ManyToOne(optional = false)
    /**
     * 一对多双向 （外键）
     * @JoinColumn注释的是保存表与表之间关系的字段，它要标注在实体属性上
     * 注：name值：一般以一的表为主导
     * 双方都有彼此的信息。 默认情况下，关联的实体的主键一般是用来做外键的
     * name属性是用来标识表中所对应的字段的名称
     * 不设置name的值，则在默认情况下，name的取值遵循以下规则：
     * name=关联表的名称+“_”+ 关联表主键的字段名
     */
    //定义关联的表字段名name为originId，对应的是User.id（默认以关联表的主键为外键）
    //定义的是数据库中的存储字段（关联的表）
    @JoinColumn(name = "originId")
    private User orign;

    //把这个列(关联表的主键id)提取到我们的Model（UserFollow表）中，不允许为null，不允许更新，插入
    //作用：能够高效快捷查询orign的信息
    @Column(nullable = false , updatable = false,insertable = false)
    private String originId;


    /**
     * 定义关注的目标，关注的人
     * 也是多对一：你可以被很多人关注，每次一关注都是一条记录
     * 所有就是 多个UserFollower 对应 一个 User 的情况
     * 多个介质表对应一个target
     *
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "targetId")
    private User target;
    //把这个列(关联表的主键id)提取到我们的Model（UserFollow表）中，不允许为null，不允许更新，插入
    //作用：能够高效快捷查询orign的信息
    @Column(nullable = false,updatable = false,insertable = false)
    private String targetId;


    /**
     * 别名，也是对target的备注
     */
    @Column
    private String alias;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updateAt = LocalDateTime.now();
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getOrign() {
        return orign;
    }

    public void setOrign(User orign) {
        this.orign = orign;
    }

    public String getOrigiinId() {
        return originId;
    }

    public void setOrigiinId(String origiinId) {
        this.originId = origiinId;
    }

    public User getTarget() {
        return target;
    }

    public void setTarget(User target) {
        this.target = target;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }
}
