CREATE OR REPLACE FORCE VIEW EXTN_NWCG_FROZEN_LOCATIONS_VW ("CACHE ID", "ITEM_ID", "ZONE_ID", "LOCATION_ID", "FREEZE_MOVE_IN", "FREEZE_MOVE_OUT", "INVENTORY_STATUS", "QTY", "PEND_IN_QTY", "PEND_OUT_QTY")
AS
  SELECT a.node_key "CACHE ID",
    b.item_id,
    a.zone_id,
    a.location_id,
    c.freeze_move_in,
    c.freeze_move_out,
    a.inventory_status,
    SUM(a.quantity) "QTY",
    SUM(a.pend_in_qty)"PEND_IN_QTY",
    SUM(a.hard_alloc_qty) "PEND_OUT_QTY"
  FROM yfs_location_inventory a,
    yfs_inventory_item b,
    yfs_location c
  WHERE a.inventory_item_key = b.inventory_item_key
  AND a.location_id          = c.location_id
  AND a.node_key             = c.node_key
  AND (c.freeze_move_in      = 'Y'
  OR c.freeze_move_out       = 'Y')
  GROUP BY a.node_key,
    b.item_id,
    a.zone_id,
    a.location_id,
    c.freeze_move_in,
    c.freeze_move_out,
    a.inventory_status;