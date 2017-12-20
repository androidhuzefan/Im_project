package net.hzf.web.italker.push.bean.db;


        import org.hibernate.annotations.CreationTimestamp;
        import org.hibernate.annotations.GenericGenerator;
        import org.hibernate.annotations.UpdateTimestamp;

        import javax.persistence.*;
        import java.time.LocalDateTime;

/**
 * 申请记录
 */
@Entity
@Table(name = "TB_APPLY")
public class Apply {
    private static final int TYPE_ADD_USER = 1;//添加好友
    private static final int TYPE_ADD_GROUP =2;//加入群

    @Id
    @PrimaryKeyJoinColumn
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid",strategy = "uuid2")
    @Column(nullable = false,updatable = false)
    private String id;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createAt = LocalDateTime.now();

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updateAt = LocalDateTime.now();

    //申请信息的描述
    @Column(nullable = false)
    private String description;

    //附件
    @Column(columnDefinition = "TEXT")
    //比如图片地址
    private String attach;

    //当前申请的类型
    @Column(nullable = false)
    private int type;

    //申请目标 id
    //不进行强关联，不建立主外键关系
    //type：TYPE_ADD_USER:user.id
    //type:TYPE_ADD_GROUP:group.id
    @Column(nullable = false)
    private String targetId;

    // 申请人 可为空 为系统信息
    // 一个人可以有很多个申请
    @JoinColumn(name = "applicantId")
    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private User applicant;
    @Column(updatable = false,insertable = false)
    private String applicantId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }
}
