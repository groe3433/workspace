CREATE OR REPLACE FORCE VIEW EXTN_NWCG_INCIDENT_RPT_PRE_VW ("ITEM_ID", "PARENT_ITEM", "CLASS", "ITEM_DESCRIPTION", "UOM", "UNIT_PRICE", "QTY_AS_ITEM", "QTY_AS_KITS", "QTY_REFURB", "RFBXFER_IN_AS_ITEMS", "RFBXFER_IN_AS_KITS", "RFBXFER_OUT_AS_ITEMS", "RFBXFER_OUT_AS_KITS", "TRANSFER_IN", "TRANSFERRED_IN_AS_KITS", "PARTS_USED", "RCD_ITEMS", "RCD_AS_KITS", "RCD_IN_KITS", "TRANSFER_OUT", "TRANSFERRED_OUT_AS_KITS", "QTY_WO", "QTY_UNSER", "QTY_RFIREFURB", "QTY_UNSREFURB", "QTY_UNSNWT_REFURB", "QTY_UNSER_NWT", "EXTN_CACHE_SHIP_ACCT_CODE", "SA_OVERRIDE_CODE", "INCIDENT NO", "EXTN_INCIDENT_NAME", "OTHER ACCT CODE", "BLM ACCT CODE", "FS ACCT CODE", "EXTN_OVERRIDE_CODE", "YEAR", "CACHE ID")
AS
  SELECT irb.item_id,
    irb.parent_item,
    yi.tax_product_code "CLASS",
    yi.description "ITEM_DESCRIPTION",
    yi.uom,
    irb.unit_price,
    (SELECT SUM(ol.shipped_quantity)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_incident_no          = irb.incident_no
    AND NVL(oh.extn_incident_year,' ') = NVL(irb.year,' ')
    AND oh.order_header_key            = ol.order_header_key
    AND oh.document_type              IN ('0001','0007.ex')
    AND oh.order_type NOT LIKE '%Refurb%'
    AND rtrim(oh.ship_node) = rtrim(irb."CACHE ID")
    AND ol.item_id          = irb.item_id
    AND ol.unit_price       = irb.unit_price
    ) "QTY_AS_ITEM",
    --and length(irb.parent_item) = 1) "QTY_AS_ITEM",
    (
    SELECT irb.kit_qty *
      (SELECT NVL(SUM(ol.shipped_quantity),0)
      FROM yfs_order_line ol,
        yfs_order_header oh
      WHERE oh.extn_incident_no          = irb.incident_no
      AND NVL(oh.extn_incident_year,' ') = NVL(irb.year,' ')
      AND oh.order_header_key            = ol.order_header_key
      AND oh.document_type              IN ('0001','0007.ex')
      AND oh.order_type NOT LIKE '%Refurb%'
      AND ol.item_id          = irb.parent_item
      AND rtrim(oh.ship_node) = rtrim(irb."CACHE ID")
      )
    FROM dual
    ) "QTY_AS_KITS",
    (SELECT SUM(ol.ordered_qty)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_incident_no          = irb.incident_no
    AND NVL(oh.extn_incident_year,' ') = NVL(irb.year,' ')
    AND oh.order_header_key            = ol.order_header_key
    AND oh.document_type              IN ('0001','0007.ex')
    AND oh.order_type LIKE '%Refurb%'
    AND rtrim(oh.ship_node) = rtrim(irb."CACHE ID")
    AND ol.item_id          = irb.item_id
    AND ol.unit_price       = irb.unit_price
    ) "QTY_REFURB",
    --and length(irb.parent_item) = 1) "QTY_REFURB",
    (
    SELECT NVL(SUM(ol.ordered_qty),0)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_incident_no          = irb.incident_no
    AND NVL(oh.extn_incident_year,' ') = NVL(irb.year,' ')
    AND oh.order_header_key            = ol.order_header_key
    AND oh.document_type               = '0006'
    AND oh.order_type                  = 'Refurb Transfer'
    AND rtrim(oh.receiving_node)       = rtrim(irb."CACHE ID")
    AND ol.item_id                     = irb.item_id
    ) "RFBXFER_IN_AS_ITEMS",
    (SELECT irb.kit_qty *
      (SELECT NVL(SUM(ol.ordered_qty),0)
      FROM yfs_order_line ol,
        yfs_order_header oh
      WHERE oh.extn_incident_no          = irb.incident_no
      AND NVL(oh.extn_incident_year,' ') = NVL(irb.year,' ')
      AND oh.order_header_key            = ol.order_header_key
      AND oh.document_type               = '0006'
      AND oh.order_type                  = 'Refurb Transfer'
      AND rtrim(oh.receiving_node)       = rtrim(irb."CACHE ID")
      AND ol.item_id                     = irb.parent_item
      )
    FROM dual
    ) "RFBXFER_IN_AS_KITS",
    (SELECT NVL(SUM(ol.ordered_qty),0)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_incident_no          = irb.incident_no
    AND NVL(oh.extn_incident_year,' ') = NVL(irb.year,' ')
    AND oh.order_header_key            = ol.order_header_key
    AND oh.document_type               = '0006'
    AND oh.order_type                  = 'Refurb Transfer'
    AND rtrim(oh.ship_node)            = rtrim(irb."CACHE ID")
    AND ol.item_id                     = irb.item_id
    ) "RFBXFER_OUT_AS_ITEMS",
    (SELECT irb.kit_qty *
      (SELECT NVL(SUM(ol.ordered_qty),0)
      FROM yfs_order_line ol,
        yfs_order_header oh
      WHERE oh.extn_incident_no          = irb.incident_no
      AND NVL(oh.extn_incident_year,' ') = NVL(irb.year,' ')
      AND oh.order_header_key            = ol.order_header_key
      AND oh.document_type               = '0006'
      AND oh.order_type                  = 'Refurb Transfer'
      AND rtrim(oh.ship_node)            = rtrim(irb."CACHE ID")
      AND ol.item_id                     = irb.parent_item
      )
    FROM dual
    ) "RFBXFER_OUT_AS_KITS",
    (SELECT SUM(ol.ordered_qty)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_to_incident_no       = irb.incident_no
    AND NVL(oh.extn_incident_year,' ') = NVL(irb.year,' ')
    AND oh.order_header_key            = ol.order_header_key
    AND oh.document_type LIKE '0008%'
    AND rtrim(oh.ship_node) = rtrim(irb."CACHE ID")
    AND ol.item_id          = irb.item_id
    AND ol.unit_price       = irb.unit_price
    ) "TRANSFER_IN",
    --and length(irb.parent_item) = 1) "TRANSFER_IN",
    (
    SELECT irb.kit_qty *
      (SELECT SUM(ol.ordered_qty)
      FROM yfs_order_line ol,
        yfs_order_header oh
      WHERE oh.extn_to_incident_no       = irb.incident_no
      AND NVL(oh.extn_incident_year,' ') = NVL(irb.year,' ')
      AND oh.order_header_key            = ol.order_header_key
      AND oh.document_type LIKE '0008%'
      AND rtrim(oh.ship_node) = rtrim(irb."CACHE ID")
      AND ol.item_id          = irb.parent_item
      )
    FROM dual
    ) "TRANSFERRED_IN_AS_KITS",
    (SELECT SUM(ol.ordered_qty)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_incident_no          = irb.incident_no
    AND NVL(oh.extn_incident_year,' ') = NVL(irb.year,' ')
    AND oh.order_header_key            = ol.order_header_key
    AND oh.document_type               = '0001'
    AND rtrim(oh.ship_node)            = rtrim(irb."CACHE ID")
    AND oh.order_type LIKE '%Refurb%'
    AND ol.item_id    = irb.item_id
    AND ol.unit_price = irb.unit_price
    ) "PARTS_USED",
    --and length(irb.parent_item) = 1) "PARTS_USED",
    (
    (
    SELECT NVL(SUM(c1.quantity),0)
    FROM yfs_receipt_line c1,
      yfs_receipt_header c2
    WHERE c1.receipt_header_key        = c2.receipt_header_key
    AND c2.extn_incident_no            = irb.incident_no
    AND NVL(c2.extn_incident_year,' ') = NVL(irb.year,' ')
    AND rtrim(c2.receivingnode_key)    = rtrim(irb."CACHE ID")
    AND c1.item_id                     = irb.item_id
    AND c2.document_type               = '0010'
    AND c1.extn_receiving_price        = irb.unit_price
    AND c1.extn_received_as_component  = 'N'
    ) +
    (SELECT NVL(SUM(lpn.quantity),0)
    FROM nwcg_lpn_return lpn,
      yfs_receipt_header lpn2
    WHERE lpn.return_header_key          = lpn2.receipt_header_key
    AND lpn2.extn_incident_no            = irb.incident_no
    AND NVL(lpn2.extn_incident_year,' ') = NVL(irb.year,' ')
    AND rtrim(lpn2.receivingnode_key)    = rtrim(irb."CACHE ID")
    AND trim(lpn.item_id)                = trim(irb.item_id)
    AND lpn.unit_price                   = irb.unit_price
    )) "RCD_ITEMS",
    --and length(irb.parent_item) = 1 ) "RCD_ITEMS",
    (
    SELECT SUM(c1.quantity)
    FROM yfs_receipt_line c1,
      yfs_receipt_header c2
    WHERE c1.receipt_header_key        = c2.receipt_header_key
    AND c2.extn_incident_no            = irb.incident_no
    AND NVL(c2.extn_incident_year,' ') = NVL(irb.year,' ')
    AND rtrim(c2.receivingnode_key)    = rtrim(irb."CACHE ID")
    AND c1.item_id                     = irb.item_id
    AND c1.extn_receiving_price        = irb.unit_price
    AND c1.extn_received_as_component  = 'Y'
    ) "RCD_AS_KITS",
    --and length(irb.parent_item) = 1 ) "RCD_AS_KITS",
    (
    SELECT irb.kit_qty * (
      (SELECT NVL(SUM(c1.quantity),0)
      FROM yfs_receipt_line c1,
        yfs_receipt_header c2
      WHERE c1.receipt_header_key        = c2.receipt_header_key
      AND c2.extn_incident_no            = irb.incident_no
      AND NVL(c2.extn_incident_year,' ') = NVL(irb.year,' ')
      AND rtrim(c2.receivingnode_key)    = rtrim(irb."CACHE ID")
      AND c1.item_id                     = irb.parent_item
      ) +
      (SELECT NVL(SUM(lpn.quantity),0)
      FROM nwcg_lpn_return lpn,
        yfs_receipt_header lpn2
      WHERE lpn.return_header_key          = lpn2.receipt_header_key
      AND lpn2.extn_incident_no            = irb.incident_no
      AND NVL(lpn2.extn_incident_year,' ') = NVL(irb.year,' ')
      AND rtrim(lpn2.receivingnode_key)    = rtrim(irb."CACHE ID")
      AND rtrim(lpn.item_id)               = rtrim(irb.parent_item)
      ))
    FROM dual
    ) "RCD_IN_KITS",
    (SELECT SUM(ol.ordered_qty)
    FROM yfs_order_line ol,
      yfs_order_header oh
    WHERE oh.extn_incident_no          = irb.incident_no
    AND NVL(oh.extn_incident_year,' ') = NVL(irb.year,' ')
    AND oh.order_header_key            = ol.order_header_key
    AND rtrim(oh.ship_node)            = rtrim(irb."CACHE ID")
    AND oh.document_type LIKE '0008%'
    AND ol.item_id    = irb.item_id
    AND ol.unit_price = irb.unit_price
    ) "TRANSFER_OUT",
    --and length(irb.parent_item) = 1) "TRANSFER_OUT",
    (
    SELECT irb.kit_qty *
      (SELECT NVL(SUM(ol.ordered_qty),0)
      FROM yfs_order_line ol,
        yfs_order_header oh
      WHERE oh.extn_incident_no          = irb.incident_no
      AND NVL(oh.extn_incident_year,' ') = NVL(irb.year,' ')
      AND oh.order_header_key            = ol.order_header_key
      AND rtrim(oh.ship_node)            = rtrim(irb."CACHE ID")
      AND oh.document_type LIKE '0008%'
      AND ol.item_id = irb.parent_item
      )
    FROM dual
    ) "TRANSFERRED_OUT_AS_KITS",
    (SELECT SUM(wol.actual_quantity)-SUM(wol.transfer_qty)
    FROM nwcg_master_work_order wo,
      nwcg_master_work_order_line wol
    WHERE wo.incident_no                = irb.incident_no
    AND NVL(wo.incident_year,' ')       = NVL(irb.year,' ')
    AND rtrim(wo.node)                  = rtrim(irb."CACHE ID")
    AND wol.item_id                     = rtrim(irb.item_id)
    AND rtrim(wo.master_work_order_key) = rtrim(wol.master_work_order_key)
    ) "QTY_WO",
    --and length(irb.parent_item) = 1) "QTY_WO",
    (
    SELECT SUM(c1.quantity)
    FROM yfs_receipt_line c1,
      yfs_receipt_header c2
    WHERE c1.receipt_header_key        = c2.receipt_header_key
    AND c2.extn_incident_no            = irb.incident_no
    AND NVL(c2.extn_incident_year,' ') = NVL(irb.year,' ')
    AND rtrim(c2.receivingnode_key)    = rtrim(irb."CACHE ID")
    AND c1.item_id                     = irb.item_id
    AND c1.extn_receiving_price        = irb.unit_price
      --and c1.extn_received_as_component = 'N'
    AND c1.disposition_code = 'UNSERVICE'
    ) "QTY_UNSER",
    --and length(irb.parent_item) = 1 ) "QTY_UNSER",
    --below commented out due to change in 1048 refurb transfers - jimmy
    --    (
    --    SELECT SUM(c1.quantity_rfi_refurb)
    --    FROM nwcg_incident_return c1
    --    WHERE c1.incident_no          = irb.incident_no
    --    AND NVL(c1.incident_year,' ') = NVL(irb.year,' ')
    --    AND rtrim(c1.cache_id)        = rtrim(irb."CACHE ID")
    --    AND c1.item_id                = irb.item_id
    --    AND c1.unit_price             = irb.unit_price
    --    ) "QTY_RFIREFURB",
    (
    SELECT SUM(MWOL.RFI_REFURB_QUANTITY)
    FROM NWCG_MASTER_WORK_ORDER_LINE MWOL
    WHERE TRIM(MWOL.ITEM_ID)              = TRIM(IRB.ITEM_ID)
    AND mwol.is_replaced_item             = 'N'
    AND trim(mwol.MASTER_WORK_ORDER_KEY) IN
      ( SELECT DISTINCT TRIM(mwo.MASTER_WORK_ORDER_KEY)
      FROM NWCG_MASTER_WORK_ORDER mwo
      WHERE MWO.INCIDENT_NO          = IRB.INCIDENT_NO
      AND NVL(MWO.INCIDENT_YEAR,' ') = NVL(IRB.YEAR,' ')
      )
    ) "QTY_RFIREFURB",
    --and length(irb.parent_item) = 1 ) "QTY_RFIREFURB",
    (
    SELECT SUM(c1.quantity_uns_refurb)
    FROM nwcg_incident_return c1
    WHERE c1.incident_no          = irb.incident_no
    AND NVL(c1.incident_year,' ') = NVL(irb.year,' ')
    AND rtrim(c1.cache_id)        = rtrim(irb."CACHE ID")
    AND c1.item_id                = irb.item_id
    AND c1.unit_price             = irb.unit_price
    ) "QTY_UNSREFURB",
    --and length(irb.parent_item) = 1 ) "QTY_UNSREFURB",
    (
    SELECT SUM(c1.quantity_uns_nwt_refurb)
    FROM nwcg_incident_return c1
    WHERE c1.incident_no          = irb.incident_no
    AND NVL(c1.incident_year,' ') = NVL(irb.year,' ')
    AND rtrim(c1.cache_id)        = rtrim(irb."CACHE ID")
    AND c1.item_id                = irb.item_id
    AND c1.unit_price             = irb.unit_price
    ) "QTY_UNSNWT_REFURB",
    --and length(irb.parent_item) = 1 ) "QTY_UNSNWT_REFURB",
    (
    SELECT SUM(c1.quantity)
    FROM yfs_receipt_line c1,
      yfs_receipt_header c2
    WHERE c1.receipt_header_key        = c2.receipt_header_key
    AND c2.extn_incident_no            = irb.incident_no
    AND NVL(c2.extn_incident_year,' ') = NVL(irb.year,' ')
    AND rtrim(c2.receivingnode_key)    = rtrim(irb."CACHE ID")
    AND c1.item_id                     = irb.item_id
    AND c1.extn_receiving_price        = irb.unit_price
      --and c1.extn_received_as_component = 'N'
    AND c1.disposition_code = 'UNSRV-NWT'
    ) "QTY_UNSER_NWT",
    --and length(irb.parent_item) = 1 ) "QTY_UNSER_NWT",
    (
    SELECT MAX(extn_cache_ship_acct_code)
    FROM yfs_order_header oh,
      yfs_order_line ol
    WHERE oh.extn_incident_no          = irb.incident_no
    AND NVL(oh.extn_incident_year,' ') = NVL(irb.year,' ')
    AND oh.order_header_key            = ol.order_header_key
    AND oh.document_type               = '0001'
    AND rtrim(oh.ship_node)            = rtrim(irb."CACHE ID")
    AND ol.item_id                     = irb.item_id
    ) "EXTN_CACHE_SHIP_ACCT_CODE",
    (SELECT MAX(extn_sa_override_code)
    FROM yfs_order_header oh,
      yfs_order_line ol
    WHERE oh.extn_incident_no          = irb.incident_no
    AND NVL(oh.extn_incident_year,' ') = NVL(irb.year,' ')
    AND oh.order_header_key            = ol.order_header_key
    AND oh.document_type               = '0001'
    AND rtrim(oh.ship_node)            = rtrim(irb."CACHE ID")
    AND ol.item_id                     = irb.item_id
    ) "SA_OVERRIDE_CODE",
    m.incident_no "INCIDENT NO",
    m.incident_name "EXTN_INCIDENT_NAME",
    m.incident_other_acct_code "OTHER ACCT CODE",
    m.incident_blm_acct_code "BLM ACCT CODE",
    m.incident_fs_acct_code "FS ACCT CODE",
    m.override_code "EXTN_OVERRIDE_CODE",
    m.year,
    irb."CACHE ID"
  FROM extn_nwcg_incident_rpt_base_vw irb,
    nwcg_incident_order m,
    yfs_item yi
  WHERE ( (irb.incident_no = m.incident_no
  AND irb.year             = m.year)
  OR (irb.incident_no      = m.replaced_incident_no
  AND irb.year             = m.replaced_incident_year)
  OR (irb.incident_no      = m.replaced_incident_no_2
  AND irb.year             = m.replaced_incident_year_2) )
  AND irb.item_id          = yi.item_id;