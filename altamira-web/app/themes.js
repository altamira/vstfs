function viewmodel($scope, $filter, $window, $http){
	
	$scope.serverURL = '/engine-rest';
	$scope.user = "Esli Gomes";
	
	$scope.setColorTheme = function(bg, fg) {
		$scope.bgcolor = bg;
		$scope.fgcolor = fg;
	};
	
	var init = function() {
		$scope.bgcolor = 'dark';
		$scope.fgcolor = 'white';
		
		$scope.tasks = [];
		
		var result = $http({
			method: 'GET',
	        url: $scope.serverURL + '/task/?assignee=' + $scope.user + '&sortBy=priority&sortOrder=desc&firstResult=0&maxResults=4'});
		
		result.success(function(data) {
			$.each(data, function(item){
				$scope.tasks.unshift(item);
			});
			// TODO Add pagination
		});
	};
	
	init();
};
viewmodel.$inject = ['$scope', '$filter', '$window', '$http'];
