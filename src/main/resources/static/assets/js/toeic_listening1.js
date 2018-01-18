
var textlist=[];
	var choicelist=[];
	var feedbacklist=[];
	var correctlist=[];
	var correctlist2=[];
	var textlistb=[];
var letterlist=["A","B","C","D"];	
	var choicelist2=[];
	var feedbacklistb=[];
	var correctlistb=[];
		var imagelist=[];
		var imagelistb=[];
	var audiolist=[];
	var audiolistb=[];
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
	var playlist=[];
	
	var numberofquestions=0;
	var completedquestions=0;
	var audiono=0;
	var audiofile="";
	var scriptlist=[];
	
function makerandomlists () {


var questions = numberofquestions;
var i = 0;
while (i<numberofquestions) {
var	x = Math.floor(Math.random()*questions);
var nextimage = imagelist[x];


		

	var choice1 = "";
			var  choice2 = "";
			var  choice3 = "";
			var  choice4 = "";
			
			var feedback1 = "";
			var  feedback2 = "";
			var  feedback3 = "";
			var  feedback4 = "";
			
			var audio1 = "";
			var  audio2 = "";
			var  audio3 = "";
			var  audio4 = "";
			
		
		var r=Math.floor(Math.random()*numberofchoices);
		
		
		if (r=== 0){
			choice1 = textlist[x*numberofchoices];
			choice2 = textlist[x*numberofchoices+1];
			choice3 = textlist[x*numberofchoices+2];
			choice4 = textlist[x*numberofchoices+3];
			
			
			
			
			feedback1 = feedbacklist[x*numberofchoices];
			feedback2 = feedbacklist[x*numberofchoices+1];
			feedback3 = feedbacklist[x*numberofchoices+2];
			feedback4 = feedbacklist[x*numberofchoices+3];
			
			audio1 = audiolist[x*numberofchoices];
			audio2 = audiolist[x*numberofchoices+1];
			audio3 = audiolist[x*numberofchoices+2];
			audio4 = audiolist[x*numberofchoices+3];
			
			
			
			
		}
		if (r===1){
			choice1 = textlist[x*numberofchoices+1];
			choice2 = textlist[x*numberofchoices];
			choice3 = textlist[x*numberofchoices+2];
			choice4 = textlist[x*numberofchoices+3];
			
			
			
			
			feedback1 = feedbacklist[x*numberofchoices+1];
			feedback2 = feedbacklist[x*numberofchoices];
			feedback3 = feedbacklist[x*numberofchoices+2];
			feedback4 = feedbacklist[x*numberofchoices+3];
			audio1 = audiolist[x*numberofchoices+1];
			audio2 = audiolist[x*numberofchoices];
			audio3 = audiolist[x*numberofchoices+2];
			audio4 = audiolist[x*numberofchoices+3];	
			
		}
		if (r===3){
			choice1 =textlist[x*numberofchoices+1];
			choice2 =textlist[x*numberofchoices+2];
			choice3 =textlist[x*numberofchoices + 3];
		choice4 = textlist[x*numberofchoices];
			
			
		
			feedback1 = feedbacklist[x*numberofchoices+1];
			feedback2 = feedbacklist[x*numberofchoices+2];
			feedback3 = feedbacklist[x*numberofchoices+3];
			feedback4 = feedbacklist[x*numberofchoices];	
			audio1 = audiolist[x*numberofchoices+1];
			audio2 = audiolist[x*numberofchoices+2];
			audio3 = audiolist[x*numberofchoices+3];
			audio4 = audiolist[x*numberofchoices];
			
		}
		if (r===2){
			choice1 =textlist[x*numberofchoices+1];
			choice2 =textlist[x*numberofchoices+2];
			choice3 =textlist[x*numberofchoices];
		choice4 = textlist[x*numberofchoices+3];
			
			
		
			feedback1 = feedbacklist[x*numberofchoices+1];
			feedback2 = feedbacklist[x*numberofchoices+2];
			feedback3 = feedbacklist[x*numberofchoices];
			feedback4 = feedbacklist[x*numberofchoices+3];	
			audio1 = audiolist[x*numberofchoices+1];
			audio2 = audiolist[x*numberofchoices+2];
			audio3 = audiolist[x*numberofchoices];
			audio4 = audiolist[x*numberofchoices+3];
			
		}
		

imagelistb.push(nextimage);
		
	textlistb.push(choice1);
		textlistb.push(choice2);
			textlistb.push(choice3);
			textlistb.push(choice4);
			feedbacklistb.push(feedback1);
			feedbacklistb.push(feedback2);
			feedbacklistb.push(feedback3);
			feedbacklistb.push(feedback4);
			audiolistb.push(audio1);
			audiolistb.push(audio2);
			audiolistb.push(audio3);
			audiolistb.push(audio4);
		
		
		// deletes selected item from list of available question
		imagelist.splice(x, 1);
		
		
		textlist.splice(x*numberofchoices, numberofchoices);
		feedbacklist.splice(x*numberofchoices, numberofchoices);
		audiolist.splice(x*numberofchoices, numberofchoices);
		
		i++;
		questions = questions-1;
}
	
 showQuestion();	
}
function showQuestion(){
	
document.getElementById("instructions").innerHTML= instructions  ;	
document.getElementById("scriptbutton").innerHTML ='<input type="submit" onClick="javascript:seeScript()" name="next" id="next" value="Script" />';
document.getElementById("nextbutton").innerHTML ='';


// answeredlist=[];	
txt="";
document.getElementById("centreposition").innerHTML='<span id="col1"></span><span id="script3"></span><span id="image"></span>';
document.getElementById("col1").innerHTML="";	
//txt=txt + '<img src="images/' + imagelistb[questionnumber] + '" alt="TOEIC photograph" align="top" />';	
txt=txt + "<form id='form1' name='form1' STYLE='margin: 0px; padding: 0px;'>";

n = questionnumber;
txt=txt+  'Question '+ (n+1) + '&nbsp;&nbsp;<br><span id="result_' + n + '"><img src="../IELTS/blank.gif" style="border:0" alt="" /></span><span id="feedback'+n+'"></span>';
	txt=txt +"<br><div id='choices'>";


	
	 for(j=0;j<numberofchoices;) {
     
		txt=txt+ '<input type="radio" name="choices' + n + '" value="'+j+'" onClick="submitAnswer('+n+','+j+')"><label id="label_' + n + '_' + j + '" for="answer_' + n + '_' + j + '">&nbsp;<a   class="achoice" onClick="submitAnswer('+n+','+j+')" id="'+n + '_' + j + '">'  + choicelist[(4*n)+j] + '</a></label> <br>';
		
		j++;

			 
				
 }
				
				 txt=txt+'</form>';
			document.getElementById("col1").innerHTML=txt;		 
document.getElementById("image").innerHTML='<img src="images/' + imagelistb[questionnumber] + '" alt="TOEIC photograph" align="top" />';	
	 
document.getElementById("score").innerHTML='<span id="score">&nbsp;&nbsp;&nbsp;Score: '+score+ '/'+ n +'</span>';
	
	
	
	
	changeAudio();	
	
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
	

	instructions=xmlDoc.getElementsByTagName("instructions")[0].childNodes[0].nodeValue;

	
	
	
	var x=xmlDoc.getElementsByTagName("item");
	
	
	
	numberofquestions = x.length;

 for (i=0;i<x.length;i++)
 {
answeredlist.push(0);
  imagelist.push(x[i].getElementsByTagName("img")[0].childNodes[0].nodeValue)  ;	

 	for (j=0;j<4;j++)
	{
   
  choicelist.push(x[i].getElementsByTagName("choice")[j].childNodes[0].nodeValue)  ;
  audiolist.push(x[i].getElementsByTagName("choice")[j].getAttribute("audio")); 
  textlist.push(x[i].getElementsByTagName("choice")[j].getAttribute("text")); 

  var feedback=x[i].getElementsByTagName("choice")[j].getAttribute("feedback");
				
feedbacklist.push(feedback);

 }
  
 
	
  
  


	



 }
makerandomlists ();		
 //showQuestion();	


 

}


