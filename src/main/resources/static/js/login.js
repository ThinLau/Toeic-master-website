

$(document).ready(function(){
	
	// confirm password
	  $('#register').prop('disabled', true);
	  $('#pass_hint').hide();
	  $('#confirm-pass').keyup(function () {
	      'use strict';
	      if (!$('#pass').val().localeCompare($(this).val())) {
	          $('#pass_hint').hide().html('');
	          this.setCustomValidity('');
	          $('#register').prop('disabled', false);
	      } else {
	          $('#pass_hint').show().html('Mật khẩu không khớp');
	          this.setCustomValidity('Mật khẩu không khớp');
	          $('#register').prop('disabled', true);
	      }
	  });
	  
	  
	// Toggle Function
	  $('.toggle').click(function(){
	    // Switches the Icon
	    $(this).children('i').toggleClass('fa-pencil');
	    // Switches the forms  
	    $('.form').animate({
	      height: "toggle",
	      'padding-top': 'toggle',
	      'padding-bottom': 'toggle',
	      opacity: "toggle"
	    }, "slow");

	  });
	  
	});