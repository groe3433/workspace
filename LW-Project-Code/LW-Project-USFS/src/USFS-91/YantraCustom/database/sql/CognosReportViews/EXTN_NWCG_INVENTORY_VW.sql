CREATE OR REPLACE FORCE VIEW EXTN_NWCG_INVENTORY_VW ("CACHE ID", "ZONE ID", "LOCATION_ID", "ITEM ID", "DESCRIPTION", "UOM", "STATUS", "TRACKABLE_ID", "QTY", "PEND_IN_QTY", "PEND_OUT_QTY")
AS
  SELECT a.node_key "CACHE ID",
    a.zone_id "ZONE ID",
    a.location_id,
    B.ITEM_ID "ITEM ID",
    
    c.short_description "DESCRIPTION",
    c.uom,
    a.inventory_status "STATUS",
    '' "TRACKABLE_ID",
    SUM(a.quantity) "QTY",
    SUM(a.pend_in_qty) "PEND_IN_QTY",
    SUM(a.hard_alloc_qty) "PEND_OUT_QTY"
  FROM yfs_location_inventory a,
    yfs_inventory_item b,
    yfs_item c
  WHERE a.inventory_item_key = b.inventory_item_key
  AND b.item_id              = c.item_id
  AND C.IS_SERIAL_TRACKED    = 'N'
  AND c.status='3000'
  GROUP BY a.node_key,
    a.zone_id,
    a.location_id,
    b.item_id,
    c.short_description,
    c.uom,
    a.inventory_status
  UNION
  SELECT a.shipnode_key "CACHE ID",
    d.zone_id "ZONE ID",
    a.location_id,
    b.item_id "ITEM ID",
    c.short_description "DESCRIPTION",
    c.uom,
    a.inventory_status "STATUS",
    a.serial_no "TRACKABLE_ID",
    1 "QTY",
    (SELECT SUM(e.pend_in_qty)
    FROM yfs_location_inventory e
    WHERE e.location_id      = a.location_id
    AND e.node_key           = a.shipnode_key
    AND e.inventory_item_key = a.inventory_item_key
    ) "PEND_IN_QTY",
    (SELECT SUM(e.hard_alloc_qty)
    FROM yfs_location_inventory e
    WHERE e.location_id      = a.location_id
    AND e.node_key           = a.shipnode_key
    AND e.inventory_item_key = a.inventory_item_key
    ) "PEND_OUT_QTY"
  FROM yfs_global_serial_num a,
    yfs_inventory_item b,
    yfs_item c,
    YFS_LOCATION D
  WHERE A.INVENTORY_ITEM_KEY = B.INVENTORY_ITEM_KEY
  AND B.ITEM_ID              = C.ITEM_ID
  AND c.status='3000'
  AND c.is_serial_tracked    = 'Y'
  AND a.at_node              = 'Y'
  AND a.location_id          = d.location_id
  AND a.shipnode_key         = d.node_key 
  ;