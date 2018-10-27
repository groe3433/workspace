CREATE OR REPLACE FORCE VIEW EXTN_NWCG_RETURN_WSHEET_RPT_VW ("ITEM_ID", "ITEM_DESCRIPTION", "ITEM_TYPE", "UOM", "TRACKABLE_ID", "QTY_AS_ITEM", "QTY_RET", "QTY_RFI", "QTY_NRFI", "QTY_UNS", "QTY_UNS_NWT", "INCIDENT NO", "INCIDENT NAME", "OTHER ACCT CODE", "BLM ACCT CODE", "FS ACCT CODE", "OVERRIDE_CODE", "UNIT_PRICE", "YEAR", "CACHE ID")
AS
  SELECT trim(a.item_id) "ITEM_ID",
    yi.short_description "ITEM_DESCRIPTION",
    yi.item_type,
    yi.uom,
    (SELECT serial_no FROM nwcg_trackable_item WHERE item_id = a.item_id
    ) "TRACKABLE_ID",
    SUM (quantity_shipped) "QTY_AS_ITEM",
    (SELECT (
      (SELECT NVL(SUM(c1.quantity),0)
      FROM yfs_receipt_line c1,
        yfs_receipt_header c2
      WHERE c1.receipt_header_key       = c2.receipt_header_key
      AND c2.extn_incident_no           =b.incident_no
      AND c2.extn_incident_year         = a.incident_year
      AND rtrim(c2.receivingnode_key)   = trim(cache_id)
      AND c1.item_id                    = a.item_id
      AND c2.document_type              = '0010'
      AND c1.extn_receiving_price       = a.unit_price
      AND c1.extn_received_as_component = 'N'
      ) +
      (SELECT NVL(SUM(lpn.quantity),0)
      FROM nwcg_lpn_return lpn,
        yfs_receipt_header lpn2
      WHERE lpn.return_header_key       = lpn2.receipt_header_key
      AND lpn2.extn_incident_no         =b.incident_no
      AND lpn2.extn_incident_year       = a.incident_year
      AND rtrim(lpn2.receivingnode_key) = trim(cache_id)
      AND trim(lpn.item_id)             =a.item_id
      AND lpn.unit_price                = a.unit_price
      )) a
    FROM dual
    ) "QTY_RET",
    (SELECT (
      (SELECT NVL(SUM(c1.quantity),0)
      FROM yfs_receipt_line c1,
        yfs_receipt_header c2
      WHERE c1.receipt_header_key       = c2.receipt_header_key
      AND c2.extn_incident_no           =b.incident_no
      AND c2.extn_incident_year         = a.incident_year
      AND rtrim(c2.receivingnode_key)   = trim(cache_id)
      AND c1.item_id                    = a.item_id
      AND c2.document_type              = '0010'
      AND C1.DISPOSITION_CODE           ='RFI'
      AND c1.extn_receiving_price       = a.unit_price
      AND c1.extn_received_as_component = 'N'
      ) +
      (SELECT NVL(SUM(lpn.quantity),0)
      FROM nwcg_lpn_return lpn,
        yfs_receipt_header lpn2
      WHERE lpn.return_header_key       = lpn2.receipt_header_key
      AND lpn2.extn_incident_no         =b.incident_no
      AND lpn2.extn_incident_year       = a.incident_year
      AND rtrim(lpn2.receivingnode_key) = trim(cache_id)
      AND trim(lpn.item_id)             =a.item_id
      AND lpn.disposition_code          ='RFI'
      AND lpn.unit_price                = a.unit_price
      )) a
    FROM dual
    ) "QTY_RFI",
    (SELECT (
      (SELECT NVL(SUM(c1.quantity),0)
      FROM yfs_receipt_line c1,
        yfs_receipt_header c2
      WHERE c1.receipt_header_key       = c2.receipt_header_key
      AND c2.extn_incident_no           =b.incident_no
      AND c2.extn_incident_year         = a.incident_year
      AND rtrim(c2.receivingnode_key)   = trim(cache_id)
      AND c1.item_id                    = a.item_id
      AND c2.document_type              = '0010'
      AND C1.DISPOSITION_CODE           ='NRFI'
      AND c1.extn_receiving_price       = a.unit_price
      AND c1.extn_received_as_component = 'N'
      ) +
      (SELECT NVL(SUM(lpn.quantity),0)
      FROM nwcg_lpn_return lpn,
        yfs_receipt_header lpn2
      WHERE lpn.return_header_key       = lpn2.receipt_header_key
      AND lpn2.extn_incident_no         =b.incident_no
      AND lpn2.extn_incident_year       = a.incident_year
      AND rtrim(lpn2.receivingnode_key) = trim(cache_id)
      AND trim(lpn.item_id)             =a.item_id
      AND lpn.disposition_code          ='NRFI'
      AND lpn.unit_price                = a.unit_price
      )) a
    FROM dual
    ) "QTY_NRFI",
    (SELECT (
      (SELECT NVL(SUM(c1.quantity),0)
      FROM yfs_receipt_line c1,
        yfs_receipt_header c2
      WHERE c1.receipt_header_key       = c2.receipt_header_key
      AND c2.extn_incident_no           =b.incident_no
      AND c2.extn_incident_year         = a.incident_year
      AND rtrim(c2.receivingnode_key)   = trim(cache_id)
      AND c1.item_id                    = a.item_id
      AND c2.document_type              = '0010'
      AND C1.DISPOSITION_CODE           ='UNSERVICE'
      AND c1.extn_receiving_price       = a.unit_price
      AND c1.extn_received_as_component = 'N'
      ) +
      (SELECT NVL(SUM(lpn.quantity),0)
      FROM nwcg_lpn_return lpn,
        yfs_receipt_header lpn2
      WHERE lpn.return_header_key       = lpn2.receipt_header_key
      AND lpn2.extn_incident_no         =b.incident_no
      AND lpn2.extn_incident_year       = a.incident_year
      AND rtrim(lpn2.receivingnode_key) = trim(cache_id)
      AND trim(lpn.item_id)             =a.item_id
      AND lpn.disposition_code          ='UNSERVICE'
      AND lpn.unit_price                = a.unit_price
      )) a
    FROM dual
    ) "QTY_UNS",
    (SELECT (
      (SELECT NVL(SUM(c1.quantity),0)
      FROM yfs_receipt_line c1,
        yfs_receipt_header c2
      WHERE c1.receipt_header_key       = c2.receipt_header_key
      AND c2.extn_incident_no           =b.incident_no
      AND c2.extn_incident_year         = a.incident_year
      AND rtrim(c2.receivingnode_key)   = trim(cache_id)
      AND c1.item_id                    = a.item_id
      AND c2.document_type              = '0010'
      AND C1.DISPOSITION_CODE           ='UNSRV-NWT'
      AND c1.extn_receiving_price       = a.unit_price
      AND c1.extn_received_as_component = 'N'
      ) +
      (SELECT NVL(SUM(lpn.quantity),0)
      FROM nwcg_lpn_return lpn,
        yfs_receipt_header lpn2
      WHERE lpn.return_header_key       = lpn2.receipt_header_key
      AND lpn2.extn_incident_no         =b.incident_no
      AND lpn2.extn_incident_year       = a.incident_year
      AND rtrim(lpn2.receivingnode_key) = trim(cache_id)
      AND trim(lpn.item_id)             =a.item_id
      AND lpn.disposition_code          ='UNSRV-NWT'
      AND lpn.unit_price                = a.unit_price
      )) a
    FROM dual
    ) "QTY_UNS_NWT",
    b.incident_no "INCIDENT NO",
    b.incident_name "INCIDENT NAME",
    b.incident_other_acct_code "OTHER ACCT CODE",
    b.incident_blm_acct_code "BLM ACCT CODE",
    b.incident_fs_acct_code "FS ACCT CODE",
    b.override_code,
    a.unit_price,
    b.year,
    a.cache_id "CACHE ID"
  FROM nwcg_incident_return a,
    nwcg_incident_order b,
    yfs_item yi
  WHERE ( (a.incident_no = b.incident_no
  AND a.incident_year    = b.year)
  OR (a.incident_no      = b.replaced_incident_no
  AND a.incident_year    = b.replaced_incident_year)
  OR (a.incident_no      = b.replaced_incident_no_2
  AND a.incident_year    = b.replaced_incident_year_2))
  AND a.item_id          = yi.item_id
  GROUP BY a.item_id,
    yi.short_description,
    yi.item_type,
    yi.uom,
    a.unit_price,
    b.incident_no,
    b.incident_name,
    b.incident_other_acct_code,
    b.incident_blm_acct_code,
    b.incident_fs_acct_code,
    b.override_code,
    a.unit_price,
    b.year,
    cache_id,
    a.incident_year;