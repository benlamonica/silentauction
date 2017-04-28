<!DOCTYPE html>
<html lang="en">
  <head>
    <#include "bootstrap-header.ftl">
    <title>${(action?capitalize)!"Create"} account</title>
  </head>
  <body>
      <div class="container">
      <#if currentUser??>
      <#include "nav.ftl">
      </#if>
      <h4 class="text-center">${(action?capitalize)!"Create"} account</h4>
      <div id="error_message" class="alert alert-danger">
        <strong>Invalid Username or Password!</strong>
      </div>
      <form class="form-horizontal" id="account-form" action="/${action!"create"}-account.html" method="POST">
        <div class="form-group">
          <label class="control-label col-sm-2" for="name">Name</label>
          <div class="col-sm-10">
            <input type="text" class="form-control" id="name" name="name" value="${(currentUser.name)!""}" placeholder="Full Name">
          </div>
        </div>
        <div class="form-group">
          <label class="control-label col-sm-2" for="email">E-mail Address</label>
          <div class="col-sm-10">
            <input type="email" class="form-control" id="email" name="email" value="${(currentUser.email)!""}" placeholder="E-mail Address">
          </div>
        </div>
        <#if currentUser??>
        <div class="form-group">
          <label class="control-label col-sm-2" for="email">Email Verified</label>
          <div class="col-sm-10">
            <#if currentUser.emailVerified>
                Yes
            <#else>
                No <a href="/resend-validation-email.html">Resend Validation Email</a>
            </#if>
          </div>
        </div>
        </#if>
        <div class="form-group">
          <label class="control-label col-sm-2" for="name">Phone <small>(Optional)</small></label>
          <div class="col-sm-10">
            <input type="phone" class="form-control" id="phone" name="phone" value="${(currentUser.phone)!""}" placeholder="###-###-####">
          </div>
        </div>
        <div class="form-group">
          <label class="control-label col-sm-2" for="password"><#if action?? && action == "edit">Change </#if>Password</label>
          <div class="col-sm-10">
            <input class="form-control" type="password" class="form-control" id="password" name="password" placeholder="Password">
          </div>
        </div>
        <div class="form-group">
          <label class="control-label col-sm-2" for="password">Repeat Password</label>
          <div class="col-sm-10">
            <input type="password" class="form-control" id="password2" name="password2" placeholder="Password">
          </div
        </div>
        <div class="form-group">
          <div class="col-sm-offset-2 col-sm-10">
            <div class="checkbox">
                <label>
                    <input type="checkbox" id="wantsEmail" name="wantsEmail" checked="${(currentUser.wantsEmail()?c)!"true"}"/> Receive e-mail notifications of out bids
                </label>
            </div>
          </div>
        </div>
        <div class="form-group">
          <div class="col-sm-offset-2 col-sm-10">
            <div class="checkbox">
                <label>
                    <input type="checkbox" id="wantsSMS" name="wantsSMS" checked="${(currentUser.wantsSms()?c)!"true"}"/> Receive text message notifications of out bids
                </label>
            </div>
          </div>
        </div>
        <div class="control-group">
          <button type="submit" id="submitButton" class="btn btn-primary">Save Account</button>
        </div>
      </form>
    </div>
    <#include "bootstrap-footer.ftl">
    <script type="text/javascript">
        function showError(msg) {
            $("#error_message").html("<strong>"+msg+"</strong>").show();
        }
    
        $("#submitButton").click(function(event) {
            $("error_message").hide();
        
            if ($("#name").val().trim() == "") {
                showError("Name must be entered.");
                event.preventDefault();
                return false;
            }
        
            if (!/\w+@\w+\.\w+/.test($("#email").val().trim())) {
                showError("Email must be entered and valid.");
                event.preventDefault();
                return false;
            }

            if ($("#account-form").attr("action").startsWith("/edit") && $("#password").val().trim().size != 0 && $("#password").val().trim().length != 0 && $("#password").val().trim().length < 6) {
                showError("Password must be at least 6 characters long.");
                event.preventDefault();
                return false;
            }

            if ($("#password").val() != $("#password2").val()) {
                showError("Password fields do not match.");
                event.preventDefault();
                return false;
            }
            
            return true;
        });
        if (window.location.href.indexOf("?error") == -1) {
            $("#error_message").hide();
        }
    </script>
  </body>
</html>