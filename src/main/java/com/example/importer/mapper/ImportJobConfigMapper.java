package com.example.importer.mapper;

import com.example.importer.domain.ImportJobConfig;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ImportJobConfigMapper {
    List<ImportJobConfig> findAllEnabled();

    List<ImportJobConfig> findAll();

    ImportJobConfig findByJobCode(@Param("jobCode") String jobCode);
}
