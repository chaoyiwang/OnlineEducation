package com.education.eduservice.entity.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class SubjectData {
    @ExcelProperty(index = 0)
    private String firstClassSubjectName;

    @ExcelProperty(index = 1)
    private String secondClassSubjectName;

}
