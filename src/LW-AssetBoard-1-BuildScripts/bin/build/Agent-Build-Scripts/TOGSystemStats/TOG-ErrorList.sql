select 'ERRORCODE' || ',' || 'COUNTnewLine' from dual;
select ERRORCODE || ',' || count(*) || 'newLine'
from OMCTMETA.YFS_REPROCESS_ERROR 
where STATE='Initial' group by ERRORCODE;
select 'T-YFC0009' || ',' || 'N-YDM00065  YDM00005newLine' from dual;