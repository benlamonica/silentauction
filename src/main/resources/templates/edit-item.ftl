<!DOCTYPE html>
<html lang="en">
  <head>
    <#include "bootstrap-header.ftl">
    <title>Silent Auction</title>
  </head>
  <body>
    <div class="container">
        <#include "nav.ftl">
        <form action="edit-item.html" method="POST" enctype="multipart/form-data">
          <input type="hidden" name="id" value="${item.id}"/>
          <div class="form-group">
            <label for="item_name">Item Name</label>
            <input type="text" class="form-control" name="name" value="${(item.name)!""}" placeholder="Item Name">
          </div>
          <div class="form-group">
            <label for="description">Description</label>
            <textarea class="form-control" name="description">${item.description!""}</textarea>
          </div>
          <div class="form-group">
            <label for="minimum_bid">Minimum Bid</label>
            <input type="text" class="form-control" name="minimum_bid" value="${(item.minimumBid)!"0.00"}"/>
          </div>
          <div class="form-group">
            <label for="donor">Donor</label>
            <input type="text" class="form-control" name="donor" value="${(item.donor)!""}"/>
          </div>
          <div class="form-group">
            <label for="item_picture">Item Picture</label>
            <input name="item_picture" type="file" accept="image/*" onchange="loadFile(event)">
            <img class="img-responsive" src="images/${item.id}.jpg" id="item_picture_output"/>
            <script>
              var loadFile = function(event) {
                var output = document.getElementById('item_picture_output');
                output.src = URL.createObjectURL(event.target.files[0]);
              };
            </script>
          </div>
          <button type="submit" class="btn btn-success">${saveText} Item</button>
        </form>
    </div>
    <#include "bootstrap-footer.ftl">
  </body>
</html>