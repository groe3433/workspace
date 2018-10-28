CREATE OR REPLACE FORCE VIEW EXTN_NWCG_KIT_QTY1_VW ("CACHE_ID", "COMPONENT_ITEM", "PARENT_ITEM", "KIT_QUANTITY", "QTY_KITS1")
AS
  SELECT a.organization_code "CACHE_ID",
    comp.item_id "COMPONENT_ITEM",
    parent.item_id "PARENT_ITEM",
    ki.kit_quantity,
    (
    (SELECT SUM( (a1.quantity + a1.pend_in_qty) - (a1.hard_alloc_qty + a1.soft_alloc_qty))
    FROM yfs_location_inventory a1,
      yfs_inventory_item b1
    WHERE a1.inventory_item_key = b1.inventory_item_key
    AND b1.item_id              = parent.item_id
    AND a1.node_key             = a.organization_code
    AND a1.inventory_status     = 'RFI'
    AND a1.location_id NOT     IN ('CYCLE_COUNT_BIN', 'PHYSICAL_COUNT_BIN', 'PACK_BIN')
    )                           * ki.kit_quantity) "QTY_KITS1"
  FROM yfs_organization a,
    yfs_item comp,
    yfs_kit_item ki,
    yfs_item parent
  WHERE comp.item_key          = ki.component_item_key
  AND ki.item_key              = parent.item_key
  AND a.is_node                = 'Y'
  AND a.organization_code NOT IN ('HIGH_VOLUME_TEMPLATE','LOW_VOLUME_TEMPLATE');