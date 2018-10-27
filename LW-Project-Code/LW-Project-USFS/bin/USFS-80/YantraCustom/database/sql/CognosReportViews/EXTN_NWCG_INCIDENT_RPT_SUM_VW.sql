CREATE OR REPLACE FORCE VIEW EXTN_NWCG_INCIDENT_RPT_SUM_VW ("CACHE_ID", "INCIDENT_NO", "YEAR", "ITEM_ID", "UNIT_PRICE")
AS
  SELECT DISTINCT a.ship_node "CACHE_ID",
    a.extn_incident_no "INCIDENT_NO",
    a.extn_incident_year "YEAR",
    b.item_id,
    to_number(b.unit_price) "UNIT_PRICE"
  FROM yfs_order_header a,
    yfs_order_line b
  WHERE a.order_header_key = b.order_header_key
  AND a.document_type     IN ('0001','0007.ex','0008.ex','0006')
  UNION
  SELECT DISTINCT a.ship_node "CACHE_ID",
    a.extn_to_incident_no "INCIDENT_NO",
    a.extn_to_incident_year "YEAR",
    b.item_id,
    to_number(b.unit_price) "UNIT_PRICE"
  FROM yfs_order_header a,
    yfs_order_line b
  WHERE a.order_header_key = b.order_header_key
  AND a.document_type     IN ('0008.ex')
  /*
  UNION
  SELECT DISTINCT a.ship_node "CACHE_ID",
  a.extn_to_incident_no "INCIDENT_NO",
  a.extn_to_incident_year "YEAR",
  b.item_id,
  to_number(b.unit_price) "UNIT_PRICE"
  FROM yfs_order_header a,
  yfs_order_line b
  WHERE a.order_header_key = b.order_header_key
  AND a.document_type     IN ('0008.ex')
  */
  UNION
  SELECT DISTINCT a.receivingnode_key "CACHE_ID",
    a.extn_incident_no "INCIDENT_NO",
    a.extn_incident_year "YEAR",
    b.item_id,
    to_number(nir.unit_price) "UNIT_PRICE"
  FROM yfs_receipt_header a,
    yfs_receipt_line b,
    nwcg_incident_return nir
  WHERE a.receipt_header_key = b.receipt_header_key
  AND b.item_id              = nir.item_id
  AND a.extn_incident_no     = nir.incident_no
  AND a.extn_incident_year   = nir.incident_year
  UNION
  SELECT DISTINCT a.receivingnode_key "CACHE_ID",
    a.extn_incident_no "INCIDENT_NO",
    a.extn_incident_year "YEAR",
    b.item_id,
    to_number(nir.unit_price) "UNIT_PRICE"
  FROM yfs_receipt_header a,
    nwcg_lpn_return b,
    nwcg_incident_return nir
    --, -- Added by Vishy
    --  nwcg_incident_order m
  WHERE a.receipt_header_key = b.return_header_key
  AND trim(b.item_id)        = trim(nir.item_id)
  AND a.extn_incident_no     = nir.incident_no
  AND a.extn_incident_year   = nir.incident_year;