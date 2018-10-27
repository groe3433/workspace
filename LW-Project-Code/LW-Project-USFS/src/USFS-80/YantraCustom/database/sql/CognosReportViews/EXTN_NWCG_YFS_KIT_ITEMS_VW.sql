CREATE OR REPLACE FORCE VIEW EXTN_NWCG_YFS_KIT_ITEMS_VW ("PARENT_ID", "PARENT_SERIAL_NO", "PARENT_DESC", "COMP_ITEM_ID", "COMPONENT_SERIAL_NO", "COMP_DESC", "KIT_QUANTITY")
AS
  SELECT c.item_id "PARENT_ID",
    (SELECT serial_no FROM nwcg_trackable_item_h WHERE item_id = c.item_id
    ) "PARENT_SERIAL_NO",
    c.description "PARENT_DESC",
    a.item_id "COMP_ITEM_ID",
    (SELECT serial_no FROM nwcg_trackable_item_h WHERE item_id = a.item_id
    ) "COMPONENT_SERIAL_NO",
    a.description "COMP_DESC",
    b.kit_quantity
  FROM yfs_item a,
    yfs_kit_item b,
    yfs_item c
  WHERE a.item_key = b.component_item_key
  AND b.item_key   = c.item_key
  AND a.kit_code  IS NOT NULL;