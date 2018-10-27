CREATE OR REPLACE FORCE VIEW EXTN_NWCG_IE_RPT_1_VW ("ITEM ID", "ITEM DESCRIPTION", "UOM", "CACHE ID", "QUANTITY", "LOCATION ID", "SHIP_BY_DATE", "QTY_AS_KITS")
AS
  SELECT DISTINCT yfs_item.item_id "ITEM ID",
    yfs_item.short_description "ITEM DESCRIPTION",
    yfs_item.uom,
    yfs_location_inventory.node_key "CACHE ID",
    yfs_location_inventory.quantity,
    yfs_location_inventory.location_id "LOCATION ID",
    yfs_location_inventory.ship_by_date,
    (SELECT SUM(a1.kit_quantity *
      (SELECT SUM((a1.quantity  +a1.pend_in_qty) - (a1.hard_alloc_qty+a1.soft_alloc_qty))
      FROM yfs_location_inventory a1,
        yfs_inventory_item b1,
        yfs_item yi
      WHERE a1.inventory_item_key = b1.inventory_item_key
      AND yi.item_key             = a1.item_key
      AND b1.item_id              = yi.item_id
      AND a1.node_key             = yfs_location_inventory.node_key
      AND a1.inventory_status     = 'RFI'
      AND a1.location_id NOT     IN ('CYCLE_COUNT_BIN', 'PHYSICAL_COUNT_BIN', 'PACK_BIN')
      ))
    FROM yfs_kit_item a1,
      yfs_item c1
    WHERE c1.kit_code        != 'PK'
    AND c1.item_id            = yfs_item.item_id
    AND a1.component_item_key = c1.item_key
    AND c1.organization_code  = 'NWCG'
    ) "QTY_AS_KITS"
  FROM yfs_item,
    yfs_location_inventory,
    yfs_inventory_item yii
  WHERE yfs_item.item_id                        = yii.item_id
  AND yfs_location_inventory.inventory_item_key = yii.inventory_item_key
  AND yfs_location_inventory.inventory_status   = 'RFI'
  AND yfs_location_inventory.ship_by_date      IS NOT NULL
  AND yfs_location_inventory.location_id NOT   IN ('CYCLE_COUNT_BIN', 'PHYSICAL_COUNT_BIN', 'PACK_BIN')
  GROUP BY yfs_item.item_id,
    yfs_item.short_description,
    yfs_item.uom,
    yfs_location_inventory.node_key,
    yfs_location_inventory.quantity,
    yfs_location_inventory.location_id,
    yfs_location_inventory.ship_by_date;