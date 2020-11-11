package com.education.servicebase.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor//有参构造方法
@NoArgsConstructor//无参构造方法
public class EducationException extends RuntimeException {

    private Integer code;//状态码

    private String msg;//异常信息

}
