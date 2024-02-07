package io.github.dongguabai.server.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dongguabai
 * @Description
 * @Date 创建于 2020-06-20 00:04
 */
@RestController
public class TaskController {

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("a")
    public Object registTask() {


        restTemplate.getForObject("http://localhost:9999/a?node="+"aaa",Void.class);
        return null;
    }
}
