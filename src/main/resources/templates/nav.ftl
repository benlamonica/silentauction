        <nav class="navbar navbar-default">
          <div class="container-fluid">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
              <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="glyphicon glyphicon-align-justify"></span>
              </button>
              <a class="navbar-brand" href="${navUrl!"/items.html"}"><span class="glyphicon ${navIcon!"glyphicon-th"}"></span> ${navText!"All Items"}</a>
            </div>

            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
              <ul class="nav navbar-nav">
                <#if (navText??) && (navText != "All Items")>
                <li><a href="items.html">All Items</a></li>
                </#if>
                <li><a href="items.html?filter=my_bids">My Bids (Donating ${(currentDonation?string.currency)!"$0"})</a></li>
                <!--<li><a href="items.html?filter=my_donations">My Donations</a></li>-->
                <li><a href="items.html?filter=no_bids">No Bid Items</a></li>
                <li><a href="report.html">Total Donations</a></li>
                <#if currentUser?? && currentUser.admin><li><a href="winners.html">Item Winners</a></li></#if>
                <li><a href="about.html">About this auction</a></li>
                <li><a href="edit-account.html">Edit Account</a></li>
                <#if currentUser?? && currentUser.admin><li><a href="users.html">Users</a></li></#if>
              </ul>
              <form class="navbar-form navbar-left" action="/logout">
                <button type="submit" class="btn btn-danger">Sign Out - ${(currentUser.shortName)!""}</button>
              </form>
            </div><!-- /.navbar-collapse -->
            <#if auction?? && !auction.auctionClosed>
                <div class="text-center" style="color: #3c763d"><strong>Auction will end in ${(auction.timeLeft)}!</strong></div>
            <#elseif auction?? && auction.auctionClosed>
                <div class="alert">Auction has ended. Bidding has been completed! Please contact <a href="mailto:${(auction.organizerEmail)}">${(auction.organizer)}</a> to submit donations.</div>
            </#if>
          </div><!-- /.container-fluid -->
        </nav>
        <#if (msg!"") != ""> 
        <div class="alert alert-success">
          <strong>${msg}</strong>
        </div>
        </#if>
        <#if (errMsg!"") != ""> 
        <div class="alert alert-success">
          <strong>${errMsg}</strong>
        </div>
        </#if>
        