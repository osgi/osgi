<%@ page language="java" contentType="text/html"%>
<html>
<head>
<jsp:useBean id="user" class="org.osgi.test.cases.webcontainer.tw1.SimpleUserBean" scope="session"/>
<jsp:setProperty name="user" property="*"/> 
<title>welcome.jsp</title>
</head>
<body>
Email-${user.email}<br/>
WelcomeString-${user.message}<br/>
</body>
</html>