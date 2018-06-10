 $(document).ready(function(){
    	  
    	  var num = 1;
    	  var audioPath;
    	  var currQuestion		// cau hoi hien tai. cau lon// num in exercise question
    	  var subQuestion;    // tong so cau hoi nho   // question_number in exercise question detail
    	  var exerciseQuestionId;
    	  
    	  var paragraph;
    	  
    	  
    	  var exerciseId = $('#exercise-id').val();
    	  var exAlreadyDoId = $('#exalreadydo-id').val();
    	  var totalQuestion = $('#total-question').text();
    	  
    	  
    	  
    	  // when firs load the page will hide the previous button
    	  $('#btn-previous').hide();
    	  if(totalQuestion == 1) $('#btn-next').hide();
    	  initQuestion(num);
    	  
    	// when click on btn-repeat
    	  $('#btn-repeat').off('click').on('click',function(e){
              e.preventDefault();
             
              $("#jquery_jplayer_1").jPlayer('destroy');
              
              $("#jquery_jplayer_1").jPlayer({
		          ready: function (event) {

		            $(this).jPlayer("setMedia", {
		              
		            	mp3:audioPath
		            });
		          },
		          swfPath: "../assets/plugins/jplayer/swf",
		          supplied: "mp3",
		          wmode: "window",
		          useStateClassSkin: true,
		          autoBlur: false,
		          smoothPlayBar: true,
		          keyEnabled: true,
		          remainingDuration: true,
		          toggleDuration: true
		        });
          })
    	  
    	 
    	  
    	  // when click on btn-again
    	  $('#btn-again').off('click').on('click',function(e){
              e.preventDefault();
                
              $("#jquery_jplayer_1").jPlayer('destroy');
              
              
              
              initQuestion(num);
              
          })
    	  
    	  
    	  // when click the previous button
    	  $('#btn-previous').off('click').on('click',function(e){
              e.preventDefault();
              num --;
              $('#btn-next').show();
              
              if(num == 1)
            	  $('#btn-previous').hide();
              
              $("#jquery_jplayer_1").jPlayer('destroy');
              
              initQuestion(num);
              
          })

    	  // when click the next button
    	  $('#btn-next').off('click').on('click',function(e){
              e.preventDefault();
              
              $('#btn-previous').show();
              num ++;
            // check end num then next button will be hide
            if(num == totalQuestion)
            	$('#btn-next').hide();
              
              $("#jquery_jplayer_1").jPlayer('destroy');
              
              initQuestion(num);
              
              
          }) // end btn-next click event
    	  
          
          // when click on btn-check 
    	  $('#btn-check').off('click').on('click',function(e){
              e.preventDefault();
              // state = 1 is for the tapescript button
              getAnswer(1);
     
          }) // end btn-check click event
          
          
         // when click on btn-check 
    	  $('#btn-tapescript').off('click').on('click',function(e){
              e.preventDefault();
              console.log('into the tapescript button event');
              // state = 2 is for the tapescript button
              getAnswer(2);
     
           // show the conversation
              $('.conversation').empty();
              $('.conversation').append('<p class="conversation-content"></p>');
			  $('.conversation-content').html(paragraph).fadeIn("slow");
          }) // end btn-check click event
          
          
          
          
       // DO GET
      	function initQuestion(num){
      		$.ajax({
      			type : "GET",
      			url : "/get-question?num="+num+"&exerciseId="+exerciseId+"&exAlreadyDoId="+exAlreadyDoId,
      			success: function(result){
    				if(result.status == "Done"){
    				
    					 audioPath = "/upload/audio/"+ result.data.audio;
    				
    					// set mp3 for jplayer
    					$("#jquery_jplayer_1").jPlayer({
    				          ready: function (event) {

    				            $(this).jPlayer("setMedia", {
    				              
    				            	mp3:audioPath
    				            });
    				          },
    				          swfPath: "../assets/plugins/jplayer/swf",
    				          supplied: "mp3",
    				          wmode: "window",
    				          useStateClassSkin: true,
    				          autoBlur: false,
    				          smoothPlayBar: true,
    				          keyEnabled: true,
    				          remainingDuration: true,
    				          toggleDuration: true
    				        });
    					// end set mp3 for jplayer
    					
    					
    					// set current question number
    					currQuestion = result.data.num;
    					$('#current-question').text(currQuestion);
    					
    					// set sub question number for this question
    					subQuestion = result.data.subQuestion;
    					
    					//set exercise question id
    					exerciseQuestionId = result.data.id;
    							
    					// get paragraph
    					paragraph = result.data.paragraph;
    					
    					// remove a coversation-content p
    					$('.conversation').empty();
    					
    					
    					// append question
    					//-- root question is class : '.question-area'
    					// for each sub question. append new question.
    					
    					// clear all question
    					$('.question-area').empty();
    					
    					// the conversation and talk: each conversation has 3 sub question
    					// using ajax to get and show sub question
    					
    					$.ajax({
    		    			type : "GET",
    		    			url : "/get-answer?exerciseQuestionId="+exerciseQuestionId,
    		    			success: function(result1){
    		    				if(result.status == "Done"){
    		    					
    		    				$.each(result1.data, function(i, question){
    		    					
    		    					// append sub question
    		    					var subquestion_id = "question_"+i;	
    		    					$('.question-area').append('<div id="'+subquestion_id+'"></div>');
    		    					
    		    					// add the question-sentence
    	        					var question_sentence='<span class="question-sentence">Question '+currQuestion+'.'+(i+1)+': </span><span class="question-sentence">'+question.question+'</span>';
    	        					$('#'+subquestion_id).append(question_sentence);
    	        					// add 4 choice for the sentence
    	        					
    	        					$('#'+subquestion_id).append('<div class="choice_1"></div>');
    	        					$('#'+subquestion_id).append('<div class="choice_2"></div>');
    	        					$('#'+subquestion_id).append('<div class="choice_3"></div>');
    	        					$('#'+subquestion_id).append('<div class="choice_4"></div>'); 			
    	        					
    	        					// add choice element
    	        					appendChoice('#'+subquestion_id+' .choice_1','A',i,question.option1);
    	        					appendChoice('#'+subquestion_id+' .choice_2','B',i,question.option2);
    	        					appendChoice('#'+subquestion_id+' .choice_3','C',i,question.option3);
    	        					appendChoice('#'+subquestion_id+' .choice_4','D',i,question.option4);
    	        					
    		    			     });  // end loop
    		    					
    		    				
    		    				
    		    				}else{
    		  					console.log("Fail: ", result);
    		  				}
    		  			},
    		  			error : function(e) {
    		  				console.log("ERROR: ", e);
    		  			}
    		  		});  // end the 2nd $.ajax() // get question .
  		
    				}else{
    					console.log("Fail: ", result);
    				}
    			},
    			error : function(e) {
    				console.log("ERROR: ", e);
    			}
    		});
    	} // end ajaxGet function
    	  
   // append choice function 
    function appendChoice(selector, choice, i, sentence){
    	
    	// name of radio button is: name ="choice_answer_"+ cau hoi nho.
    		var answer_status = '<div class="answer-status"></div>';
    		var radio = '<input type="radio" name="choice_answer_'+i+'" value="'+choice+'"/>';
    		var answer = '<span class="text-primary">&nbsp;'+ choice +'.&nbsp;</span>';
    		var answer_content = '<span class="answer-content">'+sentence+'</span>';
    	
    		$(selector).append(answer_status,radio,answer,answer_content);
    		
    	}
    	  
    // function for get the answer
    // state = 1 is for the score button
    // state = 2 for tapescript button
    function getAnswer(state){
    	
    	// check all question is select the answer..            
        // count the number of radio button is checked on the page
          var total = $('input:radio:checked').length;
          if (total != subQuestion){
              alert("Bạn chưa trả lời hết các câu hỏi!.");
              return false;
          }
         
          $.ajax({
    			type : "GET",
    			url : "/get-answer?exerciseQuestionId="+exerciseQuestionId,
    			success: function(result){
    				if(result.status == "Done"){
    					
    				$.each(result.data, function(i, question){
    					
    					// get the correct answer for each sub question
      					var correctAnswer = question.correctAnswer;
      					
      					var selectValue = $('input[name="choice_answer_'+i+'"]:checked').val();
      					
      					// localeCompare :
      					// string a = string b -> return 0
      					// string a < string b -> return -1
      					// string a > string b -> return 1
      					
      					// check the selected answer is true or not. then show the icon to know it
      					// green icon is true. red icon is false
      					// both score and tapescript need this check
      					if(!selectValue.localeCompare(correctAnswer))
      						$('input[name="choice_answer_'+i+'"]:checked').parent().find('.answer-status').html('<i class="fa fa-check text-success" aria-hidden="true"></i>');
      					else $('input[name="choice_answer_'+i+'"]:checked').parent().find('.answer-status').html('<i class="fa fa-times text-danger" aria-hidden="true"></i>');
      					
      					// the below code for the tapescript button
      					// if state = 1 will return.
      					if(state == 2) {
	      					// set the correct answer with green icon on class answer-status
	      					$('input[name="choice_answer_'+i+'"][value="'+correctAnswer+'"]').parent().find('.answer-status').html('<i class="fa fa-check text-success" aria-hidden="true"></i>');

      					}
      					
    					
    			
    			     });  // end loop
    					
    				
    				
    				}else{
  					console.log("Fail: ", result);
  				}
  			},
  			error : function(e) {
  				console.log("ERROR: ", e);
  			}
  		});
    	
    }
    
    	  
    	  
      }); // end document.ready
