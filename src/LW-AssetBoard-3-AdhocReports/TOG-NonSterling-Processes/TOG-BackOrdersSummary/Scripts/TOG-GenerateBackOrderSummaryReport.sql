select 'ORDER_NO,ORDER_DATE,ORDER_TYPE,ITEM_ID,ITEM_DESCRIPTION,TRUE_BACKORDER_QTY,BACKORDER_HELD_QTY,TOTAL_INV,CRD_INV,EST_INV,JST_INVnewLine' from dual;
With File1 as (
SELECT ORDER_NO1 as ORDER_NO, 
       ORD_DATE as ORDER_DATE,
       ORD_TYPE as ORDER_TYPE,
       ITEM_ID as ITEM_ID,
       DESCR as ITEM_DESCRIPTION,
      QTY1 as TrueBOQty,
      nvl(QTY2,0) as BOQtyHeld
      from 
      (
SELECT yoh.order_no as ORDER_NO1,
       yoh.order_header_key as ORDER_KEY1,
       yoh.order_date as ORD_DATE,
       yoh.order_type as ORD_TYPE,
       yol.item_id as ITEM_ID,
       yi.description as DESCR,
       sum(YOL.ORDERED_QTY) as QTY1
  FROM OMCTMETA.yfs_order_header yoh, OMCTMETA.yfs_order_line yol, OMCTMETA.yfs_item yi
 WHERE yoh.order_header_key = yol.order_header_key
       AND yol.SCHED_FAILURE_REASON_CODE = 'NOT_ENOUGH_PRODUCT_CHOICES'
       and YOL.ORDERED_QTY > '0'
       and yol.item_id = yi.item_id
       group by yoh.order_no,yoh.order_header_key, yoh.order_type, yoh.order_date, yol.item_id,yi.description) 
       LEFT OUTER JOIN 
       (
       SELECT yol1.order_header_key as ORDER_KEY2,
       sum(YOL1.ORDERED_QTY) as QTY2
       FROM  OMCTMETA.yfs_order_line yol1
where yol1.SCHED_FAILURE_REASON_CODE = 'Other Constraints' group by yol1.order_header_key)  on ORDER_KEY1=ORDER_KEY2
group by ORDER_NO1,ORD_DATE, ORD_TYPE, ITEM_ID,DESCR,QTY1, QTY2
),
file2 as (
select item_id, 
 ( select sum(quantity) from OMCTMETA.yfs_inventory_supply A1, OMCTMETA.yfs_inventory_item B1  where A1.inventory_item_key = B1.inventory_item_key and B1.item_id = A.Item_Id and shipnode_key in ('CRD','EST','JST') ) TOT_INV,
 ( select sum(quantity) from OMCTMETA.yfs_inventory_supply A1, OMCTMETA.yfs_inventory_item B1  where A1.inventory_item_key = B1.inventory_item_key and B1.item_id = A.Item_Id and shipnode_key='CRD') CRD_INV,
 ( select sum(quantity) from OMCTMETA.yfs_inventory_supply A1, OMCTMETA.yfs_inventory_item B1  where A1.inventory_item_key = B1.inventory_item_key and B1.item_id = A.Item_Id and shipnode_key='EST' ) EST_INV,
 ( select sum(quantity) from OMCTMETA.yfs_inventory_supply A1, OMCTMETA.yfs_inventory_item B1  where A1.inventory_item_key = B1.inventory_item_key and B1.item_id = A.Item_Id and shipnode_key='JST' ) JST_INV
 from OMCTMETA.yfs_item A
)
 SELECT TRIM(ORDER_NO)||','||
      to_char(ORDER_DATE,'MM-DD-YYYY')||','||
      TRIM(ORDER_TYPE)||','||
      TRIM(File1.ITEM_ID)||','||
      TRIM((REPLACE(ITEM_DESCRIPTION, ',', ' ')))||','||
     TrueBOQty||','||
     BOQtyHeld||','||
    nvl(TOT_INV,0)||','||
    nvl(CRD_Inv,0)||','||
    nvl(EST_Inv,0)||','||
    NVL(JST_INV,0)||'newLine'
From File1, file2 where file1.Item_id = file2.item_id;