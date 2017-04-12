        <nav class="navbar navbar-default">
          <div class="container-fluid">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
              <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="glyphicon glyphicon-align-justify"></span>
              </button>
              <a class="navbar-brand" href="${navUrl}"><span class="glyphicon ${navIcon}"></span> ${navText}</a>
            </div>

            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
              <ul class="nav navbar-nav">
                <li><a href="items.html">All Items</a></li>
                <li><a href="items.html?filter=my_bids">My Bids</a></li>
                <li><a href="items.html?filter=my_donations">My Donations</a></li>
                <li><a href="report.html">Total Donations</a></li>
                <li><a href="winners.html">Item Winners</a></li>
              </ul>
              <form class="navbar-form navbar-left" action="login.html">
                <button type="submit" class="btn btn-danger">Logout</button>
              </form>
            </div><!-- /.navbar-collapse -->
          </div><!-- /.container-fluid -->
        </nav>