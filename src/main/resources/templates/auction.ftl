<!DOCTYPE html>
<html lang="en">
  <head>
    <#setting datetime_format="MM/dd HH:mma">
    <#include "bootstrap-header.ftl">
    <title>Silent Auction - ${(auction.name)!""}</title>
  </head>
  <body>
    <div class="container">
        <#include "nav.ftl">
        <h3><#if currentUser.admin><a style="float: right" class="btn btn-primary" href="/edit-auction.html">Edit</a></#if>${auction.name}</h3>
        <img class="img-responsive" src="images/auction.jpg"/>
        <p>
            ${auction.description!""}
        </p>
    </div>
    <#include "bootstrap-footer.ftl">
  </body>
</html>