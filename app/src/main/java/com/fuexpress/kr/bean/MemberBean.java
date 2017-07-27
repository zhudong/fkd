package com.fuexpress.kr.bean;

/**
 * Created by Longer on 2017/5/18.
 * 会员组的数据封装Bean
 */
public class MemberBean {

    private String memberName;

    private boolean isSelected;

    private int memberId;

    private String singAndPrice;

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getSingAndPrice() {
        return singAndPrice;
    }

    public void setSingAndPrice(String singAndPrice) {
        this.singAndPrice = singAndPrice;
    }

    public MemberBean(String name, boolean isSelected) {
        memberName = name;
        this.isSelected = isSelected;
    }

    public MemberBean(){}

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
}
