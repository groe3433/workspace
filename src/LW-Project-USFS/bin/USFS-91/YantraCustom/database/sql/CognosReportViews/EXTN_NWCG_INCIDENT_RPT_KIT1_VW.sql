CREATE OR REPLACE FORCE VIEW EXTN_NWCG_INCIDENT_RPT_KIT1_VW ("CACHE ID", "INCIDENT_NO", "YEAR", "PARENT_ITEM", "COMP_ITEM", "COMP_KIT_QTY")
AS
  SELECT DISTINCT m1."CACHE ID",
    m1.incident_no,
    m1.year,
    km."KIT ID" "PARENT_ITEM",
    km."COMPONENT ITEM" "COMP_ITEM",
    km.KIT_QUANTITY "COMP_KIT_QTY"
  FROM
    (SELECT DISTINCT a.ship_node "CACHE ID",
      nio.incident_no,
      nio.year,
      b.item_id,
      b.item_description
    FROM yfs_order_header a,
      yfs_order_line b,
      nwcg_incident_order nio,
      yfs_item yi
    WHERE a.order_header_key = b.order_header_key
    AND b.item_id            = yi.item_id
    AND a.extn_incident_no   = nio.incident_no
    AND a.extn_incident_year = nio.year
    AND a.document_type     IN ('0001','0007.ex','0008.ex')
    UNION
    SELECT DISTINCT a.ship_node "CACHE ID",
      nio.incident_no,
      nio.year,
      b.item_id,
      b.item_description
    FROM yfs_order_header a,
      yfs_order_line b,
      nwcg_incident_order nio,
      yfs_item yi
    WHERE a.order_header_key    = b.order_header_key
    AND b.item_id               = yi.item_id
    AND a.extn_to_incident_no   = nio.incident_no
    AND a.extn_to_incident_year = nio.year
    AND a.document_type        IN ('0001','0007.ex','0008.ex')
    ) m1,
    extn_nwcg_kit_contents_vw km
  WHERE m1.item_id = km."KIT ID";