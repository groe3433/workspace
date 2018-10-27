CREATE OR REPLACE FORCE VIEW EXTN_NWCG_KIT_CONTENTS_VW ("KIT ID", "PARENT_DESCRIPTION", "PARENT_UNIT_COST", "COMPONENT ITEM", "CHILD_DESCRIPTION", "CHILD_UOM", "CHILD_UNIT_COST", "KIT_QUANTITY")
AS
  SELECT DISTINCT p.item_id "KIT ID",
    p.short_description "PARENT_DESCRIPTION",
    p.unit_cost "PARENT_UNIT_COST",
    c.item_id "COMPONENT ITEM",
    c.short_description "CHILD_DESCRIPTION",
    c.uom "CHILD_UOM",
    c.unit_cost "CHILD_UNIT_COST",
    ki.kit_quantity
  FROM yfs_item c,
    yfs_kit_item ki,
    yfs_item p
  WHERE c.item_key = ki.component_item_key
  AND ki.item_key  = p.item_key;