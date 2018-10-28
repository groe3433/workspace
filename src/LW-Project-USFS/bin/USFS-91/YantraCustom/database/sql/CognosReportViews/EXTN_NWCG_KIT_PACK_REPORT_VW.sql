CREATE OR REPLACE FORCE VIEW EXTN_NWCG_KIT_PACK_REPORT_VW ("KIT ITEM", "PARENT DESCRIPTION", "PARENT_UNIT_COST", "PARENT_UOM", "KIT SERIAL", "CACHE ID", "COMPONENT ITEM", "COMPONENT DESCRIPTION", "COMPONENT_UOM", "COMPONENT SERIAL", "COMPONENT_QUANTITY", "UNIT_WEIGHT", "UNIT_WEIGHT_UOM", "VOLUME", "VOLUME_UOM", "IS_SERIAL_TRACKED", "STATUS", "ITEM_INSTRUCTIONS", "COMPANY", "CACHE_ADDRESS1", "CACHE_ADDRESS2", "CITY", "STATE", "ZIP")
AS
  SELECT DISTINCT x.item_id "KIT ITEM",
    x.short_description "PARENT DESCRIPTION",
    x.unit_cost "PARENT_UNIT_COST",
    x.unit_of_measure "PARENT_UOM",
    x.serial_no "KIT SERIAL",
    x.node_key "CACHE ID",
    y.item_id "COMPONENT ITEM",
    y.short_description "COMPONENT DESCRIPTION",
    y.unit_of_measure "COMPONENT_UOM",
    y.serial_no "COMPONENT SERIAL",
    yki.kit_quantity "COMPONENT_QUANTITY",
    x.unit_weight,
    x.unit_weight_uom,
    x.volume,
    x.volume_uom,
    'Y' "IS_SERIAL_TRACKED",
    'COMPLETE' "STATUS",
    (SELECT i.instructions
    FROM extn_nwcg_item_instructions_vw i
    WHERE i.item_key       = x.item_key
    AND i.instruction_type = 'PACK'
    ) "ITEM_INSTRUCTIONS",
    d.company,
    d.address_line1"CACHE_ADDRESS1",
    d.address_line2 "CACHE_ADDRESS2",
    d.city "CITY",
    d.state "STATE",
    d.zip_code "ZIP"
  FROM
    (SELECT DISTINCT a1.item_id,
      b1.item_key,
      b1.unit_cost,
      b1.short_description,
      a1.unit_of_measure,
      a1.node_key,
      a1.quantity,
      a1.serial_no,
      a1.parent_serial_no,
      b1.unit_weight,
      b1.unit_weight_uom,
      ROUND(((b1.unit_height*b1.unit_length*b1.unit_width)*0.0005),2) "VOLUME",
      'CUFEET' "VOLUME_UOM"
    FROM nwcg_work_order_serial_kit a1,
      yfs_item b1
    WHERE a1.serial_no   = a1.parent_serial_no
    AND trim(a1.item_id) = trim(b1.item_id)
    ) x,
    (SELECT DISTINCT a2.item_id,
      b2.unit_cost,
      b2.short_description,
      a2.unit_of_measure,
      a2.node_key,
      a2.quantity,
      a2.serial_no,
      a2.parent_serial_no
    FROM nwcg_work_order_serial_kit a2,
      yfs_item b2
    WHERE (a2.serial_no IS NULL
    OR (a2.serial_no    != a2.parent_serial_no
    AND a2.serial_no    IN
      (SELECT serial_no
      FROM yfs_global_serial_num
      WHERE parent_serial_key IN
        (SELECT global_serial_key
        FROM yfs_global_serial_num
        WHERE serial_no = a2.parent_serial_no
        )
      )))
    AND trim(a2.item_id) = trim(b2.item_id)
    ) y,
    yfs_person_info d,
    yfs_organization e,
    yfs_item p,
    yfs_item c1,
    yfs_kit_item yki
  WHERE x.parent_serial_no    = y.parent_serial_no
  AND rtrim(x.node_key)       = rtrim(e.organization_code)
  AND e.corporate_address_key = d.person_info_key
  AND trim(x.item_id)         = trim(p.item_id)
  AND p.item_key              = yki.item_key
  AND trim(y.item_id)         = trim(c1.item_id)
  AND c1.item_key             = yki.component_item_key;