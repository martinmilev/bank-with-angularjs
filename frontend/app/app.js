var app = angular.module('bankApp', ['ngRoute']);

app.config(function ($routeProvider, $locationProvider) {
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

app.controller('TransactionsHistoryCtrl', function ($scope, $http) {
  // an empty list of transactions should be displayed 
  // before loading of the history
  $scope.transactions = [];
  $http.get("/v1/transactions")
          .then(function (response) {
            $scope.transactions = response.data;
          });
});

app.controller('HomePageCtrl', function ($scope, $http) {
  $scope.account = {};
  $scope.operation = {type: "deposit"};

  $http.get("/v1/useraccount")
          .then(function (response) {
            $scope.account = response.data;
          });

  $scope.executeOperation = function (operation) {
    $scope.message = {};
    $http.post("/v1/operation", operation).then(function (response) {
      $scope.account.balance = response.data.balance;
      $scope.message.success = "Operation completed successfully: ";
    }, function (error) {
      $scope.message.error = "Insufficient amount in your account: ";
      return;
    });
  }

  $scope.closeAlert = function(index) {
    $scope.alerts.splice(index, 1);
  };
});

