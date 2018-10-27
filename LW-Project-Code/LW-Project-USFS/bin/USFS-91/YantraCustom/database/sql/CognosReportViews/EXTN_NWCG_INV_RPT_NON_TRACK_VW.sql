CREATE OR REPLACE FORCE VIEW EXTN_NWCG_INV_RPT_NON_TRACK_VW ("ITEM ID", "INVENTORY_ITEM_KEY", "DESCRIPTION", "SERIALIZED_FLAG", "UNIT_COST", "UOM", "CACHE ID", "TRANS TYPE", "SERIAL_NO", "LOCATION_ID", "INVENTORY_STATUS", "ENTERPRISE_CODE", "TRANS DATE", "REASON_TEXT", "DOCUMENT NO", "REFERENCE_2", "REFERENCE_3", "REFERENCE_4", "REFERENCE_5", "MODEL_NUMBER", "MANUFACTURER_SERIAL", "ACQUISITION_COST", "ACQUISITION_DATE", "OWNER_UNIT_ID", "INCIDENT_BLM_ACCT_CODE", "INCIDENT_FS_ACCT_CODE", "INCIDENT_OTHER_ACCT_CODE", "DOCUMENT NUMBER", "LAST_TRANSACTION_DATE", "OPERATION", "QUANTITY", "QTY_RFI", "TASK TYPE", "TASK TYPE NAME", "ACTIVITY GROUP")
AS
  SELECT a.item_id "ITEM ID",
    a.inventory_item_key,
    c.description,
    c.serialized_flag,
    c.unit_cost,
    c.uom,
    b.node_key "CACHE ID",
    SUBSTR(b.reason_code,instr(b.reason_code,'-')+1) "TRANS TYPE",
    b.serial_no,
    b.location_id,
    b.inventory_status,
    b.enterprise_code,
    b.createts "TRANS DATE",
    b.reason_text,
    b.reference_1 "DOCUMENT NO",
    b.reference_2,
    b.reference_3,
    b.reference_4,
    b.reference_5,
    d.lot_attribute_3 "MODEL_NUMBER",
    d.secondary_serial "MANUFACTURER_SERIAL",
    d.acquisition_cost,
    d.acquisition_date,
    d.owner_unit_id,
    d.blm_account_code "INCIDENT_BLM_ACCT_CODE",
    d.fs_account_code "INCIDENT_FS_ACCT_CODE",
    d.other_account_code "INCIDENT_OTHER_ACCT_CODE",
    d.last_document_number "DOCUMENT NUMBER",
    d.last_transaction_date,
    b.operation,
    CASE b.operation
      WHEN '-'
      THEN (b.quantity * -1)
      WHEN '+'
      THEN b.quantity
    END "QUANTITY",
    (SELECT SUM((a1.quantity+a1.pend_in_qty) - (a1.hard_alloc_qty+a1.soft_alloc_qty))
    FROM yfs_location_inventory a1
    WHERE a1.inventory_item_key = a.inventory_item_key
    AND a1.inventory_status     = 'RFI'
    AND a1.node_key             = b.node_key
    AND a1.location_id NOT     IN ('CYCLE_COUNT_BIN', 'PHYSICAL_COUNT_BIN', 'PACK_BIN')
    ) "QTY_RFI",
    DECODE(b.task_type,'          ','INVENTORY_ADJUSTMENT',b.task_type) "TASK TYPE",
    (SELECT e.task_type_name
    FROM yfs_task_type e
    WHERE e.task_type       = b.task_type
    AND e.organization_code = b.node_key
    UNION
    SELECT 'INVENTORY_ADJUSTMENT' FROM dual WHERE b.task_type = '          '
    ) "TASK TYPE NAME",
    (SELECT e.activity_group_id
    FROM yfs_task_type e
    WHERE e.task_type       = b.task_type
    AND e.organization_code = b.node_key
    UNION
    SELECT 'INVENTORY_ADJUSTMENT' FROM dual WHERE b.task_type = '          '
    ) "ACTIVITY GROUP"
  FROM yfs_inventory_item a,
    yfs_locn_inventory_audit b,
    yfs_item c,
    nwcg_trackable_item d
  WHERE a.inventory_item_key = b.inventory_item_key
  AND a.item_id              = c.item_id
  AND a.organization_code    =c.organization_code
  AND a.product_class        = c.default_product_class
  AND a.uom                  = c.uom
  AND b.location_id NOT     IN ('CYCLE_COUNT_BIN', 'PHYSICAL_COUNT_BIN', 'PACK_BIN')
  AND b.adjustment_type      = 'ADJUSTMENT'
  AND a.item_id              = d.item_id (+)
  AND c.serialized_flag      ='N';