CREATE OR REPLACE FORCE VIEW EXTN_NWCG_ACCT_SUMMARY_VW ("INCIDENT_NO", "YEAR", "CACHE_ID", "ITEM_ID", "TRACKABLE_ID", "UOM", "SHORT_DESCRIPTION", "QTY_ISSUED", "QTY_RETURNED", "QTY_WORKORDERED")
AS
  SELECT DISTINCT INCIDENT_NO,
    YEAR,
    CACHE_ID,
    ITEM_ID,
    TRACKABLE_ID,
    UOM,
    SHORT_DESCRIPTION,
    QTY_ISSUED,
    QTY_RETURNED,
    QTY_WORKORDERED
  FROM
    (SELECT incidentReturn.incident_no "INCIDENT_NO",
      incidentReturn.incident_year "YEAR",
      incidentReturn.cache_id "CACHE_ID",
      incidentReturn.item_id "ITEM_ID",
      (SELECT DISTINCT incidentReturn.trackable_id
      FROM nwcg_incident_return IR
      WHERE IR.cache_id              = incidentReturn.cache_id
      AND incidentReturn.trackable_id= IR.trackable_id
      AND incidentReturn.item_id     = IR.item_id
      AND ( (IR.incident_no          = incident.incident_no
      AND IR.incident_year           = incident.year)
      OR (IR.incident_no             = incident.replaced_incident_no
      AND IR.incident_year           = incident.replaced_incident_year)
      OR ( IR.incident_no            = incident.replaced_incident_no_2
      AND IR.incident_year           = incident.replaced_incident_year_2) )
      ) "TRACKABLE_ID" ,
      (SELECT DISTINCT item.uom
      FROM NWCG_INCIDENT_RETURN IR,
        yfs_item item
      WHERE item.item_id=incidentreturn.item_id
      ) "UOM",
      (SELECT DISTINCT item.short_description
      FROM NWCG_INCIDENT_RETURN IR,
        yfs_item item
      WHERE item.item_id=incidentreturn.item_id
      ) "SHORT_DESCRIPTION",
      (SELECT SUM(Qty_Issued.Quantity_Shipped)
      FROM nwcg_incident_return Qty_Issued
      WHERE Qty_Issued.cache_id      = incidentReturn.cache_id
      AND incidentReturn.trackable_id= Qty_Issued.trackable_id
      AND incidentReturn.item_id     =Qty_Issued. item_id
      AND ( (Qty_Issued.incident_no  = incident.incident_no
      AND Qty_Issued.incident_year   = incident.year)
      OR (Qty_Issued.incident_no     = incident.replaced_incident_no
      AND Qty_Issued.incident_year   = incident.replaced_incident_year)
      OR ( Qty_Issued.incident_no    = incident.replaced_incident_no_2
      AND Qty_Issued.incident_year   = incident.replaced_incident_year_2) )
      )"QTY_ISSUED",
      (SELECT NVL(SUM(incidentReturn.quantity_uns_nwt_return + incidentReturn.quantity_rfi + incidentReturn.quantity_rfi_refurb + incidentReturn.quantity_uns_nwt_refurb),0)
      FROM yfs_receipt_line rl,
        yfs_receipt_header rh
      WHERE rh.receipt_header_key           = rl.receipt_header_key
      AND rtrim(rh.receivingnode_key)       = rtrim(incidentReturn.cache_id)
      AND rh.document_type                  = '0010'
      AND incidentReturn.incident_no        = rh.extn_incident_no
      AND incidentReturn.item_id            = rl.item_id
      AND trim(incidentReturn.trackable_id) = trim(rl.serial_no)
      )+
      -- To cover incident transfer returns - modified as PI 1050 on 09/30/2013
      (
      SELECT NVL(SUM(incidentReturn.quantity_uns_nwt_return + incidentReturn.quantity_rfi + incidentReturn.quantity_rfi_refurb + incidentReturn.quantity_uns_nwt_refurb),0)
      FROM nwcg_serial_record serial_record
      WHERE serial_record.document_type      = '0008.ex'
      AND rtrim(serial_record.order_type)    = 'ICBSR_INIT_TRANSFER'
      AND rtrim(serial_record.incident_no)   = rtrim(incidentReturn.incident_no)
      AND rtrim(serial_record.incident_year) = rtrim(incidentReturn.incident_year)
      AND rtrim(serial_record.node)          = rtrim(incidentReturn.cache_id)
      AND rtrim(serial_record.item_id)       = rtrim(incidentReturn.item_id)
      AND rtrim(serial_record.serial_no)     = rtrim(incidentReturn.Trackable_id)
      ) "QTY_RETURNED",
      (SELECT NVL(SUM(NVL(Qty_Issued.quantity_nrfi,0)) , 0)
      FROM nwcg_incident_return Qty_Issued
      WHERE qty_issued.quantity_rfi_refurb   IN ('0')
      AND qty_issued.quantity_uns_nwt_refurb IN ('0')
      AND qty_issued.quantity_uns_refurb     IN ('0')
      AND Qty_Issued.cache_id                 = incidentReturn.cache_id
      AND incidentReturn.trackable_id         = Qty_Issued.trackable_id
      AND incidentReturn.item_id              = Qty_Issued.item_id
      AND ( (Qty_Issued.incident_no           = incident.incident_no
      AND Qty_Issued.incident_year            = incident.year)
      OR (Qty_Issued.incident_no              = incident.replaced_incident_no
      AND Qty_Issued.incident_year            = incident.replaced_incident_year)
      OR ( Qty_Issued.incident_no             = incident.replaced_incident_no_2
      AND Qty_Issued.incident_year            = incident.replaced_incident_year_2) )
      )"QTY_WORKORDERED"
    FROM nwcg_incident_return incidentReturn,
      nwcg_incident_order incident
    WHERE ( (incidentReturn.incident_no = incident.incident_no
    AND incidentReturn.incident_year    = incident.year)
    OR (incidentReturn.incident_no      = incident.replaced_incident_no
    AND incidentReturn.incident_year    = incident.replaced_incident_year)
    OR ( incidentReturn.incident_no     = incident.replaced_incident_no_2
    AND incidentReturn.incident_year    = incident.replaced_incident_year_2) )
    AND incidentReturn.trackable_id    <> '  '
      -- and incidentReturn.issue_no is not null
    )
  ORDER BY ITEM_ID ASC,
    TRACKABLE_ID ASC;