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

app.controller('TransactionsHistoryCtrl', function($scope, $http) {
  // an empty list of transactions should be displayed 
  // before loading of the history
  $scope.transactions = [];
  $http.get("/v1/transactions")
    .then(function(response) {
      $scope.transactions = response.data;
    });
});

app.controller('HomePageCtrl', function($scope, $http) {
  $scope.account = {};
  $http.get("/v1/")
    .then(function(response) {
      $scope.account = response.data;
  });

  $scope.operation = {};
  // var value = $scope.operation.amount;
  // var type = $scope.operation.type.valueOf();
  // var amount;

  $scope.submitForm = function() {
    $http.post("/v1/operation", $scope.operation);
  }
    // if (type == "deposit") {
    //   amount = balance + value;
    // }
    // if (type == "withdraw") {
    //   amount = balance - value;
    // }
    // $http.post("/v1/operation", {type : type, amount : amount});
  // };
});

// app.controller('HomePageCtrl', function($scope, $http) {
//   $http.get("/v1/")
//     .then(function(response) {
//     $scope.account = response.data;
//   });
//
//   $scope.operation = {};
//   $scope.submitForm = function() {
//     $http({
//       method: 'POST',
//       url: '/v1/operation',
//       data: $scope.operation
//     });
//   }
// });
