The auction is over! Thank you for participating in the ${auction.name}

<#if (wonItems?size > 0)>
-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
${auction.endOfAuctionInstructionsText!""}
-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

Please contact ${auction.organizer} at ${auction.organizerEmail} if you have any questions.

Total Donation: ${totalDonation?string.currency!"0.00"}

Congratulations! You have the winning bid on the following items:
-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
<#list wonItems as item>
Name: ${item.name}
Description: ${item.description}
Donor: ${item.donor}
Bid Amount: ${(item.getHighBidForUser(user))?string.currency!"$0.00"}

</#list>
</#if>
<#if (lostItems?size > 0)>

Sorry! You were outbid on these items:
-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
<#list lostItems as item>
Name: ${item.name}
Bid Amount: ${(item.getHighBidForUser(user))?string.currency!"$0.00"}
Outbid By: ${(item.getAmountOutBidByForUser(user))?string.currency!"$0.00"}

</#list>
</#if>