function loadQuestion(){
		questionnumber ++ ;
	 showQuestion();
	
}
function seeScript(){
	document.getElementById("script3").innerHTML= "<BR />" + letterlist[0] + ". " + textlistb [4*questionnumber] + "<BR />" + letterlist[1] + ". " + textlistb [(4*questionnumber)+1]  + "<BR />" + letterlist[2] + ". " + textlistb [(4*questionnumber)+2] + "<BR />" + letterlist[3] + ". " + textlistb [(4*questionnumber)+3]+ "<BR />"; 
	document.getElementById("scriptbutton").innerHTML ='<input type="submit" onClick="javascript:hideScript()" name="next" id="next" value="Hide Script" />';
	
}
function hideScript(){
	document.getElementById("script3").innerHTML= "";
	document.getElementById("scriptbutton").innerHTML ='<input type="submit" onClick="javascript:seeScript()" name="next" id="next" value="Script" />';
	
}
function changeAudio(){
hideScript();
	audiono=0;

playlist=[];
Questionfile="audio/Question" + (questionnumber+1) + ".mp3";
playlist.push(Questionfile);
 for (i=0;i<4;i++){
audioitem="audio/" + letterlist[i] + ".mp3";
playlist.push(audioitem);		
audioitem="audio/" + audiolistb[(4*questionnumber)+i];
playlist.push(audioitem);
	}
audiofile = playlist[0];
	
	$("#jquery_jplayer").jPlayer( "clearFile" );
		
	$("#jquery_jplayer").jPlayer("setFile",playlist[audiono]).jPlayer("stop"); 


	
	
	
   return false; 
  
}



