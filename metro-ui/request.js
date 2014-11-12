$(document).ready(function(){
	var serverURL = 'http://192.168.0.117:8080/activiti-explorer/webservice';

	$('#request-login-button').click(function(){
	
		alert(serverURL + '/purchaseOrder/login' + ',' + $('#request-login-username').val() + ',' + $('#request-login-password').val());
		
		var r = $.ajax({
			type: 'GET',
			url: serverURL + '/purchaseOrder/login',
			dataType: 'json',
			crossDomain: 'true',
			otherSettings: 'othervalues',
			username: $('#request-login-username').val(),
			password: $('#request-login-password').val(),
			success: function (responseData, textStatus, jqXHR) {
				console.log("in");
			},
			error: function (responseData, textStatus, errorThrown) {
				alert('POST failed.' + responseData);
			}
		});

	});
	
	$('#request-form-button').click(function(){
		
		var items = [];
		
		$('#request-items-table > tbody:first tr').each(function() {
		
			var item = {};
			
			item.code = $('td[name="code"]', this).text();
			item.weight = $('td[name="weight"]', this).text();
			item.date = $('td[name="date"]', this).text();
			
			items.push(item);
			
		});
		
		var request = '{"id":0,"date":"' + $('#request-form-date').val() + '","creator":"' + $('#request-form-creator').val() + '","items":' + JSON.stringify(items) + '}';
		
		alert(request);
		
		var r = $.post(serverURL + '/purchaseOrder/addRequest', request, 'json');
			
		r.done(function(data) {
			alert( 'request created successful, id:' + data);
		})
		.fail(function() {
			alert( 'request error' );
		})
		/*.always(function() {
			alert( 'finished' );
		})*/;
		
		return false;
	});

	$('#request-items-form-button').click(function(){
		
		//TODO get material description
		var description = '';

		var r = $.getJSON(serverURL + '/material?code=' + $('#request-items-form-code').val());
			
		r.done(function(data) {
			description = data;
			//alert('get description: ' + data);
		})
		.fail(function() {
			description = '*** get description error ***';
			alert('get description error');
		})
		.always(function() {
			$('#request-items-table > tbody:first').append('<tr style="background-color:#FFC966; text-transform:uppercase; "><td name="code" style="text-align:center">' + $('#request-items-form-code').val() + '</td><td name="description" style="text-transform:none;">' + description + '</td><td name="weight" style="text-align:right">' + $('#request-items-form-weight').val() + '</td><td name="date" style="text-align:center">' + $('#request-items-form-date').val() + '</td></tr>');
		});

		return false;		

	});
});