create or replace
PROCEDURE CLEARSHIPMENT AS 
BEGIN

  DBMS_OUTPUT.PUT_LINE('Starting purge of YFS_AUDIT for orders not found in YFS_SHIPMENT');
  DBMS_OUTPUT.PUT_LINE('Creating savepoint, for exception rollbacks');
  savepoint SP1;

  DELETE FROM YFS_AUDIT 
	WHERE table_name = 'YFS_SHIPMENT'
	AND not exists ( SELECT shipment_key FROM yfs_shipment where shipment_key = YFS_AUDIT.table_key );
  dbms_output.put_line('# ROWS DELETED FROM YFS_AUDIT '||to_char(SQL%rowcount));
  commit;
  
  DBMS_OUTPUT.PUT_LINE('Completed purge of YFS_AUDIT for orders not found in YFS_SHIPMENT');

        EXCEPTION
              
          WHEN STORAGE_ERROR THEN
            DBMS_OUTPUT.PUT_LINE
              ('PL/SQL ran out of memory when purging YFS_AUDIT for orders not found in YFS_SHIPMENT.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
            
          WHEN TIMEOUT_ON_RESOURCE THEN
            DBMS_OUTPUT.PUT_LINE
              ('A time out occurred while the database is waiting for a resource during YFS_AUDIT purge process for orders not found in YFS_SHIPMENT.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
              
          WHEN OTHERS THEN 
            DBMS_OUTPUT.PUT_LINE
              ('An error occurred during YFS_AUDIT purge process for orders not found in YFS_SHIPMENT.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;

END CLEARSHIPMENT;