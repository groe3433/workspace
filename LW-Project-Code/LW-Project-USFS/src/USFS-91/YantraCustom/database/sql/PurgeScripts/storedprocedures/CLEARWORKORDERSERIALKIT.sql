create or replace
PROCEDURE CLEARWORKORDERSERIALKIT AS 
BEGIN
   DECLARE

    rcount  NUMBER  := 0;
    total  NUMBER  := 0;

    CURSOR del_record_cur IS
      SELECT rowid FROM YFS_AUDIT WHERE table_name = 'NWCG_WORK_ORDER_SERIAL_KIT' and TO_CHAR (CREATETS, 'YYYYMMDD') < TO_CHAR(SYSDATE-3*360,'YYYYMMDD');
      
    BEGIN
  
      DBMS_OUTPUT.PUT_LINE('Starting purge of YFS_AUDIT for NWCG_WORK_ORDER_SERIAL_KIT OLDER THAN 3*360 days at '||to_char(sysdate,'MM-DD-YYYY HH24:MI:SS'));
      DBMS_OUTPUT.PUT_LINE('Creating savepoint, for exception rollbacks');
  
      FOR rec IN del_record_cur LOOP
          DELETE FROM YFS_AUDIT
          WHERE rowid = rec.rowid;

          total := total + 1;
          rcount := rcount + 1;

          IF (rcount >= 1000) THEN
            COMMIT;
            savepoint SP1;
            rcount := 0;
          END IF;

      END LOOP;
      COMMIT;
      DBMS_OUTPUT.PUT_LINE('Deleted ' || to_char(total) || ' records from YFS_AUDIT for orders not found in NWCG_WORK_ORDER_SERIAL_KIT at '||to_char(sysdate,'MM-DD-YYYY HH24:MI:SS'));
    END;
        EXCEPTION
              
          WHEN STORAGE_ERROR THEN
            DBMS_OUTPUT.PUT_LINE
              ('PL/SQL ran out of memory when purging YFS_AUDIT for orders not found in NWCG_WORK_ORDER_SERIAL_KIT.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
            
          WHEN TIMEOUT_ON_RESOURCE THEN
            DBMS_OUTPUT.PUT_LINE
              ('A time out occurred while the database is waiting for a resource during YFS_AUDIT purge process for orders not found in NWCG_WORK_ORDER_SERIAL_KIT.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
              
          WHEN OTHERS THEN 
            DBMS_OUTPUT.PUT_LINE
              ('An error occurred during YFS_AUDIT purge process for orders not found in NWCG_WORK_ORDER_SERIAL_KIT.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;

END CLEARWORKORDERSERIALKIT;