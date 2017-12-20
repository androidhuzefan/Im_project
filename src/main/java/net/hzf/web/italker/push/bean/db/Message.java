package net.hzf.web.italker.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 一对多的关系
 * User与Message的关系就是一对多关系
 * User能发送多条信息
 * User能接受多条信息
 *
 * 一个群Group可以接受多条信息Message
 */
@Entity
@Table(name = "TB_MESSAGE")
public class Message {
    private static final int TYPE_STR = 1;//字符串类型
    private static final int TYPE_PIC = 1;//图片类型
    private static final int TYPE_FILE = 3 ;//文件类型
    private static final int TYPE_AUDIO = 4;//语音类型

    @Id
    //这里不自动生成uuid，id由代码写入，由客户端负责生成
    //消息通过接口发送到服务器，服务器存储消息，并且id和客户端的id一样
    //目的：为了避免复杂的服务器和客户端的映射关系
    @GenericGenerator(name = "uuid",strategy =  "uuid2")
    @Column(nullable = false , updatable = false)
    private String id;

    //发送的消息
    //Text为了发送更多的信息
    @Column(nullable = false,columnDefinition = "TEXT")
    private String content;

    //附件
    @Column
    private String attach;

    //消息类型
    @Column(nullable = false)
    private int type;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updateAt = LocalDateTime.now();

    //接收者 可以为空
    @JoinColumn(name = "receiverId")
    @ManyToOne
    private User receiver;
    //该列会自动生成
    @Column(updatable = false,insertable = false)
    private String receiverId;

    //发送者
    //一对多的关系：一个发送者对应多条信息
    @JoinColumn(name = "senderId")
    @ManyToOne(optional = false)
    private User sender;
    // 这个字段仅仅只是为了对应sender的数据库字段senderId
    // 不允许手动的更新或者插入
    //为了给用户的Model添加懒依赖
    @Column(updatable = false,insertable = false ,nullable = false)
    private String senderId;


    // 一个群可以接收多个消息,群可以为空
    @ManyToOne
    @JoinColumn(name = "groupId")
    private Group group;
    @Column(updatable = false,insertable = false)
    private String groupId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
