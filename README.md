# OSS -> Elasticsearch 导入服务

这是一个基于 **SOFABoot + Spring Boot 2.3.12** 的导入服务示例，用于从 OSS 读取 `.dat` / `.txt` 文件并写入 **Elasticsearch 7.10**。

## 方案特点

- **主配置文件为 `application.properties`**。
- **OSS / ES / MySQL / MyBatis** 全部已接入。
- **任务配置落库**：文件路径、分隔符、字段映射、索引名、处理器类型、批次大小、cron 表达式均可通过数据库配置。
- **动态 cron 任务**：支持刷新数据库配置后动态重载任务，无需重启应用。
- **扩展性**：新增文件导入时，只需新增数据库记录；如果有单独逻辑，实现一个新的 `FileImportHandler` 即可。
- **日志清晰**：任务启动、注册、批量写入、异常行、执行结果都会记录。

## 核心表结构

- `import_job_config`: 导入任务配置。
- `import_job_log`: 导入执行日志。

## 如何新增一个导入文件

1. 将文件上传到 OSS。
2. 在 `import_job_config` 新增一条记录，设置：
   - `oss_object_key`
   - `file_suffix`
   - `delimiter`
   - `index_name`
   - `field_mappings`
   - `handler_type`
   - `cron_expression`
3. 若有特殊行转换逻辑，实现 `FileImportHandler` 并返回新的 `handlerType`。
4. 调用 `POST /api/import/jobs/refresh` 刷新动态任务。

## API

- `GET /api/import/jobs`：查看所有任务。
- `POST /api/import/jobs/{jobCode}/run`：手动执行某个任务。
- `POST /api/import/jobs/refresh`：刷新动态 cron 任务。
