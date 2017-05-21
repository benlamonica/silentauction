<!DOCTYPE html>
<html lang="en">
  <head>
    <#include "bootstrap-header.ftl">
    <link rel="stylesheet" href="/webjars/Eonasdan-bootstrap-datetimepicker/css/bootstrap-datetimepicker.min.css" />
    <title>Silent Auction - Edit Auction</title>
  </head>
  <body>
    <div class="container">
        <#include "nav.ftl">
        <form action="edit-auction.html" method="POST" enctype="multipart/form-data">
          <div class="form-group">
            <label for="name">Auction Name</label>
            <input type="text" class="form-control" name="name" value="${(auction.name)!""}" placeholder="Auction Name">
          </div>
          <div class="form-group">
            <label for="description">Description</label>
            <textarea class="form-control" name="description">${(auction.description)!""}</textarea>
          </div>
          <div class="form-group">
            <label for="endOfAuctionInstructions">End Of Auction Instructions</label>
            <textarea class="form-control" name="endOfAuctionInstructions">${(auction.endOfAuctionInstructions)!""}</textarea>
          </div>
          <div class="form-group">
            <label for="name">Organizer Name</label>
            <input type="text" class="form-control" name="organizer" value="${(auction.organizer)!""}" placeholder="Organizer Name">
          </div>
          <div class="form-group">
            <label for="name">Organizer Email</label>
            <input type="text" class="form-control" name="organizerEmail" value="${(auction.organizerEmail)!""}" placeholder="Organizer Email">
          </div>
          <div class="row">
                <div class='col-sm-6'>
                    <label for="datetimepicker">Ends At</label>
                    <input type='text' class="form-control" id='datetimepicker' value="${(auction.endDate)!""}" name="endsAt"/>
                </div>
            </div>
          <div class="form-group">
            <label for="auction_picture">Auction Picture</label>
            <input name="auction_picture" type="file" accept="image/*" onchange="loadFile(event)">
            <img class="img-responsive" src="images/auction.jpg" id="auction_picture_output"/>
            <script>
              var loadFile = function(event) {
                var output = document.getElementById('auction_picture_output');
                output.src = URL.createObjectURL(event.target.files[0]);
              };
            </script>
          </div>
          <button type="submit" class="btn btn-success">Save Auction</button>
        </form>
    </div>
    <#include "bootstrap-footer.ftl">
    <script type="text/javascript" src="/webjars/momentjs/min/moment.min.js"></script>
    <script type="text/javascript" src="/webjars/Eonasdan-bootstrap-datetimepicker/js/bootstrap-datetimepicker.min.js"></script>
    <script type="text/javascript">
        $(function () {
            $('#datetimepicker').datetimepicker();
        });
    </script>
  </body>
</html>