CREATE OR REPLACE FORCE VIEW EXTN_NWCG_OWR_RPT1_VW ("WORK ORDER NO", "CACHE ID", "ORDER NO", "ORDER_HEADER_KEY", "INCIDENT_NO", "FS_ACCT_CODE", "BLM_ACCT_CODE", "OTHER_ACCT_CODE", "OVERRIDE_CODE", "SHIP_ACCT_CODE", "EXTN_SA_OVERRIDE_CODE", "EXTN_RA_OVERRIDE_CODE", "INCIDENT_NAME", "CUSTOMER_ID", "YEAR", "CREATETS", "ITEM_ID", "UOM", "SHIP_BY_DATE", "WORDERED", "REPAIRED", "OUTSD", "DESCRIPTION", "ACTIVITY_LOCATION_ID", "ZONE_ID", "TOTAL_ADJUSTMENT_COST")
                                   AS
  SELECT DISTINCT wo.work_order_no AS "WORK ORDER NO",
    wo.node_key                    AS "CACHE ID",
    oh.order_no                    AS "ORDER NO",
    oh.order_header_key            AS order_header_key,
    wo.extn_incident_no            AS incident_no,
    wo.extn_fs_acct_code           AS fs_acct_code,
    wo.extn_blm_acct_code          AS blm_acct_code,
    wo.extn_other_acct_code        AS other_acct_code,
    oh.extn_override_code          AS override_code,
    oh.extn_cache_ship_acct_code   AS ship_acct_code,
    oh.extn_sa_override_code,
    oh.extn_ra_override_code,
    nio.incident_name                               AS incident_name,
    nio.customer_id                                 AS customer_id,
    nio.year                                        AS YEAR,
    wo.createts                                     AS createts,
    wo.item_id                                      AS item_id,
    wo.uom                                          AS uom,
    wo.ship_by_date                                 AS ship_by_date,
    wo.quantity_allocated                           AS Wordered,
    wo.quantity_completed                           AS repaired,
    (wo.quantity_allocated - wo.quantity_completed) AS outsd,
    yi.short_description                            AS description,
    woa.activity_location_id                        AS activity_location_id,
    yl.zone_id                                      AS zone_id,
    oh.total_adjustment_amount                      AS total_adjustment_cost
  FROM yfs_work_order wo,
    yfs_order_header oh,
    nwcg_incident_order nio,
    yfs_item yi,
    yfs_work_order_acty_dtl woa,
    yfs_location yl
  WHERE trim(wo.work_order_no) = oh.extn_refurb_wo(+)
  AND wo.status               != '1600'
  AND wo.extn_incident_no      = nio.incident_no
  AND wo.extn_incident_year    = nio.year
  AND wo.item_id               = trim(yi.item_id)
  AND wo.work_order_key        = woa.work_order_key
  AND woa.activity_location_id = yl.location_id
  ORDER BY wo.work_order_no;