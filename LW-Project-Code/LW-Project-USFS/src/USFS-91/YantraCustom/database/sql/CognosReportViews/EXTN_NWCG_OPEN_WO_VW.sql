CREATE OR REPLACE FORCE VIEW EXTN_NWCG_OPEN_WO_VW ("CACHE ID", "WORK_ORDER_NO", "SERVICE_ITEM_ID", "ITEM ID", "UOM", "STATUS", "QTY")
AS
  SELECT TRIM (wo.node_key) "CACHE ID",
    TRIM (wo.work_order_no) work_order_no,
    wo.service_item_id,
    wo.item_id "ITEM ID",
    wo.uom,
    s.description status,
    wo.quantity_requested - wo.quantity_completed qty
  FROM yfs_work_order wo,
    yfs_status s
  WHERE wo.status        = s.status
  AND s.process_type_key = 'VAS'
  AND wo.status          = '1100'
  UNION ALL
  SELECT mwo.node "CACHE ID",
    mwo.master_work_order_no work_order_no,
    mwo.service_item_id,
    mwol.item_id "ITEM ID",
    mwol.unit_of_measure uom,
    mwol.status,
    NVL(mwol.actual_quantity,0) - NVL (mwol.refurbished_quantity, 0) qty
  FROM nwcg_master_work_order mwo,
    nwcg_master_work_order_line mwol
  WHERE TRIM (mwo.master_work_order_key)                                = TRIM (mwol.master_work_order_key)
  AND NVL(mwol.actual_quantity,0) - NVL (mwol.refurbished_quantity, 0) != 0
  AND mwol.status                                                      != 'Work Order Completed'
    -- added by JayP on 05/11/2013 to fix problem PI #1143
  AND mwol.status != 'Work Order Transferred';