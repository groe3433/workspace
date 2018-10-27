CREATE OR REPLACE FORCE VIEW EXTN_NWCG_KPACK_REPORT_NT_VW ("CACHE ID", "KIT ID", "PARENT_DESCRIPTION", "PARENT_UNIT_COST", "COMPONENT ITEM", "CHILD_DESCRIPTION", "CHILD_UOM", "CHILD_UNIT_COST", "KIT_QUANTITY", "COMPANY", "CACHE_ADDRESS1", "CACHE_ADDRESS2", "CITY", "STATE", "ZIP", "UNIT_WEIGHT", "UNIT_WEIGHT_UOM", "VOLUME", "VOLUME_UOM", "ITEM_INSTRUCTIONS")
AS
  SELECT DISTINCT e.organization_code "CACHE ID",
    p.item_id "KIT ID",
    p.description "PARENT_DESCRIPTION",
    p.unit_cost "PARENT_UNIT_COST",
    c.item_id "COMPONENT ITEM",
    c.description "CHILD_DESCRIPTION",
    c.uom "CHILD_UOM",
    c.unit_cost "CHILD_UNIT_COST",
    ki.kit_quantity,
    d.company,
    d.address_line1 "CACHE_ADDRESS1",
    d.address_line2 "CACHE_ADDRESS2",
    d.city "CITY",
    d.state "STATE",
    d.zip_code "ZIP",
    p.unit_weight,
    p.unit_weight_uom,
    ROUND(((p.unit_height*p.unit_length*p.unit_width)*0.0005),2) "VOLUME",
    'CUFEET' "VOLUME_UOM",
    (SELECT i.instructions
    FROM extn_nwcg_item_instructions_vw i
    WHERE i.item_key       = p.item_key
    AND i.instruction_type = 'PACK'
    ) "ITEM_INSTRUCTIONS"
  FROM yfs_item c,
    yfs_kit_item ki,
    yfs_item p,
    yfs_person_info d,
    yfs_organization e
  WHERE c.item_key = ki.component_item_key
  AND ki.item_key  = p.item_key
  AND e.is_node    = 'Y'
  AND e.organization_code NOT LIKE '%TEMPLATE%'
  AND e.corporate_address_key = d.person_info_key;