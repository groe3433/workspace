CREATE OR REPLACE FORCE VIEW EXTN_NWCG_REDISTRIBUTE_RPT_VW ("ORGANIZATION CODE", "CACHE ID", "ITEM_ID", "DESCRIPTION", "UOM", "QTY_RFI", "QTY_MAX")
AS
  SELECT x.organization_code "ORGANIZATION CODE",
    x.node_key "CACHE ID",
    x.item_id,
    x.description,
    x.uom,
    x.qty_rfi,
    (SELECT im.maximum_qty_1
    FROM yfs_item_status_rules im
    WHERE im.item_id    = x.item_id
    AND im.shipnode_key = x.node_key
    ) "QTY_MAX"
  FROM
    (SELECT a.node_key,
      b.organization_code,
      b.item_id,
      c.description,
      c.uom,
      SUM((a.quantity+a.pend_in_qty) - (a.hard_alloc_qty+a.soft_alloc_qty)) "QTY_RFI"
    FROM yfs_location_inventory a,
      yfs_inventory_item b,
      yfs_item c
    WHERE a.inventory_status = 'RFI'
    AND a.inventory_item_key = b.inventory_item_key
    AND b.item_id            = c.item_id
    AND a.location_id NOT   IN ('CYCLE_COUNT_BIN', 'PHYSICAL_COUNT_BIN', 'PACK_BIN')
    GROUP BY a.node_key,
      b.organization_code,
      b.item_id,
      c.description,
      c.uom
    ) x;