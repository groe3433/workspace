CREATE OR REPLACE FORCE VIEW EXTN_NWCG_INCIDENT_SKIT1_VW ("SUPER_PARENT", "SUPER_PARENT_DESC", "SUPER_PARENT_QTY_ISSUED", "TRANSFER_IN", "TRANSFER_OUT", "PARENT_ITEM", "PARENT_DESC", "PARENT_KIT_QTY", "COMP_ITEM", "ITEM_DESCRIPTION", "COMP_KIT_QTY", "CLASS", "UOM", "INCIDENT NO", "EXTN_INCIDENT_NAME", "OTHER ACCT CODE", "BLM ACCT CODE", "FS ACCT CODE", "EXTN_OVERRIDE_CODE", "YEAR", "CACHE ID")
AS
  SELECT k2_vw.super_parent,
    (SELECT description FROM yfs_item WHERE item_id = k2_vw.super_parent
    ) "SUPER_PARENT_DESC",
    (SELECT SUM(ol.shipped_quantity)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_incident_no = k2_vw.incident_no
    AND oh.extn_incident_year = k2_vw.year
    AND oh.order_header_key   = ol.order_header_key
    AND oh.document_type      = '0001'
    AND rtrim(oh.ship_node)   = rtrim(k2_vw."CACHE ID")
    AND ol.item_id            = k2_vw.super_parent
    ) "SUPER_PARENT_QTY_ISSUED",
    (SELECT SUM(ol.ordered_qty)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_to_incident_no            = k2_vw.incident_no
    AND oh.extn_to_incident_year            = k2_vw.year
    AND rtrim(oh.extn_to_incident_cache_id) = rtrim(k2_vw."CACHE ID")
    AND oh.order_header_key                 = ol.order_header_key
    AND oh.document_type LIKE '0008%'
    AND ol.item_id = k2_vw.super_parent
    ) "TRANSFER_IN",
    (SELECT SUM(ol.ordered_qty)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_incident_no = k2_vw.incident_no
    AND oh.extn_incident_year = k2_vw.year
    AND rtrim(oh.ship_node)   = rtrim(k2_vw."CACHE ID")
    AND oh.order_header_key   = ol.order_header_key
    AND oh.document_type LIKE '0008%'
    AND ol.item_id = k2_vw.super_parent
    ) "TRANSFER_OUT",
    k2_vw.parent_item,
    (SELECT description FROM yfs_item WHERE item_id = k2_vw.parent_item
    ) "PARENT_DESC",
    k2_vw.parent_kit_qty,
    k2_vw.comp_item,
    yi.description "ITEM_DESCRIPTION",
    k2_vw.comp_kit_qty,
    yi.tax_product_code "CLASS",
    yi.uom,
    m.incident_no "INCIDENT NO",
    m.incident_name "EXTN_INCIDENT_NAME",
    m.incident_other_acct_code "OTHER ACCT CODE",
    m.incident_blm_acct_code "BLM ACCT CODE",
    m.incident_fs_acct_code "FS ACCT CODE",
    m.override_code "EXTN_OVERRIDE_CODE",
    m.year,
    k2_vw."CACHE ID"
  FROM extn_nwcg_incident_rpt_kit2_vw k2_vw,
    nwcg_incident_order m,
    yfs_item yi
  WHERE ( (k2_vw.incident_no = m.incident_no
  AND k2_vw.year             = m.year)
  OR (k2_vw.incident_no      = m.replaced_incident_no
  AND k2_vw.year             = m.replaced_incident_year)
  OR (k2_vw.incident_no      = m.replaced_incident_no_2
  AND k2_vw.year             = m.replaced_incident_year_2) )
  AND k2_vw.comp_item        = yi.item_id;