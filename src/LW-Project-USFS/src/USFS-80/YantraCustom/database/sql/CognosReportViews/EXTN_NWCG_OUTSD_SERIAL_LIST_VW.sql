CREATE OR REPLACE FORCE VIEW EXTN_NWCG_OUTSD_SERIAL_LIST_VW ("ITEM_ID", "TRACKABLE_ID", "INCIDENT_NO", "INCIDENT_YEAR", "OVER_RECEIPT", "CACHE_ID", "UNIT_PRICE")
AS
  SELECT a.item_id,
    a.trackable_id,
    a.incident_no,
    a.incident_year,
    a.over_receipt,
    a.cache_id,
    a.unit_price
  FROM nwcg_incident_return a,
    yfs_item b
  WHERE a.quantity_shipped - a.quantity_returned > 0
  AND a.over_receipt                             = 'N'
  AND a.item_id                                  = b.item_id
  AND a.received_as_component                    = 'N'
  AND b.is_serial_tracked                        = 'Y';