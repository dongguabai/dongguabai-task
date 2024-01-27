package com.dongguabai.dongguabaitask.client;

import com.dongguabai.dongguabaitask.client.regist.SimpleTaskRegist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author Dongguabai
 * @Description todo 提供jar包，引用上传Task
 * @Date 创建于 2020-06-21 08:51
 */
@RestController
public class RegistController implements ApplicationListener<WebServerInitializedEvent> {

    private int port;

    @Autowired
    private SimpleTaskRegist simpleTaskRegist;

    @RequestMapping("a")
    public void a(@RequestParam("node")String node){
        print(node,"a");
    }

    private void print(String node,String methodName) {
        System.out.println("------------");
        System.out.println("Time："+new Date().toLocaleString());
        System.out.println("NODE："+node);
        System.out.println("Method："+methodName);
        System.out.println("Port："+port);
        System.out.println("------------");
    }

    @RequestMapping("b")
    public void b(@RequestParam("node")String node){
        print(node,"b");
    }

    @RequestMapping("c")
    public void c(@RequestParam("node")String node){
        print(node,"c");
    }

    @RequestMapping("d")
    public void d(@RequestParam("node")String node){
        print(node,"d");
    }

    @RequestMapping("e")
    public void e(@RequestParam("node")String node){
        print(node,"e");
    }


    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        port = event.getWebServer().getPort();
        simpleTaskRegist.regist("a","0 */1 * * * ?","127.0.0.1:"+port+"-a");
        simpleTaskRegist.regist("b","0 */2 * * * ?","127.0.0.1:"+port+"-b");
        simpleTaskRegist.regist("c","0 */1 * * * ?","127.0.0.1:"+port+"-c");
        simpleTaskRegist.regist("d","0 */2 * * * ?","127.0.0.1:"+port+"-d");
        simpleTaskRegist.regist("e","0 */1 * * * ?","127.0.0.1:"+port+"-e");
    }


}