function submitAnswer(question,choicenumber) {
var choice='choices'+question;
var result='result_'+question;
var feed='feedback'+question;
var qid="questionanswered"+question;



setCheckedValue(document.forms['form1'].elements[choice], choicenumber);


	
	var last_char=choicenumber;
	
	var feedback3=feedbacklistb[(question*numberofchoices)+last_char];
			

	
	
	if (feedback3==="Correct"){
		
	feedback3="  " +"<span id='green'>"+feedback3+"</span>";
	
	document.getElementById(result).innerHTML = '<img src="../IELTS/correct.gif" style="border:0" alt="Correct!" />';
	
	
	if ( answeredlist[question]===0){
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
 
 
	
  //document.getElementById("score").innerHTML = ' &nbsp;&nbsp;&nbsp;Score: ' + score + '/' + (numberofquestions) ; 
   document.getElementById(feed).innerHTML = feedback3;
   



	 if (questionnumber < numberofquestions-1){
		
		
		document.getElementById("scriptbutton").innerHTML ='<input type="submit" onClick="javascript:seeScript()" name="next" id="next" value="Script" />';
				document.getElementById("nextbutton").innerHTML ='<input type="submit" onClick="javascript:loadQuestion()" name="next" id="next" value="Next" />';
	
		//questionnumber++;
		
		}else{
			document.getElementById("scriptbutton").innerHTML ='<input type="submit" onClick="javascript:seeScript()" name="next" id="next" value="Script" />';
			document.getElementById("nextbutton").innerHTML ='<input type="submit" onClick="javascript:showResults()" name="next" id="next" value="Finish" />';
			
		
	 }
  


  
  
}
function showResults(){
document.getElementById("instructions").innerHTML="";


txt="";
	txt=txt + "<div id='results'>";
	
if ((score/numberofquestions)>0.5){
	resultsmessage="<p>Congratulations! You have completed the test.</p><p> Do you wish to start again?</p>";
}else{
	resultsmessage="<p>You have completed the test.</p><p> Do you wish to start again?</p>";
}
	

     
		txt=txt+ '<label id="results"><p>'  + resultsmessage + '</label>';
		
				 
	


	
	document.getElementById("col1").innerHTML=txt;
	document.getElementById("nextbutton").innerHTML ='<input type="submit" onClick="javascript:restart()" name="restart" id="restart" value="Restart" />';
	document.getElementById("scriptbutton").innerHTML ='';
	document.getElementById("script3").innerHTML ='';
}

function restart(){
		window.location.href=window.location.href;
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
