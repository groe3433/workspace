create or replace
procedure ARCHIVEOUTBOUNDMESSAGES 

IS 

begin
  DBMS_OUTPUT.PUT_LINE('Starting INSERT INTO NWCG_OUTBOUND_MESSAGE_H for records older than 90days');
  DBMS_OUTPUT.PUT_LINE('Creating savepoint, for exception rollbacks');
  savepoint sp1;

INSERT INTO NWCG_OUTBOUND_MESSAGE_H
       SELECT  MESSSAGE_KEY,
          DISTRIBUTION_ID,
          MESSAGE,
          SYSTEM_NAME,
          MESSAGE_TYPE,
          MESSAGE_NAME,
          MESSAGE_STATUS,
          LATEST_INBOUND,
          CREATETS,
          MODIFYTS,
          CREATEUSERID,
          MODIFYUSERID,
          CREATEPROGID,
          MODIFYPROGID,
          LOCKID,
          ENTITY_KEY,
          ENTITY_NAME,
          ENTITY_VALUE
        FROM    NWCG_OUTBOUND_MESSAGE
        WHERE   TO_CHAR (CREATETS, 'YYYYMMDD') < TO_CHAR(SYSDATE-90,'YYYYMMDD');
        
        COMMIT;
        
    DBMS_OUTPUT.PUT_LINE('DELETING records older than 90 days from NWCG_OUTBOUND_MESSAGE');

        delete  
          FROM    NWCG_OUTBOUND_MESSAGE
          WHERE   TO_CHAR (CREATETS, 'YYYYMMDD') < TO_CHAR(SYSDATE-90,'YYYYMMDD');        
          COMMIT;
        
        EXCEPTION
          WHEN DUP_VAL_ON_INDEX THEN
            DBMS_OUTPUT.PUT_LINE
              ('Duplicate Key Value found when INSERTING into NWCG_OUTBOUND_MESSAGE_H table.  Rolling back changes for messages');
             -- ROLLBACK TO SAVEPOINT SP1;
              
          WHEN STORAGE_ERROR THEN
            DBMS_OUTPUT.PUT_LINE
              ('PL/SQL ran out of memory when archiving NWCG_OUTBOUND_MESSAGE.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
            
          WHEN TIMEOUT_ON_RESOURCE THEN
            DBMS_OUTPUT.PUT_LINE
              ('A time out occurred while the database is waiting for a resource during NWCG_OUTBOUND_MESSAGE archiving process.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
              
          WHEN OTHERS THEN 
            DBMS_OUTPUT.PUT_LINE
              ('An error occurred during NWCG_OUTBOUND_MESSAGE archiving process.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
            
        
END;