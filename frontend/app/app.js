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

app.controller('TransactionsHistoryCtrl', function($scope, $http) {
  $scope.pageCounter = 0;
  $scope.status = {next: false, back: false};
  $scope.records = [];

  $http.get("/v1/transactions", {params: {startingFromCursor: '', isNext: false}})
  .then(function(response) {
    $scope.records = response.data;
    $scope.status.back = true;
    $scope.pageCounter = 0;
  });

  $scope.goBack = function() {
    $http.get("/v1/transactions", {params: {startingFromCursor: $scope.records[0].id, isNext: false}})
      .then(function(response) {
        $scope.records = response.data;
        $scope.pageCounter--;
        $scope.status.next = false;

        if ($scope.pageCounter == 0) {
            $scope.status.back = true;
        }
    });
  }

  $scope.goNext = function() {
    $http.get("/v1/transactions", {params: {startingFromCursor: $scope.records[$scope.records.length - 1].id, isNext: true}})
      .then(function(response) {
        if (response.data[0] == undefined) {
          $scope.status.next = true;
        } else {
          $scope.records = response.data;
          $scope.pageCounter++;
          $scope.status.back = false;
        }
    });
  }
});

app.controller('HomePageCtrl', function($scope, $http) {
  $scope.account = {};
  $scope.operation = {type: "deposit"};

  $http.get("/v1/useraccount")
    .then(function(response) {
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

  $scope.closeAlert = function (index) {
    $scope.alerts.splice(index, 1);
  };
});

