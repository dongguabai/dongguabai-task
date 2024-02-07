package io.github.dongguabai.server.persistence;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author Dongguabai
 * @Description
 * @Date 创建于 2020-06-20 00:27
 */
public interface GenericMapper<T> extends Mapper<T>, MySqlMapper<T> {
}