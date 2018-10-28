CREATE OR REPLACE PROCEDURE OMCTMETA.open_order_extract
AS
  errmessage VARCHAR(2000);
  v_status OMCTMETA.yfs_order_release_status.status%TYPE;
  CURSOR get_status_cursor
  IS
    SELECT
      status
    FROM
      OMCTMETA.yfs_status
    WHERE
      process_type_key = 'ORDER_FULFILLMENT'
    AND status NOT IN ('1000', '1400', '3700', '9000')
    ORDER BY
      status;
BEGIN
BEGIN
  EXECUTE immediate 'truncate table OMCTMETA.exel_order_extract_step1';
  EXECUTE immediate 'truncate table OMCTMETA.exel_order_extract_step2';
  EXECUTE immediate 'truncate table OMCTMETA.exel_order_extract_step3';
  EXECUTE immediate 'truncate table OMCTMETA.exel_order_extract_step3b';
  EXECUTE immediate 'truncate table OMCTMETA.exel_order_extract_step4';
EXCEPTION
WHEN OTHERS THEN
  dbms_output.put_line (SQLERRM(SQLCODE));
END;
COMMIT;
DELETE
  OMCTMETA.exel_order_extract_step1;
DELETE
  OMCTMETA.exel_order_extract_step2;
DELETE
  OMCTMETA.exel_order_extract_step3;
DELETE
  OMCTMETA.exel_order_extract_step3b;
DELETE
  OMCTMETA.exel_order_extract_step4;
COMMIT;
INSERT
INTO
  OMCTMETA.exel_order_extract_step1
SELECT
  a.order_header_key,
  SUM(status_quantity) Partially_Shipped_Qty
FROM
  OMCTMETA.yfs_order_header a,
  OMCTMETA.yfs_order_release_status c
WHERE
  a.order_header_key  = c.order_header_key
AND a.document_type   ='0001'
AND c.status_quantity > 0
AND c.status         IN ('1100','1300','1500')
GROUP BY
  a.order_header_key;
COMMIT;
INSERT
INTO
  OMCTMETA.exel_order_extract_step2
SELECT
  a.*
FROM
  OMCTMETA.exel_order_extract_step1 a
WHERE
  EXISTS
  (
    SELECT
      *
    FROM
      OMCTMETA.yfs_order_release_status c
    WHERE
      a.order_header_key  = c.order_header_key
    AND c.status          = '3700'
    AND c.STATUS_QUANTITY > 0
  );
COMMIT;
BEGIN
  FOR get_status_cursor_rec IN get_status_cursor
  LOOP
  BEGIN
    v_status := get_status_cursor_rec.status;
    INSERT
    INTO
      OMCTMETA.exel_order_extract_step3
    SELECT
      c.status,
      c.status_quantity,
      c.order_header_key,
      c.order_line_key,
      c.order_release_key,
      c.order_Line_schedule_key
    FROM
      OMCTMETA.yfs_order_release_status c
    WHERE
      c.status_quantity > 0
    AND c.status        = v_status;
  EXCEPTION
  WHEN OTHERS THEN
    ROLLBACK;
  END;
  COMMIT;
END LOOP;
END;
COMMIT;
INSERT
INTO
  OMCTMETA.exel_order_extract_step3b
SELECT
  b.prime_line_no,
  b.order_header_key,
  b.item_id,
  c.status,
  b.extn_ltoos_flag,
  c.status_quantity,
  c.ORDER_RELEASE_KEY,
  c.order_line_schedule_key
FROM
  OMCTMETA.yfs_order_line b,
  OMCTMETA.exel_order_extract_step3 c
WHERE
  b.order_line_key = c.order_line_key;
COMMIT;
INSERT
INTO
  OMCTMETA.exel_order_extract_step4
SELECT
  Order_No,
  Vendor_ID AS Brand_Code,
  a.Order_type,
  TO_CHAR (a.Order_date, 'MM/DD/YYYY') OrderDate,
  item_id,
  prime_line_no,
  b.status,
  d.release_no,
  CASE
    WHEN b.status             > '1500'
    AND NVL (D.RELEASE_NO, 0) > 1
    THEN 'Multiple Releases'
    ELSE
      CASE
        WHEN b.status              > '1500'
        AND NVL (D.RELEASE_NO, 0) <= 1
        THEN 'First Pass'
        ELSE
          CASE
            WHEN b.status <= '1500'
            AND
              (
                NVL (D.RELEASE_NO, 0) >= 1
              OR Partially_Shipped_Qty > 0
              )
            THEN 'Multiple Releases'
            ELSE 'First Pass'
          END
      END
  END Release_Message,
  CASE
    WHEN b.status        >= '1100'
    AND b.extn_ltoos_flag = 'Y'
    THEN 'Yes'
    ELSE 'No'
  END LTOOS,
  NVL (e.ship_node, ' ') Site,
  b.status_quantity
FROM
  OMCTMETA.yfs_order_header a
LEFT OUTER JOIN OMCTMETA.exel_order_extract_step2 f
ON
  a.order_header_key = f.order_header_key,
  OMCTMETA.exel_order_extract_step3b b
LEFT OUTER JOIN OMCTMETA.yfs_order_release d
ON
  b.ORDER_RELEASE_KEY = d.order_release_key
LEFT OUTER JOIN OMCTMETA.yfs_order_line_schedule e
ON
  b.order_Line_schedule_key = e.order_Line_Schedule_key
WHERE
  a.order_header_key = b.order_header_key
AND a.document_type  = '0001'
AND a.enterprise_key = 'Thirty-One' ;
COMMIT;
EXCEPTION
WHEN OTHERS THEN
  dbms_output.put_line (SQLERRM(SQLCODE));
END open_order_extract;