CREATE OR REPLACE FORCE VIEW EXTN_NWCG_RECEIPT_DTL_RPT_VW ("DOCUMENT TYPE", "RECEIPT DATE", "RECEIPT_NO", "SHIPMENT_KEY", "ORDER NO", "ITEM_ID", "DESCRIPTION", "UOM", "PRODUCT_CLASS", "QUANTITY", "EXTN_RECEIVING_PRICE", "SERIAL_NO", "SHIPMENT NO", "CARRIER", "BUYER", "SELLER", "CACHE ID", "SUPPLIER_UNIT_COST", "UNIT_COST")
AS
  SELECT a.document_type "DOCUMENT TYPE",
    a.receipt_date "RECEIPT DATE",
    a.receipt_no,
    a.shipment_key,
    b.order_no "ORDER NO",
    b.item_id,
    d.description,
    b.uom,
    b.product_class,
    b.quantity,
    b.extn_receiving_price,
    b.serial_no,
    c.shipment_no "SHIPMENT NO",
    c.scac "CARRIER",
    e.buyer_organization_code "BUYER",
    e.seller_organization_code "SELLER",
    a.receivingnode_key "CACHE ID",
    (SELECT si.unit_cost
    FROM nwcg_supplier_item si
    WHERE rtrim(si.item_id)   = rtrim(b.item_id)
    AND rtrim(si.supplier_id) = rtrim(e.seller_organization_code)
    ) "SUPPLIER_UNIT_COST",
    (SELECT yips.list_price
    FROM yfs_item_price_set yips,
      yfs_price_set yps
    WHERE yips.item_id       = b.item_id
    AND yips.price_set_key   = yps.price_set_key
    AND yps.active_flag      = 'Y'
    AND yps.valid_till_date >= a.receipt_date
    ) "UNIT_COST"
  FROM yfs_receipt_header a,
    yfs_receipt_line b,
    yfs_shipment c,
    yfs_item d,
    yfs_order_header e
  WHERE a.receipt_header_key = b.receipt_header_key
  AND a.shipment_key         = c.shipment_key
  AND b.item_id              = d.item_id
  AND b.order_no             = e.order_no;