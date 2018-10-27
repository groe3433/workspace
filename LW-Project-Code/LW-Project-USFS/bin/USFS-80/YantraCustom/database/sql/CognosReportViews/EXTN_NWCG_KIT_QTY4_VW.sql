CREATE OR REPLACE FORCE VIEW EXTN_NWCG_KIT_QTY4_VW ("CACHE_ID", "COMPONENT_ITEM", "PARENT1", "KIT_QTY1", "PARENT2", "KIT_QTY2", "PARENT3", "KIT_QTY3", "PARENT4", "KIT_QUANTITY", "QTY_KITS4")
AS
  SELECT a.organization_code "CACHE_ID",
    z.component_item,
    z.parent1,
    z.kit_qty1,
    z.parent2,
    z.kit_qty2,
    z.parent3,
    z.kit_qty3,
    parent4.item_id "PARENT4",
    ki4.kit_quantity,
    (
    (SELECT SUM( (a1.quantity + a1.pend_in_qty) - (a1.hard_alloc_qty + a1.soft_alloc_qty))
    FROM yfs_location_inventory a1,
      yfs_inventory_item b1
    WHERE a1.inventory_item_key = b1.inventory_item_key
    AND b1.item_id              = parent4.item_id
    AND a1.node_key             = a.organization_code
    AND a1.inventory_status     = 'RFI'
    AND a1.location_id NOT     IN ('CYCLE_COUNT_BIN', 'PHYSICAL_COUNT_BIN', 'PACK_BIN')
    )                           * z.kit_qty1 * z.kit_qty2 * z.kit_qty3 * ki4.kit_quantity) "QTY_KITS4"
  FROM
    (SELECT y.component_item,
      y.parent1,
      y.kit_qty1,
      y.parent2,
      y.kit_qty2,
      parent3.item_id "PARENT3",
      parent3.item_key "ITEM_KEY",
      ki3.kit_quantity "KIT_QTY3"
    FROM
      (SELECT x.component_item,
        x.parent_item "PARENT1",
        x.kit_quantity "KIT_QTY1",
        parent2.item_id "PARENT2",
        parent2.item_key "ITEM_KEY",
        ki2.kit_quantity "KIT_QTY2"
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
        yfs_kit_item ki2,
        yfs_item parent2
      WHERE x.item_key = ki2.component_item_key
      AND ki2.item_key = parent2.item_key
      ) y,
      yfs_kit_item ki3,
      yfs_item parent3
    WHERE y.item_key = ki3.component_item_key
    AND ki3.item_key = parent3.item_key
    )z,
    yfs_organization a,
    yfs_kit_item ki4,
    yfs_item parent4
  WHERE z.item_key             = ki4.component_item_key
  AND ki4.item_key             = parent4.item_key
  AND a.is_node                = 'Y'
  AND a.organization_code NOT IN ('HIGH_VOLUME_TEMPLATE','LOW_VOLUME_TEMPLATE');