package io.github.dongguabai.server.persistence.repository;

import io.github.dongguabai.server.persistence.GenericMapper;
import io.github.dongguabai.server.persistence.entity.Job;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author Dongguabai
 * @Description
 * @Date 创建于 2020-06-20 22:25
 */
public interface JobMapper extends GenericMapper<Job> {
    @Select("select * from dongguabai_job where server_node_path =#{nodePath} and status = 0 and execute_ms<=#{currentTimeMillis}")
    List<Job> selectExecuteJobs(@Param("nodePath") String nodePath, @Param("currentTimeMillis") long currentTimeMillis);

    @Update("update dongguabai_job set server_node_path =#{nodePath} where status = 0 and server_node_path = #{removedPath}")
    int masterTakeOver(@Param("nodePath")String nodePath, @Param("removedPath")String removedPath);

    @Update("update dongguabai_job set status = #{status} where id = #{id}")
    int updateJobStatus(@Param("id")int id, @Param("status")int status);
}
