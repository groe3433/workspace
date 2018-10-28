CREATE OR REPLACE FORCE VIEW EXTN_NWCG_INCIDENT_RPT_BSRL_VW ("CACHE ID", "ITEM_ID", "SERIAL_NO", "PARENT_ID", "PARENT_SERIAL", "EXTN_INCIDENT_NO", "EXTN_INCIDENT_YEAR", "ISSUED_XFER")
AS
  SELECT a.cache_id "CACHE ID",
    a.item_id "ITEM_ID",
    a.serial_no,
    a.parent_item_id_1 "PARENT_ID",
    a.parent_serial_no_1 "PARENT_SERIAL",
    a.incident_no "EXTN_INCIDENT_NO",
    a.incident_year "EXTN_INCIDENT_YEAR",
    a.issued_xfer
  FROM nwcg_issue_trackable_list a
  GROUP BY a.cache_id,
    a.item_id,
    a.serial_no,
    a.parent_item_id_1,
    a.parent_serial_no_1,
    a.incident_no,
    a.incident_year,
    a.issued_xfer;