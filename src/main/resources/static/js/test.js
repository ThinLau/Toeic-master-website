 $(document).ready(function(){
    	  
    	  var num = 1;
    	  var examId = $('#exam-id').val();
    	  var exAlreadyDoId = $('#exalreadydo-id').val();
    	  var subQuestion = 1;    	
    	  var countDownDate =  ($('#time-out').val()) *60*1000;   	  
    	  var now =  0;
    	  
    	  var isSubmit = false;
    	  
    	  // when first load the page will hide the previous button
    	  $('#btn-previous').hide();
    	  initQuestion(num);
    	    
    	  
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

              $("#jquery_jplayer_1").jPlayer('destroy');
              
              initQuestion(num);
              
          }) // end btn-next click event
          
          
           $('.showQuestionContent').off('click').on('click',function(e){
        	  var numInExam =  $(e.target).attr('data-id');
        	 $("#myModal .modal-title").html('Nội dung cho câu hỏi số: '+numInExam );
        	   
        	 // find examination_question has num_in_exam and exam_id
        	 $.ajax({
       			type : "GET",
       			url : "/get-question-by-num-in-exam?examId="+examId+"&numInExam="+numInExam,
       			success: function(result){
     				if(result.status == "Done"){
     					var part = result.data.part;
    					var examQuestionId = result.data.id;
    					 subQuestion = result.data.subQuestion;
    					var paragraph = result.data.paragraph;
    					var paragraph2 = result.data.paragraph2;

    					var audioPath = "/upload/audio/"+ result.data.audio;
    					var photoPath = "/upload/photo/"+ result.data.photo;

    					$("#jquery_jplayer_2").jPlayer('destroy');
    					switch(part){
    						case 1:  // part 1: photo
    							// display audio and image
    							$(".image2").css("display", "block");
    							$(".audio2").css("display", "block");
    							$(".conversation2").hide();
    									
    							$('.part-sumary2').text('Look at the picture and listen to the sentences in the Part 1 TOEIC Test.'+
    									' Choose the sentence that best describes the picture:');
    							
    							// set mp3 for jplayer
    							setAudioFile('jquery_jplayer_2',audioPath,2);

    						// set image
    							$('#exercise-image2').attr('src', photoPath);
    							
    							getQuestion(examQuestionId, 1, '.question-area2');
    							break;
    						case 2:  // part 2: question response
    							$(".image2").css("display", "none");
    							$(".audio2").css("display", "block");
    							$(".conversation2").hide();
    							
    							$('.part-sumary2').text('Listen to the question and the three responses in the Part 2 TOEIC Test.'+
    									' Choose the response that best answers the question:');
    							
    							// set mp3 for jplayer
    							setAudioFile('jquery_jplayer_2',audioPath,2);
    							
    							getQuestion(examQuestionId,2,'.question-area2');
    							break;
    						case 3: case 4:  // part 3 and part 4: short conversation and short talk
    							$(".image2").css("display", "none");
    							$(".audio2").css("display", "block");
    						
    							if(!isSubmit)
    								$(".conversation2").hide();
    							else {
    								$(".conversation2").show();
    								$('.conversation2').empty();
	       				            $('.conversation2').append('<p class="conversation-content"></p>');
	       							$('.conversation-content').html(paragraph);
    							}
    							
    							$("#question2").show();
    							$('.part-sumary2').text('Listen to the dialogue.'+
    									' Then read each question and choose the best answer:');
    							
    							// set mp3 for jplayer
    							setAudioFile('jquery_jplayer_2',audioPath,2);

    							getQuestion(examQuestionId,3,'.question-area2');
    							
    							break;

    						case 5:  // part 5: incomplete sentence
    							$(".image2").css("display", "none");
    							$(".audio2").css("display", "none");
    							$(".conversation2").hide();
    							$("#question2").show();
    							
    							$('.part-sumary2').text('Choose the word that best completes the sentence in the Part 5 TOEIC Reading Test:');
    							
    							getQuestion(examQuestionId,5, '.question-area2');
    							break;
    						case 6:  // part 6: text completion
    							$(".image2").css("display", "none");
    							$(".audio2").css("display", "none");
    							$(".conversation2").show();
    							$("#question2").show();
    							
    							$('.part-sumary2').text('Choose the word or phrase that best completes the blanks in the Part 6 TOEIC Reading Test:');
    							
    							$('.conversation2').empty();
    				             $('.conversation2').append('<p class="conversation-content"></p>');
    							 $('.conversation-content').html(paragraph);
    							
    							 getQuestion(examQuestionId,6,'.question-area2');
    							break;
    						case 7:  // part 7: single passage
    							$(".image2").css("display", "none");
    							$(".audio2").css("display", "none");
    							$(".conversation2").show();
    							$("#question2").show();
    							
    							$('.part-sumary2').text('Read the passage and choose the correct answer in the Part 7 TOEIC Reading Test:');
    							
    							$('.conversation2').empty();
    				             $('.conversation2').append('<p class="conversation-content"></p>');
    							 $('.conversation-content').html(paragraph);
    							
    							 getQuestion(examQuestionId,7,'.question-area2');
    							break;
    						case 8:  // part 8: double passage
    							$(".image2").css("display", "none");
    							$(".audio2").css("display", "none");
    							$(".conversation2").show();
    							$("#question2").show();
    							
    							$('.conversation2').empty();
    				                $('.conversation2').append('<p class="conversation-content"></p>');
    							
    							if(paragraph2 == null){
    			   					 $('.conversation-content').html(paragraph);
    							}else{ // double passage
    								 $('.conversation-content').html('<p style="font-weight: bold;">Passage 1:</p>' 
    										 + paragraph + '<hr/><p style="font-weight: bold;">Passage 2:</p>'
    										 + paragraph2);
    							}
    							
    							$('.part-sumary2').text('Read the passage and choose the correct answer in the Part 8 TOEIC Reading Test:');
    							getQuestion(examQuestionId,8,'.question-area2');
    							break;
    						
    					} // end switch
     					
     					
     					
     					
     					 // show modal
     		             $('#myModal').modal();
     				}
     			}
       			});
        	 
        	
              
          }) 
          
       
    	  
           // when click the finish button
    	  $('#btn-finish').off('click').on('click',function(e){
              e.preventDefault();
              
              if(!isSubmit) submit();
              
          }) // end btn-finish click event
          
       function submit(){
    		  var index = 1, n = 1;
              var trueQuestion = 0;
              var failQuestion = 0;
              isSubmit = true;
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
    					
    					var mess = ''+trueQuestion+'/' + (trueQuestion + failQuestion);
    					//swal("Kết quả bài thi!", mess+ "\n Nhấp vào số thứ tự câu hỏi trên bảng đáp án để xem đáp mỗi câu.");
    					swal("Kết quả bài thi: "+mess,"Nhấp vào số thứ tự câu hỏi trên bảng đáp án để xem đáp án của câu đó.");
    					$('#result').text('Kết quả: '+trueQuestion+'/' + (trueQuestion + failQuestion));
    					
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
    									
    							$('.part-sumary').text('Look at the picture and listen to the sentences in the Part 1 TOEIC Test.'+
    									' Choose the sentence that best describes the picture:');
    							
    							// set mp3 for jplayer
    							setAudioFile('jquery_jplayer_1',audioPath,1);

    						// set image
    							$('#exercise-image').attr('src', photoPath);
    							
    							getQuestion(examQuestionId, 1, '.question-area1');
    							break;
    						case 2:  // part 2: question response
    							$(".image").css("display", "none");
    							$(".audio").css("display", "block");
    							$(".conversation").hide();
    							
    							$('.part-sumary').text('Listen to the question and the three responses in the Part 2 TOEIC Test.'+
    									' Choose the response that best answers the question:');
    							
    							// set mp3 for jplayer
    							setAudioFile('jquery_jplayer_1',audioPath,1);
    							
    							getQuestion(examQuestionId,2,'.question-area1');
    							break;
    						case 3: case 4:  // part 3 and part 4: short conversation and short talk
    							console.log('into part 3: + num ='+ num);
    							$(".image").css("display", "none");
    							$(".audio").css("display", "block");
    							$(".conversation").css("display", "none");
    							$("#question").show();
    							$('.part-sumary').text('Listen to the dialogue.'+
    									' Then read each question and choose the best answer:');
    							
    							// set mp3 for jplayer
    							setAudioFile('jquery_jplayer_1',audioPath,1);

    							getQuestion(examQuestionId,3,'.question-area1');
    							
    							break;

    						case 5:  // part 5: incomplete sentence
    							$(".image").css("display", "none");
    							$(".audio").css("display", "none");
    							$(".conversation").hide();
    							$("#question").show();
    							
    							$('.part-sumary').text('Choose the word that best completes the sentence in the Part 5 TOEIC Reading Test:');
    							
    							getQuestion(examQuestionId,5,'.question-area1');
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
    							
    							 getQuestion(examQuestionId,6,'.question-area1');
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
    							
    							 getQuestion(examQuestionId,7,'.question-area1');
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
    							getQuestion(examQuestionId,8,'.question-area1');
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

    	  
    function getQuestion(examQuestionId,part, questionArea){
    	// clear all question
		$(questionArea).empty();
		
		$.ajax({
			type : "GET",
			url : "/get-exam-answer?examQuestionId="+examQuestionId,
			success: function(result1){
				if(result1.status == "Done"){
					
				$.each(result1.data, function(i, question){
					
					// append sub question
					var subquestion_id = "question_"+i;	
					$(questionArea).append('<div id="'+subquestion_id+'"></div>');
					var question_content ="";
					if(question.question != null)
						question_content = question.question;
					// add the question-sentence
					var question_number='<span class="question-sentence">Question '+question.numInExam+': </span>';
					$(questionArea + ' #'+subquestion_id).append(question_number);
					
					if(!isSubmit & (part == 1 || part == 2)) return;  // end function
					
					var question_sentence='<span class="question-sentence">'+question_content+'</span>';
					$(questionArea +' #'+subquestion_id).append(question_sentence);
					// add 4 choice for the sentence
					
					$(questionArea +' #'+subquestion_id).append('<div class="choice_A"></div>');
					$(questionArea +' #'+subquestion_id).append('<div class="choice_B"></div>');
					$(questionArea +' #'+subquestion_id).append('<div class="choice_C"></div>');
					$(questionArea +' #'+subquestion_id).append('<div class="choice_D"></div>'); 			
					
					// add choice element
					appendChoice(questionArea +' #'+subquestion_id+' .choice_A','A',i,question.option1);
					appendChoice(questionArea +' #'+subquestion_id+' .choice_B','B',i,question.option2);
					appendChoice(questionArea +' #'+subquestion_id+' .choice_C','C',i,question.option3);
					appendChoice(questionArea +' #'+subquestion_id+' .choice_D','D',i,question.option4);
					
					
					// set right answer icon. 
					if(isSubmit){
						var correctAnswer = question.correctAnswer;
						$(questionArea +' #'+subquestion_id+' .choice_'+correctAnswer + ' .status_'+correctAnswer).addClass('fa fa-check text-success');
					}
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
    		
    		var answer = '<i class="status_'+choice+'" aria-hidden="true"></i>&nbsp;&nbsp;<span class="text-primary" style="font-weight:bold;">&nbsp;'+ choice +'.&nbsp;</span>';
    		var answer_content = '<span class="answer-content">'+sentence+'</span>';
    	
    		$(selector).append(answer_status,answer,answer_content);
    		
    	}
    	// function: set audio file
		 function setAudioFile(selector, audioPath, index){    	
			// set mp3 for jplayer
				$("#"+selector).jPlayer({
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
			          toggleDuration: true,
			          cssSelectorAncestor: "#jp_container_"+index
			        });
				// end set mp3 for jplayer
		 
		 }
    	  
	 // Update the count down every 1 second
	    var x = setInterval(function() {
	    	// time + 1s
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
