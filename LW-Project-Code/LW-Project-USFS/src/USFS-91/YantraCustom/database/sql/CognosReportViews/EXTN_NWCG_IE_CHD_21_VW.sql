CREATE OR REPLACE FORCE VIEW EXTN_NWCG_IE_CHD_21_VW ("PARENT_ITEM_KEY", "CHILD_ITEM_KEY", "PARENT_ITEM_ID", "PARENT_DESCRIPTION", "CHILD_ITEM_ID", "CHILD_DESCRIPTION", "CHILD_QTY")
                           AS
  SELECT yki.item_key      AS parent_item_key,
    yki.component_item_key AS child_item_key,
    yi1.item_id            AS parent_item_id,
    yi1.short_description  AS parent_description,
    yi2.item_id            AS child_item_id,
    yi2.short_description  AS child_description,
    yki.kit_quantity       AS child_qty
  FROM yfs_kit_item yki,
    yfs_item yi1,
    yfs_item yi2
  WHERE yki.item_key         = yi1.item_key
  AND yki.component_item_key = yi2.item_key
  ORDER BY yi1.item_id;