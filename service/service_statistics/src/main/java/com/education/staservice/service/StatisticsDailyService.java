package com.education.staservice.service;

import com.education.staservice.entity.StatisticsDaily;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 服务类
 * </p>
 *
 * @author testjava
 * @since 2020-10-07
 */
public interface StatisticsDailyService extends IService<StatisticsDaily> {
    //统计某一天注册人数
    void createStatisticsByDay(String day);
    //图表显示，返回两部分数据，日期json数组，数量json数组
    Map<String, Object> getChartData(String begin, String end, String type);
}
