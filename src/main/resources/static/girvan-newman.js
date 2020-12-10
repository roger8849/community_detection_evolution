$(document).ready(function () {
  $('[data-toggle="tooltip"]').tooltip({
    placement : 'top'
  });


  $('#gnButton').click(function(){
    girvanNewman();
  });

  function girvanNewman() {
    var serviceUrl = "http://localhost:8080/community/betweenness/image/?confidence=0.6&support=0.1&count=20&shouldIgnoreNumbers=true&areMentionsTopics=false&loadFrom=TWITTER";

    var text = $('#text').val();
    if( text != null && text !== undefined && text !== '' ){
      serviceUrl +="&text=" + text;
      // $.ajax({
      //   url: serviceUrl,
      //   type: "GET",
      //   async: true,
      //   content: "image/png"
      // }).then(function (data) {
      //   $('#img-container').html('<img src="data:image/png;' + data + '" />');
      // });

      $('#img-container').html('<img src="' + serviceUrl + '" />');

    } else {
      alert("Search text is a required attribute.")
    }

  }




});



