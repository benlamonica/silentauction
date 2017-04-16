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
        <div class="container-fluid">
          <div class="row">
            <#list items as item>
            <div class="col-xs-6 col-sm-3"><a href="item.html?id=${item.id}"><img class="img-thumbnail" src="images/thumbs/${item.id}.jpg"/><p class="text-center"><small>${item.name} - ${item.highBidAmount?string.currency!"$0.00"}</small></p></a></div>
            </#list>
          </div>
        </div>
    </div>
    <#include "bootstrap-footer.ftl">
  </body>
</html>