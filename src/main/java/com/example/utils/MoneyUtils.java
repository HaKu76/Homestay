package com.example.utils;

import com.example.pojo.dto.query.extend.MoneyDto;
import com.example.pojo.vo.ChartVO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 货币统计工具类
 */
public class MoneyUtils {

    /**
     * 生成指定天数内的每日成交额统计报表
     *
     * @param dayRange     统计天数（必须为非负整数）
     * @param moneyDtos    成交记录列表（必须包含有效的支付时间和金额）
     * @return 每日成交额统计结果（格式：MM-dd → 金额）
     */
    public static List<ChartVO> generateDailySalesReport(
            int dayRange,
            List<MoneyDto> moneyDtos)
    {
        // 1. 计算起始日期（当前日期 - dayRange天）
        LocalDate startDay = LocalDate.now().minusDays(dayRange);

        // 2. 初始化结果列表
        List<ChartVO> chartData = new ArrayList<>();

        // 3. 遍历每一天（从起始到当前日期）
        for (int dayOffset = 0; dayOffset <= dayRange; dayOffset++) {
            LocalDate currentDate = startDay.plusDays(dayOffset);

            // 4. 生成日期键（格式：MM-dd）
            String dateKey = String.format("%02d-%02d",
                    currentDate.getMonthValue(),
                    currentDate.getDayOfMonth());

            // 5. 过滤并计算该日总金额
            double totalAmount = moneyDtos.stream()
                    .filter(order -> order.getPayTime() != null
                            && order.getPayTime().toLocalDate().equals(currentDate))
                    .mapToDouble(order -> order.getAmount().doubleValue())
                    .sum();

            // 6. 仅添加有成交额的日期
            if (totalAmount > 0) {
                chartData.add(new ChartVO(dateKey, totalAmount));
            }
        }
        return chartData;
    }
}
