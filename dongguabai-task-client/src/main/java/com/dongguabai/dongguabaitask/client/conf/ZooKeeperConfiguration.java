package com.dongguabai.dongguabaitask.client.conf;

@Configuration
public class ZooKeeperConfiguration {

    private static final String ZOOKEEPER_URL = "192.168.43.6:2181";

    @Bean
    public CuratorFramework getCuratorFramework(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(ZOOKEEPER_URL,retryPolicy);
        client.start();
        return client;
    }
}