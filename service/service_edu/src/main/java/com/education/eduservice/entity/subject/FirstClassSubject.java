package com.education.eduservice.entity.subject;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FirstClassSubject {
    private String id;
    private String title;
    //一个一级分类有多个二级分类
    private List<SecondClassSubject> children = new ArrayList<>();
}
