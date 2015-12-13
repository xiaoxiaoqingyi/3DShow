package com.example.user.photocollecting.Util;

import com.example.user.photocollecting.entity.Goods;

import java.util.Comparator;

/**
 * Created by Administrator on 2015/12/2.
 */
public class FileComparator implements Comparator<Goods> {
    public int compare(Goods file1, Goods file2) {
        if(file1.getLastModified() < file2.getLastModified())
        {
            return 1;
        }else
        {
            return -1;
        }
    }
}
