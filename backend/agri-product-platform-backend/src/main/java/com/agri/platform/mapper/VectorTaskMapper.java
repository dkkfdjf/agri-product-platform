package com.agri.platform.mapper;

import com.agri.platform.entity.VectorTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface VectorTaskMapper extends BaseMapper<VectorTask> {

    @Select("SELECT * FROM tb_vector_task WHERE status = 0 ORDER BY create_time LIMIT #{limit}")
    List<VectorTask> findPendingTasks(int limit);

    @Update("UPDATE tb_vector_task SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(Integer id, Integer status);
}
