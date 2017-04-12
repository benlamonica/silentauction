<!DOCTYPE html>
<html lang="en">
  <head>
    <#include "bootstrap-header.ftl">
    <title>Auction Winners</title>
  </head>
  <body>
    <div class="container">
        <#include "nav.ftl">
        <#if isAdmin == true>
        <table class="table table-condensed">
        <tr><th>Item</th><th>Name</th><th>Email</th><th>Amt</th></tr>
        <#list items as item>
        <tr><td><a href="item.html?id=${item.id}">${item.name}</a></td><td>${item.highBidder.name}</td><td>${item.highBidder.email}</td><td>${item.highBid.bid}</td></tr>
        </#list>
        </table>
        <#else>
            You must be an admin to view this report.
        <#/if>
    </div>
    <#include "bootstrap-footer.ftl">
  </body>
</html>