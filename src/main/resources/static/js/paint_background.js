$(document).ready(function(){
	
	$('#btn-next').off('click').on('click',function(e){
		e.preventDefault();
		changeColor();
	});
		
	var numExamQuestion = 1;
	var subExamQuestionDetail = 0;
	
	var subQuestion;
		
	var i;
	function changeColor() {
		
		if(numExamQuestion < 10){
			
			subQuestion = parseInt($("#ques_"+ numExamQuestion).val());
			
			subExamQuestionDetail = subExamQuestionDetail + subQuestion; 
			console.log(subExamQuestionDetail);
			
			for(i = (subExamQuestionDetail - subQuestion); i <= subExamQuestionDetail; i++){
		    	 $("#question_" + i).css("background-color", "yellow");	
		    	 $("#question_" + (i - subQuestion )).css("background-color", "white");	
			}
		}
		
		numExamQuestion++;
		
		/*if(numExamQuestion < 5) {
			 numExamQuestion = numExamQuestion + temp; 
		     console.log(numExamQuestion);
			 			 
			 $("#question_" + numExamQuestion).css("background-color", "yellow");		   
		     $("#question_" + (numExamQuestion-1)).css("background-color", "white");
		    	    
		} else if (numExamQuestion < 11){
			 temp = 3;
			 numExamQuestion = numExamQuestion + temp; 
		     console.log(numExamQuestion);
			 
		     for(i = (numExamQuestion - temp); i <= numExamQuestion; i++){
		    	 $("#question_" + i).css("background-color", "yellow");	
		    	 $("#question_" + (i - temp )).css("background-color", "white");	
		    	 
			 }  else if(numExamQuestion < 15){
				 temp = 1;
				 numExamQuestion = numExamQuestion + temp; 
			     console.log(numExamQuestion);
			     $("#question_" + i).css("background-color", "yellow");	
		    	 $("#question_" + (i - temp )).css("background-color", "white");
			 }
		    
		} */   
		
	   /*if(numExamQuestion > 0){
			t = t + temp;
		    var col = $("#question_" + numExamQuestion);
		    col.style.backgroundColor = colors;
		   
		    var colb = $("#question_" + (t-1));      	    
		    colb.style.backgroundColor = color;	 
	    	    
	    if(t > 3){
	    	t2 = t + 1;
	    	t3 = t + 2;
	    	
	    	var col = document.getElementById("question_" + t);
		    col.style.backgroundColor = colors;
	    	
		    col = document.getElementById("question_" + t2);      		    
		    col.style.backgroundColor = colors;	 
		    
	    	col = document.getElementById("question_" + t3);      		    
		    col.style.backgroundColor = colors;
		    
		    col = document.getElementById("question_" + (t-1));      		    
		    col.style.backgroundColor = color;
		    
		    col = document.getElementById("question_" + (t-2));      		    
		    col.style.backgroundColor = color;	  
		    
		    col = document.getElementById("question_" + (t-3));      		    
		    col.style.backgroundColor = color;	  
		  temp = 3;
	    } 
	    if((t) > 9){
	    	var col = document.getElementById("question_" + t);
		    col.style.backgroundColor = colors;
		    
		    col = document.getElementById("question_" + (t+1));      		    
		    col.style.backgroundColor = color;	 
		    
	    	col = document.getElementById("question_" + (t+2));      		    
		    col.style.backgroundColor = color;
		    temp = 1;
	    } */
	   /*  if(t > 3){
	    		    	  	    	    
	    	console.log(t);
	    	    
	    	var col = document.getElementById("question_" + t);
		    col.style.backgroundColor = colors;
		    
		    aa = $("#ques_" + t).val();	
		    
		    
	    	col = document.getElementById("question_" + (t-aa-1));      		    
		    col.style.backgroundColor = color;
	 	  
		    temp = parseInt(aa);
		   
		    
		    
		    
		    console.log(aa + " xz");
		    
	    } */
	    
	   }
	
});