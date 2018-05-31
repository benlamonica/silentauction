<!DOCTYPE html>
<html lang="en">
  <head>
    <#include "bootstrap-header.ftl">
    <title>Auction Users</title>
  </head>
  <body>
    <div class="container">
        <#include "nav.ftl">
        <a class="btn btn-primary" href="/send-all-eoa-email.html">Send All End of Auction Emails</a>
        <table class="table table-condensed">
        <tr><th>Name</th><th>Email</th><th>Phone</th><th></th></tr>
        <#list users as user>
        <tr>
        	<td><a href="end-of-auction.html?userId=${user.id}">${user.name}</a></td>
        	<td>${(user.email)!"n/a"}</td><td>${user.phone!"n/a"}</td>
        	<td><#if user.admin><a class="btn btn-info" href="remove-admin.html?userId=${user.id}">Remove Admin</a><#else><a class="btn btn-info" href="add-admin.html?userId=${user.id}">Add Admin</a></#if></td>
        	<td><a class="btn btn-danger" href="send-reset-password-link.html?userId=${user.id}">Send Reset Password Email</a></td>
        	<td><a class="btn btn-default" href="send-eoa-email.html?userId=${user.id}">Send EOA Email</a></td>
    	</tr>
        </#list>
        </table>
    </div>
    <#include "bootstrap-footer.ftl">
  </body>
</html>