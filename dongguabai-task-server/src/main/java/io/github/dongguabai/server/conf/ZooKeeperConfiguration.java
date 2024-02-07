package io.github.dongguabai.server.conf;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Dongguabai
 * @Description
 * @Date 创建于 2020-06-20 00:27
 */
@Configuration
public class ZooKeeperConfiguration {

    private static final String ZOOKEEPER_URL = "192.168.2.52:2181";

    @Bean
    public CuratorFramework getCuratorFramework(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(ZOOKEEPER_URL,retryPolicy);
        client.start();
        return client;
    }
}