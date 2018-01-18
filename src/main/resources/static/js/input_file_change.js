$(document).ready(function(){
	
	$('#photo-file').on('change', function(ev) {
	    var f = ev.target.files[0];
	    var fr = new FileReader();
	    
	    fr.onload = function(ev2) {
	        console.dir(ev2);
	        $('#image').attr('src', ev2.target.result);
	    };
	    
	    fr.readAsDataURL(f);
	});

	var $audio = $('#audio');
	$('#audio-file').on('change', function(e) {
	  var target = e.currentTarget;
	  var file = target.files[0];
	  var reader = new FileReader();
	  
	  console.log($audio[0]);
	   if (target.files && file) {
	        var reader = new FileReader();
	        reader.onload = function (e) {
	            $audio.attr('src', e.target.result);
	            $audio.play();
	        }
	        reader.readAsDataURL(file);
	    }
	});
	
	 var image = document.getElementById("image");
	    function updateSrc() {
	        image.src = image.src.split("?")[0] + "?" + new Date().getTime();

	    }
	    setInterval(updateSrc, 500);
	
});