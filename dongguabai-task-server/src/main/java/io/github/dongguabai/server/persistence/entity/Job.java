package io.github.dongguabai.server.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Dongguabai
 * @Description
 * @Date 创建于 2020-06-20 22:25
 */
@Table(name = "dongguabai_job")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Job {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;

    private Long executeMs;

    private Integer serverSeq;

    private String serverNodePath;

    /**
     * todo 项目组
     */
    private String groupName;

    private String taskName;

    /**
     * 0：未执行；1：已执行;-1：执行异常
     */
    private int status;

    public Job copy(){
        Job job = new Job();
        job.setId(id);
        job.setExecuteMs(executeMs);
        job.setServerSeq(serverSeq);
        job.setServerNodePath(serverNodePath);
        job.setGroupName(groupName);
        job.setTaskName(taskName);
        return job;
    }
}
