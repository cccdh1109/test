package com.example.importer.mapper;

import com.example.importer.domain.ImportJobLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImportJobLogMapper {
    int insert(ImportJobLog log);

    int updateStatus(ImportJobLog log);
}
