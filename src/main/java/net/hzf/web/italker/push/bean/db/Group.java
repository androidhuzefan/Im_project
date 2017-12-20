package net.hzf.web.italker.push.bean.db;


import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 *  Group与User关系：User（一）是Group（多）的创造者
 *
 *  Group（一）与Message（多）是一对多的关系:
 *  Group是接受者
 *  Group能接受多条信息
 */
@Entity
@Table(name = "TB_GROUP")
public class Group {

    @Id
    @PrimaryKeyJoinColumn
    @GenericGenerator(name = "uuid",strategy = "uuid2")
    @GeneratedValue(generator = "uuid")
    @Column(nullable = false,updatable = false)
    private String id;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    // 定义为更新时间戳，在创建时就已经写入
    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updateAt = LocalDateTime.now();

    //群名字
    @Column(nullable = false)
    private String name;

    //描述
    @Column(nullable = false)
    private String description;

    //头像
    @Column(nullable = false)
    private String picture;

    //群的创造者
    @JoinColumn(name = "ownerId")
    /**
     * OneToMany:Fetch类型选择FetchType.LAZY(懒加载)  ==》不能造成死循环
     * ManyToOne:Fetch类型选择FetchType.EAGER（急加载）
     * 急加载：加载群消息的同时必须加载owner用户的消息（比喻：多一个不影响）
     * 懒加载：加载用户消息不加载集合的消息 （比喻：多一群就影响）
     * 原因：若OneToMany的用户User包含群列表，该列表包含发起的群，若OneToMany运用急加载，又要加载发起群的消息
     * 发起群的ManyToOne又是运用急加载，又是查询User的消息，这样会造成无限循环，最终系统崩坏
     */
    // cascade：联级级别为ALL，所有的更改（更新，删除等）都将进行关系更新
    // optional: 可选为false，必须有一个创建者
    @ManyToOne(optional = false,fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private User owner;
    @Column(nullable = false,updatable = false,insertable = false)
    private String ownerId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
