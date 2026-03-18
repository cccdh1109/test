INSERT INTO import_job_config(job_code, job_name, cron_expression, enabled, oss_object_key, file_suffix, delimiter, index_name, field_mappings, handler_type, batch_size, description)
VALUES ('demo_user_import', '演示用户导入', '0 0/10 * * * ?', 1, 'demo/users.dat', '.dat', '|', 'user_profile', 'user_id,user_name,city', 'default', 200, '标准导入示例')
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

INSERT INTO import_job_config(job_code, job_name, cron_expression, enabled, oss_object_key, file_suffix, delimiter, index_name, field_mappings, handler_type, batch_size, description)
VALUES ('demo_upper_import', '大写处理示例', '0 0/30 * * * ?', 0, 'demo/upper.txt', '.txt', '|', 'upper_demo', 'code,name,remark', 'upper_case_demo', 100, '自定义处理器示例')
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;
