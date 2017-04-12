<!DOCTYPE html>
<html lang="en">
  <head>
    <#include "bootstrap-header.ftl">
    <title>Donation Report</title>
  </head>
  <body>
    <div class="container">
        <#include "nav.ftl">
        <table class="table table-condensed">
        <tr><th>Item</th><th>Amt</th></tr>
        <#list items as item>
        <tr><td><a href="item.html?id=${item.id}">${item.name}</a></td><td>${item.highBidAmount}</td></tr>
        </#list>
        <tr class="success"><td>Total</td><td>${totalAmount}</td></tr>
        </table>
    </div>
    <#include "bootstrap-footer.ftl">
  </body>
</html>