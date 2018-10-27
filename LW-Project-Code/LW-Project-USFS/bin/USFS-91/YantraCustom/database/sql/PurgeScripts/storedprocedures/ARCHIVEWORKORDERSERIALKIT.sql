create or replace
PROCEDURE ARCHIVEWORKORDERSERIALKIT AS 

i_count INTEGER := 0;
d_count INTEGER := 0;

CURSOR WO_RECORD IS

select NWCG_WORK_ORDER_SERIAL_KIT.work_order_tags_key FROM NWCG_WORK_ORDER_SERIAL_KIT , yfs_work_order_h 
				WHERE NWCG_WORK_ORDER_SERIAL_KIT.work_order_key = yfs_work_order_h.work_order_key;
        
BEGIN

  DBMS_OUTPUT.PUT_LINE('Starting INSERT INTO NWCG_WORK_ORDER_SERIAL_KIT_H where receipt no has been purged at '||to_char(sysdate,'MM-DD-YYYY HH24:MI:SS'));
  DBMS_OUTPUT.PUT_LINE('Creating savepoint, for exception rollbacks');
  savepoint SP1;
  
FOR wo_rec in WO_RECORD
LOOP

INSERT INTO NWCG_WORK_ORDER_SERIAL_KIT_H
       SELECT  
		WORK_ORDER_TAGS_KEY,
    QUANTITY,
    WORK_ORDER_COMPONENT_KEY,
    WORK_ORDER_KEY,
    ITEM_ID,
    PRODUCT_CLASS,
    UNIT_OF_MEASURE,
    ENTERPRISE_CODE,
    DOCUMENT_TYPE,
    NODE_KEY,
    SELLER_ORGANIZATION_CODE,
    SHIP_BY_DATE,
    TAG_NUMBER,
    LOT_NUMBER,
    LOT_KEY_REFERENCE,
    LOT_MANUFACTURE_DATE,
    LOT_ATTRIBUTE1,
    LOT_ATTRIBUTE2,
    LOT_ATTRIBUTE3,
    REVISION_NO,
    BATCH_NO,
    SERIAL_NO,
    SEC_SERIAL_NO_1,
    SEC_SERIAL_NO_2,
    SEC_SERIAL_NO_3,
    SEC_SERIAL_NO_4,
    SEC_SERIAL_NO_5,
    SEC_SERIAL_NO_6,
    CREATETS,
    SYSDATE,
    CREATEUSERID,
    MODIFYUSERID,
    CREATEPROGID,
    MODIFYPROGID,
    LOCKID,
    PARENT_SERIAL_NO
       FROM NWCG_WORK_ORDER_SERIAL_KIT
       where NWCG_WORK_ORDER_SERIAL_KIT.work_order_tags_key = wo_rec.work_order_tags_key;
       
  i_count := i_count + SQL%rowcount;
        
  DELETE  
     FROM NWCG_WORK_ORDER_SERIAL_KIT
       where NWCG_WORK_ORDER_SERIAL_KIT.work_order_tags_key = wo_rec.work_order_tags_key;
       
  d_count := d_count + SQL%rowcount;
       
  COMMIT;
    
END LOOP;

DBMS_OUTPUT.PUT_LINE('inserted '||TO_CHAR(i_count)||' rows to NWCG_WORK_ORDER_SERIAL_KIT_H');
DBMS_OUTPUT.PUT_LINE('deleted '||TO_CHAR(d_count)||' rows from NWCG_WORK_ORDER_SERIAL_KIT');
DBMS_OUTPUT.PUT_LINE('Completed archive of NWCG_WORK_ORDER_SERIAL_KIT at '||to_char(sysdate,'MM-DD-YYYY HH24:MI:SS'));
        
        EXCEPTION
          WHEN DUP_VAL_ON_INDEX THEN
            DBMS_OUTPUT.PUT_LINE
              ('Duplicate Key Value found when INSERTING into NWCG_WORK_ORDER_SERIAL_KIT_H table.  Rolling back changes for messages');
             -- ROLLBACK TO SAVEPOINT SP1;
              
          WHEN STORAGE_ERROR THEN
            DBMS_OUTPUT.PUT_LINE
              ('PL/SQL ran out of memory when archiving NWCG_WORK_ORDER_SERIAL_KIT.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
            
          WHEN TIMEOUT_ON_RESOURCE THEN
            DBMS_OUTPUT.PUT_LINE
              ('A time out occurred while the database is waiting for a resource during NWCG_WORK_ORDER_SERIAL_KIT archiving process.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
              
          WHEN OTHERS THEN 
            DBMS_OUTPUT.PUT_LINE
              ('An error occurred during NWCG_WORK_ORDER_SERIAL_KIT archiving process.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
END ARCHIVEWORKORDERSERIALKIT;
