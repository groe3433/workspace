CREATE OR REPLACE FORCE VIEW EXTN_NWCG_YFS_COMMENTS_VW ("TABLE_NAME", "TABLE_KEY", "COMMENTS")
AS
  SELECT table_name,
    table_key,
    ltrim(MAX(sys_connect_by_path(note_text,'^')),'^') AS comments
  FROM
    (SELECT table_name,
      table_key,
      note_text,
      row_number() over (partition BY table_key order by note_text)    AS curr,
      row_number() over (partition BY table_key order by note_text) -1 AS prev
    FROM yfs_notes
    )
  GROUP BY table_name,
    table_key
    CONNECT BY prev = prior curr
  AND table_key     = prior table_key
    START WITH curr = 1;