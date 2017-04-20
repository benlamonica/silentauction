<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <title>Silent Auction Login</title>

    <!-- Bootstrap -->
    <link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="site.css" rel="stylesheet">
  </head>
  <body>
    <div class="container">
      <h4 class="text-center">Silent Auction Login</h4>
      <div id="error_message" class="alert alert-danger">
        <strong>Invalid Username or Password!</strong>
      </div>
      <form class="form" action="/create-account.html" method="POST">
        <div class="form-group">
          <label for="name">Name</label>
          <input type="text" class="form-control" id="name" name="name" placeholder="Full Name">
          <label for="email">E-mail Address</label>
          <input type="email" class="form-control" id="email" name="email" placeholder="E-mail Address">
          <label for="name">Phone</label>
          <input type="phone" class="form-control" id="phone" name="phone" placeholder="###-###-####">
        </div>
        <div class="form-group">
          <label for="password">Password</label>
          <input type="password" class="form-control" id="password" name="password" placeholder="Password">
          <label for="password">Repeat Password</label>
          <input type="password" class="form-control" id="password" name="password2" placeholder="Password">
        </div>
        <div class="form-group">
          <label for="wantsEmail">Receive e-mail notifications of out bids</label>
          <input type="checkbox" class="form-control" id="wantsEmail" name="wantsEmail" checked="true">
          <label for="wantsSMS">Receive SMS notifications of out bids</label>
          <input type="checkbox" class="form-control" id="wantsSMS" name="wantsSMS" checked="true">
        </div>
        <div class="form-group">
          <button type="submit" id="submitButton" class="btn btn-primary">Create Account</button>
        </div>
      </form>
    </div>

    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="/webjars/jquery/dist/jquery.min.js"></script>
    <script type="text/javascript">
        if (window.location.href.indexOf("?error") == -1) {
            $("#error_message").hide();
        }
    </script>
  </body>
</html>