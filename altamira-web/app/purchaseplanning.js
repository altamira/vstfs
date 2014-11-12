var app = angular.module('App', []);

app.controller('Controller', function($scope, $filter, $window, $http, $location) {
	
	var $ = jQuery;
	
	var serverURL = '/altamira-bpm/rest';
	
	var init = function(){
		
		$scope.showDialog = false;
		
		//$scope.suppliers = [{'id':1, 'name': 'USIMINAS'}, {'id':2, 'name': 'MULTIACOS'}, {'id':3, 'name': 'LEALFER'}, {'id':4, 'name': 'MBA'}, {'id':5, 'name': 'ACOFER'}];
		$http({
			method: 'GET',
			url: serverURL + '/supplier/suppliers',
			headers: {'Accept':'application/json'},
		})
		.success(function(result) {
			if (result.error == 0) {
				$scope.suppliers = result.data;
			} else {
				alert(result.message);
			}
		})
		.error(function() {
			alert("Não foi possível carregar a lista de Fornecedores !");
		});
		
		$http({
			method: 'GET',
			url: serverURL + '/standard/standards',
			headers: {'Accept':'application/json'},
		})
		.success(function(result) {
			if (result.error == 0) {
				$scope.standards = result.data;
			} else {
				alert(result.message);
			}
		})
		.error(function() {
			alert("Não foi possível carregar a lista de Normas !");
		});

		$scope.standard = 'SAE 1008'; //$scope.standards[1];
		
		$http({
			method: 'GET',
			url: serverURL + '/quotation/74',
			headers: {'Content-Type': 'application/json', 'Accept':'application/json'},
		})
		.success(function(result) {
			if (result.error == 0) {
				$scope.quotation = result.data;
			} else {
				alert(result.message);
			}
		})
		.error(function() {
			alert("Não foi possível carregar a Cotação " + $location.search()['id'] + " !");
		});

		/*
		$scope.quotation = {"id":0, "date": 1391186829413, "creator": "Esli Gomes", "items": 
		 [
		   {"id": 0, "lamination": "FQ", "treatment": "DE", "thickness": 2.00, "weight": 15.2, "quotes": 
		    [
		      {"id": 0, "supplier": {"id": 0, "name":"MULTIACOS"}, "standard": "SAE-1008", "weight":8.0, "price": 1020.00},
		      {"id": 0, "supplier": {"id": 3, "name":"LEALFER"}, "standard": "SAE-1008", "weight":7.5, "price": 1032.00},
		      {"id": 0, "supplier": {"id": 4, "name":"MBA"}, "standard": "SAE-1008", "weight":15.2, "price": 1095.00,
		       "stocks": 
		       [
		         {"id": 0, "width": 200, "length": 0, "weight": 2.4},
		         {"id": 0, "width": 85, "length": 0, "weight": 3.2},
		         {"id": 0, "width": 265, "length": 0, "weight": 5.7}
		       ]
		      },
		      {"id": 0, "supplier": {"id": 1, "name":"USIMINAS"}, "standard": "SAE-1008", "weight":9.5, "price": 1065.00},
		      {"id": 0, "supplier": {"id": 5, "name":"ACOFER"}, "standard": "SAE-1008", "weight":12.2, "price": 985.00}
		    ]
		   },
		   {"id": 0, "lamination": "FQ", "treatment": "DE", "thickness": 2.65, "weight": 10.5, "quotes": 
		    [
		      {"id": 0, "supplier": {"id": 2, "name":"MULTIACOS"}, "standard": "SAE-1008", "weight":10.7, "price": 1120.00},
		      {"id": 0, "supplier": {"id": 1, "name":"USIMINAS"}, "standard": "SAE-1008", "weight":8.0, "price": 1014.00},
		      {"id": 0, "supplier": {"id": 4, "name":"MBA"}, "standard": "SAE-1008", "weight":5.0, "price": 995.00},
		      {"id": 0, "supplier": {"id": 3, "name":"LEALFER"}, "standard": "SAE-1008", "weight":5.5, "price": 1132.00},
		      {"id": 0, "supplier": {"id": 5, "name":"ACOFER"}, "standard": "SAE-1008", "weight":2.1, "price": 1085.00}
		    ]
		   },
		   {"id": 0, "lamination": "FQ", "treatment": "DE", "thickness": 2.65, "weight": 8.5, "quotes": 
		    [
		      {"id": 0, "supplier": {"id": 1, "name":"USIMINAS"}, "standard": "SAE-1008", "weight":8.0, "price": 1014.00},
		      {"id": 0, "supplier": {"id": 2, "name":"MULTIACOS"}, "standard": "SAE-1008", "weight":10.7, "price": 1120.00},
		      {"id": 0, "supplier": {"id": 3, "name":"LEALFER"}, "standard": "SAE-1008", "weight":5.5, "price": 1132.00},
		      {"id": 0, "supplier": {"id": 4, "name":"MBA"}, "standard": "SAE-1008", "weight":5.0, "price": 995.00},
		      {"id": 0, "supplier": {"id": 5, "name":"ACOFER"}, "standard": "SAE-1008", "weight":2.1, "price": 1085.00}
		    ]
		   },
		   {"id": 0, "lamination": "FF", "treatment": "PR", "thickness": 3.00, "weight": 16.0, "quotes": 
		    [
		      {"id": 0, "supplier": {"id": 4, "name":"MBA"}, "standard": "SAE-1008", "weight":5.0, "price": 995.00},
		      {"id": 0, "supplier": {"id": 3, "name":"LEALFER"}, "standard": "SAE-1008", "weight":5.5, "price": 1132.00},
		      {"id": 0, "supplier": {"id": 1, "name":"USIMINAS"}, "standard": "SAE-1008", "weight":8.0, "price": 1014.00},
		      {"id": 0, "supplier": {"id": 2, "name":"MULTIACOS"}, "standard": "SAE-1008", "weight":10.7, "price": 1120.00},
		      {"id": 0, "supplier": {"id": 5, "name":"ACOFER"}, "standard": "SAE-1008", "weight":2.1, "price": 1085.00}
		    ]
		   }
		 ]
		};*/

	};

	$scope.criteriaMatch = function( supplier ) {
	  return function( item ) {
	    return item.supplier.name == supplier.name;
	  };
	};
		
	$scope.notExist = function( item, supplier ) {
	  return function( item ) {
		  angular.forEach(quotes, function(data) {
	    	if (data.supplier.name == supplier.name)
	    		return false;
		  });
		  return true;
	  };
	};
	
	$scope.bestPrice = function( item, quote ) {
	  var bestprice = 99999.99;
	  angular.forEach(item.quotes, function(data) {
    	if (data.price <= bestprice) {
    		bestprice = data.price;
    	}
	  });
	  return quote.price <= bestprice;
	};

	$scope.stocksSum = function( quote ) {
	  var weight = 0;
	  angular.forEach(quote.stocks, function(data) {
		  weight += data.weight;
	  });
	  return weight;
	};
		
	$scope.edit = function(quote) {
		alert("id: " + quote.id + ", supplier: " + quote.supplier.name + ", price: " + quote.price + ", weight: " + quote.weight + ", standard: " + quote.standard);
	};
	
	$scope.create = function(){
		var r = $.getJSON(serverURL + '/quotation/items', $scope.request, 'json');
			
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
			url: serverURL + '/quotation',
			headers: {'Content-Type': 'application/json', 'Accept':'application/json'},
			data: $scope.quotation
		})
		.success(function(result) {
			if (result.error == 0) {
				$scope.quotation = result.data;
				alert("Cotação criada com sucesso !");
				init();
			} else {
				alert(result.message);
			}
		})
		.error(function() {
			alert("Erro ao criar uma nova Cotação !");
		});

	};
	
	$scope.get = function(){
		$http({
			method: 'GET',
			url: serverURL + '/quotation/' + $scope.quotation.id,
			headers: {'Content-Type': 'application/json', 'Accept':'application/json'}
		})
		.success(function(result) {
			if (result.error == 0) {
				$scope.quotation = result.data;
				$scope.search();
			} else {
				alert(result.message);
			}
		})
		.error(function() {
			alert("Erro ao criar uma nova Requisição !");
		});

	};
	
	$scope.add = function(item, supplier){

		var quote = {id: 0, supplier: supplier, weight:7.5, price: 1032.00, standard: 'SAE-1008', stocks: [{id: 0, width: 200, length: 0, weight: 2.4}, {id: 0, width: 085, length: 0, weight: 6.2}]};
		
		$scope.showDialog = true;
		
//		$.Dialog({
//		    overlay: true,
//		    shadow: true,
//		    flat: true,
//		    icon: '<img src="favicon.ico">',
//		    title: 'Cotação ' + supplier.name,
//		    content: '<form>' +
//            '<label>Preço</label>' +
//            '<div class="input-control text"><input type="text" name="price" ng-model="quote.price">' +
//            '<button class="btn-clear"></button></div> ' +
//            '<label>Norma <span ng-model="standard"</span>{{$scope.standard}}</label>' +
//            '<div class="input-control select">' +
//            '<select ng-model="standard" ng-options="s.name for s in $scope.standards"></select><br>' +
//            '<button class="btn-clear"></button></div> ' +
//            '<div class="input-control checkbox">' +
//            '<label><input type="checkbox" name="c1" checked/>' +
//            '<span class="check"></span>Check me out</label></div>'+
//            '<div class="form-actions">' +
//            '<button class="button primary">Login to...</button> '+
//            '<button class="button" type="button" onclick="$.Dialog.close()">Cancel</button> '+
//            '</div>'+
//            '</form>',
//		    onShow: function(_dialog){
//		    	//var content = ;
// 
//	            $.Dialog.title('Cotação ' + supplier.name);
//	            //$.Dialog.content(content);
//	            $.Metro.initInputs();
//		    }
//		});
		
		//alert(item.lamination + ", " + item.thickness + ", " + supplier.name);
		
//		quote.id = 0;
//	    quote.supplier = supplier;
//	    quote.weight = 9.99;
//	    quote.price = 123;
//	    quote.standard = "SAE-1008";
		
		$http({
			method: 'POST',
			url: serverURL + '/quotation/item/' + item.id + '/quote',
			headers: {'Content-Type': 'application/json', 'Accept':'application/json'},
			data: quote
		})
		.success(function(result) {
			if (result.error == 0) {
				quote = result.data;
				item.quotes.push(quote);
			} else {
				alert(result.message);
			}
		})
		.error(function() {
			alert("Erro ao criar um novo item da Cotação !");
		});
		
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
	
	init();
});
//viewmodel.$inject = ['$scope', '$filter', '$window', '$http'];
