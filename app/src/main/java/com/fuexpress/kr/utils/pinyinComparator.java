package com.fuexpress.kr.utils;


import com.fuexpress.kr.bean.SortCountrtyModel;

import java.util.Comparator;

/**
 *
 * @author xiaanming
 *
 */
public class pinyinComparator implements Comparator<SortCountrtyModel> {

    public int compare(SortCountrtyModel o1, SortCountrtyModel o2) {
        if (o1.getSortLetters().equals("@")
                || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }

}