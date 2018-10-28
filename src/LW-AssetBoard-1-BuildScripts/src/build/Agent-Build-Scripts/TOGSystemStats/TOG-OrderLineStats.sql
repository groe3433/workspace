select 'SystemDate' || ',' || 'OrderLineCountnewLine' from dual;
select 
  (trunc(sysdate,'MM') - 1 MONTH) || ',' || 
  (count(*)) || 'newLine' 
from OMCTMETA.YFS_ORDER_LINE
WHERE 
  createts >= trunc(sysdate,'MM') - 1 MONTH and 
  createts < trunc(sysdate,'MM');