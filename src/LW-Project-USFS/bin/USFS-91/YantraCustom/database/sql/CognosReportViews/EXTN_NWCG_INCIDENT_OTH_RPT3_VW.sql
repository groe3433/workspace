CREATE OR REPLACE FORCE VIEW EXTN_NWCG_INCIDENT_OTH_RPT3_VW ("ITEM_ID", "CLASS", "ITEM_DESCRIPTION", "UOM", "QTY_AS_ITEM", "QTY_AS_KITS", "TRANSFER_IN", "PARTS_USED", "RCD_ITEMS", "RCD_AS_KITS", "TRANSFER_OUT", "QTY_WO", "QTY_UNSER", "INCIDENT NO", "EXTN_INCIDENT_NAME", "OTHER ACCT CODE", "BLM ACCT CODE", "FS ACCT CODE", "EXTN_OVERRIDE_CODE", "EXTN_CACHE_SHIP_ACCT_CODE", "UNIT_PRICE", "YEAR", "CACHE ID")
AS
  SELECT m.item_id,
    m.tax_product_code "CLASS",
    m.item_description,
    m.uom,
    (SELECT SUM(ol.shipped_quantity)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_incident_no = m.incident_no
    AND oh.order_header_key   = ol.order_header_key
    AND oh.document_type      = '0001'
    AND rtrim(oh.ship_node)   = rtrim(m.primary_cache_id)
    AND ol.item_id            = m.item_id
    ) "QTY_AS_ITEM",
    (SELECT SUM(a1.kit_quantity *
      (SELECT SUM(ol.shipped_quantity)
      FROM yfs_order_line ol,
        yfs_order_header oh,
        yfs_item c2
      WHERE oh.extn_incident_no = m.incident_no
      AND oh.order_header_key   = ol.order_header_key
      AND oh.document_type      = '0001'
      AND ol.item_id            = c2.item_id
      AND c2.item_key           = a1.item_key
      AND ol.kit_code          != 'PK'
      AND rtrim(oh.ship_node)   = rtrim(m.primary_cache_id)
      ))
    FROM yfs_kit_item a1,
      yfs_item c1
    WHERE c1.item_id          = m.item_id
    AND a1.component_item_key = c1.item_key
    AND c1.organization_code  = 'NWCG'
    ) "QTY_AS_KITS",
    (SELECT SUM(ol.shipped_quantity)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_incident_no = m.incident_no
    AND oh.order_header_key   = ol.order_header_key
    AND oh.document_type LIKE '0008%'
    AND rtrim(oh.receiving_node) = rtrim(m.primary_cache_id)
    AND ol.item_id               = m.item_id
    ) "TRANSFER_IN",
    (SELECT SUM(ol.ordered_qty)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_incident_no = m.incident_no
    AND oh.order_header_key   = ol.order_header_key
    AND oh.document_type      = '0001'
    AND rtrim(oh.ship_node)   = rtrim(m.primary_cache_id)
    AND oh.order_type LIKE '%Refurb%'
    AND ol.item_id = m.item_id
    ) "PARTS_USED",
    (SELECT SUM(rl.quantity)
    FROM yfs_receipt_line rl,
      yfs_receipt_header rh
    WHERE rh.receipt_header_key     = rl.receipt_header_key
    AND rtrim(rh.receivingnode_key) = rtrim(m.primary_cache_id)
    AND rh.extn_incident_no         = m.incident_no
    AND rh.document_type            = '0010'
    AND rl.item_id                  = m.item_id
    ) "RCD_ITEMS",
    (SELECT SUM(a1.kit_quantity *
      (SELECT SUM(rl.quantity)
      FROM yfs_receipt_line rl,
        yfs_receipt_header rh,
        yfs_item c2
      WHERE rh.document_type          = '0010'
      AND rtrim(rh.receivingnode_key) = rtrim(m.primary_cache_id)
      AND rh.extn_incident_no         = m.incident_no
      AND rl.receipt_header_key       = rh.receipt_header_key
      AND rl.item_id                  = c2.item_id
      AND c2.item_key                 = a1.item_key
      ))
    FROM yfs_kit_item a1,
      yfs_item c1
    WHERE c1.kit_code        != 'PK'
    AND c1.item_id            = m.item_id
    AND a1.component_item_key = c1.item_key
    AND c1.organization_code  = 'NWCG'
    ) "RCD_AS_KITS",
    (SELECT SUM(ol.shipped_quantity)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_incident_no = m.incident_no
    AND oh.order_header_key   = ol.order_header_key
    AND oh.document_type LIKE '0008%'
    AND rtrim(oh.ship_node) = rtrim(m.primary_cache_id)
    AND ol.item_id          = m.item_id
    ) "TRANSFER_OUT",
    (SELECT SUM(wo.quantity_allocated)
    FROM yfs_work_order wo
    WHERE wo.extn_incident_no = m.incident_no
    AND wo.document_type      = '7001'
    AND rtrim(wo.node_key)    = rtrim(m.primary_cache_id)
    AND rtrim(wo.item_id)     = rtrim(m.item_id)
    ) "QTY_WO",
    (SELECT SUM(nir.quantity_uns_return + quantity_uns_nwt_return)
    FROM nwcg_incident_return nir
    WHERE rtrim(nir.incident_no) = rtrim(m.incident_no)
    AND rtrim(nir.cache_id)      = rtrim(m.primary_cache_id)
    AND rtrim(nir.item_id)       = rtrim(m.item_id)
    ) "QTY_UNSER",
    m.incident_no "INCIDENT NO",
    m.incident_name "EXTN_INCIDENT_NAME",
    m.incident_other_acct_code "OTHER ACCT CODE",
    m.incident_blm_acct_code "BLM ACCT CODE",
    m.incident_fs_acct_code "FS ACCT CODE",
    m.override_code "EXTN_OVERRIDE_CODE",
    m.extn_cache_ship_acct_code,
    m.unit_price,
    m.year,
    m.primary_cache_id "CACHE ID"
  FROM
    (SELECT DISTINCT nio.incident_no,
      nio.incident_name,
      nio.incident_other_acct_code,
      nio.incident_blm_acct_code,
      nio.incident_fs_acct_code,
      nio.override_code,
      a.extn_cache_ship_acct_code,
      b.item_id,
      b.item_description,
      b.uom,
      b.unit_price,
      nio.year,
      nio.primary_cache_id,
      yi.tax_product_code
    FROM yfs_order_header a,
      yfs_order_line b,
      nwcg_incident_order nio,
      yfs_item yi
    WHERE a.order_header_key = b.order_header_key
    AND b.item_id            = yi.item_id
    AND yi.kit_code          = 'PK'
    AND a.extn_incident_no   = nio.incident_no
    ) m ;