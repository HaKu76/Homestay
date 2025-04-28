package com.example.aop;

import com.example.pojo.dto.query.base.QueryDto;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

// 自动处理分页参数的切面
@Aspect
@Component
public class PagerAspect {

    /**
     * 拦截带@Pager注解的方法
     *
     * @param pager 使用的分页注解
     */
    @Around("@annotation(pager)")
    public Object handlePageableParams(ProceedingJoinPoint joinPoint, Pager pager) throws Throwable {
        Object[] args = joinPoint.getArgs();
        // 遍历方法参数，找到分页参数对象并配置
        for (Object arg : args) {
            if (arg instanceof QueryDto) {
                configPager((QueryDto) arg);
            }
        }
        return joinPoint.proceed(args);
    }

    /**
     * 配置分页参数（页码转偏移量）
     */
    private void configPager(QueryDto queryDTO) {
        if (queryDTO.getCurrent() != null && queryDTO.getSize() != null) {
            // 计算数据库查询的起始位置：例如第2页（每页10条）=> 偏移量10
            queryDTO.setCurrent((queryDTO.getCurrent() - 1) * queryDTO.getSize());
        }
    }
}

