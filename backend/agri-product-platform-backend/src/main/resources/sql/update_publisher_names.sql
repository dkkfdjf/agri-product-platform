-- 将已发布商品的发布方从用户名更新为真实姓名
-- UPDATE tb_product p
-- LEFT JOIN tb_user u ON p.own_name = u.user_name
-- SET p.own_name = u.real_name
-- WHERE u.real_name IS NOT NULL AND u.real_name != '';

-- 如果上述 SQL 执行有问题，使用这个版本（MySQL）
UPDATE tb_product p
INNER JOIN tb_user u ON p.own_name = u.user_name
SET p.own_name = u.real_name
WHERE u.real_name IS NOT NULL AND u.real_name != '';

-- 回滚脚本（如果需要恢复原来的用户名）
-- UPDATE tb_product p
-- INNER JOIN tb_user u ON p.own_name = u.real_name
-- SET p.own_name = u.user_name
-- WHERE u.real_name IS NOT NULL AND u.real_name != '';
