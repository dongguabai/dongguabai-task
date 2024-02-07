package io.github.dongguabai.server.service;

import io.github.dongguabai.server.node.Node;
import io.github.dongguabai.server.persistence.entity.Job;
import io.github.dongguabai.server.persistence.repository.JobMapper;
import io.github.dongguabai.server.util.CronUtils;
import io.github.dongguabai.server.node.NodeManager;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dongguabai
 * @Description
 * @Date 创建于 2020-06-20 22:23
 */
@Service
public class JobService {

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private CuratorFramework zkClient;

    @Autowired
    private RestTemplate restTemplate;

    public void genJobs(List<String> taskNames, List<String> datas, Node node) {
        System.out.println("genJobs........");
        System.out.println("taskNames..."+taskNames);
        System.out.println("datas...."+datas);
        if (taskNames == null || taskNames.isEmpty()){
            return;
        }

        List<Job> jobList = new ArrayList<>();
        for (int i = 0; i < taskNames.size(); i++) {
            String cron = datas.get(i);
            if (cron == null || "".equals(cron)){
                continue;
            }
            Job job = new Job();
            job.setTaskName(taskNames.get(i));
            job.setServerNodePath(node.getNodePath());
            job.setServerSeq(node.getSeq());
            job.setStatus(0);
            job.setGroupName("");
            Long endMs = LocalDateTime.now().plus(1, ChronoUnit.MINUTES).toInstant(ZoneOffset.of("+8")).toEpochMilli();
            List<Long> msList = CronUtils.getExecuteMs(cron, endMs);
            if (CollectionUtils.isEmpty(msList)){
                continue;
            }
            List<Job> jobs = msList.stream().map(ms -> {
                Job copy = job.copy();
                copy.setExecuteMs(ms);
                return copy;
            }).collect(Collectors.toList());
            jobList.addAll(jobs);
        }
        System.out.println(jobList);
        jobMapper.insertList(jobList);
    }

    /**
     * todo 优化执行间隔
     * @param node
     */
    public void execute(Node node) {
        List<Job> jobs = jobMapper.selectExecuteJobs(node.getNodePath(),System.currentTimeMillis());
        if (CollectionUtils.isEmpty(jobs)){
            return;
        }
        for (Job job : jobs) {
            try {
                List<String> list = zkClient.getChildren().forPath(NodeManager.TASK_ROOT_PATH + "/" + job.getTaskName());
                if (CollectionUtils.isEmpty(list)){
                    jobMapper.updateJobStatus(job.getId(),-1);
                    continue;
                }
                //todo 简化执行

                String requestUrl = list.get((int) (Math.random() * list.size()));
                requestUrl = requestUrl.replaceAll("-","/");

                restTemplate.getForObject("http://"+requestUrl+"?node="+node.toString(),Void.class);
                jobMapper.updateJobStatus(job.getId(),1);
            } catch (Exception e) {
                jobMapper.updateJobStatus(job.getId(),-1);
                e.printStackTrace();
            }
        }

    }

    public void masterTakeOver(String nodePath, String removedPath) {
        jobMapper.masterTakeOver(nodePath,removedPath);
    }
}
