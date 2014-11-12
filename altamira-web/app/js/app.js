'use strict';


// Declare app level module which depends on filters, and services
angular.module('myApp', [
  'ngRoute',
  'myApp.filters',
  'myApp.services',
  'myApp.directives',
  'myApp.controllers'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/index', {
      templateUrl: 'template/index.html', 
      controller: 'IndexController'
  });
  $routeProvider.when('/create-request', {
      templateUrl: 'template/create-request.html', 
      controller: 'CreateRequestController'
  });
  $routeProvider.when('/create-quotation', {
      templateUrl: 'template/create-quotation.html', 
      controller: 'CreateQuotationController'
  });  
  $routeProvider.otherwise({
      redirectTo: '/index'
  });
}]);
