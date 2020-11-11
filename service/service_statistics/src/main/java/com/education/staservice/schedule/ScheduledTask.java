package com.education.staservice.schedule;

import com.education.staservice.service.StatisticsDailyService;
import com.education.staservice.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class ScheduledTask {
    @Autowired
    private StatisticsDailyService dailyService;

    //定时每天凌晨1点执行
    @Scheduled(cron = "0 0 1 * * ?")
    public void task() {
        //获取前一天日期：当前日期-1
        String day = DateUtil.formatDate(DateUtil.addDays(new Date(), -1));
        dailyService.createStatisticsByDay(day);
    }
}
