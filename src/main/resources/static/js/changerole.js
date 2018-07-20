$(document).ready(function(){

	$('.chrole').off('click').on('click',function(e){
        e.preventDefault();
        
        var row = $(this).closest("tr");    // Find the row
        var value = row.find("#userid").val();
        var roleval = row.find("#roleid").val();
        
        console.log(roleval);
        console.log(value);           	
        $('#user-id').val(value);	
        
        if(roleval == 1)
        	$("#radio01").attr('checked', 'checked');
        if(roleval == 2)
        	$("#radio02").attr('checked', 'checked');
        
		$('#change-role').modal({backdrop: 'static', keyboard: false})  
	});
	
}); 