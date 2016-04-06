select  from operation as op where op.datetime = (select max(datetime) from operation as op2 where op.operation_id = op2.operation_id);


select * from operation where relative_file_path = '.DS_Store';

select operation_id, max(datetime), operation, relative_file_path, user_id from operation where relative_file_path = '.DS_Store' and user_id = 1 group by relative_file_path;


SELECT op.operation_id, op.datetime, op.operation, op.relative_file_path, op.user_id FROM operation op
INNER JOIN (
    SELECT relative_file_path, user_id, MAX(datetime) datetime
    FROM operation
    WHERE user_id = 1
    GROUP BY relative_file_path, user_id
    ORDER BY relative_file_path
) op2 ON op.relative_file_path = op2.relative_file_path AND op.user_id = op2.user_id AND op.datetime = op2.datetime ORDER BY op.relative_file_path;