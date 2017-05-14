<!DOCTYPE html>
<html lang="en">
  <head>
    <#include "bootstrap-header.ftl">
    <title>Silent Auction</title>
  </head>
  <body>
    <div class="container">
        <#include "nav.ftl">
        <h4>${viewName}</h4>
        <table class="table table-condensed">
        <tr><th></th><th>Item</th><th>My Bid</th><th>Outbid By</th></tr>
        <#list items as item>
        <tr ${(item.highBidder==currentUser)?string("class=\"success\"", "class=\"danger\"")}><td><a href="item.html?id=${item.id}"><img class="img-thumbnail" style="max-height: 3em; max-width: 3em;" src="images/thumbs/${item.id}.jpg"/></a></td><td><a href="item.html?id=${item.id}">${item.name}</a></td><td>${(item.getHighBidForUser(currentUser))?string.currency!"$0.00"}</td><td>${(item.getAmountOutBidByForUser(currentUser))?string.currency!"$0.00"}</td></tr>
        </#list>
        </table>
    </div>
    <#include "bootstrap-footer.ftl">
  </body>
</html>