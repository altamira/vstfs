<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.util.List" %>
<%@ page import="com.purchaseorder.model.RequestItemModel" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Purchase Request</title>
</head>
<body>
	<%
		List<RequestItemModel> list = (List<RequestItemModel>) session.getAttribute("requestItemList");

		String sessionId = (String) session.getAttribute("sessionId");
		if (request.getSession().getId().equals(sessionId)) 
		{
			String processInstanceId = (String) session.getAttribute("processInstanceId");
			if (processInstanceId == null) 
			{
	%>
				<script>
					alert("Process instance id not found.");
				</script>
				<c:redirect url="${pageContext.request.contextPath}/../pages/index.jsp"></c:redirect>
	<%
			}
		}
		else 
		{
	%>
			<script>
				alert("Session Expired.");
			</script>
			<c:redirect url="${pageContext.request.contextPath}/../pages/index.jsp"></c:redirect>
	<%
		}
	%>
	<p>
		SessionId -	<c:out value="${sessionId}"></c:out>
		<br />
		Process Instance Id - <c:out value="${processInstanceId}"></c:out>
		
	</p>
	<h2>Add Material Items to Request</h2>
	<form action="${pageContext.request.contextPath}/webservice/purchaseOrder/request" method="post">
		<table>
			<tr>
				<td>Material Code :</td>
				<td><input type="text" name="materialCode" /></td>
			</tr>
			<tr>
				<td>Material Arrival Date :</td>
				<td><input type="text" name="requestArrivalDate" /></td>
			</tr>
			<tr>
				<td>Material Weight :</td>
				<td><input type="text" name="requestWeight" /></td>
			</tr>
			<tr>
				<td colspan="2">
					<input type="submit" name="request_add" value="Add Item" />
				</td>
			</tr>
		</table>
	</form>
	
	<br />
	<br />
	
	<h3>List of Request Items</h3>
	
	<table border="2px" style="text-align: center;">
		<tr>
			<th>Material Code</th>
			<th>Lamination</th>
			<th>Treatment</th>
			<th>Thickness (mm)</th>
			<th>Width (mm)</th>
			<th>Length (mm)</th>
			<th>Material Requested Arrival Date</th>
			<th>Weight(T)</th>
		</tr>
		<c:forEach items="${requestItemList}" var="requestItemList">
			<tr>
				<td>${requestItemList.materialCode}</td>
				<td>${requestItemList.materialLamination}</td>
				<td>${requestItemList.materialTreatment}</td>
				<td>${requestItemList.materialThickness}</td>
				<td>${requestItemList.materialWidth}</td>
				<td>${requestItemList.materialLength}</td>
				<td>${requestItemList.requestArrivalDate}</td>
				<td>${requestItemList.requestWeight}</td>
			</tr>
		</c:forEach>
	</table>
	<br />
	<form action="${pageContext.request.contextPath}/webservice/purchaseOrder/submit_request" method="post">
		<input type="radio" name="next_step" value="request" checked="checked">Create new Purchase Request</input>
		<input type="radio" name="next_step" value="quotation">Create new Quotation</input>
		<br />
		<br />
		<input type="submit" name="request_submit" value="Submit Purchase Request">
	</form>
</body>
</html>