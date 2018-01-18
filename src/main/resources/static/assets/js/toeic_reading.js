
var textlist=[];
	var choicelist=[];
	var feedbacklist=[];
	var correctlist=[];
	var correctlist2=[];
	var textlist2=[];

	var choicelist2=[];
	var feedbacklist2=[];
	var correctlist2=[];
		var imagelist=[];
		var imagelist2=[];
	var audiolist=[];
	var audiolist2=[];
	var instructions="";
	var scriptlist=[];
	var scriptlist2=[];
	var score=0;
	var questionnumber=0;
	var answeredlist=[];
	var questionanswered1=0;
	var questionanswered2=0;
	var answered=0;
	var correct=0;
	var numberofchoices=4;
	var extractno=1;
	var partslist=[4];
	var completedlist=[4];
	
	var numberofquestions=partslist[0];
	var completedquestions=0;
	var extractslist=[0];
	var extractslist2=[1];
	
	

	function restart(){
		 choicelist=[];
	feedbacklist=[];
	 correctlist=[];
 correctlist2=[];
	 textlist2=[];

	 choicelist2=[];
	 feedbacklist2=[];
	 correctlist2=[];
		 imagelist=[];
		 imagelist2=[];
	 audiolist=[];
	 audiolist2=[];
	 instructions="";
 scriptlist=[];
	 scriptlist2=[];
	 score=0;
 questionnumber=0;
 extractno=1;
	completedquestions=0;
	 questionanswered=0;
	 answered=0;
	 correct=0;
	 testSetUp();
	}
	
	function reset(){
	
		for (i=0;i<gaplist.length;i++)
		{
		var z="q"+i;
		
		document.getElementById(z).value ='';
		}
		clearScores();
	}
	function testSetUp(){
	var txt="";
	
	
	
	
	
	if (window.XMLHttpRequest)
	  {
	  xhttp=new XMLHttpRequest();
	  }
	else
	  {
	  xhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	
	xhttp.open("GET",testfile,false);
	xhttp.send("");
	xmlDoc=xhttp.responseXML;
	

var rand=xmlDoc.getElementsByTagName("random")[0].childNodes[0].nodeValue;
	instructions=xmlDoc.getElementsByTagName("instructions")[0].childNodes[0].nodeValue;

	
	document.getElementById("extracttoeic7").innerHTML=xmlDoc.getElementsByTagName("extract1")[0].childNodes[0].nodeValue;
	
	var x=xmlDoc.getElementsByTagName("item");
	
	var qnum=0;

 for (i=0;i<x.length;i++)
 {
 textlist.push(x[i].getElementsByTagName("text")[0].childNodes[0].nodeValue)  ;	

 	
    for (j=0;j<4;j++)
	{
   
  choicelist.push(x[i].getElementsByTagName("choice")[j].childNodes[0].nodeValue)  ;

  var feedback=x[i].getElementsByTagName("choice")[j].getAttribute("feedback");
				
feedbacklist.push(feedback);

 }
  
  
  

	extractslist2.sort(function() {return 0.5 - Math.random()}) 
	
 showQuestion();


 }
		
	


 

}


function loadQuestion(){
	completedquestions=0;
	extractno++
	numberofquestions=partslist[extractno-1];
	
	for(n=0;n<extractno-1;n++) {
		completedquestions=completedquestions+partslist[n];
		
	}
	
	 showQuestion();
	
}
function showQuestion(){
	//z=extractslist[extractno-1];
	ex="extract"+(extractno);
	
document.getElementById("extracttoeic7").innerHTML=xmlDoc.getElementsByTagName(ex)[0].childNodes[0].nodeValue;	 
	
document.getElementById("instructions").innerHTML= instructions  ;	
document.getElementById("nextbutton").innerHTML ="";


// answeredlist=[];	

	
txt="<form id='form1' name='form1'>";
x=extractno-1;

y=completedquestions+partslist[x];

 for(n=completedquestions;n<y;) {
	  answeredlist.push(0);

txt=txt+  "<br>"+ (n+1) + "&nbsp;&nbsp;" + textlist[n]+ '<span id="result_' + n + '"><img src="../IELTS/blank.gif" style="border:0" alt="" /></span><span id="feedback'+n+'"></span>';
	txt=txt +"<p></p><div id='choices'>";
	

	
	 for(j=0;j<numberofchoices;) {
     
		txt=txt+ '<input type="radio" name="choices' + n + '" value="'+j+'" onClick="submitAnswer('+n+','+j+')">&nbsp;<a   class="achoice" onClick="submitAnswer('+n+','+j+')" id="'+n + '_' + j + '">'  + choicelist[(4*n)+j] + '</a> <br>';
		
		j++;

				 }
				n++
				
 }
				
				 txt=txt+'</form>';
				 

	 
document.getElementById("score").innerHTML='<span id="score">&nbsp;&nbsp;&nbsp;Score: '+score+ '/'+y +'</span>';
	
	document.getElementById("text").innerHTML=txt;
	
	//document.getElementById("buttons").innerHTML='<input type="submit" onClick="javascript:seeAnswers()" name="seeAnswers" id="seeAnswers" value="See Answers">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp  <input type="submit" onClick="javascript:checkAnswers()" name="checkAnswers" id="checkAnswers" value="Check Answers">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp    <input type="submit" onClick="javascript:nextQuestions()" name="reset" id="reset" value="Next">';
	
	
	
}


function submitAnswer(question,choicenumber) {
var choice='choices'+question;
var result='result_'+question;
var feed='feedback'+question;
var qid="questionanswered"+question;



setCheckedValue(document.forms['form1'].elements[choice], choicenumber);


	
	var last_char=choicenumber;
	
	var feedback3=feedbacklist[(question*numberofchoices)+1*last_char];
			

	
	
	if (feedback3=="Correct"){
		
	feedback3="  " +"<span id='green'>"+feedback3+"</span>";
	
	document.getElementById(result).innerHTML = '<img src="../IELTS/correct.gif" style="border:0" alt="Correct!" />';
	
	
	if ( answeredlist[question]==0){
		answeredlist[question]=1;
		score++;
	}
	}
		
		
		
	
	
	
	else
	{
		
		
	answeredlist[question]=1;
	
		 document.getElementById(result).innerHTML = '<img src="../IELTS/incorrect.gif" style="border:0" alt="Incorrect!" />';
		feedback3="  " +"<span id='red'>"+feedback3+"</span>";
		
		
	}
 
 
	
  document.getElementById("score").innerHTML = ' &nbsp;&nbsp;&nbsp;Score: ' + score + '/' + (completedquestions+numberofquestions) ; 
   document.getElementById(feed).innerHTML = feedback3;
   
y=0;
	x=completedquestions+numberofquestions;

 for(n=completedquestions;n<x;n++) {
		 
	if (answeredlist[n]==1){
	y++
	
	
	
	
		
		 }
}

	
	 if (y==numberofquestions){
		 if (extractno<1){
		
		document.getElementById("nextbutton").innerHTML ='<input type="submit" onClick="javascript:loadQuestion()" name="next" id="next" value="Next Questions" />';
	
		//questionnumber++;
		
		}else{
			document.getElementById("nextbutton").innerHTML ='<input type="submit" onClick="javascript:showResults()" name="next" id="next" value="Finish" />';
			
		}
	 }
  


  
  
}
function showResults(){
document.getElementById("instructions").innerHTML="";
document.getElementById("extracttoeic7").innerHTML="";

txt="";
	txt=txt + "<div id='results'>";
	
if ((score/(completedquestions+numberofquestions))>0.5){
	resultsmessage="<p>Congratulations! You have completed the test.</p><p> Do you wish to start again?</p>";
}else{
	resultsmessage="<p>You have completed the test.</p><p> Do you wish to start again?</p>";
}
	

     
		txt=txt+ '<label id="results"><p>'  + resultsmessage + '</label>';
		
				 
	

		//document.getElementById("extract").innerHTML="";
	
	document.getElementById("text").innerHTML=txt;
	document.getElementById("nextbutton").innerHTML ='<input type="submit" onClick="javascript:restart()" name="restart" id="restart" value="Restart" />';
}
function setCheckedValue(radioObj, newValue) {
	
	if(!radioObj)
		return;
	var radioLength = radioObj.length;
	if(radioLength == undefined) {
		radioObj.checked = (radioObj.value == newValue.toString());
		return;
	}
	for(var i = 0; i < radioLength; i++) {
		radioObj[i].checked = false;
		if(radioObj[i].value == newValue.toString()) {
			radioObj[i].checked = true;
		}
	}
}
