CREATE OR REPLACE FORCE VIEW EXTN_NWCG_INCIDENT_OTH_RPTS_VW ("ITEM_ID", "SERIAL_NO", "CLASS", "ITEM_DESCRIPTION", "UOM", "QTY_AS_ITEM", "QTY_AS_KITS", "TRANSFER_IN", "TRANSFER_IN_AS_KITS", "RFBXFER_IN_AS_ITEMS", "RFBXFER_IN_AS_KITS", "RCD_ITEMS", "RCD_KIT_ITEMS", "RCD_IN_KITS", "TRANSFER_OUT", "TRANSFER_OUT_AS_KITS", "RFBXFER_OUT_AS_ITEMS", "RFBXFER_OUT_AS_KITS", "INCIDENT NO", "EXTN_INCIDENT_NAME", "OTHER ACCT CODE", "BLM ACCT CODE", "FS ACCT CODE", "EXTN_OVERRIDE_CODE", "YEAR", "CACHE ID")
AS
  SELECT irb.item_id,
    irb.serial_no,
    yi.tax_product_code "CLASS",
    yi.description "ITEM_DESCRIPTION",
    yi.uom,
    (SELECT SUM(st.quantity)
    FROM yfs_order_header oh,
      yfs_shipment_line sl,
      yfs_shipment_tag_serial st
    WHERE oh.extn_incident_no = irb.extn_incident_no
    AND oh.extn_incident_year = irb.extn_incident_year
    AND oh.document_type     IN ('0001','0007.ex')
    AND irb.transaction_type  = 'SHIPMENT'
    AND rtrim(oh.ship_node)   = rtrim(irb."CACHE ID")
    AND sl.item_id            = irb.item_id
    AND oh.order_header_key   = sl.order_header_key
    AND sl.shipment_line_key  = st.shipment_line_key
    AND st.serial_no          = irb.serial_no
    ) "QTY_AS_ITEM",
    (SELECT SUM(st.quantity)
    FROM yfs_order_header oh,
      yfs_shipment_line sl,
      yfs_shipment_tag_serial st
    WHERE oh.extn_incident_no = irb.extn_incident_no
    AND oh.extn_incident_year = irb.extn_incident_year
    AND oh.document_type     IN ('0001','0007.ex')
    AND irb.transaction_type  = 'SHIPMENT'
    AND rtrim(oh.ship_node)   = rtrim(irb."CACHE ID")
    AND sl.item_id            = irb.parent_id
    AND st.serial_no          = irb.parent_serial
      --  and irb.parent_id is not null
    AND oh.order_header_key  = sl.order_header_key
    AND sl.shipment_line_key = st.shipment_line_key
    ) "QTY_AS_KITS",
    (SELECT NVL(SUM(ol.ordered_qty),0)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_to_incident_no            = irb.extn_incident_no
    AND oh.extn_to_incident_year            = irb.extn_incident_year
    AND rtrim(oh.extn_to_incident_cache_id) = rtrim(irb."CACHE ID")
    AND oh.order_header_key                 = ol.order_header_key
    AND oh.document_type LIKE '0008%'
    AND irb.transaction_type = 'RECEIPT'
    AND ol.item_id           = irb.item_id
    AND ol.extn_trackable_id = irb.serial_no
    ) "TRANSFER_IN",
    (SELECT NVL(SUM(ol.ordered_qty),0)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_to_incident_no            = irb.extn_incident_no
    AND oh.extn_to_incident_year            = irb.extn_incident_year
    AND rtrim(oh.extn_to_incident_cache_id) = rtrim(irb."CACHE ID")
    AND oh.order_header_key                 = ol.order_header_key
    AND oh.document_type LIKE '0008%'
    AND irb.transaction_type = 'RECEIPT'
    AND ol.item_id           = irb.parent_id
    AND ol.extn_trackable_id = irb.parent_serial
    ) "TRANSFER_IN_AS_KITS",
    (SELECT NVL(SUM(ol.ordered_qty),0)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_incident_no = irb.extn_incident_no
    AND oh.extn_incident_year = irb.extn_incident_year
    AND oh.order_header_key   = ol.order_header_key
    AND oh.document_type      = '0006'
    AND oh.order_type         = irb.order_type
    AND irb.order_type        = 'Refurb Transfer'
    AND irb.transaction_type  = 'RECEIPT'
    AND ol.item_id            = irb.item_id
    AND ol.serial_no          = irb.serial_no
    ) "RFBXFER_IN_AS_ITEMS",
    (SELECT NVL(SUM(ol.ordered_qty),0)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_incident_no = irb.extn_incident_no
    AND oh.extn_incident_year = irb.extn_incident_year
    AND oh.order_header_key   = ol.order_header_key
    AND oh.document_type      = '0006'
    AND oh.order_type         = irb.order_type
    AND irb.order_type        = 'Refurb Transfer'
    AND irb.transaction_type  = 'RECEIPT'
    AND ol.item_id            = irb.parent_id
    AND ol.serial_no          = irb.parent_serial
    ) "RFBXFER_IN_AS_KITS",
    (
    (SELECT NVL(SUM(rl.quantity),0)
    FROM yfs_receipt_line rl,
      yfs_receipt_header rh
    WHERE rh.receipt_header_key       = rl.receipt_header_key
    AND rtrim(rh.receivingnode_key)   = rtrim(irb."CACHE ID")
    AND rh.extn_incident_no           = irb.extn_incident_no
    AND rh.extn_incident_year         = irb.extn_incident_year
    AND rh.document_type              = '0010'
    AND irb.transaction_type          = 'RECEIPT'
    AND rl.item_id                    = irb.item_id
    AND rl.serial_no                  = irb.serial_no
    AND rl.extn_received_as_component = 'N'
    ) +
    (SELECT NVL(SUM(lpn.quantity),0)
    FROM nwcg_lpn_return lpn,
      yfs_receipt_header lpn2
    WHERE lpn.return_header_key       = lpn2.receipt_header_key
    AND lpn2.extn_incident_no         = irb.extn_incident_no
    AND lpn2.extn_incident_year       = irb.extn_incident_year
    AND rtrim(lpn2.receivingnode_key) = rtrim(irb."CACHE ID")
    AND trim(lpn.item_id)             = trim(irb.item_id)
    AND trim(lpn.serial_no)           = trim(irb.serial_no)
    )) "RCD_ITEMS",
    (SELECT SUM(rl.quantity)
    FROM yfs_receipt_line rl,
      yfs_receipt_header rh
    WHERE rh.receipt_header_key       = rl.receipt_header_key
    AND rtrim(rh.receivingnode_key)   = rtrim(irb."CACHE ID")
    AND rh.extn_incident_no           = irb.extn_incident_no
    AND rh.extn_incident_year         = irb.extn_incident_year
    AND rh.document_type              = '0010'
    AND irb.transaction_type          = 'RECEIPT'
    AND rl.item_id                    = irb.item_id
    AND rl.serial_no                  = irb.serial_no
    AND rl.extn_received_as_component = 'Y'
    ) "RCD_KIT_ITEMS",
    (
    (SELECT NVL(SUM(rl.quantity),0)
    FROM yfs_receipt_line rl,
      yfs_receipt_header rh
    WHERE rh.receipt_header_key     = rl.receipt_header_key
    AND irb.receipt_line_key        = rl.receipt_line_key
    AND rtrim(rh.receivingnode_key) = rtrim(irb."CACHE ID")
    AND rh.extn_incident_no         = irb.extn_incident_no
    AND rh.extn_incident_year       = irb.extn_incident_year
    AND rh.document_type            = '0010'
    AND irb.transaction_type        = 'RECEIPT'
    AND irb.parent_id              IS NOT NULL
      --  and rl.item_id = irb.parent_id
      --  and rl.serial_no = irb.parent_serial
    AND rl.extn_received_as_component = 'N'
    ) +
    (SELECT NVL(SUM(rl.quantity),0)
    FROM yfs_receipt_line rl,
      yfs_receipt_header rh
    WHERE rh.receipt_header_key       = rl.receipt_header_key
    AND irb.receipt_line_key          = rl.receipt_line_key
    AND rtrim(rh.receivingnode_key)   = rtrim(irb."CACHE ID")
    AND rh.extn_incident_no           = irb.extn_incident_no
    AND rh.extn_incident_year         = irb.extn_incident_year
    AND rh.document_type              = '0010'
    AND irb.transaction_type          = 'RECEIPT'
    AND irb.parent_id                IS NOT NULL
    AND rl.extn_received_as_component = 'Y'
    ) +
    (SELECT NVL(SUM(lpn.quantity),0)
    FROM nwcg_lpn_return lpn,
      yfs_receipt_header rh
    WHERE rh.receipt_header_key     = lpn.return_header_key
    AND rtrim(rh.receivingnode_key) = rtrim(irb."CACHE ID")
    AND rh.extn_incident_no         = irb.extn_incident_no
    AND rh.extn_incident_year       = irb.extn_incident_year
    AND rh.document_type            = '0010'
    AND irb.transaction_type        = 'RECEIPT'
    AND trim(lpn.item_id)           = trim(irb.parent_id)
    AND trim(lpn.serial_no)         = trim(irb.parent_serial)
    )) "RCD_IN_KITS",
    (SELECT NVL(SUM(ol.ordered_qty),0)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_incident_no = irb.extn_incident_no
    AND oh.extn_incident_year = irb.extn_incident_year
    AND oh.order_header_key   = ol.order_header_key
    AND rtrim(oh.ship_node)   = rtrim(irb."CACHE ID")
    AND oh.document_type LIKE '0008%'
    AND irb.transaction_type = 'SHIPMENT'
    AND ol.item_id           = irb.item_id
    AND ol.extn_trackable_id = irb.serial_no
    ) "TRANSFER_OUT",
    (SELECT NVL(SUM(ol.ordered_qty),0)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_incident_no = irb.extn_incident_no
    AND oh.extn_incident_year = irb.extn_incident_year
    AND rtrim(oh.ship_node)   = rtrim(irb."CACHE ID")
    AND oh.order_header_key   = ol.order_header_key
    AND oh.document_type LIKE '0008%'
    AND irb.transaction_type = 'SHIPMENT'
    AND ol.item_id           = irb.parent_id
    AND ol.extn_trackable_id = irb.parent_serial
    ) "TRANSFER_OUT_AS_KITS",
    (SELECT NVL(SUM(ol.ordered_qty),0)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_incident_no = irb.extn_incident_no
    AND oh.extn_incident_year = irb.extn_incident_year
    AND oh.order_header_key   = ol.order_header_key
    AND oh.document_type      = '0006'
    AND oh.order_type         = irb.order_type
    AND irb.order_type        = 'Refurb Transfer'
    AND irb.transaction_type  = 'RECEIPT'
    AND ol.item_id            = irb.item_id
    AND ol.serial_no          = irb.serial_no
    ) "RFBXFER_OUT_AS_ITEMS",
    (SELECT NVL(SUM(ol.ordered_qty),0)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_incident_no = irb.extn_incident_no
    AND oh.extn_incident_year = irb.extn_incident_year
    AND oh.order_header_key   = ol.order_header_key
    AND oh.document_type      = '0006'
    AND oh.order_type         = irb.order_type
    AND irb.order_type        = 'Refurb Transfer'
    AND irb.transaction_type  = 'RECEIPT'
    AND ol.item_id            = irb.parent_id
    AND ol.serial_no          = irb.parent_serial
    ) "RFBXFER_OUT_AS_KITS",
    m.incident_no "INCIDENT NO",
    m.incident_name "EXTN_INCIDENT_NAME",
    m.incident_other_acct_code "OTHER ACCT CODE",
    m.incident_blm_acct_code "BLM ACCT CODE",
    m.incident_fs_acct_code "FS ACCT CODE",
    m.override_code "EXTN_OVERRIDE_CODE",
    m.year,
    irb."CACHE ID"
  FROM extn_nwcg_incident_rpt_sr_vw irb,
    nwcg_incident_order m,
    yfs_item yi
  WHERE ( (irb.extn_incident_no = m.incident_no
  AND irb.extn_incident_year    = m.year)
  OR (irb.extn_incident_no      = m.replaced_incident_no
  AND irb.extn_incident_year    = m.replaced_incident_year)
  OR (irb.extn_incident_no      = m.replaced_incident_no_2
  AND IRB.EXTN_INCIDENT_YEAR    = M.REPLACED_INCIDENT_YEAR_2) )
  AND irb.item_id               = yi.item_id;