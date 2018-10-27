CREATE OR REPLACE FORCE VIEW EXTN_NWCG_LOSS_USE_SUM_RPT_VW ("ITEM_ID", "KIT ID", "CLASS", "ITEM_DESCRIPTION", "UOM", "QTY_AS_ITEM", "QTY_REFURB", "QTY_AS_KITS", "TRANSFER_IN", "TRANSFERRED_IN_AS_KITS", "PARTS_USED", "RCD_ITEMS", "RCD_AS_KITS", "RCD_IN_KITS", "TRANSFER_OUT", "TRANSFERRED_OUT_AS_KITS", "QTY_WO", "QTY_UNSER", "QTY_UNSER_NWT", "EXTN_CACHE_SHIP_ACCT_CODE", "SA_OVERRIDE_CODE", "INCIDENT NO", "EXTN_INCIDENT_NAME", "OTHER ACCT CODE", "BLM ACCT CODE", "FS ACCT CODE", "EXTN_OVERRIDE_CODE", "YEAR", "CACHE ID")
AS
  SELECT irb."COMPONENT ITEM" "ITEM_ID",
    irb."KIT ID",
    yi.tax_product_code "CLASS",
    yi.description "ITEM_DESCRIPTION",
    yi.uom,
    (SELECT SUM(ol.shipped_quantity)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_incident_no = m.incident_no
    AND oh.extn_incident_year = m.year
    AND oh.order_header_key   = ol.order_header_key
    AND oh.document_type      = '0001'
    AND oh.order_type NOT LIKE '%Refurb%'
    AND rtrim(oh.ship_node)  = rtrim(irb.cache_id)
    AND ol.item_id           = irb."COMPONENT ITEM"
    AND LENGTH(irb."KIT ID") = 1
    ) "QTY_AS_ITEM",
    (SELECT SUM(ol.shipped_quantity)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_incident_no = m.incident_no
    AND oh.extn_incident_year = m.year
    AND oh.order_header_key   = ol.order_header_key
    AND oh.document_type      = '0001'
    AND oh.order_type LIKE '%Refurb%'
    AND rtrim(oh.ship_node)  = rtrim(irb.cache_id)
    AND ol.item_id           = irb."COMPONENT ITEM"
    AND LENGTH(irb."KIT ID") = 1
    ) "QTY_REFURB",
    (SELECT irb.kit_qty *
      (SELECT SUM(ol.shipped_quantity)
      FROM yfs_order_line ol,
        yfs_order_header oh
      WHERE oh.extn_incident_no = m.incident_no
      AND oh.extn_incident_year = m.year
      AND oh.order_header_key   = ol.order_header_key
      AND oh.document_type      = '0001'
      AND oh.order_type NOT LIKE '%Refurb%'
      AND ol.item_id          = irb."KIT ID"
      AND rtrim(oh.ship_node) = rtrim(irb.cache_id)
      )
    FROM dual
    ) "QTY_AS_KITS",
    (SELECT SUM(ol.ordered_qty)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_to_incident_no            = m.incident_no
    AND oh.extn_to_incident_year            = m.year
    AND rtrim(oh.extn_to_incident_cache_id) = rtrim(irb.cache_id)
    AND oh.order_header_key                 = ol.order_header_key
    AND oh.document_type LIKE '0008%'
    AND ol.item_id           = irb."COMPONENT ITEM"
    AND LENGTH(irb."KIT ID") = 1
    ) "TRANSFER_IN",
    (SELECT irb.kit_qty *
      (SELECT SUM(ol.ordered_qty)
      FROM yfs_order_line ol,
        yfs_order_header oh
      WHERE oh.extn_to_incident_no            = m.incident_no
      AND oh.extn_to_incident_year            = m.year
      AND rtrim(oh.extn_to_incident_cache_id) = rtrim(irb.cache_id)
      AND oh.order_header_key                 = ol.order_header_key
      AND oh.document_type LIKE '0008%'
      AND ol.item_id = irb."KIT ID"
      )
    FROM dual
    ) "TRANSFERRED_IN_AS_KITS",
    (SELECT SUM(ol.ordered_qty)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_incident_no = m.incident_no
    AND oh.extn_incident_year = m.year
    AND oh.order_header_key   = ol.order_header_key
    AND oh.document_type      = '0001'
    AND rtrim(oh.ship_node)   = rtrim(irb.cache_id)
    AND oh.order_type LIKE '%Refurb%'
    AND ol.item_id           = irb."COMPONENT ITEM"
    AND LENGTH(irb."KIT ID") = 1
    ) "PARTS_USED",
    (SELECT SUM(nir.quantity_returned)
    FROM nwcg_incident_return nir
    WHERE nir.incident_no         = m.incident_no
    AND nir.incident_year         = m.year
    AND rtrim(nir.cache_id)       = rtrim(irb.cache_id)
    AND nir.item_id               = irb."COMPONENT ITEM"
    AND nir.received_as_component = 'N'
    AND LENGTH(irb."KIT ID")      = 1
    ) "RCD_ITEMS",
    (SELECT SUM(nir.quantity_returned)
    FROM nwcg_incident_return nir
    WHERE nir.incident_no         = m.incident_no
    AND nir.incident_year         = m.year
    AND rtrim(nir.cache_id)       = rtrim(irb.cache_id)
    AND nir.item_id               = irb."COMPONENT ITEM"
    AND nir.received_as_component = 'Y'
    AND LENGTH(irb."KIT ID")      = 1
    ) "RCD_AS_KITS",
    (SELECT irb.kit_qty *
      (SELECT SUM(nir.quantity_returned)
      FROM nwcg_incident_return nir
      WHERE nir.incident_no = m.incident_no
      AND nir.incident_year = m.year
      AND nir.cache_id      = irb.cache_id
      AND nir.item_id       = irb."KIT ID"
      )
    FROM dual
    ) "RCD_IN_KITS",
    (SELECT SUM(ol.ordered_qty)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE rtrim(oh.extn_incident_no) = rtrim(m.incident_no)
    AND oh.extn_incident_year        = m.year
    AND oh.order_header_key          = ol.order_header_key
    AND rtrim(oh.ship_node)          = rtrim(irb.cache_id)
    AND oh.document_type LIKE '0008%'
    AND ol.item_id           = irb."COMPONENT ITEM"
    AND LENGTH(irb."KIT ID") = 1
    ) "TRANSFER_OUT",
    (SELECT irb.kit_qty *
      (SELECT SUM(ol.ordered_qty)
      FROM yfs_order_line ol,
        yfs_order_header oh
      WHERE rtrim(oh.extn_incident_no) = rtrim(m.incident_no)
      AND oh.extn_incident_year        = m.year
      AND oh.order_header_key          = ol.order_header_key
      AND rtrim(oh.ship_node)          = rtrim(irb.cache_id)
      AND oh.document_type LIKE '0008%'
      AND ol.item_id = irb."KIT ID"
      )
    FROM dual
    ) "TRANSFERRED_OUT_AS_KITS",
    (SELECT SUM(wo.quantity_allocated)
    FROM yfs_work_order wo
    WHERE wo.extn_incident_no = m.incident_no
    AND wo.extn_incident_year = m.year
    AND wo.document_type      = '7001'
    AND rtrim(wo.node_key)    = rtrim(irb.cache_id)
    AND rtrim(wo.item_id)     = rtrim(irb."COMPONENT ITEM")
    AND LENGTH(irb."KIT ID")  = 1
    ) "QTY_WO",
    (SELECT SUM(nir.quantity_uns_return)
    FROM nwcg_incident_return nir
    WHERE nir.incident_no    = m.incident_no
    AND nir.incident_year    = m.year
    AND rtrim(nir.cache_id)  = rtrim(irb.cache_id)
    AND rtrim(nir.item_id)   = rtrim(irb."COMPONENT ITEM")
    AND LENGTH(irb."KIT ID") = 1
    ) "QTY_UNSER",
    (SELECT SUM(quantity_uns_nwt_return)
    FROM nwcg_incident_return nir
    WHERE nir.incident_no    = m.incident_no
    AND nir.incident_year    = m.year
    AND rtrim(nir.cache_id)  = rtrim(irb.cache_id)
    AND rtrim(nir.item_id)   = rtrim(irb."COMPONENT ITEM")
    AND LENGTH(irb."KIT ID") = 1
    ) "QTY_UNSER_NWT",
    (SELECT MAX(extn_cache_ship_acct_code)
    FROM yfs_order_header oh,
      yfs_order_line ol
    WHERE oh.extn_incident_no = m.incident_no
    AND oh.extn_incident_year = m.year
    AND oh.order_header_key   = ol.order_header_key
    AND oh.document_type      = '0001'
    AND rtrim(oh.ship_node)   = rtrim(irb.cache_id)
    AND ol.item_id            = irb."COMPONENT ITEM"
    ) "EXTN_CACHE_SHIP_ACCT_CODE",
    (SELECT MAX(extn_sa_override_code)
    FROM yfs_order_header oh,
      yfs_order_line ol
    WHERE oh.extn_incident_no = m.incident_no
    AND oh.extn_incident_year = m.year
    AND oh.order_header_key   = ol.order_header_key
    AND oh.document_type      = '0001'
    AND rtrim(oh.ship_node)   = rtrim(irb.cache_id)
    AND ol.item_id            = irb."COMPONENT ITEM"
    ) "SA_OVERRIDE_CODE",
    m.incident_no "INCIDENT NO",
    m.incident_name "EXTN_INCIDENT_NAME",
    m.incident_other_acct_code "OTHER ACCT CODE",
    m.incident_blm_acct_code "BLM ACCT CODE",
    m.incident_fs_acct_code "FS ACCT CODE",
    m.override_code "EXTN_OVERRIDE_CODE",
    m.year,
    irb.cache_id "CACHE ID"
  FROM extn_nwcg_incident_rpt_bkit_vw irb,
    nwcg_incident_order m,
    yfs_item yi
  WHERE ( (irb.incident_no = m.incident_no
  AND irb.incident_year    = m.year)
  OR (irb.incident_no      = m.replaced_incident_no
  AND irb.incident_year    = m.replaced_incident_year) )
  AND irb."COMPONENT ITEM" = yi.item_id;