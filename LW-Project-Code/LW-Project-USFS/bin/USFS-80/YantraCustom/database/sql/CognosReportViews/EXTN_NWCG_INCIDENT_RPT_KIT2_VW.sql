CREATE OR REPLACE FORCE VIEW EXTN_NWCG_INCIDENT_RPT_KIT2_VW ("CACHE ID", "INCIDENT_NO", "YEAR", "SUPER_PARENT", "PARENT_ITEM", "PARENT_KIT_QTY", "COMP_ITEM", "COMP_KIT_QTY")
AS
  SELECT DISTINCT m1."CACHE ID",
    incident_no,
    YEAR,
    m1.PARENT_ITEM "SUPER_PARENT",
    m1."COMP_ITEM" "PARENT_ITEM",
    m1.COMP_KIT_QTY "PARENT_KIT_QTY",
    km."COMPONENT ITEM" "COMP_ITEM",
    km.KIT_QUANTITY "COMP_KIT_QTY"
  FROM extn_nwcg_incident_rpt_kit1_vw m1,
    extn_nwcg_kit_matrix_vw km
  WHERE m1."COMP_ITEM" = km."KIT ID"
  AND m1."CACHE ID"    = km."CACHE ID"
  ORDER BY m1.PARENT_ITEM;