$(document).ready(function() {
  $("#play-pause-img").click(function() {
    if($(this).attr('src') == "images/playbutton.jpg") {
      $(this).attr("src","images/pausebutton.jpg");
    } else {
      $(this).attr("src","images/playbutton.jpg");
    }
  });
});