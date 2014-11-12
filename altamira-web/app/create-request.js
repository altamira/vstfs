function viewmodel($scope, $filter, $window, $http) {

    var serverURL = 'http://localhost:8080/altamira-bpm/rest';

    var init = function() {
        var fullDate = new Date();
        var twoDigitMonth = fullDate.getMonth() + "";
        if (twoDigitMonth.length === 1)
            twoDigitMonth = "0" + twoDigitMonth;
        var twoDigitDate = fullDate.getDate() + "";
        if (twoDigitDate.length === 1)
            twoDigitDate = "0" + twoDigitDate;
        
        $scope.currentDate = fullDate.getFullYear() + "-" + twoDigitMonth + "-" + twoDigitDate;
        $scope.request = {id: 0, date: $scope.currentDate, creator: 'Helio Toda', items: []};
        
        $scope.type = [{id: 0, description: 'Shape'}, {id: 1, description: 'Coil'}];
        $scope.laminations = [{id: 0, description: 'FQ'}, {id: 1, description: 'FF'}];
        $scope.treatments = ['PR', 'DE', 'GA'];
        $scope.thicknesses = [0.65, 0.85, 0.90, 1.20, 1.40, 2.00, 2.20];
        $scope.widths = [80, 90, 100, 120, 200, 240, 260, 300, 320, 330, 350, 400, 450];
        $scope.lengths = [0, 100, 200, 500, 800, 900, 950, 1000, 1100, 1200, 1500, 1750, 1900, 2000, 2100, 2200, 2400, 3000, 32000];
        
        reset();
    };

    var reset = function() {
        $scope.material = {id: 0, lamination: 'FQ', treatment: 'PR', thickness: 0.85, width: 240, length: 0};
        $scope.item = {id: 0, material: $scope.material, weight: 15.2, date: $scope.currentDate};

        // functions have been describe process the data for display
        //$scope.search();

    };

    $scope.submit = function() {

        if ($scope.request.items.length === 0) {
            $window.confirm('A requisicao não tem items.');
            return;
        }

        var confirm = $window.confirm('Confirma o envio desta Requisicao de Compra ?');
        if (confirm) {
            $http({
                method: 'POST',
                url: serverURL + '/request',
                headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
                data: $scope.request
            })
            .success(function(result) {
                if (result.error === 0) {
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
        }
        ;

    };

    $scope.add = function(item) {

        $http({
            method: 'POST',
            url: serverURL + '/material',
            headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
            data: item.material
        })
        .success(function(result) {
            if (result.error === 0) {
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

    $scope.remove = function(item) {

        $scope.item = item;

        var index = $scope.request.items.indexOf(item);
        $scope.request.items.splice(index, 1);

    };

    init();
};
viewmodel.$inject = ['$scope', '$filter', '$window', '$http'];