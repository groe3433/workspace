CREATE OR REPLACE FORCE VIEW EXTN_NWCG_ITEM_PURCHASE_RPT_VW ("EXTN_PO_NO", "ORDER_NO", "DOCUMENT_TYPE", "ENTERPRISE_KEY", "CACHE ID", "ITEM ID", "ITEM DESCRIPTION", "UOM", "ORDERED_QTY", "EXTN_ORIG_REQ_QTY", "PURCHASE_DATE", "RECD_QTY", "ORDER_STATUS", "RECEIPT_DATE", "RECEIPT_PRICE")
AS
  SELECT a.extn_po_no,
    a.order_no,
    a.document_type,
    a.enterprise_key,
    b.receiving_node "CACHE ID",
    b.item_id "ITEM ID",
    b.item_description "ITEM DESCRIPTION",
    b.uom,
    b.ordered_qty,
    b.extn_orig_req_qty,
    to_date(TO_CHAR(b.createts,'DD-MON-YYYY')) "PURCHASE_DATE",
    b.received_quantity "RECD_QTY",
    (SELECT MAX(status)
    FROM yfs_order_release_status
    WHERE order_header_key = a.order_header_key
    ) "ORDER_STATUS",
    (SELECT MAX (to_date(TO_CHAR(c.createts,'DD-MON-YYYY')))
    FROM yfs_receipt_line c
    WHERE c.order_header_key = b.order_header_key
    ) "RECEIPT_DATE",
    (SELECT NVL(MIN(c.extn_receiving_price),0)
    FROM yfs_receipt_line c
    WHERE c.order_header_key = b.order_header_key
    ) "RECEIPT_PRICE"
  FROM yfs_order_header a,
    yfs_order_line b
  WHERE a.order_header_key = b.order_header_key;