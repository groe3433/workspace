CREATE OR REPLACE FORCE VIEW EXTN_NWCG_OWR_RPT2_VW ("ORDER_HEADER_KEY", "ITEM_ID", "UNIT_COST", "UOM", "ORDERED_QTY", "TOTALPRICE")
                                AS
  SELECT ol.order_header_key    AS order_header_key,
    ol.item_id                  AS item_id,
    ol.unit_cost                AS unit_cost,
    ol.uom                      AS uom,
    ol.ordered_qty              AS ordered_qty,
    ol.unit_cost*ol.ordered_qty AS totalPrice
  FROM yfs_order_header oh,
    yfs_order_line ol
  WHERE oh.order_header_key = ol.order_header_key
  AND oh.extn_refurb_wo    IS NOT NULL;