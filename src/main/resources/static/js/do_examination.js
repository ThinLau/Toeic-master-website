 $(document).ready(function(){
    	  
    	  var num = 1;
    	  var examId = $('#exam-id').val();
    	  var exAlreadyDoId = $('#exalreadydo-id').val();
    	  var previousQuestion = 0;
    	  var currQuestion = 0;
    	  var isNext = true;
    	  var subQuestion = 1; 
    	  var sub  = 1;
    	  
    	  var countDownDate =  ($('#time-out').val()) *60*1000;
    	  
    	  var now =  0;
    	  
    	  var isSubmit = false;
    	  
    	  // when first load the page will hide the previous button
    	  $('#btn-previous').hide();
    	  initQuestion(num);
    	    
    	  
    	  // when click the previous button
    	  $('#btn-previous').off('click').on('click',function(e){
              e.preventDefault();
            
              isNext = false;
              num --;
              $('#btn-next').show();
	             
	          if(num == 1)
	            	$('#btn-previous').hide();
	              
	          $("#jquery_jplayer_1").jPlayer('destroy');
	          
              $.ajax({
        			type : "GET",
        			url : "/get-exam-question?num="+num+"&examId="+examId,
        			success: function(result){
      				if(result.status == "Done"){

      					sub = result.data.subQuestion;
      					
      	              initQuestion(num);
      				}}
        			});

              
          })

    	  // when click the next button
    	  $('#btn-next').off('click').on('click',function(e){
              e.preventDefault();
              
              isNext = true;
              
              $('#btn-previous').show();
              num ++;

              $("#jquery_jplayer_1").jPlayer('destroy');
              
              initQuestion(num);
              
          }) // end btn-next click event
    	  
           // when click the finish button
    	  $('#btn-finish').off('click').on('click',function(e){
              e.preventDefault();
              
              if(!isSubmit) submit();
              
          }) // end btn-finish click event
          
       function submit(){
    		  var index = 1, n = 1;
              var trueQuestion = 0;
              var failQuestion = 0;
          
              answer(index, n, trueQuestion, failQuestion);       
    	  }
          
       function answer(index, n, trueQuestion, failQuestion){
    		 
    		  $.ajax({
      			type : "GET",
      			url : "/get-exam-question?num="+index+"&examId="+examId,
      			success: function(result1){
    				if(result1.status == "Done"){
    					
    					 $.ajax({
    		      			type : "GET",
    		      			url : "/get-exam-answer?examQuestionId="+result1.data.id,
    		      			success: function(result){
    		      				if(result.status == "Done"){
    		      					
    		      				$.each(result.data, function(i, question){
    		      					// get the correct answer for each sub question
    		        					var correctAnswer = question.correctAnswer;
    		        					var tmp = n+ i;// current question
    		        					
    		        					var selectValue = $('input[name="answer_'+tmp+'"]:checked').val();

    		        					if(selectValue == undefined) selectValue="W"; //wrong answer
    		        					//console.log('index '+ index+' question: '+ tmp+ ' answer: '+ 
    		        						//	correctAnswer +' check value: '+selectValue );
    		        					
    		        					
    		        					if(!selectValue.localeCompare(correctAnswer)){ // it true
    		        							trueQuestion ++;
    		        							
    		        						}
    		        					else{ 
    		        						if(selectValue.localeCompare("W") == -1)  // neu select value != W. -1 is less than
    		        							$('#question_'+ tmp +' .status_'+selectValue).addClass('fa fa-times text-danger');
    		        						failQuestion ++;
    		        					
    		        					}
    		        					$('#question_'+ tmp +' .status_'+correctAnswer).addClass('fa fa-check text-success');
    		      			     });  // end loop
    		      					
    		      				answer(index+1,n + result1.data.subQuestion, trueQuestion, failQuestion);	
    		      				}
    		      			}
    		    		});
    		    		
    					
    				}  // end if
    				else {  // end all question
    					
    					//alert('Số câu đúng: '+trueQuestion+'/' + (trueQuestion + failQuestion));
    					$('#result').text(trueQuestion+'/' + (trueQuestion + failQuestion));
    					var examResult = Math.round(100*(trueQuestion / (trueQuestion + failQuestion)) * 100) / 100;
    					// update exam already do status
    					 $.ajax({
     		      			type : "GET",
     		      			url : "/update-exam-already-do-status?exAlreadyDoId="+exAlreadyDoId + "&examResult="+examResult,
     		      			success: function(result){
     		      				// 
     		      			}
    					 });
    					
    					 
    					 
    					clearInterval(x);
    				}
    					
    				
      			}
            });
           
    		  
       }
          
       // DO GET
      	function initQuestion(num){

    		 if(!isNext) currQuestion = currQuestion - sub - subQuestion;

      		$.ajax({
      			type : "GET",
      			url : "/get-exam-question?num="+num+"&examId="+examId,
      			success: function(result){
    				if(result.status == "Done"){
    				
    					var part = result.data.part;
    					var examQuestionId = result.data.id;
    					subQuestion = result.data.subQuestion;
    					var paragraph = result.data.paragraph;
    					var paragraph2 = result.data.paragraph2;

    					
    					var audioPath = "/upload/audio/"+ result.data.audio;
    					var photoPath = "/upload/photo/"+ result.data.photo;
    					
    				
    					switch(part){
    						case 1:  // part 1: photo
    							// display audio and image
    							$(".image").css("display", "block");
    							$(".audio").css("display", "block");
    							$(".conversation").hide();
    							$("#question").hide();
    									
    							$('.part-sumary').text('Look at the picture and listen to the sentences in the Part 1 TOEIC Test.'+
    									' Choose the sentence that best describes the picture:');
    							
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
        	
        					
        					// set image
        						$('#exercise-image').attr('src', photoPath);
    							
        						currQuestion++;
        						$('#current-question').show();
        						$('#current-question').text('Question '+ currQuestion);
    							break;
    						case 2:  // part 2: question response
    							$(".image").css("display", "none");
    							$(".audio").css("display", "block");
    							$(".conversation").hide();
    							$("#question").hide();
    							
    							$('.part-sumary').text('Listen to the question and the three responses in the Part 2 TOEIC Test.'+
    									' Choose the response that best answers the question:');
    							
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
    							
    	    					currQuestion++;
    	    					$('#current-question').show();
    	    					$('#current-question').text('Question '+ currQuestion);
    							break;
    						case 3: case 4:  // part 3 and part 4: short conversation and short talk
    							$(".image").css("display", "none");
    							$(".audio").css("display", "block");
    							$(".conversation").hide();
    							$("#question").show();
    							$('.part-sumary').text('Listen to the dialogue.'+
    									' Then read each question and choose the best answer:');
    							
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
    							
    	    					
    	    					getQuestion(examQuestionId);
    	    					$('#current-question').hide();
    							break;

    						case 5:  // part 5: incomplete sentence
    							$(".image").css("display", "none");
    							$(".audio").css("display", "none");
    							$(".conversation").hide();
    							$("#question").show();
    							
    							$('.part-sumary').text('Choose the word that best completes the sentence in the Part 5 TOEIC Reading Test:');
    							
    							getQuestion(examQuestionId);
    	    					$('#current-question').hide();
    							break;
    						case 6:  // part 6: text completion
    							$(".image").css("display", "none");
    							$(".audio").css("display", "none");
    							$(".conversation").show();
    							$("#question").show();
    							
    							$('.part-sumary').text('Choose the word or phrase that best completes the blanks in the Part 6 TOEIC Reading Test:');
    							
    							$('.conversation').empty();
    	    		             $('.conversation').append('<p class="conversation-content"></p>');
    	    					 $('.conversation-content').html(paragraph);
    							
    	    					 getQuestion(examQuestionId);
     	    					$('#current-question').hide();
    							break;
    						case 7:  // part 7: single passage
    							$(".image").css("display", "none");
    							$(".audio").css("display", "none");
    							$(".conversation").show();
    							$("#question").show();
    							
    							$('.part-sumary').text('Read the passage and choose the correct answer in the Part 7 TOEIC Reading Test:');
    							
    							$('.conversation').empty();
    	    		             $('.conversation').append('<p class="conversation-content"></p>');
    	    					 $('.conversation-content').html(paragraph);
    							
    	    					 getQuestion(examQuestionId);
     	    					$('#current-question').hide();
    							break;
    						case 8:  // part 8: double passage
    							$(".image").css("display", "none");
    							$(".audio").css("display", "none");
    							$(".conversation").show();
    							$("#question").show();
    							
    							$('.conversation').empty();
   	    		                $('.conversation').append('<p class="conversation-content"></p>');
    							
    							if(paragraph2 == null){
    		       					 $('.conversation-content').html(paragraph);
    	    					}else{ // double passage
    	    						 $('.conversation-content').html('<p style="font-weight: bold;">Passage 1:</p>' 
    	    								 + paragraph + '<hr/><p style="font-weight: bold;">Passage 2:</p>'
    	    								 + paragraph2);
    	    					}
    	    					
    							$('.part-sumary').text('Read the passage and choose the correct answer in the Part 8 TOEIC Reading Test:');
    							getQuestion(examQuestionId);
     	    					$('#current-question').hide();
    							break;
    						
    					}
    				}else{
    					console.log("Fail: ", result);
    				}
    			},
    			error : function(e) {
    				console.log("ERROR: ", e);
    			}
    		});
    	} // end ajaxGet function

    	  
    function getQuestion(examQuestionId){
    	// clear all question
		$('.question-area').empty();
		 console.log('current question in  get question 111: '+ currQuestion);
		$.ajax({
			type : "GET",
			url : "/get-exam-answer?examQuestionId="+examQuestionId,
			success: function(result1){
				if(result1.status == "Done"){
					
				$.each(result1.data, function(i, question){
					
					// append sub question
					var subquestion_id = "question_"+i;	
					$('.question-area').append('<div id="'+subquestion_id+'"></div>');
					console.log('current question in  get question 222: '+ currQuestion);
					currQuestion++;
					var question_content ="";
					if(question.question != null)
						question_content = question.question;
					// add the question-sentence
					var question_sentence='<span class="question-sentence">Question '+currQuestion+': </span><span class="question-sentence">'+question_content+'</span>';
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
    }
    	  
    
    // append choice function 
    function appendChoice(selector, choice, i, sentence){
    	
    	// name of radio button is: name ="choice_answer_"+ cau hoi nho.
    		var answer_status = '<div class="answer-status"></div>';
    		
    		var answer = '<span class="text-primary">&nbsp;'+ choice +'.&nbsp;</span>';
    		var answer_content = '<span class="answer-content">'+sentence+'</span>';
    	
    		$(selector).append(answer_status,answer,answer_content);
    		
    	}
    	  
	 // Update the count down every 1 second
	    var x = setInterval(function() {
	
	        // Get todays date and time
	        now = now + 1000;
	        
	        // Find the distance between now an the count down date
	        var distance = countDownDate - now;
	        
	        // Time calculations for days, hours, minutes and seconds
	        var days = Math.floor(distance / (1000 * 60 * 60 * 24));
	        var hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
	        var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
	        var seconds = Math.floor((distance % (1000 * 60)) / 1000);
	        
	        // Output the result in an element with id="demo"
	        document.getElementById("countdown").innerHTML =  hours + "h "
	        		+ minutes + "m " + seconds + "s ";
	        
	        // If the count down is over, write some text 
	        if (distance <= 0) {
	            clearInterval(x);
	            alert("Thời gian làm bài đã hết");
	            if(!isSubmit) submit();
	        }
	    }, 1000);
    	  
    	  
      }); // end document.ready
