create or replace
PROCEDURE CLEARROSSACCOUNTCODES AS 
BEGIN

  DBMS_OUTPUT.PUT_LINE('Starting purge of YFS_AUDIT for values not found in NWCG_ROSS_ACCOUNT_CODES');
  DBMS_OUTPUT.PUT_LINE('Creating savepoint, for exception rollbacks');
  savepoint SP1;

  DELETE FROM YFS_AUDIT 
	WHERE table_name = 'NWCG_ROSS_ACCOUNT_CODES'
	AND not exists ( SELECT ROSS_ACCOUNT_CODE_KEY FROM NWCG_ROSS_ACCOUNT_CODES where ROSS_ACCOUNT_CODE_KEY = YFS_AUDIT.table_key );
  dbms_output.put_line('# ROWS DELETED FROM YFS_AUDIT '||to_char(SQL%rowcount));
  commit;
  
  DBMS_OUTPUT.PUT_LINE('Completed purge of YFS_AUDIT for orders not found in NWCG_ROSS_ACCOUNT_CODES');

        EXCEPTION
              
          WHEN STORAGE_ERROR THEN
            DBMS_OUTPUT.PUT_LINE
              ('PL/SQL ran out of memory when purging YFS_AUDIT for values not found in NWCG_ROSS_ACCOUNT_CODES.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
            
          WHEN TIMEOUT_ON_RESOURCE THEN
            DBMS_OUTPUT.PUT_LINE
              ('A time out occurred while the database is waiting for a resource during YFS_AUDIT purge process for values not found in NWCG_ROSS_ACCOUNT_CODES.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
              
          WHEN OTHERS THEN 
            DBMS_OUTPUT.PUT_LINE
              ('An error occurred during YFS_AUDIT purge process for values not found in NWCG_ROSS_ACCOUNT_CODES.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;

END CLEARROSSACCOUNTCODES;