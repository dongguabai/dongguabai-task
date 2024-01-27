package com.dongguabai.dongguabaitask.client.regist;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 任务注册（这里简化，直接 Controller 注册，其实还可以使用注解、接口编程方式处理）;
 * 一般是需要启动一个 Web Server（Netty）去执行 job
 *
 * @author Dongguabai
 * @Date 创建于 2020-06-20 00:57
 */
@Component
public class SimpleTaskRegist {

    private static final String TASK_ROOT_PATH = "/dongguabai-task/task";

    @Autowired
    private CuratorFramework zkClient;

    public void regist( String taskName, String cron, String requestUrl) {
        String taskPath = TASK_ROOT_PATH + "/" + taskName;
        createPath(taskPath,CreateMode.PERSISTENT,cron.getBytes());
        createPath(taskPath+"/"+requestUrl,CreateMode.EPHEMERAL,new byte[0]);
    }

    private void createPath(String path, CreateMode model,byte[] data) {
        try {
            zkClient.create().creatingParentsIfNeeded()
                    .withMode(model).forPath(path,data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
