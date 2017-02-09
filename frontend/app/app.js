var app = angular.module('bankApp', ['ngRoute']);

app.config(function ($routeProvider, $locationProvider, $httpProvider) {
  $routeProvider

          .when('/', {
            templateUrl: 'view/index_page.view.html'
          })
          .when('/history', {
            templateUrl: 'view/transaction_history_page.view.html'
          })
          .when('/passchange', {
            templateUrl: 'view/password_change_page.view.html'
          })
          .otherwise({
            redirectTo: '/'
          });

  $locationProvider.html5Mode(true).hashPrefix('*');

	$httpProvider.interceptors.push(function($q, $location) {
		return {
			'responseError': function(rejection) {
			  if (rejection.status == 401) {
                window.location.href = "/login";
              } else {
                return $q.reject(rejection);
              }
			}
		};
	});
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

app.controller('LogoutCtrl', function ($scope, $http, $location) {

  $scope.logout = function() {
    $http.get("/logout").then( function (response) {
      $location.path("/logout");
    });
  }
});

app.controller('ChangePasswordCtrl', function ($scope, $http) {
  $scope.message = {};
  $scope.message.success = "";
  $scope.message.failure = "";

  $scope.changePassword = function(oldPassword, newPassword, validateNewPassword) {

    if (newPassword === validateNewPassword) {

      $scope.query = {};
      $scope.query.oldPass = oldPassword;
      $scope.query.newPass = newPassword;

      $http.post("/v1/useraccount/changePassword", $scope.query).then(function successCallback(response) {
        $scope.message.success = "Password changed successfully!!!";
        $scope.message.failure = "";
      }, function errorCallback(failure) {
        $scope.message.failure = "Incorrect old password!!!";
        $scope.message.success = "";
        return;
      });
    } else {
      $scope.message.failure = "New password doesn't match the one in the validation field!!!";
      $scope.message.success = "";
    }

  }

});
