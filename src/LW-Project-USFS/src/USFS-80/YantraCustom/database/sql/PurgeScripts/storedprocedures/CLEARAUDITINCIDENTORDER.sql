create or replace
PROCEDURE CLEARAUDITINCIDENTORDER AS 
BEGIN

  DBMS_OUTPUT.PUT_LINE('Starting purge of YFS_AUDIT for orders not found in NWCG_INCIDENT_ORDER at '||to_char(sysdate,'MM-DD-YYYY HH24:MI:SS'));
  DBMS_OUTPUT.PUT_LINE('Creating savepoint, for exception rollbacks');
  savepoint SP1;

  delete from YFS_AUDIT
  where table_name = 'NWCG_INCIDENT_ORDER' and not exists (select * from nwcg_incident_order
      where  TRIM(nwcg_incident_order.incident_key) = TRIM(yfs_audit.table_key) );
  dbms_output.put_line('# ROWS DELETED FROM YFS_AUDIT '||to_char(SQL%rowcount));
  commit;
  
  DBMS_OUTPUT.PUT_LINE('Completed purge of YFS_AUDIT for orders not found in NWCG_INCIDENT_ORDER at '||to_char(sysdate,'MM-DD-YYYY HH24:MI:SS'));

        EXCEPTION
              
          WHEN STORAGE_ERROR THEN
            DBMS_OUTPUT.PUT_LINE
              ('PL/SQL ran out of memory when purging YFS_AUDIT for orders not found in NWCG_INCIDENT_ORDER.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
            
          WHEN TIMEOUT_ON_RESOURCE THEN
            DBMS_OUTPUT.PUT_LINE
              ('A time out occurred while the database is waiting for a resource during YFS_AUDIT purge process for orders not found in NWCG_INCIDENT_ORDER.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
              
          WHEN OTHERS THEN 
            DBMS_OUTPUT.PUT_LINE
              ('An error occurred during YFS_AUDIT purge process for orders not found in NWCG_INCIDENT_ORDER.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
              
END CLEARAUDITINCIDENTORDER;