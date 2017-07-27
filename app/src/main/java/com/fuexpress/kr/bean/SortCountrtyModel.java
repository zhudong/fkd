package com.fuexpress.kr.bean;

/**
 * Created by k550 on 2016/3/22.
 */
public class SortCountrtyModel {
    private String country;   //��ʾ������
    private String sortLetters;  //��ʾ����ƴ��������ĸ
    private String number;
    private String courntyCode;
    public SortCountrtyModel(){

    }
    public SortCountrtyModel(String country, String sortLetters, String number, String courntyCode) {
        this.country = country;
        this.sortLetters = sortLetters;
        this.number = number;
        this.courntyCode = courntyCode;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public void setPhone(String number){this.number =number;}
    public String getPhone(){return this.number;}
    public String getSortLetters() {
        return sortLetters;
    }
    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
    public void setCourntyCode(String courntyCode){this.courntyCode=courntyCode;}
    public String getCourntyCode(){return this.courntyCode;}
}
