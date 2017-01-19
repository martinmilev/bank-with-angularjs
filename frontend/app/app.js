var app = angular.module('bankApp', ['ngRoute']);

app.config(function($routeProvider, $locationProvider) {
  $routeProvider
    .when('/', {
      templateUrl: 'view/index_page.view.html'
    })
    .when('/history', {
      templateUrl: 'view/transaction_history_page.view.html'
    })
    .otherwise({
      redirectTo: '/'
    });

  $locationProvider.html5Mode(true);
});

app.controller('HomePageCtrl', function($scope, $http) {
  $http.get("/v1/")
    .then(function(response) {
    $scope.account = response.data;
  });

  $scope.operation = {};
  $scope.submitForm = function() {
    $http({
      method: 'POST',
        url: '/v1/operation',
      data: $scope.operation
    });
  }
});

app.controller('TransactionsHistoryCtrl', function($scope, $http) {
  // an empty list of transactions should be displayed 
  // before loading of the history
  $scope.transactions = [];
  $http.get("/v1/transactions")
    .then(function(response) {
      $scope.transactions = response.data;
    });
});
