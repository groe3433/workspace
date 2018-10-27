CREATE OR REPLACE FORCE VIEW EXTN_NWCG_EXT_AFRS_SHP_RPT_VW ("CACHE ID", "ACTUAL_SHIPMENT_DATE", "TOT_SHIPPED", "TOT_POUNDS", "TOT_CUBES", "TOT_SHIPPED_VALUE")
AS
  SELECT x.shipnode_key "CACHE ID",
    to_date(TO_CHAR(x.actual_shipment_date,'DD-MON-YYYY')) "ACTUAL_SHIPMENT_DATE",
    SUM(x.ship_count) "TOT_SHIPPED",
    SUM(x.pounds) "TOT_POUNDS",
    SUM(x.cubes) "TOT_CUBES",
    SUM(x.shipped_value) "TOT_SHIPPED_VALUE"
  FROM
    (SELECT a.shipnode_key,
      a.actual_shipment_date,
      COUNT(b.shipment_line_key) "SHIP_COUNT",
      SUM(b.quantity) "SHIPPED",
      SUM(b.quantity * c.unit_weight) "POUNDS",
      SUM(b.quantity *c.unit_height*unit_length*unit_width) "CUBES",
      SUM(b.quantity *
      (SELECT a1.list_price
      FROM yfs_item_price_set a1,
        yfs_price_set b1
      WHERE a1.item_id     = b.item_id
      AND a1.price_set_key = b1.price_set_key
      AND b1.active_flag   = 'Y'
      )) "SHIPPED_VALUE",
      b.item_id
    FROM yfs_shipment a,
      yfs_shipment_line b,
      yfs_item c
    WHERE a.document_type IN ('0001','0007.ex')
    AND a.shipment_key     = b.shipment_key
    AND b.item_id          = c.item_id
    GROUP BY a.shipnode_key,
      a.actual_shipment_date,
      b.item_id
    ) x
  GROUP BY x.shipnode_key,
    x.actual_shipment_date;