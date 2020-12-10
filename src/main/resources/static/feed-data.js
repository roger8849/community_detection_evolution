$(document).ready(function () {
  $('[data-toggle="tooltip"]').tooltip({
    placement : 'top'
  });

  toogleButtons('stop');
  $('#feedbtn').click(function () {
    toogleButtons('feed');
  });

  $('#stopbtn').click(function(){
    toogleButtons('stop');
  });

  function toogleButtons(buttonName){
    var serviceUrl = "http://localhost:8080/twitter/";
    if(buttonName === 'feed'){
      var text = $('#text').val();
      if( text != null && text != undefined && text !== '' ){
        try{
          // serviceUrl += "feed-data/?text=" + text;
          // serviceUrl = encodeURI(serviceUrl);
          serviceUrl += "feed-data/"
          callService(serviceUrl, text);
          $('#stopbtn').removeAttr('disabled');
          $('#feedbtn').attr('disabled', 'disabled');
        } catch (e) {
          alert("Couldn't start feed please try again");
        }

      } else {
        alert("Search terms can't be null");
      }
    } else {
      serviceUrl += "stop-feed";
      try{
        callService(serviceUrl, "");
        $('#stopbtn').attr('disabled', 'disabled');
        $('#feedbtn').removeAttr('disabled');
      } catch (e) {
        alert("Couldn't stop feed please try again");
      }
    }
  }

  function callService(serviceUrl, value) {
    $.ajax({
      url: serviceUrl,
      type: "POST",
      data: jQuery.param({text: value}),
      async: true
    }).then(function (data) {
    });
  }

});



