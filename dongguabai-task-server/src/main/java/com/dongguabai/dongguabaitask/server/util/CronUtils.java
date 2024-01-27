package com.dongguabai.dongguabaitask.server.util;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dongguabai
 * @Description
 * @Date 创建于 2020-06-20 22:47
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CronUtils {


    public static List<Long> getExecuteMs(String cron, Long endMs) {
        CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));
        ExecutionTime executionTime = ExecutionTime.forCron(parser.parse(cron));
        List<Long> list = new ArrayList<>();
        ZonedDateTime nextExecution = ZonedDateTime.now();
        for (; ; ) {
            nextExecution = executionTime.nextExecution(nextExecution).orElse(null);
            if (nextExecution == null) {
                return list;
            }
            long ms = nextExecution.toInstant().toEpochMilli();
            if (ms <= endMs) {
                list.add(ms);
            } else {
                break;
            }
        }
        return list;
    }

}
