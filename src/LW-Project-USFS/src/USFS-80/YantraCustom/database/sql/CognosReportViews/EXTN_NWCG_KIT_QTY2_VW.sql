CREATE OR REPLACE FORCE VIEW EXTN_NWCG_KIT_QTY2_VW ("CACHE_ID", "COMPONENT_ITEM", "PARENT1", "KIT_QTY1", "PARENT2", "KIT_QTY2", "QTY_KITS2")
AS
  SELECT a.organization_code "CACHE_ID",
    x.component_item,
    x.parent_item "PARENT1",
    x.kit_quantity "KIT_QTY1",
    parent2.item_id "PARENT2",
    ki2.kit_quantity "KIT_QTY2",
    (
    (SELECT SUM( (a1.quantity + a1.pend_in_qty) - (a1.hard_alloc_qty + a1.soft_alloc_qty))
    FROM yfs_location_inventory a1,
      yfs_inventory_item b1
    WHERE a1.inventory_item_key = b1.inventory_item_key
    AND b1.item_id              = parent2.item_id
    AND a1.node_key             = a.organization_code
    AND a1.inventory_status     = 'RFI'
    AND a1.location_id NOT     IN ('CYCLE_COUNT_BIN', 'PHYSICAL_COUNT_BIN', 'PACK_BIN')
    )                           * x.kit_quantity * ki2.kit_quantity) "QTY_KITS2"
  FROM
    (SELECT comp.item_id "COMPONENT_ITEM",
      parent.item_id "PARENT_ITEM",
      parent.item_key,
      ki.kit_quantity
    FROM yfs_item comp,
      yfs_kit_item ki,
      yfs_item parent
    WHERE comp.item_key = ki.component_item_key
    AND ki.item_key     = parent.item_key
    ) x,
    yfs_organization a,
    yfs_kit_item ki2,
    yfs_item parent2
  WHERE x.item_key             = ki2.component_item_key
  AND ki2.item_key             = parent2.item_key
  AND a.is_node                = 'Y'
  AND a.organization_code NOT IN ('HIGH_VOLUME_TEMPLATE','LOW_VOLUME_TEMPLATE');