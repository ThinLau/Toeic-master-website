$(document).ready(function(){
	var currentQuestion = 1;  
	$('.question-number').text('Câu '+ currentQuestion+ ': ');
	
	$('#btn-save').off('click').on('click',function(e){
		e.preventDefault();
	
		currentQuestion ++;
		
		// set question-number
		//$('.question-number').text('Câu '+ currentQuestion+ ': ');
		
	});
	
	
});