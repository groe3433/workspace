CREATE OR REPLACE FORCE VIEW EXTN_NWCG_ACCT_TRANS_RPT_VW ("CACHE ID", "INCIDENT NO", "INCIDENT_NAME", "YEAR", "TRANSACTION_NO", "TRANS_TYPE", "TRANS_DATE", "INCIDENT_BLM_ACCT_CODE", "INCIDENT_FS_ACCT_CODE", "INCIDENT_FS_OVERRIDE_CODE", "INCIDENT_OTHER_ACCT_CODE", "TRANS_AMOUNT")
AS
  SELECT a.cache_id "CACHE ID",
    a.incident_no "INCIDENT NO",
    a.incident_name,
    a.incident_year "YEAR",
    a.transaction_no,
    a.trans_type,
    to_date(TO_CHAR(a.trans_date,'DD-MON-YYYY'),'DD-MON-YYYY') "TRANS_DATE",
    a.incident_blm_acct_code,
    a.incident_fs_acct_code,
    a.incident_fs_override_code,
    a.incident_other_acct_code,
    SUM(a.trans_amount) "TRANS_AMOUNT"
  FROM nwcg_billing_transaction a
  GROUP BY a.cache_id,
    a.incident_no,
    a.incident_name,
    a.incident_year,
    a.transaction_no,
    a.trans_type,
    to_date(TO_CHAR(a.trans_date,'DD-MON-YYYY'),'DD-MON-YYYY'),
    a.incident_blm_acct_code,
    a.incident_fs_acct_code,
    a.incident_fs_override_code,
    a.incident_other_acct_code;