<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Steel Purchasing</title>
</head>
<body>
	<form action="${pageContext.request.contextPath}/webservice/purchaseOrder/createSteelPurchasingInstance" method="post">
		<input type="submit" name="submit" value="Click here to start steel purchasing process" />
	</form>
</body>
</html>