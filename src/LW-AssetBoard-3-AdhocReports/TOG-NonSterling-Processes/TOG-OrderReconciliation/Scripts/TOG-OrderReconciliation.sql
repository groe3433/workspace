SELECT
  'Orders Received for Reconcile : '
  || trim(COUNT(*))
  || 'newLine'  
FROM
  OMCTMETA.extn_order_recon;
SELECT
  'Orders Reconciled : '
  || trim(COUNT(pp_order_no))
  || 'newLine' 
FROM
  OMCTMETA.extn_order_recon
WHERE
  pp_order_no IN
  (
    SELECT
      order_no
    FROM
      OMCTMETA.yfs_order_header
    WHERE
      order_no IN
      (
        SELECT
          pp_order_no
        FROM
          OMCTMETA.extn_order_recon
      )
  );
WITH
  missmatch AS
  (
    SELECT
      pp_order_no
    FROM
      OMCTMETA.extn_order_recon
    WHERE
      pp_order_no NOT IN
      (
        SELECT
          order_no
        FROM
          OMCTMETA.yfs_order_header
        WHERE
          order_no IN
          (
            SELECT
              pp_order_no
            FROM
              OMCTMETA.extn_order_recon
          )
      )
  )
  ,
  exceptions AS
  (
    SELECT
      trim(SUBSTR(error_reference,11)) order_no
    FROM
      OMCTMETA.yfs_reprocess_error
    WHERE
      ((errortxnid > '25' OR sysdate > (sysdate - 1 DAY)) 
      AND flow_name='PP_OMS_CreateOrder')
  )
SELECT
  'Orders in OM Exception : '
  || COUNT(missmatch.pp_order_no)
  || 'newLine'   
FROM
  missmatch,
  exceptions
WHERE
  missmatch.pp_order_no=exceptions.order_no;
SELECT
  'Orders Missing in OM : '
  || trim(COUNT(pp_order_no))
  || 'newLine'  
FROM
  OMCTMETA.extn_order_recon
WHERE
  pp_order_no IS NOT NULL AND 
  pp_order_no NOT IN
  (
    SELECT
      order_no
    FROM
      OMCTMETA.yfs_order_header
    WHERE
      order_no IN
      (
        SELECT
          pp_order_no
        FROM
          OMCTMETA.extn_order_recon
      )
  );
SELECT
  pp_order_no || 'newLine' 
FROM
  OMCTMETA.extn_order_recon
WHERE
  pp_order_no IS NOT NULL AND 
  pp_order_no NOT IN
  (
    SELECT
      order_no
    FROM
      OMCTMETA.yfs_order_header
    WHERE
      order_no IN
      (
        SELECT
          pp_order_no
        FROM
          OMCTMETA.extn_order_recon
      )
  );