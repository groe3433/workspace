SELECT 'TABLE_NAME' || ',' || 'SYSTEM_DATE' || ',' || 'COUNTnewLine' FROM dual;
SELECT 'EXTN_SCHEDULED_ORDER_TEMP' || ',' || sysdate || ',' || count(*) || 'newLine' FROM OMCTMETA.EXTN_SCHEDULED_ORDER_TEMP;
SELECT 'EXTN_RELEASED_ORDER_TEMP' || ',' || sysdate || ',' || count(*) || 'newLine' FROM OMCTMETA.EXTN_RELEASED_ORDER_TEMP;
SELECT 'EXTN_BACKORDERED_ORDER_TEMP' || ',' || sysdate || ',' || count(*) || 'newLine' FROM OMCTMETA.EXTN_BACKORDERED_ORDER_TEMP;