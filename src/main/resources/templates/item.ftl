<!DOCTYPE html>
<html lang="en">
  <head>
    <#setting datetime_format="MM/dd HH:mma">
    <#include "bootstrap-header.ftl">
    <title>Silent Auction - ${item.name}</title>
  </head>
  <body>
    <div class="container">
        <#include "nav.ftl">
        <p class="text-center"><mark>${(item.highBidder?ensure_ends_with("'s"))!} High Bid: ${item.highBidAmount?string.currency!"$0"}</mark></p>
        <#if errorMessage??>
            <p class="warning">${errorMessage}</p>      
        </#if>
        <form class="form-inline" action="bid.html" method="POST">
        <input type="hidden" name="id" value="${item.id}"/>
          <div class="form-group">
            <label class="sr-only" for="exampleInputAmount">Amount (in dollars)</label>
            <div class="input-group">
              <div class="input-group-addon">$</div>
              <input type="text" pattern="[0-9]+" class="form-control" name="bidAmount" placeholder="Amount">
            </div>
          </div>
          <button type="submit" class="btn btn-primary btn-block">Bid</button>
        </form>
        <h3>${item.name}</h3>
        <img class="img-responsive" src="images/${item.id}.jpg"/>
        <p>
            <strong>Donor:</strong> <span id="#donor">${item.donor!""}</span>
        </p>
        <p->
            <strong>Description:</strong><span id="#description"> ${item.description!""}</span>
        </p>
        <table class="table table-condensed">
        <tr><th>Bidder</th><th>Amt</th><th>At</th></tr>
        <#list item.bids as bid>
        <tr ${bid?is_first?string("class=\"success\"", "")}><td>${bid.user.shortName!""}</td><td>${bid.bid?string.currency!""}</td><td>${bid.formattedBidTime}</td></tr>
        </#list>
        </table>
        <form class="form-inline" action="delete-item.html" method="POST">
          <input type="hidden" name="id" value="${item.id}"/>
          <button type="submit" class="btn btn-danger btn-block">Remove Item</button>
        </form>
    </div>
    <#include "bootstrap-footer.ftl">
  </body>
</html>