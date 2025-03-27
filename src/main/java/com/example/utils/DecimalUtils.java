package com.example.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额计算工具类
 */
public class DecimalUtils {

    /**
     * 计算商品购买总金额（数量 × 单价 × 折扣），并保留两位小数向上取整
     *
     * 计算逻辑：
     * 1. 将购买数量转换为BigDecimal类型
     * 2. 计算原始总金额（数量 × 单价）
     * 3. 应用折扣（总金额 × 折扣系数）
     * 4. 结果保留两位小数并向上取整（如 12.345 → 12.35）
     *
     * @param buyNumber 购买数量（必须为正整数）
     * @param price     单价（必须为非负数）
     * @param discount  折扣系数（0.0 ~ 1.0 之间，如 0.8 表示八折）
     * @return 计算后的总金额（保留两位小数）
     */
    public static BigDecimal calculateTotal(
            Integer buyNumber,
            BigDecimal price,
            Double discount)
    {
        // 1. 将购买数量转换为BigDecimal
        BigDecimal quantity = BigDecimal.valueOf(buyNumber);

        // 2. 计算原始总金额（数量 × 单价）
        BigDecimal originalTotal = quantity.multiply(price);

        // 3. 应用折扣（总金额 × 折扣系数）
        BigDecimal discountFactor = BigDecimal.valueOf(discount);
        BigDecimal discountedTotal = originalTotal.multiply(discountFactor);

        // 4. 保留两位小数并向上取整
        return discountedTotal
                .setScale(2, RoundingMode.CEILING);
    }
}