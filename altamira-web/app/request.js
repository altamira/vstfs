function viewmodel($scope, $filter, $window, $http){
	
	var $ = jQuery;
	
	var serverURL = '/altamira-bpm/rest';
	
	var init = function() {
		var fullDate = new Date();
		var twoDigitMonth = fullDate.getMonth() + ""; if(twoDigitMonth.length == 1) twoDigitMonth = "0" + twoDigitMonth;
		var twoDigitDate = fullDate.getDate() + ""; if(twoDigitDate.length == 1) twoDigitDate="0" + twoDigitDate;
		var currentDate = fullDate.getFullYear() + "-" + twoDigitMonth + "-" + twoDigitDate;
		$scope.request = {id:0, date: currentDate, creator: 'Helio Toda', items: []};
		reset();
	};

	var reset = function(){
		$scope.item = {id: 0, material : {id: 0, code:'ALPRFQ30-KG20000F330', description:'ACO LAMINADO'}, weight:15.2, date:'2014-05-10'};

	    // functions have been describe process the data for display
	    $scope.search();

	};
	
	$scope.create = function(){
		var r = $.getJSON(serverURL + '/request/items', $scope.request, 'json');
			
		r.done(function(data) {
			//alert('get description: ' + data);
		})
		.fail(function() {
		})
		.always(function() {
			
		});

		init();
	};
	
	$scope.submit = function(){
		$http({
			method: 'POST',
			url: serverURL + '/request',
			headers: {'Content-Type': 'application/json', 'Accept':'application/json'},
			data: $scope.request
		})
		.success(function(result) {
			if (result.error == 0) {
				$scope.request = result.data;
				alert("Requisição " + $scope.request.id + " criada com sucesso !");
				init();
			} else {
				alert(result.message);
			}
		})
		.error(function() {
			alert("Erro ao criar uma nova Requisição !");
		});

	};
	
	$scope.get = function(){
		$http({
			method: 'GET',
			url: serverURL + '/request/' + $scope.request.id,
			headers: {'Content-Type': 'application/json', 'Accept':'application/json'}
		})
		.success(function(result) {
			if (result.error == 0) {
				$scope.request = result.data;
				$scope.search();
			} else {
				alert(result.message);
			}
		})
		.error(function() {
			alert("Erro ao criar uma nova Requisição !");
		});

	};
	
	$scope.add = function(item){

		$http({
			method: 'GET',
	        url: serverURL + '/material?code=' + item.material.code
		})
		.success(function(result) {
			if (result.error == 0) {
				item.material = result.data;
				$scope.request.items.unshift(item);
				reset();
			} else {
				alert(result.message);
			}
		})
		.error(function() {
			alert("Erro ao incluir item !");
		});
		
	};
	
	$scope.edit = function(item){
		$scope.item = item;
	};
	
	$scope.remove = function(item){
		var confirm = $window.confirm('Confirma a exclusão do item ' + item.material.code + ' ?');
		if (confirm){
			
			/************************************************************************************************
			 * Forma 1: Exclusao enviando o conteudo do objeto em formato JSON                              *
			 *          
			 *          Necessita configurar corretamente os headers Content-Type e Accept, caso contrario  *
			 *          AngularJS acrescenta o type "application/json" ao conteudo existente de Content-Type*
			 *          que acaba ficando com 2 valores: application/json, text/*.* isso causa erro         *
			 *          no lado do servidor.                                                                *
			 *                                                                                              *
			 *          Outra forma de evitar esse problema é setar o default header no início do modulo:   *
			 *                                                                                              *
			 *          $http.defaults.headers.common['Content-Type'] = 'application/json';                 *
			 *          $http.defaults.headers.common['Accept'] = 'application/json';                       *
             *                                                                                              *
			 *          Util se for necessário um controle de concorrencia para comparar o valor de cada um *
			 *          dos campos com o registro no banco de dados para evitar apagar algum registro que   *
			 *          tenha sido alterado por outro usuário enquanto esta sendo acesso aqui nesta tela    *
			 ***********************************************************************************************/
//			var result $http({
//				method: 'DELETE',
//		        url: serverURL + '/request/item',
//		        data: data,
//		        headers: {'Content-Type': 'application/json', 'Accept':'application/json'}
//			})
			
			/************************************************************************************************
			 * Forma 2: Excluindo pelo ID do objeto                                                         *
			 *          A diferenca é que não necessita manipular os headers, porque o id do objeto vai ser *
			 *          passado por parametro, neste caso o servidor vai ignorar o conteudo da requisição.  *
			 *                                                                                              *
			 *          Util se não for necessário verificar o estado do objeto para efeito de controle de  *
			 *          concorrência                                                                        *
			 ***********************************************************************************************/ 
			//$http.delete(serverURL + '/request/item/' + item.id)
			var result = $http({
				method: 'DELETE',
		        url: serverURL + '/request/item/' + item.id
			});
			
			/************************************************************************************************
			 * Em ambos os casos o servidor retorna o resultado da operação em formato JSON:                *
			 *                                                                                              *
			 * Formato da mensagem: {error: 12345, message: 'Alteração efetuada com sucesso !'}             *
			 ***********************************************************************************************/ 
			result.success(function(data) {
				if (data.error == 0) {
					//alert(data.message);
					var index = $scope.request.items.indexOf(item);
					$scope.request.items.splice(index, 1);
					$scope.search(); // TODO do not refresh entire table
				} else {
					alert(data.message);
				};
			})
			.error(function() {
				alert("Erro ao carregar os dados. O Servidor esta indisponível.");
			});
			
		}
	};
	
	$scope.sortingOrder = 'code';
    $scope.reverse = false;
    $scope.itemsPerPage = 5;
    $scope.currentPage = 0;
    
    var searchMatch = function (haystack, needle) {
        if (!needle) {
            return true;
        }
        return haystack.toString().toLowerCase().indexOf(needle.toLowerCase()) !== -1;
    };

    // init the filtered items
    $scope.search = function () {
        $scope.filteredItems = [];
        $scope.groupedItems = [];
        $scope.pagedItems = [];

        $scope.filteredItems = $filter('filter')($scope.request.items, function (item) {
            for(var attr in item) {
                if (searchMatch(item[attr], $scope.query))
                    return true;
            }
            return false;
        });
        // take care of the sorting order
        if ($scope.sortingOrder !== '') {
            $scope.filteredItems = $filter('orderBy')($scope.filteredItems, $scope.sortingOrder, $scope.reverse);
        }
        $scope.currentPage = 0;
        // now group by pages
        $scope.groupToPages();
    };
    
    // calculate page in place
    $scope.groupToPages = function () {
        $scope.pagedItems = [];
        
        for (var i = 0; i < $scope.filteredItems.length; i++) {
            if (i % $scope.itemsPerPage === 0) {
                $scope.pagedItems[Math.floor(i / $scope.itemsPerPage)] = [ $scope.filteredItems[i] ];
            } else {
                $scope.pagedItems[Math.floor(i / $scope.itemsPerPage)].push($scope.filteredItems[i]);
            }
        }
    };
    
    $scope.range = function (start, end) {
        var ret = [];
        if (!end) {
            end = start;
            start = 0;
        }
        for (var i = start; i < end; i++) {
            ret.push(i);
        }
        return ret;
    };
    
    $scope.prevPage = function () {
        if ($scope.currentPage > 0) {
            $scope.currentPage--;
        }
    };
    
    $scope.nextPage = function () {
        if ($scope.currentPage < $scope.pagedItems.length - 1) {
            $scope.currentPage++;
        }
    };
    
    $scope.setPage = function () {
        $scope.currentPage = this.n;
    };

    // change sorting order
    $scope.sort_by = function(newSortingOrder) {
        if ($scope.sortingOrder == newSortingOrder)
            $scope.reverse = !$scope.reverse;

        $scope.sortingOrder = newSortingOrder;

        // icon setup
        $('th i').each(function(){
            // icon reset
            $(this).removeClass().addClass('icon-menu');
        });
        if ($scope.reverse)
            $('#th.'+newSortingOrder+' i').removeClass().addClass('icon-arrow-up-3');
        else
            $('#th.'+newSortingOrder+' i').removeClass().addClass('icon-arrow-down-3');
    };

	init();
};
viewmodel.$inject = ['$scope', '$filter', '$window', '$http'];