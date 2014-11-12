$(document).ready(function() {
	// TODO Show waiting icon
	//$("#gcd-ct-extrato").addClass("gcd-loading");

	var user = "esli.gomes";
	
	$.getJSON('/engine-rest/task/?assignee=' + user + '&sortBy=priority&sortOrder=desc&firstResult=0&maxResults=4', function(data){
		$.each(data, function(i, item){
			$("#tasklist").append('<div class="tile half triple bg-transparent"><div class="brand"><div class="label"><h2><i class="icon-arrow-right-3 on-left fg-amber"></i><span class="fg-white padding10">' + item.name + '</span></h2></div><div class="brand"><span class="badge bg-amber" style="bottom:12px">12</span></div></div></div>');
		});
		// TODO Add pagination
		/*$("#extrato-usuario")
			.tablesorter({widthFixed: true,widgets: ['zebra']})
			.tablesorterPager({container: $("#pager")});
		$("#gcd-ct-extrato").removeClass("gcd-loading");*/
	});
});

