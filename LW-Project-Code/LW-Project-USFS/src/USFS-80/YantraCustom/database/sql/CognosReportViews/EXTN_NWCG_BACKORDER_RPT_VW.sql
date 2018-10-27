CREATE OR REPLACE FORCE VIEW EXTN_NWCG_BACKORDER_RPT_VW ("CACHE ID", "INCIDENT NO", "YEAR", "EXTN_INCIDENT_NAME", "ORDER_HEADER_KEY", "ORDER NO", "EXTN_REQUEST_NO", "PRIME_LINE_NO", "ITEM ID", "ITEM_SHORT_DESCRIPTION", "EXTN_BACKORDERED_QTY", "UOM", "CUSTOMER ID", "SHIPPING_UNIT_ID", "CUSTOMER_NAME", "QTY_RFI", "BACKORDER_TIME")
AS
  SELECT yoh.ship_node "CACHE ID",
    yoh.extn_incident_no "INCIDENT NO",
    nio.year,
    yoh.extn_incident_name,
    yoh.order_header_key,
    yoh.order_no "ORDER NO",
    yol.extn_request_no,
    yol.prime_line_no,
    yol.item_id "ITEM ID",
    yol.item_short_description,
    yol.extn_backordered_qty,
    yol.uom,
    yoh.bill_to_id "CUSTOMER ID",
    NVL(yoh.ship_to_id,'****') "SHIPPING_UNIT_ID",
    (SELECT extn_customer_name
    FROM yfs_customer
    WHERE trim(customer_id) = trim(yoh.bill_to_id)
    ) "CUSTOMER_NAME",
    (SELECT (SUM(yli.quantity + yli.pend_in_qty) - SUM(yli.hard_alloc_qty + yli.soft_alloc_qty))
    FROM yfs_location_inventory yli,
      yfs_inventory_item yii
    WHERE yli.inventory_item_key = yii.inventory_item_key
    AND yii.item_id              = yol.item_id
    AND yli.inventory_status     = 'RFI'
    AND rtrim(yli.node_key)      = rtrim(yoh.ship_node)
    AND yli.location_id NOT     IN ('CYCLE_COUNT_BIN', 'PHYSICAL_COUNT_BIN', 'PACK_BIN')
    ) "QTY_RFI",
    yol.createts "BACKORDER_TIME"
  FROM yfs_order_header yoh,
    nwcg_incident_order nio,
    yfs_order_line yol
  WHERE yoh.extn_incident_no   = nio.incident_no
  AND yoh.extn_incident_year   = nio.year
  AND yoh.order_header_key     = yol.order_header_key
  AND yol.extn_backordered_qty > 0
  AND yol.extn_back_order_flag ='Y';