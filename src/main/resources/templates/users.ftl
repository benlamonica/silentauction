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
        <tr><td><a href="end-of-auction.html?userId=${user.id}">${user.name}</a></td><td>${(user.email)!"n/a"}</td><td>${user.phone!"n/a"}</td><td><a class="btn btn-default" href="send-eoa-email.html?userId=${user.id}">Send EOA Email</a></tr>
        </#list>
        </table>
    </div>
    <#include "bootstrap-footer.ftl">
  </body>
</html>