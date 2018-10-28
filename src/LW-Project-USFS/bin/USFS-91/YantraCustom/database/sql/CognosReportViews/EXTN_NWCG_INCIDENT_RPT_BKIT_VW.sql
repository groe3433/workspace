CREATE OR REPLACE FORCE VIEW EXTN_NWCG_INCIDENT_RPT_BKIT_VW ("CACHE_ID", "INCIDENT_NO", "INCIDENT_YEAR", "KIT ID", "COMPONENT ITEM", "KIT_QTY", "UNIT_COST")
AS
  SELECT DISTINCT m1.cache_id,
    m1.incident_no,
    m1.incident_year,
    km."KIT ID",
    km."COMPONENT ITEM",
    km.KIT_QUANTITY "KIT_QTY",
    kyi.unit_cost
  FROM
    (SELECT DISTINCT nir.cache_id,
      nir.incident_no,
      nir.incident_year,
      nir.item_id
    FROM nwcg_incident_return nir
    ) m1,
    extn_nwcg_kit_contents_vw km,
    yfs_item kyi
  WHERE m1.item_id             = km."KIT ID"
  AND km."COMPONENT ITEM"      = kyi.item_id
  AND km."COMPONENT ITEM" NOT IN
    (SELECT DISTINCT b.item_id
    FROM yfs_order_header a,
      yfs_order_line b
    WHERE a.order_header_key = b.order_header_key
    AND a.document_type     IN ('0001','0007.ex','0008.ex')
    )
  UNION
  SELECT DISTINCT m1.cache_id,
    m1.incident_no,
    m1.incident_year,
    km."KIT ID",
    km."COMPONENT ITEM",
    km.KIT_QUANTITY "KIT_QTY",
    kyi.unit_cost
  FROM
    (SELECT DISTINCT nir.cache_id,
      nir.incident_no,
      nir.incident_year,
      nir.item_id
    FROM nwcg_incident_return nir
    ) m1,
    extn_nwcg_kit_contents_vw km,
    yfs_item kyi
  WHERE m1.item_id             = km."KIT ID"
  AND km."COMPONENT ITEM"      = kyi.item_id
  AND km."COMPONENT ITEM" NOT IN
    (SELECT DISTINCT b.item_id
    FROM yfs_receipt_header a,
      yfs_receipt_line b,
      nwcg_incident_return nir
    WHERE a.receipt_header_key = b.receipt_header_key
    AND b.item_id              = nir.item_id
    AND a.extn_incident_no     = nir.incident_no
    AND a.extn_incident_year   = nir.incident_year
    );