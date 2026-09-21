package com.agri.platform.mapper;

import com.agri.platform.entity.VectorIndex;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VectorIndexMapper extends BaseMapper<VectorIndex> {

    @Select("SELECT * FROM tb_vector_index WHERE doc_type = #{docType} AND doc_id = #{docId}")
    List<VectorIndex> findByDocTypeAndDocId(String docType, Integer docId);

    @Select("SELECT * FROM tb_vector_index")
    List<VectorIndex> findAll();
}
