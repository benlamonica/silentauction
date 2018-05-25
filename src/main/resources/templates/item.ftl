<!DOCTYPE html>
<html lang="en">
  <head>
    <#setting datetime_format="MM/dd HH:mma">
    <#include "bootstrap-header.ftl">
    <title>Silent Auction - ${(item.name)!""}</title>
  </head>
  <body>
    <div class="container">
        <#include "nav.ftl">
        <p class="text-center"><mark>${(item.highBidder.shortName?ensure_ends_with("'s"))!} High Bid: ${item.highBidAmount?string.currency!item.minimumBid?string.currency}</mark></p>
        <#if auction?? && !auction.auctionClosed>
        <form class="form" action="bid.html" method="POST">
        <input type="hidden" name="id" value="${item.id}"/>
          <div class="form-group">
            <label class="sr-only" for="bidAmount">Amount (numbers only)</label>
            <div class="input-group">
              <div class="input-group-addon">$</div>
              <input type="text" pattern="[0-9,.]{1,13}" class="form-control" name="bidAmount" placeholder="Amount (numbers only)">
            </div>
          </div>
          <button type="submit" class="btn btn-primary btn-block">Bid</button>
        </form>
        </#if>
        <h3>${item.name}</h3>
        <img class="img-responsive" src="images/${item.id}.jpg"/>
        <p>
            <strong>Donor:</strong> <span id="#donor">${item.donor!""}</span>
        </p>
        <p>
            <strong>Description:</strong><span id="#description"> ${item.description!""}</span>
        </p>
        <table class="table table-condensed">
        <tr><th>Bidder</th><th>Amt</th><th>At</th><#if currentUser.admin><th></th></#if></tr>
        <#list item.bids as bid>
        <tr ${bid?is_first?string("class=\"success\"", "")}><td>${bid.user.shortName!""}</td><td>${bid.bid?string.currency!""}</td><td>${bid.formattedBidTime}</td><#if currentUser.admin><td><form class="form-inline" method="POST" action="delete-bid.html?itemId=${item.id}&bidId=${bid.id}"><button class="btn btn-danger">X</button></form></td></#if></tr>
        </#list>
        </table>
        <#if (currentUser.admin)>
        <form class="form" action="edit-item.html" method="GET">
          <input type="hidden" name="id" value="${item.id}"/>
          <button type="submit" class="btn btn-primary btn-block">Edit Item</button>
        </form>
        </#if>
        <#if currentUser.admin>
        <form class="form" action="delete-item.html" method="POST">
          <input type="hidden" name="id" value="${item.id}"/>
          <button type="submit" class="btn btn-danger btn-block">Remove Item</button>
        </form>
        </#if>
    </div>
    <#include "bootstrap-footer.ftl">
  </body>
</html>