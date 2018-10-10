package com.zzh12138.jzvideodemo.bean;

/**
 * Created by zhangzhihao on 2018/9/21 10:30.
 */
public class CommentBean {
    private String id;
    private String userIcon;
    private String userName;
    private int praiseNum;
    private String content;
    private CommentBean parentComment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPraiseNum() {
        return praiseNum;
    }

    public void setPraiseNum(int praiseNum) {
        this.praiseNum = praiseNum;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CommentBean getParentComment() {
        return parentComment;
    }

    public void setParentComment(CommentBean parentComment) {
        this.parentComment = parentComment;
    }
}
