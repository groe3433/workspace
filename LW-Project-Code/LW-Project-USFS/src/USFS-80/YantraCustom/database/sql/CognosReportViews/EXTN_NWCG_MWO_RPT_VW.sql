CREATE OR REPLACE FORCE VIEW EXTN_NWCG_MWO_RPT_VW ("WORK ORDER NO", "CACHE ID", "MASTER_WORK_ORDER_TYPE", "INCIDENT_NO", "INCIDENT_YEAR", "INCIDENT_NAME", "INCIDENT_TYPE", "BLM_ACCOUNT_CODE", "FS_ACCOUNT_CODE", "OTHER_ACCOUNT_CODE", "OVERRIDE_CODE", "CREATE_DATE", "ITEM_ID", "ITEM_DESC", "PRIMAY_SERIAL_NO", "PRODUCT_CLASS", "UNIT_OF_MEASURE", "ACTUAL_QUANTITY", "REFURBISHED_QUANTITY", "REFURB_COST", "STATUS", "TRANSFER_QTY")
AS
  SELECT a.master_work_order_no "WORK ORDER NO",
    a.node "CACHE ID",
    a.master_work_order_type,
    a.incident_no,
    a.incident_year,
    a.incident_name,
    a.incident_type,
    a.blm_account_code,
    a.fs_account_code,
    a.other_account_code,
    a.override_code,
    to_date(TO_CHAR(a.createts,'DD-MON-YYYY'),'DD-MON-YYYY') "CREATE_DATE",
    b.item_id,
    b.item_desc,
    b.primay_serial_no,
    b.product_class,
    b.unit_of_measure,
    b.actual_quantity,
    b.refurbished_quantity,
    b.refurb_cost,
    B.STATUS,
    B.TRANSFER_QTY -- CR1451 jimmy
  FROM nwcg_master_work_order a,
    nwcg_master_work_order_line b
  WHERE RTRIM (A.MASTER_WORK_ORDER_KEY) = RTRIM (B.MASTER_WORK_ORDER_KEY)
  AND b.status IN ('Awaiting Work Order Creation','Work Order Partially Completed');