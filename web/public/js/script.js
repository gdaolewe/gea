$(document).ready(function() {
  $("#play-pause-img").click(function() {
    if($(this).attr('src') == "img/playbutton.jpg") {
      $(this).attr("src","img/pausebutton.jpg");
    } else {
      $(this).attr("src","img/playbutton.jpg");
    }
  });
});
