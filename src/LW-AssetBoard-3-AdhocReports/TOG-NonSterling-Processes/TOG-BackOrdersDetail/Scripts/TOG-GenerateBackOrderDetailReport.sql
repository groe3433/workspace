select 'ITEM_ID,ITEM_DESCRIPTION,TRUE_BO_QTY,OTHER_BO_QTY,CRD_INV,EST_INV,JST_INV,SPR_INV,EST_ALLOCATED,JST_ALLOCATED,SPR_ALLOCATEDnewLine' from dual;
With File1 as (
SELECT 
       ITEM_ID as ITEM_ID,
       DESCR as ITEM_DESCRIPTION,
      QTY1 as TrueBOQty,
      nvl(QTY2,0) as OtherBOQty
      from 
      (
SELECT   
        yol.item_id as ITEM_ID,
       yi.description as DESCR,
       sum(
            case when yol.SCHED_FAILURE_REASON_CODE = 'NOT_ENOUGH_PRODUCT_CHOICES'
                then yol.ordered_qty
                end) as QTY1,
       sum(
            case when yol.SCHED_FAILURE_REASON_CODE = 'Other Constraints'
                then yol.ordered_qty
                END) AS QTY2
  FROM OMCTMETA.yfs_order_header yoh, OMCTMETA.yfs_order_line yol, OMCTMETA.yfs_item yi
 WHERE yoh.order_header_key = yol.order_header_key
        and YOL.ORDERED_QTY > '0'
        and YOL.SCHED_FAILURE_REASON_CODE in ('NOT_ENOUGH_PRODUCT_CHOICES', 'Other Constraints')
        and yoh.document_type = '0001'
        and yol.item_id = yi.item_id
       group by yol.item_id,yi.description) 
group by ITEM_ID,DESCR,QTY1, QTY2
),
file2 as (
SELECT ITEM_ID, 
 ( SELECT SUM(QUANTITY) FROM OMCTMETA.YFS_INVENTORY_SUPPLY A1, OMCTMETA.YFS_INVENTORY_ITEM B1  WHERE A1.INVENTORY_ITEM_KEY = B1.INVENTORY_ITEM_KEY AND B1.ITEM_ID = A.ITEM_ID AND SUPPLY_TYPE='ONHAND' AND SHIPNODE_KEY='CRD') CRD_INV,
 ( SELECT SUM(QUANTITY) FROM OMCTMETA.YFS_INVENTORY_SUPPLY A1, OMCTMETA.YFS_INVENTORY_ITEM B1  WHERE A1.INVENTORY_ITEM_KEY = B1.INVENTORY_ITEM_KEY AND B1.ITEM_ID = A.ITEM_ID AND SUPPLY_TYPE='ONHAND' AND SHIPNODE_KEY='EST' ) EST_INV,
 ( SELECT SUM(QUANTITY) FROM OMCTMETA.YFS_INVENTORY_SUPPLY A1, OMCTMETA.YFS_INVENTORY_ITEM B1  WHERE A1.INVENTORY_ITEM_KEY = B1.INVENTORY_ITEM_KEY AND B1.ITEM_ID = A.ITEM_ID AND SUPPLY_TYPE='ONHAND' AND SHIPNODE_KEY='JST' ) JST_INV,
 ( SELECT SUM(QUANTITY) FROM OMCTMETA.YFS_INVENTORY_SUPPLY A1, OMCTMETA.YFS_INVENTORY_ITEM B1  WHERE A1.INVENTORY_ITEM_KEY = B1.INVENTORY_ITEM_KEY AND B1.ITEM_ID = A.ITEM_ID AND SUPPLY_TYPE='ONHAND' AND SHIPNODE_KEY='SPR' ) SPR_INV
 from OMCTMETA.yfs_item A
),
file3 as (
SELECT
ITEM_ID,
EST_Allocated,
JST_Allocated,
SPR_Allocated
FROM
(
SELECT
yol.Item_Id as ITEM_ID,
sum(
        case when yol.shipnode_key = 'EST'
                then ors.status_quantity
        end) as EST_Allocated,
sum(
        case when yol.shipnode_key = 'JST'
                then ors.status_quantity
         end) as JST_Allocated,
sum(
        case when yol.shipnode_key = 'SPR'
                then ors.status_quantity
         END) AS SPR_ALLOCATED
FROM OMCTMETA.YFS_ORDER_HEADER YOH
     JOIN OMCTMETA.YFS_ORDER_LINE YOL ON YOL.ORDER_HEADER_KEY = YOH.ORDER_HEADER_KEY
     JOIN OMCTMETA.yfs_order_release_status ors on ors.order_header_key = yoh.order_header_key and ors.order_line_key = yol.order_line_key
where yol.SHIPPED_QUANTITY = '0'
and ors.status_quantity > '0'
and ors.status in ('3200', '3200.10', '3200.30', '3200.40', '3200.50', '3200.60')
group by yol.Item_Id))
 SELECT 
    TRIM(File1.ITEM_ID)||','||
    TRIM(REPLACE(ITEM_DESCRIPTION, ',', ' '))||','||
     nvl(TrueBOQty,'0')||','||
     nvl(OtherBOQty, '')||','||
    nvl(CRD_Inv,0)||','||
    nvl(EST_Inv,0)||','||
    nvl(JST_Inv,0)||','||
    nvl(SPR_Inv,0)||','||
    nvl(EST_Allocated,0)||','||
    NVL(JST_ALLOCATED,0)||','||
    NVL(SPR_ALLOCATED,0)||'newLine'
From File1, file2, file3 where file1.Item_id = file2.item_id and file1.Item_id = file3.ITEM_ID;
