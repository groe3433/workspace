CREATE OR REPLACE FORCE VIEW EXTN_NWCG_SKU_DED_VIOLATN_VW ("CACHE ID", "LOCATION_ID", "ZONE ID", "ITEM ID", "DESCRIPTION", "UOM", "STATUS", "TAG_NUMBER", "SERIAL_NO", "QTY", "PEND_IN_QTY", "PEND_OUT_QTY")
AS
  SELECT l.node_key "CACHE ID",
    l.location_id,
    l.zone_id "ZONE ID",
    inv.item_id "ITEM ID",
    inv.short_description description,
    inv.uom,
    inv.inventory_status status,
    inv.tag_number,
    (SELECT (
      CASE
        WHEN inv.quantity = 1
        THEN serial_no
        ELSE '*Multiple'
      END)
    FROM yfs_global_serial_num
    WHERE inventory_item_key = inv.inventory_item_key
    AND shipnode_key         = l.node_key
    AND location_id          = l.location_id
    AND tag_number           = (
      CASE
        WHEN NVL (inv.tag_number, 'NoTag') = 'NoTag'
        THEN tag_number
        ELSE inv.tag_number
      END)
    AND ROWNUM = 1
    ) serial_no,
    NVL (inv.quantity, 0) qty,
    NVL (inv.pend_in_qty, 0) pend_in_qty,
    NVL (inv.pend_out_qty, 0) pend_out_qty
  FROM yfs_location l,
    (SELECT li.node_key,
      li.location_id,
      li.inventory_status,
      li.quantity,
      ii.item_id,
      ii.uom,
      i.short_description,
      li.pend_in_qty,
      li.hard_alloc_qty + li.soft_alloc_qty pend_out_qty,
      it.tag_number,
      ii.inventory_item_key
    FROM yfs_location_inventory li,
      yfs_inventory_item ii,
      yfs_item i,
      yfs_inventory_tag it
    WHERE li.inventory_item_key = ii.inventory_item_key
    AND ii.organization_code    = i.organization_code
    AND ii.item_id              = i.item_id
    AND li.inventory_tag_key    = it.inventory_tag_key(+)
    ) inv
  WHERE l.node_key        = inv.node_key
  AND l.location_id       = inv.location_id
  AND 'DedicatedLocation' = NVL (
    (SELECT 'DedicatedLocation'
    FROM yfs_sku_dedication
    WHERE node_key  = l.node_key
    AND location_id = l.location_id
    AND ROWNUM      < 2
    ), 'NotDedicated' )
  AND TRIM (inv.item_id)
    || TRIM (inv.uom) NOT IN
    (SELECT TRIM (item_id)
      || TRIM (uom)
    FROM yfs_sku_dedication
    WHERE node_key  = l.node_key
    AND location_id = l.location_id
    );