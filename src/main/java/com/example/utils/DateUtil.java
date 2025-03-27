package com.example.utils;

import com.example.pojo.dto.query.base.QueryDto;
import com.example.pojo.vo.ChartVO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 时间工具类
 */
public class DateUtil {

    /**
     * 构造时间查询器，指定开始时间与结束时间
     *
     * 当days为-1时，返回无时间限制的查询条件；
     * 否则，计算指定天数前的起始时间点（当前时间减去days天后，再加1天并设置为当天0点），
     * 并将结束时间设为当前时间，开始时间设为起始时间的前一秒。
     *
     * @param days 时间范围（-1表示不限制时间）
     * @return 包含时间范围的查询DTO
     */
    public static QueryDto startAndEndTime(Integer days) {
        if (days == -1) {
            return QueryDto.builder().build(); // 直接返回全量查询条件
        }

        LocalDateTime now = LocalDateTime.now();

        // 计算时间范围起点：当前时间减去days天后，再加1天并设置为当天0点
        LocalDateTime startTimePoint = now.minusDays(days).plusDays(1)
                .with(LocalTime.MIN);

        // 开始时间设为起点的前一秒（确保包含完整天数）
        LocalDateTime daysAgoEnd = startTimePoint.minusSeconds(1);

        // 构建查询条件（结束时间默认当前时间）
        return QueryDto.builder()
                .startTime(daysAgoEnd)
                .endTime(now)
                .build();
    }

    /**
     * 统计指定天数内的记录数量（按天分组）
     *
     * 从startDate（当前日期减去dayRange天）开始，遍历每一天到当前日期，
     * 统计每个日期在dates列表中的出现次数，返回包含非零数据的ChartVO对象列表。
     * 日期格式为MM-DD（例如：01-01表示1月1日）。
     *
     * @param dayRange 需统计的天数范围（例如：7表示统计最近7天）
     * @param dates    需统计的日期时间列表
     * @return 按日期分组的统计结果（仅包含有数据的日期）
     */
    public static List<ChartVO> countDatesWithinRange(Integer dayRange, List<LocalDateTime> dates) {
        // 1. 计算统计起始日期（当前日期减去天数范围）
        LocalDate startDate = LocalDate.now().minusDays(dayRange);

        List<ChartVO> chartVos = new ArrayList<>();

        // 2. 遍历每一天进行统计
        for (int offset = 0; offset <= dayRange; offset++) {
            // 当前统计日期
            LocalDate currentDate = startDate.plusDays(offset);

            // 日期格式化（MM-DD）
            String dateKey = String.format("%02d-%02d",
                    currentDate.getMonthValue(),
                    currentDate.getDayOfMonth());

            // 过滤符合条件的日期并统计数量
            long count = dates.stream()
                    .filter(dateTime -> dateTime.toLocalDate().equals(currentDate))
                    .count();

            // 仅添加有数据的日期
            if (count > 0) {
                chartVos.add(new ChartVO(dateKey, (int) count));
            }
        }
        return chartVos;
    }
}