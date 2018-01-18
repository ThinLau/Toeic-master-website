
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
	var partslist=[3,3,3,3,3,3,3,3,3,3];
	var completedlist=[3,6,9,12,15,18,21,24,27,30];
	
	var numberofquestions=partslist[0];
	var completedquestions=0;
	var extractslist=[0,1,2,3,4,5,6,7,8,9];
	var extractslist2=[1,2,3,4,5,6,7,8,9,10];
	var audiofile="";
	var scriptlist=[];
	

function showQuestion(){
	
	//z=extractslist[extractno-1];
	ex="extract"+(extractno);
audiofile="audio/"+audiolist[extractno-1];

	
	
document.getElementById("instructions").innerHTML= instructions  ;	
document.getElementById("scriptbutton").innerHTML ='<input type="submit" onClick="javascript:seeScript()" name="next" id="next" value="Script" />';
document.getElementById("nextbutton").innerHTML ='';


// answeredlist=[];	

	
txt="<form id='form1' name='form1'>";
x=extractno-1;

y=completedquestions+partslist[x];

 for(n=completedquestions;n<y;) {
	  answeredlist.push(0);

txt=txt+  "<br>"+ (n+1) + "&nbsp;&nbsp;" + textlist[n]+ '<br><span id="result_' + n + '"><img src="../IELTS/blank.gif" style="border:0" alt="" /></span><span id="feedback'+n+'"></span>';
	txt=txt +"<br><div id='choices'>";
	

	
	 for(j=0;j<numberofchoices;) {
     
		txt=txt+ '<input type="radio" name="choices' + n + '" value="'+j+'" onClick="submitAnswer('+n+','+j+')">&nbsp;<a   class="achoice" onClick="submitAnswer('+n+','+j+')" id="'+n + '_' + j + '">'  + choicelist[(4*n)+j] + '</a> <br>';
		
		j++;

				 }
				n++;
				
 }
				
				 txt=txt+'</form>';
				 

	 
document.getElementById("score").innerHTML='<span id="score">&nbsp;&nbsp;&nbsp;Score: '+score+ '/'+y +'</span>';
	
	document.getElementById("col1").innerHTML=txt;
	
	
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
	

var rand=xmlDoc.getElementsByTagName("random")[0].childNodes[0].nodeValue;
	instructions=xmlDoc.getElementsByTagName("instructions")[0].childNodes[0].nodeValue;

	
	
	
	var x=xmlDoc.getElementsByTagName("item");
	var audio=xmlDoc.getElementsByTagName("passage");
	
	
	var qnum=0;
	for (a=0;a<audio.length;a++)
 {
audiolist.push(audio[a].getElementsByTagName("audio")[0].childNodes[0].nodeValue)  ;	
scriptlist.push(audio[a].getElementsByTagName("script")[0].childNodes[0].nodeValue)  ;
 }

 for (i=0;i<x.length;i++)
 {
 textlist.push(x[i].getElementsByTagName("text")[0].childNodes[0].nodeValue)  ;	

 	for (j=0;j<4;j++)
	{
   
  choicelist.push(x[i].getElementsByTagName("choice")[j].childNodes[0].nodeValue)  ;

  var feedback=x[i].getElementsByTagName("choice")[j].getAttribute("feedback");
				
feedbacklist.push(feedback);

 }
  
 
	
  
  


	
 showQuestion();


 }
		
	


 

}


function loadQuestion(){
	completedquestions=0;
	extractno++;
	numberofquestions=partslist[extractno-1];
	
	for(n=0;n<extractno-1;n++) {
		completedquestions=completedquestions+partslist[n];
		
	}
	 hideScript();
	 showQuestion();
	
	
}
function seeScript(){
	document.getElementById("script1").innerHTML= scriptlist[extractno-1];
	document.getElementById("scriptbutton").innerHTML ='<input type="submit" onClick="javascript:hideScript()" name="next" id="next" value="Hide Script" />';
	
}
function hideScript(){
	document.getElementById("script1").innerHTML= "";
	document.getElementById("scriptbutton").innerHTML ='<input type="submit" onClick="javascript:seeScript()" name="next" id="next" value="Script" />';
	
}
function changeAudio(){

	
	$("#jquery_jplayer").jPlayer( "clearFile" );
		
	 $("#jquery_jplayer").jPlayer("setFile",audiofile).jPlayer("play"); 

	 
	
	 
 $("#jquery_jplayer").jPlayer("stop"); 

	
	
	
   return false; 
  
}



function submitAnswer(question,choicenumber) {
var choice='choices'+question;
var result='result_'+question;
var feed='feedback'+question;
var qid="questionanswered"+question;



setCheckedValue(document.forms['form1'].elements[choice], choicenumber);


	
	var last_char=choicenumber;
	
	var feedback3=feedbacklist[(question*numberofchoices)+last_char];
			

	
	
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
 
 
	
  document.getElementById("score").innerHTML = ' &nbsp;&nbsp;&nbsp;Score: ' + score + '/' + (completedquestions+numberofquestions) ; 
   document.getElementById(feed).innerHTML = feedback3;
   
y=0;
	x=completedquestions+numberofquestions;

 for(n=completedquestions;n<x;n++) {
		 
	if (answeredlist[n]===1){
	y++;
	
	
	
	
		
		 }
}

	
	 if (y===numberofquestions){
		 if (extractno<10){
		
		document.getElementById("scriptbutton").innerHTML ='<input type="submit" onClick="javascript:seeScript()" name="next" id="next" value="Script" />';
				document.getElementById("nextbutton").innerHTML ='<input type="submit" onClick="javascript:loadQuestion()" name="next" id="next" value="Next Questions" />';
	
		//questionnumber++;
		
		}else{
			document.getElementById("scriptbutton").innerHTML ='<input type="submit" onClick="javascript:seeScript()" name="next" id="next" value="Script" />';
			document.getElementById("nextbutton").innerHTML ='<input type="submit" onClick="javascript:showResults()" name="next" id="next" value="Finish" />';
			
		}
	 }
  


  
  
}
function showResults(){
document.getElementById("instructions").innerHTML="";


txt="";
	txt=txt + "<div id='results'>";
	
if ((score/(completedquestions+numberofquestions))>0.5){
	resultsmessage="<p>Congratulations! You have completed the test.</p><p> Do you wish to start again?</p>";
}else{
	resultsmessage="<p>You have completed the test.</p><p> Do you wish to start again?</p>";
}
	

     
		txt=txt+ '<label id="results"><p>'  + resultsmessage + '</label>';
		
				 
	


	
	document.getElementById("col1").innerHTML=txt;
	document.getElementById("nextbutton").innerHTML ='<input type="submit" onClick="javascript:restart()" name="restart" id="restart" value="Restart" />';
	document.getElementById("scriptbutton").innerHTML ='';
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
