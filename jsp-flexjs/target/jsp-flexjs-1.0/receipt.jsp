<%@page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.cybersource.example.FlexKeyProvider"%>
<%@page import="java.util.Map"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Receipt</title>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/css/bootstrap.min.css" integrity="sha384-PsH8R72JQ3SOdhVi3uxftmaW6Vc51MKb0q5P2rRUpPvrszuE4W1povHYgTpBfshb" crossorigin="anonymous">

        <style>
            .td-1 {
                word-break: break-all;
                word-wrap: break-word;
            }
        </style>
    </head>

    <body>
        <div class="container card">
            <div class="card-body">
                <h1>Receipt</h1>
                <table class="table">
                    <thead>
                        <tr>
                            <th scope="col">Key</th>
                            <th scope="col">value</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            for (Map.Entry<String, String[]> e : request.getParameterMap().entrySet()) {
                                out.println("<tr scope=\"row\">");
                                out.println("<td>" + e.getKey() + "</td>");
                                out.println("<td class=\"td-1\">" + e.getValue()[0] + "</td>");
                                out.println("</tr>");
                            }
                        %>
                        <tr scope="row">
                            <td>Signature validation</td>
                            <td>
                                <%=((FlexKeyProvider) request.getServletContext().getAttribute(FlexKeyProvider.class.getName()))
                                        .verifyTokenResponse(session, request.getParameter("flex-response"))%>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <a href="index.jsp" class="btn btn-primary">Repeat checkout process</a>
            </div>
        </div>
    </body>
</html>