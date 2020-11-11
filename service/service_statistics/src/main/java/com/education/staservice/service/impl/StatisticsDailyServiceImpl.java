package com.education.staservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.education.staservice.client.UcentreClient;
import com.education.staservice.entity.StatisticsDaily;
import com.education.staservice.mapper.StatisticsDailyMapper;
import com.education.staservice.service.StatisticsDailyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 网站统计日数据 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-10-07
 */
@Service
public class StatisticsDailyServiceImpl extends ServiceImpl<StatisticsDailyMapper, StatisticsDaily> implements StatisticsDailyService {
    @Autowired
    private UcentreClient ucentreClient;

    //统计某一天注册人数
    @Override
    public void createStatisticsByDay(String day) {
        //删除已存在的统计对象
        QueryWrapper<StatisticsDaily> dayQueryWrapper = new QueryWrapper<>();
        dayQueryWrapper.eq("date_calculated", day);
        baseMapper.delete(dayQueryWrapper);


        //获取统计信息，远程调用得到某一天注册人数
        Integer registerNum = (Integer) ucentreClient.registerCount(day).getData().get("countRegister");
        Integer loginNum = RandomUtils.nextInt(100, 200);//TODO
        Integer videoViewNum = RandomUtils.nextInt(100, 200);//TODO
        Integer courseNum = RandomUtils.nextInt(100, 200);//TODO

        //创建统计对象
        StatisticsDaily daily = new StatisticsDaily();
        daily.setRegisterNum(registerNum);//注册人数
        daily.setLoginNum(loginNum);
        daily.setVideoViewNum(videoViewNum);
        daily.setCourseNum(courseNum);
        daily.setDateCalculated(day);//统计日期

        //把获取数据添加数据库，统计分析表里面
        baseMapper.insert(daily);
    }
    //图表显示，返回两部分数据，日期json数组，数量json数组
    @Override
    public Map<String, Object> getChartData(String begin, String end, String type) {
        QueryWrapper<StatisticsDaily> dayQueryWrapper = new QueryWrapper<>();
        //type用来指定查询的列
        dayQueryWrapper.select("date_calculated", type);
        //指定查询的内容
        dayQueryWrapper.between("date_calculated", begin, end);
        List<StatisticsDaily> dayList = baseMapper.selectList(dayQueryWrapper);
        //因为返回有两部分数据，日期和日期对应的数量，前端要求数组json结构，对应后端java代码是list集合
        List<Integer> dataList = new ArrayList<Integer>();
        List<String> dateList = new ArrayList<String>();
        //遍历查询所有数据list集合，进行封装
        for (int i = 0; i < dayList.size(); i++) {
            StatisticsDaily daily = dayList.get(i);
            //封装日期list集合
            dateList.add(daily.getDateCalculated());
            //封装数量list集合
            switch (type) {
                case "register_num":
                    dataList.add(daily.getRegisterNum());
                    break;
                case "login_num":
                    dataList.add(daily.getLoginNum());
                    break;
                case "video_view_num":
                    dataList.add(daily.getVideoViewNum());
                    break;
                case "course_num":
                    dataList.add(daily.getCourseNum());
                    break;
                default:
                    break;
            }
        }
        //把封装之后两个list集合放到map集合，进行返回
        Map<String, Object> map = new HashMap<>();
        map.put("dataList", dataList);
        map.put("dateList", dateList);

        return map;
    }
}
