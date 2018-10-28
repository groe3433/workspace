CREATE OR REPLACE FORCE VIEW EXTN_NWCG_KIT_MATRIX_VW ("CACHE ID", "KIT_ITEM_KEY", "PARENT_ITEM_KEY", "KIT ID", "PARENT_QTY_RFI", "PARENT_QTY_MIN", "PARENT_QTY_MAX", "PARENT_DESCRIPTION", "PARENT_UNIT_COST", "CHILD_ITEM_KEY", "COMPONENT ITEM", "CHILD_QTY_RFI", "CHILD_QTY_MIN", "CHILD_QTY_MAX", "CHILD_QTY_ALLOC", "QTY_AVL_TO_BUILD", "CHILD_DESCRIPTION", "CHILD_UOM", "CHILD_UNIT_COST", "KIT_QUANTITY", "LOCKID", "CREATETS", "MODIFYTS", "CREATEUSERID", "MODIFYUSERID", "CREATEPROGID", "MODIFYPROGID")
AS
  SELECT DISTINCT LI.NODE_KEY "CACHE ID",
    KI.KIT_ITEM_KEY,
    KI.ITEM_KEY "PARENT_ITEM_KEY",
    P.ITEM_ID "KIT ID",
    (SELECT SUM((yli.quantity+yli.pend_in_qty)-(yli.hard_alloc_qty+yli.soft_alloc_qty))
    FROM yfs_location_inventory yli,
      yfs_inventory_item yii
    WHERE yli.inventory_item_key = yii.inventory_item_key
    AND yii.item_id              = P.item_id
    AND yli.inventory_status     = 'RFI'
    AND yli.location_id NOT     IN ('CYCLE_COUNT_BIN', 'PHYSICAL_COUNT_BIN', 'PACK_BIN')
    AND yli.node_key             = LI.node_key
    ) "PARENT_QTY_RFI",
    (SELECT im.minimum_qty_1
    FROM yfs_item_status_rules im
    WHERE im.item_id    = P.item_id
    AND im.shipnode_key = LI.node_key
    ) "PARENT_QTY_MIN",
    (SELECT im.maximum_qty_1
    FROM yfs_item_status_rules im
    WHERE im.item_id    = P.item_id
    AND im.shipnode_key = LI.node_key
    ) "PARENT_QTY_MAX",
    P.DESCRIPTION "PARENT_DESCRIPTION",
    P.UNIT_COST "PARENT_UNIT_COST",
    KI.COMPONENT_ITEM_KEY "CHILD_ITEM_KEY",
    C.ITEM_ID "COMPONENT ITEM",
    (SELECT SUM((yli.quantity+yli.pend_in_qty)-(yli.hard_alloc_qty+yli.soft_alloc_qty))
    FROM yfs_location_inventory yli,
      yfs_inventory_item yii
    WHERE yli.inventory_item_key = yii.inventory_item_key
    AND yii.item_id              = C.item_id
    AND yli.inventory_status     = 'RFI'
    AND yli.location_id NOT     IN ('CYCLE_COUNT_BIN', 'PHYSICAL_COUNT_BIN', 'PACK_BIN')
    AND yli.node_key             = LI.node_key
    ) "CHILD_QTY_RFI",
    (SELECT im.minimum_qty_1
    FROM yfs_item_status_rules im
    WHERE im.item_id    = C.item_id
    AND im.shipnode_key = LI.node_key
    ) "CHILD_QTY_MIN",
    (SELECT im.maximum_qty_1
    FROM yfs_item_status_rules im
    WHERE im.item_id    = C.item_id
    AND im.shipnode_key = LI.node_key
    ) "CHILD_QTY_MAX",
    (SELECT SUM(yid.quantity)
    FROM yfs_inventory_demand yid,
      yfs_inventory_item yii
    WHERE yid.inventory_item_key = yii.inventory_item_key
    AND yii.item_id              = C.item_id
    AND yid.demand_type          = 'ALLOCATED'
    AND yid.shipnode_key         = LI.node_key
    ) "CHILD_QTY_ALLOC",
    (SELECT MIN(ROUND(child_qty_avl))
    FROM extn_nwcg_kit_avl_vw k
    WHERE k."KIT ID" = P.ITEM_ID
    AND k."CACHE ID" = LI.node_key
    ) "QTY_AVL_TO_BUILD",
    C.DESCRIPTION "CHILD_DESCRIPTION",
    C.UOM "CHILD_UOM",
    C.UNIT_COST "CHILD_UNIT_COST",
    KI.KIT_QUANTITY,
    KI.LOCKID,
    KI.CREATETS,
    KI.MODIFYTS,
    KI.CREATEUSERID,
    KI.MODIFYUSERID,
    KI.CREATEPROGID,
    KI.MODIFYPROGID
  FROM yfs_item C,
    yfs_kit_item KI,
    yfs_item P,
    yfs_location_inventory LI,
    yfs_inventory_item II
  WHERE C.ITEM_KEY          = KI.COMPONENT_ITEM_KEY
  AND KI.ITEM_KEY           = P.ITEM_KEY
  AND LI.INVENTORY_ITEM_KEY = II.INVENTORY_ITEM_KEY;