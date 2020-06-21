package com.dongguabai.dongguabaitask.server.node;

import com.dongguabai.dongguabaitask.server.service.JobService;
import com.dongguabai.dongguabaitask.server.util.NetUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Dongguabai
 * @Description
 * @Date 创建于 2020-06-20 14:30
 */
@Component
@Slf4j
public class NodeManager implements ApplicationListener<WebServerInitializedEvent> {

    private static final String ROOT_PATH = "/dongguabai-task";

    private static final String SERVER_PATH = ROOT_PATH + "/server";

    private static final String SERVER_NODE_PATH = SERVER_PATH + "/node";

    public static final String TASK_ROOT_PATH = "/dongguabai-task/task";

    private static final Node NODE = new Node();

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(5, new TaskThreadFactory());

    private static List<String> servers;

    @Autowired
    private CuratorFramework zkClient;

    @Autowired
    private JobService jobService;


    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        String address;
        try {
            int port = event.getWebServer().getPort();
            //todo
            address = "127.0.0.1:" + port;
            //注册 task-server
            registTaskServer(address);
            TimeUnit.SECONDS.sleep(10);
            vote();
            //钩子
            hook();
            //todo 优化:1.不需要每次拉取列表，可以每个节点有所有信息（Cache）;2.重新均衡分配；3.优化服务调用影响范围
            PathChildrenCache childrenCache = new PathChildrenCache(zkClient, SERVER_PATH , true);
            childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
            childrenCache.getListenable().addListener((curatorFramework, changeEvent) -> {
                PathChildrenCacheEvent.Type eventType = changeEvent.getType();
                switch (eventType) {
                    case CHILD_REMOVED:
                        log.info("CHILD_REMOVED....");
                        String removedPath = changeEvent.getData().getPath();
                        vote();
                        System.out.println("节点删除....."+removedPath);
                        System.out.println(NODE);
                        removedPath = removedPath.replaceAll(SERVER_PATH+"/","");
                        if (NODE.isMaster() && NODE.isAvailable()){
                            jobService.masterTakeOver(NODE.getNodePath(),removedPath);
                        }
                        break;
                    case CHILD_ADDED:
                        log.info("CHILD_ADDED....");
                        vote();
                        break;
                    default:
                }

            });
            //生成job
            jobSchedule();
            //todo 精度
            new Thread(()->{
                while (true){
                    NODE.setExecute(true);
                    jobService.execute(NODE);
                    NODE.setExecute(false);
                    try {
                        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextLong(1, 5));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
            NODE.setAvailable(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void registTaskServer(String address) {

        try {
            String path = zkClient.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(SERVER_NODE_PATH, (address).getBytes());
            NODE.setNodePath(path.replaceAll(SERVER_PATH + "/", ""));
            log.info("{} regist success..", address);
        } catch (Exception e) {
            throw new RuntimeException("registTaskServer fail..", e);
        }
    }

    private void vote() throws Exception {
        List<String> list = zkClient.getChildren().forPath(SERVER_PATH);
        //排序后的服务节点
        servers = list.stream().sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());
        if (servers.size() == 1) {
            NODE.setMaster(true);
            NODE.setSeq(0);
            return;
        }
        String nodePath = NODE.getNodePath();

        int index = list.indexOf(nodePath);
        NODE.setSeq(index);
        NODE.setMaster(index == 0);
    }

    private void jobSchedule() {
        SCHEDULED_EXECUTOR_SERVICE.scheduleWithFixedDelay(() -> {
            NODE.setGenJob(true);
            long currentMs = System.currentTimeMillis();
            try {
                //获取任务列表
                List<String> list = zkClient.getChildren().forPath(TASK_ROOT_PATH);
                List<String> nodeTasks = getNodeTasks(list,NODE.getSeq(),servers.size());
                List<String> dataList = nodeTasks.stream().map(taskName -> {
                    try {
                        byte[] bytes = zkClient.getData().forPath(TASK_ROOT_PATH + "/" + taskName);
                        return new String(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }).collect(Collectors.toList());
                jobService.genJobs(nodeTasks,dataList,NODE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            NODE.setGenJob(false);
        }, 3, 60, TimeUnit.SECONDS);
    }


    private List<String> getNodeTasks2(int seq) throws Exception {
        List<String> list = zkClient.getChildren().forPath(TASK_ROOT_PATH);
        int serverSize = servers.size();

        int taskSize = list.size();
        int avNum = taskSize / serverSize;
        int more = taskSize % serverSize;

        List<String> avList = list.subList(seq * avNum, seq * avNum + avNum);
        if (more != 0 && more - 1 == seq) {
            avList.add(list.get(seq * avNum + 3 + seq));
        }
        return avList;
    }

    private void hook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NODE.setAvailable(false);
            while (NODE.isGenJob() || NODE.isExecute()) {
            }
        }));
    }

/*    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
       list.add("a");
       list.add("b");
       list.add("c");
       list.add("d");
       list.add("e");
        getNodeTasks(list, 1, 3);

    }*/

    private static List<String> getNodeTasks(List<String> list, int seq, int serverSize) {
        int taskSize = list.size();
        int avNum = taskSize / serverSize;
        int more = taskSize % serverSize;

        List<String> avList = list.subList(seq * avNum, seq * avNum + avNum);
        if (more != 0 && more - 1 == seq) {

            avList.add(list.get(avNum * serverSize + seq));
        }
        System.out.println(avList);
        return avList;
    }

}
