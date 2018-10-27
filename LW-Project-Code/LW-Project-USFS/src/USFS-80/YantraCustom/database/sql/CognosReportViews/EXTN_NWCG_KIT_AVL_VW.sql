CREATE OR REPLACE FORCE VIEW EXTN_NWCG_KIT_AVL_VW ("CACHE ID", "KIT ID", "COMPONENT ITEM", "CHILD_QTY_AVL")
AS
  SELECT DISTINCT LI.NODE_KEY "CACHE ID",
    P.ITEM_ID "KIT ID",
    C.ITEM_ID "COMPONENT ITEM",
    (SELECT NVL(SUM((yli.quantity+yli.pend_in_qty)-(yli.hard_alloc_qty+yli.soft_alloc_qty)),0)
    FROM yfs_location_inventory yli,
      yfs_inventory_item yii
    WHERE yli.inventory_item_key = yii.inventory_item_key
    AND yii.item_id              = C.item_id
    AND yli.inventory_status     = 'RFI'
    AND yli.location_id NOT     IN ('CYCLE_COUNT_BIN', 'PHYSICAL_COUNT_BIN', 'PACK_BIN')
    AND yli.node_key             = LI.node_key
    )/NVL(KI.KIT_QUANTITY,0) "CHILD_QTY_AVL"
  FROM yfs_item C,
    yfs_kit_item KI,
    yfs_item P,
    yfs_location_inventory LI,
    yfs_inventory_item II
  WHERE C.ITEM_KEY          = KI.COMPONENT_ITEM_KEY
  AND KI.ITEM_KEY           = P.ITEM_KEY
  AND LI.INVENTORY_ITEM_KEY = II.INVENTORY_ITEM_KEY
  AND II.ITEM_ID            = P.ITEM_ID
  ORDER BY "KIT ID";