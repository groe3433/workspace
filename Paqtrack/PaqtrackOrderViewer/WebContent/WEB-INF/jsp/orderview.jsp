<%-- 
  - Author: Andrew Groenewold
  - Version: 1.0.0
  - Date: February 15, 2017
  - Copyright: Copyright © 2017 by Andrew Groenewold. All Rights Reserved.
  - Category: Order View Screen
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page session="false"%>
<html>
	<head>
		<title>Paqtrack Order Viewer</title>
		<style type="text/css">
			.tg  {border-collapse:collapse;border-spacing:0;border-color:#ccc;}
			.tg td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#ccc;color:#333;background-color:#fff;}
			.tg th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#ccc;color:#333;background-color:#f0f0f0;}
			.tg .tg-4eph{background-color:#f9f9f9}
		</style>
	</head>
	<body>
		<h3>Paqtrack Order List</h3>
		<c:if test="${!empty listOrders}">
			<table class="tg">
				<tr>
					<th width="80">ID</th>
					<th width="120">Order No</th>
					<th width="120">Total Cost</th>		
					<th width="120">Customer Name</th>			
				</tr>
				<c:forEach items="${listOrders}" var="orderview">
					<tr>
						<td>${orderview.id}</td>
						<td>${orderview.orderNo}</td>
						<td>${orderview.totalCost}</td>			
						<td>${orderview.customerName}</td>				
					</tr>
				</c:forEach>
			</table>
		</c:if>
	</body>
</html>