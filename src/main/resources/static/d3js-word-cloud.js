var communityArray;
var members;
var words = [];
var compressedTestWords = [];
var communityNames = [];
$(document).ready(function () {
  // Calling service that gets communities
  initWords();

  function initCompressedWithWords() {
    $.each(words, function (index, value) {
      var a = new Object();
      a.text = value;
      a.size = 1;
      compressedTestWords.push(a);
    });
  }

  function initWords() {
    words = ["Calculating", "Communities", "please", "wait"];
    communityNames.push("Calculating communities please wait.");
    initCompressedWithWords();
  }

  function getServiceUrl() {
    var urlString = "http://localhost:8080/";
    var path = "community/topic/?";
    shouldIgnoreNumbers = shouldIgnoreNumbers === null ? "false" : "true";
    urlString += path + "text=" + twitterSearchText + "&confidence="
        + confidence + "&support=" + support + "&shouldIgnoreNumbers="
        + shouldIgnoreNumbers + "&areMentionsTopics=" + areMentionsTopics + "&loadFrom=" + loadFrom;
    return urlString;
  };

  var serviceUrl = getServiceUrl();
  console.log("Service url: " + serviceUrl);

  $.ajax({
    url: serviceUrl,
    method: "GET",
    async: true
  }).then(function (data) {
    communityArray = $.merge([], data);
    words = [];
    communityNames = [];
    compressedTestWords = [];

    if (communityArray.length === 0) {
      words = ["Couldn't", "find", "communities."];
      communityNames = [];
      communityNames.push("Couldn't find communities.");
      initCompressedWithWords();
    } else {
      communityNames = [];
      $.each(communityArray, function (key, value) {
        members = $.merge([], value.members);
        communityNames.push(value.communityName);
      });
      mapMembersToWords(members);
    }

    //Start cycling through the demo data
    showNewWords(myWordCloud);
    showCommunityNames();
  });

  var getUrlParameter = function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
      sParameterName = sURLVariables[i].split('=');

      if (sParameterName[0] === sParam) {
        return sParameterName[1] === undefined ? true : sParameterName[1];
      }
    }
  };

  var height = $(window).height(), width = $("#chart").width();

  function mapMembersToWords(newMembers) {
    $.each(newMembers, function (key, value) {
      var a = new Object();
      a.text = value.screenName;
      a.size = value.popularityIndex;
      compressedTestWords.push(a);

      word = [value.screenName, value.popularityIndex];
      words.push(a.text);
    });
  }

  $("#hashTags").height(height).width($("#text-container").width());
  var lastClassindex = 0;
  showCommunityNames();

  function showCommunityNames() {
    //Put Project Info in beetween hashtags
    if (Math.floor(Math.random() * 20) === 10) {
      var dispInfo = ["<span style='color:#a94442 !important; font-size:18px'>Project Author - <a href='https://github.com/roger8849'>@roger8849</a></span>",
        "<span style='color:#a94442 !important; font-size:18px'><a href='mailto:roger8849@gmail.com'>Email me to Fork this project.</a></span>",
        "<span style='color:#a94442 !important; font-size:18px'>Community detection on twitter.</span>"];
      communityNames = communityNames.concat(dispInfo);
    }

    for (var i = 0; i < communityNames.length; i++) {
      $("#hashTags").append(
          "<tr class='" + returnTextClass(lastClassindex) + "' id='" + i
          + "' ><td><b><p class=' text-" + returnTextClass(lastClassindex + 2)
          + "'>"
          + communityNames[i] + "</p></b></p></td></tr>");
      lastClassindex++;
    }

    $('#hashTags').animate({scrollTop: $('#hashTags').prop("scrollHeight")},
        2000);
    lastClassindex = communityNames.length;
    $('#hashTags tr').click(function () {
      console.log("index jquery: " + this.id);
      console.log("community array " + communityArray[this.id]);
      var membersOnClick = communityArray[this.id].members;
      words = [];
      compressedTestWords = [];
      mapMembersToWords(membersOnClick);
      //Start cycling through the demo data
      showNewWords(myWordCloud);
    });
  }

  function returnTextClass(index) {
    var cssClass = ["success", "info", "warning", "danger", "primary"];
    var ind = index % 5;
    return cssClass[ind];
  }

  // Encapsulate the word cloud functionality
  function wordCloud(selector) {

    var fill = d3.scale.category20();

    //Construct the word cloud's SVG element
    var svg = d3.select(selector).append("svg")
    .attr("width", width)
    .attr("height", height)
    .append("g")
    .attr("transform", "translate(" + (width / 2) + "," + (height / 2) + ")");

    //Draw the word cloud
    function draw(words) {
      var cloud = svg.selectAll("g text")
      .data(words, function (d) {
        return d.text;
      })

      //Entering words
      cloud.enter()
      .append("text")
      .style("font-family", "Impact")
      .style("fill", function (d, i) {
        return fill(i);
      })
      .attr("text-anchor", "middle")
      .attr('font-size', 1)
      .text(function (d) {
        return d.text;
      });

      //Entering and existing words
      cloud
      .transition()
      .duration(600)
      .style("font-size", function (d) {
        return d.size + "px";
      })
      .attr("transform", function (d) {
        return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
      })
      .style("fill-opacity", 1);

      //Exiting words
      cloud.exit()
      .transition()
      .duration(200)
      .style('fill-opacity', 1e-6)
      .attr('font-size', 1)
      .remove();
    }

    function returnRotation() {
      var angle = [0, -90, -60, -45, -30, 0, 30, 45, 60, 90];
      var index = Math.floor(Math.random() * 10);
      return angle[index];
    }

    //Use the module pattern to encapsulate the visualisation code. We'll
    // expose only the parts that need to be public.
    return {

      //Recompute the word cloud for a new set of words. This method will
      // asycnhronously call draw when the layout has been computed.
      //The outside world will need to call this function, so make it part
      // of the wordCloud return value.
      update: function (words) {

        var maxSize = d3.max(compressedTestWords, function (d) {
          return d.size
        });
        //Define Pixel of Text
        var pixScale = d3.scale.linear()
        .domain([0, maxSize])
        .range([10, 80]);

        d3.layout.cloud().size([(width - 50), (height - 20)])
        .words(words)
        .padding(5)
        .rotate(function () {
          return ~~(Math.random() * 2) * returnRotation();
        })
        .font("Impact")
        .fontSize(function (d) {
          return Math.floor(pixScale(d.size));
        })
        .on("end", draw)
        .start();
      }
    }

  }

  //This method tells the word cloud to redraw with a new set of words.
  //In reality the new words would probably come from a server request,
  // user input or some other source.
  function showNewWords(vis) {
    vis.update(compressedTestWords);
  }

  //Create a new instance of the word cloud visualisation.
  var myWordCloud = wordCloud('body');

  //Start cycling through the demo data
  showNewWords(myWordCloud);

});
