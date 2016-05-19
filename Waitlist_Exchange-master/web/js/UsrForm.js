//JS

$(document).ready(function(){
/*	//Not worth the time...
	var auth = gapi.auth2.getAuthInstance();
	if(auth.isSignedIn.get()){
		var name = auth.currentUser.get().getBasicProfile().getName();
		$('#title').html(name);
	}*/

	$('#accord').accordion({collapsible: true});

        //Refesh every 10 minutes.
	setTimeout(function(){window.location.reload(true);}, 1000*60*10);
	
        //Add a request to the form.
	$('#addReq').click(function(){
		$('#reqs').append('<div>I want <input type="text" class="wantC" value="a class"></input> <br/>\
				at waitlist position <input type="text" class="wantP" value="(0 for enrollment)"></input><br/>\
				and I\'ll give up <input type="text" class="haveC" value="this class"></input><br/>\
				at waitlist position <input type="text" class="wantC" value="(0 for enrollement)"></input>.</div>');
	});

        //Vote on a certain cycle.
	$('.vote').click(function(){
		var $id = $(this).attr('id');
		console.log($id + ' clicked');
//		data: JSON.stringify({idx:db[$id], uid:uid})		
		var uid = gapi.auth2.getAuthInstance().currentUser.get().getBasicProfile().getId();
		var xhr = new XMLHttpRequest();
		xhr.open('POST','http://localhost:8080/vote');
		xhr.setRequestHeader('Content-Type', 'application/json');
                xhr.onload = function() {
			if(parseInt(xhr.responseText) > 0){
				$('#' + $id + 'votes').html("<strong>You've voted!</strong>");
				$(this).attr('class','novote');
			}
                };
                xhr.send(JSON.stringify({idx:db[$id], uid:uid}));
	});

        //Submit a list of requests
	$('#submit').click(function(){
		var uid = gapi.auth2.getAuthInstance().currentUser.get().getBasicProfile().getId();
		var vals = [];
		var bd = false;
		$('#reqs').children('div').each(function(){
			var c1 = $(this).find('.haveC').val();
			var w1 = $(this).find('.haveP').val();
			var c2 = $(this).find('.wantC').val();
			var w2 = $(this).find('.wantP').val();

			if(isNaN(w1) || isNaN(w2) || ((c1 == c2) && parseInt(w1) >= parseInt(w2))){
				alert("Invalid entry with wanting " + c2 + " for " + c1);
				bd = true;
			}else
				vals.push({__Pair__: true, //Encodes the request to the python encoding of a Pair object.
					  have: {__Desire__:true, name:c1, position:w1}, 
					  want: {__Desire__:false, name:c2, position:w2},
					  id: uid})
		});

		if(bd)return;

		var xhr = new XMLHttpRequest();
		xhr.open('POST','http://localhost:8080/swap');
		xhr.setRequestHeader('Content-Type', 'application/json');
                xhr.onload = function() {
			alert("Requests sent!");
			window.location.reload(true);
                };
                xhr.send(JSON.stringify(vals));
	});

//Input validation: class1 != class2 || w1 < w2
});
