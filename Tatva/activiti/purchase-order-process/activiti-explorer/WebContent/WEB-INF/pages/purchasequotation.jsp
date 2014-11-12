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
<title>Purchase Quotation</title>
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
			<c:out value="${pageContext.request.contextPath}"></c:out>
			<c:redirect url="${pageContext.request.contextPath}/../pages/index.jsp"></c:redirect>
	<%
		}
	%>
	<p>
		SessionId -	<c:out value="${sessionId}"></c:out>
		<br />
		Process Instance Id - <c:out value="${processInstanceId}"></c:out>
		
	</p>
</body>
</html>