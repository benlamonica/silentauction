<!DOCTYPE html>
<html lang="en">
  <head>
    <#include "bootstrap-header.ftl">
    <title>Auction Winners</title>
  </head>
  <body>
    <div class="container">
        <#include "nav.ftl">
        <table class="table table-condensed">
        <tr><th>Item</th><th>Name</th><th>Email</th><th>Amt</th></tr>
        <#list items as item>
        <tr><td><a href="item.html?id=${item.id}">${item.name}</a></td><td>${(item.highBidder.name)!"n/a"}</td><td>${(item.highBidder.email)!"n/a"}</td><td>${(item.highBidAmount?string.currency)!"$0"}</td></tr>
        </#list>
        </table>
    </div>
    <#include "bootstrap-footer.ftl">
  </body>
</html>