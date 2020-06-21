package com.dongguabai.dongguabaitask.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Dongguabai
 * @Description
 * @Date 创建于 2020-06-20 00:04
 */
@RequestMapping("task")
@RestController
public class TaskController {

    @RequestMapping("regist")
    public Object registTask(@RequestParam("group") String group, @RequestParam("requestUrl") String requestUrl) {

        return null;

    }
}
