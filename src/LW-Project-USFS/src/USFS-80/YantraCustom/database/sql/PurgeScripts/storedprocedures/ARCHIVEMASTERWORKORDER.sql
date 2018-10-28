create or replace
procedure ARCHIVEMASTERWORKORDER AS
i_count INTEGER := 0;
i_count_line INTEGER := 0;
d_count INTEGER := 0;
d_count_line INTEGER := 0;

CURSOR WO_RECORD IS

select wo.master_work_order_key 
from nwcg_master_work_order wo, yfs_receipt_header_h rh
where rh.receipt_no = wo.master_work_order_no
AND NOT EXISTS ( 
SELECT 1 FROM NWCG_MASTER_WORK_ORDER_LINE 
WHERE TRIM(NWCG_MASTER_WORK_ORDER_LINE.STATUS) != 'Work Order Completed' 
AND TRIM(NWCG_MASTER_WORK_ORDER_LINE.MASTER_WORK_ORDER_KEY) = TRIM(wo.MASTER_WORK_ORDER_KEY));

BEGIN

  DBMS_OUTPUT.PUT_LINE('Starting INSERT INTO NWCG_MASTER_WORK_ORDER_H where receipt no has been purged at '||to_char(sysdate,'MM-DD-YYYY HH24:MI:SS'));
  DBMS_OUTPUT.PUT_LINE('Creating savepoint, for exception rollbacks');
  savepoint sp1;
  
FOR wo_rec in WO_RECORD
LOOP
  INSERT INTO NWCG_MASTER_WORK_ORDER_H
       SELECT  
        MASTER_WORK_ORDER_KEY,
        MASTER_WORK_ORDER_NO,
        NODE,
        ENTERPRISE,
        PURPOSE,
        MASTER_WORK_ORDER_TYPE,
        PRIORITY,
        SERVICE_ITEM_ID,
        INCIDENT_NO,
        INCIDENT_YEAR,
        INCIDENT_NAME,
        OVERRIDE_CODE,
        FS_ACCOUNT_CODE,
        BLM_ACCOUNT_CODE,
        OTHER_ACCOUNT_CODE,
        CREATETS,
        SYSDATE,
        CREATEUSERID,
        MODIFYUSERID,
        CREATEPROGID,
        MODIFYPROGID,
        LOCKID,
        INCIDENT_TYPE,
        SOURCE_NODE,
        DESTINATION_NODE,
        SOURCE_MWO_NO,
        DESTINATION_MWO_NO
      FROM nwcg_master_work_order
      WHERE nwcg_master_work_order.master_work_order_key = wo_rec.master_work_order_key;
      
  i_count := i_count + SQL%rowcount;

  DELETE  
      FROM nwcg_master_work_order
      WHERE nwcg_master_work_order.master_work_order_key = wo_rec.master_work_order_key;
      
  d_count := d_count + SQL%rowcount;
      
  INSERT INTO NWCG_MASTER_WORK_ORDER_LINE_H
      SELECT  
        MASTER_WORK_ORDER_LINE_KEY,
        MASTER_WORK_ORDER_KEY,
        ITEM_ID,
        PRODUCT_CLASS,
        UNIT_OF_MEASURE,
        ITEM_DESC,
        PRIMAY_SERIAL_NO,
        SECONDARY_SERIAL_NO_1,
        ACTUAL_QUANTITY,
        REFURBISHED_QUANTITY,
        CREATETS,
        MODIFYTS,
        CREATEUSERID,
        MODIFYUSERID,
        CREATEPROGID,
        MODIFYPROGID,
        LOCKID,
        REFURB_COST,
        STATUS,
        MANUFACTURER_NAME,
        MANUFACTURER_MODEL,
        LOT_NO,
        REVISION_NO,
        SHIP_BY_DATE,
        BATCH_NO,
        OWNER_UNIT_ID,
        NODE,
        DISPLAY_MASTER_WO_COMPONENTS,
        RFI_REFURB_QUANTITY,
        UNS_REFURB_QUANTITY,
        UNSNWT_REFURB_QUANTITY,
        IS_REPLACED_ITEM,
        TRANSFER_QTY,
        RECEIVING_PRICE,
        RECEIPT_HEADER_KEY,
        RECEIPT_LINE_KEY,
        START_NO_EARLIER_THAN,
        FINISH_NO_LATER_THAN
      FROM NWCG_MASTER_WORK_ORDER_LINE
      WHERE TRIM(NWCG_MASTER_WORK_ORDER_LINE.MASTER_WORK_ORDER_KEY) = TRIM(wo_rec.MASTER_WORK_ORDER_KEY);
      
  i_count_line := i_count_line + SQL%rowcount;
      
  DELETE 
      FROM nwcg_master_work_order_line 
      WHERE TRIM(NWCG_MASTER_WORK_ORDER_LINE.MASTER_WORK_ORDER_KEY) = TRIM(wo_rec.MASTER_WORK_ORDER_KEY);

  d_count_line := d_count_line + SQL%rowcount;
  
  COMMIT;
  
END LOOP;

DBMS_OUTPUT.PUT_LINE('inserted '||TO_CHAR(i_count)||' rows to NWCG_MASTER_WORK_ORDER_H');
DBMS_OUTPUT.PUT_LINE('deleted '||TO_CHAR(d_count)||' rows from NWCG_MASTER_WORK_ORDER');
DBMS_OUTPUT.PUT_LINE('inserted '||TO_CHAR(i_count_line)||' rows to NWCG_MASTER_WORK_ORDER_LINE_H');
DBMS_OUTPUT.PUT_LINE('deleted '||TO_CHAR(d_count_line)||' rows from NWCG_MASTER_WORK_ORDER_LINE');


        EXCEPTION
          WHEN DUP_VAL_ON_INDEX THEN
            DBMS_OUTPUT.PUT_LINE
              ('Duplicate Key Value found when INSERTING into NWCG_MASTER_WORK_ORDER_H table.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
              
          WHEN STORAGE_ERROR THEN
            DBMS_OUTPUT.PUT_LINE
              ('PL/SQL ran out of memory when archiving NWCG_MASTER_WORK_ORDER.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
            
          WHEN TIMEOUT_ON_RESOURCE THEN
            DBMS_OUTPUT.PUT_LINE
              ('A time out occurred while the database is waiting for a resource during NWCG_MASTER_WORK_ORDER archiving process.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
              
          WHEN OTHERS THEN 
            DBMS_OUTPUT.PUT_LINE
              ('An error occurred during NWCG_MASTER_WORK_ORDER archiving process.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;

END;
