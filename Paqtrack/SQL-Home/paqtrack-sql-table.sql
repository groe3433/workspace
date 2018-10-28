CREATE TABLE mysql_shopify_db.OrderView (
  id int(11) unsigned NOT NULL AUTO_INCREMENT,
  orderNo varchar(20) NOT NULL DEFAULT '',
  totalCost varchar(20) NOT NULL DEFAULT '',
  customerName varchar(20) NOT NULL DEFAULT '',
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

INSERT INTO mysql_shopify_db.OrderView
VALUES ("1","TestOrderNo1001","12.34","Sushmita Hajra");
INSERT INTO mysql_shopify_db.OrderView
VALUES ("2","TestOrderNo1002","90.25","Ted Smith");
INSERT INTO mysql_shopify_db.OrderView
VALUES ("3","TestOrderNo1003","34.78","Sarah Jones");
INSERT INTO mysql_shopify_db.OrderView
VALUES ("4","TestOrderNo1004","13.34","Nolan Grey");
INSERT INTO mysql_shopify_db.OrderView
VALUES ("5","TestOrderNo1005","14.25","Nicholas Andrew");
INSERT INTO mysql_shopify_db.OrderView
VALUES ("6","TestOrderNo1006","15.78","Sebastian Clark");
INSERT INTO mysql_shopify_db.OrderView
VALUES ("7","TestOrderNo1007","78.34","Caleb Drew");
INSERT INTO mysql_shopify_db.OrderView
VALUES ("8","TestOrderNo1008","54.25","Lavanya Das");
INSERT INTO mysql_shopify_db.OrderView
VALUES ("9","TestOrderNo1009","90.00","Eliana Mae");
INSERT INTO mysql_shopify_db.OrderView
VALUES ("10","TestOrderNo1010","72.34","Savannah Claire");
INSERT INTO mysql_shopify_db.OrderView
VALUES ("11","TestOrderNo1011","17.25","Josefina Smith");
INSERT INTO mysql_shopify_db.OrderView
VALUES ("12","TestOrderNo1012","82.44","Lorelei Paige");
INSERT INTO mysql_shopify_db.OrderView
VALUES ("13","TestOrderNo1013","19.34","Jacob Elijah");
INSERT INTO mysql_shopify_db.OrderView
VALUES ("14","TestOrderNo1014","9.25","Rowan James");
INSERT INTO mysql_shopify_db.OrderView
VALUES ("15","TestOrderNo1015","11.43","Harper Grace");


select * from mysql_shopify_db.OrderView

drop table mysql_shopify_db.OrderView
