$(document).ready(function(){
	
	var currQuestion = $('#currQuestion').val();  
	var index = 1;
	//add children element for sub_question_1
	addSubQuestionHTMLElement('sub_question_1',currQuestion,index);
	
	$('#btn-add-sub-question').off('click').on('click',function(e){
		e.preventDefault();
		
		$('#sub_question_'+index).after('<div class="question-border" id ="sub_question_'+(index+1)+'"> </div>');

		index ++;
		currQuestion++;
		addSubQuestionHTMLElement('sub_question_'+index,currQuestion,index);
		
	});
	
	function addSubQuestionHTMLElement(selector, currQuestion, index){
		
		var header = '<div  style="margin-bottom: 15px;" class="row"><div class="col-md-2"><span class ="question-number text-primary"  style="font-size: 22px;">Câu '+currQuestion +': </span></div></div>';
		var choices = '<div class="choices"></div>'

		$('#'+selector).append(header,choices);
		
		// append 4 choices
		$('#'+selector + ' .choices').append('<div class="answer row choice_A"></div>');
		$('#'+selector + ' .choices').append('<div class="answer row choice_B"></div>');
		$('#'+selector + ' .choices').append('<div class="answer row choice_C"></div>');
		$('#'+selector + ' .choices').append('<div class="answer row choice_D"></div>');
		

		addChoiceForSubQuestion(selector + ' .choices .choice_A',1,index,'A');
		addChoiceForSubQuestion(selector + ' .choices .choice_B',2,index,'B');
		addChoiceForSubQuestion(selector + ' .choices .choice_C',3,index,'C');
		addChoiceForSubQuestion(selector + ' .choices .choice_D',4,index,'D');
      
	}  // fuction apend subquestion
	
	function addChoiceForSubQuestion(selector, option,sub, value){
		 
		var choice = '<div class="col-md-2" ><label class="correct-answer-radio" >'+value+'.'+
         '<input type="radio" checked="checked" name ="radio_question_'+sub+'" value="'+value+'"/>'+
         '<span class="checkmark"></span></label></div><div class="col-md-10">'+
         '<input type="text" name="option'+option+'_'+sub+'" class="form-control" placeholder="Nhập đáp án gợi ý"/></div>'
		
         $('#'+selector).append(choice);
         
	}
	
	
	
});











