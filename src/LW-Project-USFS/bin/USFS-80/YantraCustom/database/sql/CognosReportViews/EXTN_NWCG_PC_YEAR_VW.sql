CREATE OR REPLACE FORCE VIEW EXTN_NWCG_PC_YEAR_VW ("CODE_VALUE", "CODE_SHORT_DESCRIPTION")
AS
  SELECT code_value,
    code_short_description
  FROM yfs_common_code
  WHERE code_type = 'NWCG_PC_YEAR'
  OR code_type    = 'NWCG_PC_PREYEAR';