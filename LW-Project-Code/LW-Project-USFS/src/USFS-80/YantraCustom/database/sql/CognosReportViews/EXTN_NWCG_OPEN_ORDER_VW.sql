CREATE OR REPLACE FORCE VIEW EXTN_NWCG_OPEN_ORDER_VW ("CACHE ID", "TYPE", "ISSUE", "INCIDENT", "EXTN_INCIDENT_YEAR", "EXTN_INCIDENT_NAME", "BILL_TO_ID", "ORDER_DATE", "TOTAL_AMOUNT", "STATUS")
AS
  SELECT TRIM (oh.ship_node) "CACHE ID",
    DECODE (TRIM (oh.document_type), '0001', 'Incident Issue', '0006', 'Cache-Cache Transfer', '0007.ex', 'Other Issue' ) TYPE,
    TRIM (oh.order_no) issue,
    oh.extn_incident_no incident,
    oh.extn_incident_year,
    oh.extn_incident_name,
    oh.bill_to_id,
    oh.order_date,
    oh.total_amount,
    (
    CASE
      WHEN MAX (ors.status) != MIN (ors.status)
      THEN 'Partially '
    END)
    || SUBSTR (MAX (SUBSTR (ors.status, 1, 4)
    || s.description), 5) status
  FROM yfs_order_header oh,
    yfs_order_line ol,
    yfs_order_release_status ors,
    yfs_status s
  WHERE oh.order_header_key = ol.order_header_key
  AND ol.order_line_key     = ors.order_line_key
  AND ors.status_quantity   > 0
  AND ors.status            = s.status
  AND s.process_type_key    = 'ORDER_FULFILLMENT'
  AND ors.status NOT LIKE '9000%'
  AND ors.status != '1100.0009'
  AND ors.status != '1400'
    ---Added by Vishy for CR 1118 Fix
  AND ol.shipped_quantity                                            - ol.ordered_qty < '0'
  AND SUBSTR(oh.order_headeR_key,0,4)      > TO_CHAR(sysdate,'YYYY') -2
    -- and ABS(ol.ordered_qty - ol.shipped_quantity) <> '0'
  AND oh.document_type IN ('0001', '0006', '0007.ex')
  GROUP BY oh.ship_node,
    oh.order_no,
    oh.extn_incident_no,
    oh.extn_incident_year,
    oh.extn_incident_name,
    oh.bill_to_id,
    oh.order_date,
    oh.total_amount,
    oh.document_type
  HAVING MIN (SUBSTR (ors.status, 1, 4)) < '3700';