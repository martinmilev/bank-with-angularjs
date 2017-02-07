describe('Routing', function () {
  beforeEach(angular.mock.module('bankApp'));

  it('Should map routes to controllers', function () {
    inject(function ($route) {
      expect($route.routes['/'].templateUrl).toEqual('view/index_page.view.html');
      expect($route.routes['/history'].templateUrl).toEqual('view/transaction_history_page.view.html');
      expect($route.routes['/passchange'].templateUrl).toEqual('view/password_change_page.view.html');
      expect($route.routes[null].redirectTo).toEqual('/');
    });
  });
});

describe('TransactionsHistoryCtrl', function () {
  beforeEach(angular.mock.module('bankApp'));
  var $controller;
  var $httpBackend;

  beforeEach(inject(function (_$controller_, _$httpBackend_) {
    $controller = _$controller_;
    $httpBackend = _$httpBackend_;
  }));

  it('Should load transaction history on init', inject(function ($http) {
    var $scope = {};
    $controller('TransactionsHistoryCtrl', {$scope: $scope, $http: $http});

    $httpBackend
            .expect('GET', '/v1/transactions?isNext=false&startingFromCursor=')
            .respond(200, {foo: 'bar'});
    $httpBackend.flush();

    expect($scope.records).toEqual({foo: 'bar'});
  }));

  it('Should load another transaction history', inject(function ($http) {
    var $scope = {};
    $controller('TransactionsHistoryCtrl', {$scope: $scope, $http: $http});

    $httpBackend
            .expect('GET', '/v1/transactions?isNext=false&startingFromCursor=')
            .respond(200, [1, 2, 3]);
    $httpBackend.flush();

    expect($scope.records).toEqual([1, 2, 3]);
  }));

  it('Should receive next page', inject(function ($http) {
     var $scope = {};
     $controller('TransactionsHistoryCtrl', {$scope: $scope, $http: $http});

     $scope.records = [{id: '25dfs'}, {id: '25dfs'}];

     $httpBackend
             .expect('GET', '/v1/transactions?isNext=false&startingFromCursor=')
             .respond(200, [{id: '25dfs'}, {id: '25dfs'}]);
     $httpBackend
             .expect('GET' ,'/v1/transactions?isNext=true&startingFromCursor=25dfs')
             .respond(200, [{id: '34dfs'}, {id: '34dfs'}]);
     $scope.goNext();

     $httpBackend.flush();

     expect($scope.records).toEqual([{id: '34dfs'}, {id: '34dfs'}]);
     expect($scope.status.back).toEqual(false);
     expect($scope.status.next).toEqual(false);
     expect($scope.pageCounter).toEqual(1);
  }));

  it('Should go back to previous page', inject(function ($http) {
     var $scope = {};
     $controller('TransactionsHistoryCtrl', {$scope: $scope, $http: $http});

     $scope.records = [{id: '34dfs'}, {id: '34dfs'}];

     $httpBackend
             .expect('GET', '/v1/transactions?isNext=false&startingFromCursor=')
             .respond(200, [{id: '25dfs'}, {id: '25dfs'}]);
     $httpBackend
             .expect('GET' ,'/v1/transactions?isNext=true&startingFromCursor=34dfs')
             .respond(200, [{id: '34dfs'}, {id: '34dfs'}]);
     $httpBackend
             .expect('GET', '/v1/transactions?isNext=false&startingFromCursor=34dfs')
             .respond(200, [{id: '25dfs'}, {id: '25dfs'}]);
     $scope.goNext();
     $scope.goBack();

     $httpBackend.flush();

     expect($scope.records).toEqual([{id: '25dfs'}, {id: '25dfs'}]);
     expect($scope.status.back).toEqual(true);
     expect($scope.status.next).toEqual(false);
     expect($scope.pageCounter).toEqual(0);
  }));

  it('Should not go forward when there is no next page', inject(function ($http) {
     var $scope = {};
     $controller('TransactionsHistoryCtrl', {$scope: $scope, $http: $http});

     $scope.records = [{id: '25dfs'}, {id: '25dfs'}];

     $httpBackend
             .expect('GET', '/v1/transactions?isNext=false&startingFromCursor=')
             .respond(200, [{id: '25dfs'}, {id: '25dfs'}]);
     $httpBackend
             .expect('GET' ,'/v1/transactions?isNext=true&startingFromCursor=25dfs')
             .respond(200, [{id: '34dfs'}, {id: '34dfs'}]);
     $httpBackend
             .expect('GET' ,'/v1/transactions?isNext=true&startingFromCursor=25dfs')
             .respond(200, []);
     $scope.goNext();
     $scope.goNext();

     $httpBackend.flush();

     expect($scope.records).toEqual([{id: '34dfs'}, {id: '34dfs'}]);
     expect($scope.status.back).toEqual(false);
     expect($scope.status.next).toEqual(true);
     expect($scope.pageCounter).toEqual(1);
  }));
});

describe('HomePageCtrl', function () {
  beforeEach(angular.mock.module('bankApp'));
  var $controller;
  var $httpBackend;

  beforeEach(inject(function (_$controller_, _$httpBackend_) {
    $controller = _$controller_;
    $httpBackend = _$httpBackend_;
  }));

  it('should load transaction account data on init', inject(function ($http) {
    var $scope = {};
    $controller('HomePageCtrl', {$scope: $scope, $http: $http});

    $httpBackend
            .expect('GET', '/v1/useraccount')
            .respond(200, {foo: 'bar'});
    $httpBackend.flush();

    expect($scope.account).toEqual({foo: 'bar'});
  }));

  it('sets deposit operation as default on init', inject(function ($http) {
    var $scope = {};
    $controller('HomePageCtrl', {$scope: $scope, $http: $http});
    expect($scope.operation).toEqual({type: "deposit"});
  }));


  it('should update balance after operation is executed successfully', inject(function ($http) {
    var $scope = {};
    $controller('HomePageCtrl', {$scope: $scope, $http: $http});

    $httpBackend
            .expect('GET', '/v1/useraccount')
            .respond(200, {foo: 'bar'});

    $httpBackend
            .expect('POST', '/v1/operation', {type: 'deposit'})
            .respond(200, {balance: 23.44});

    $scope.executeOperation({type: "deposit"});

    $httpBackend.flush();

    expect($scope.account.balance).toEqual(23.44);
  }));

  it('should update balance after another operation is executed successfully', inject(function ($http) {
    var $scope = {};
    $controller('HomePageCtrl', {$scope: $scope, $http: $http});

    $httpBackend
            .expect('GET', '/v1/useraccount')
            .respond(200, {foo: 'bar'});

    $httpBackend
            .expect('POST', '/v1/operation', {type: 'withdraw'})
            .respond(200, {balance: 11.23});

    $scope.executeOperation({type: "withdraw"});

    $httpBackend.flush();

    expect($scope.account.balance).toEqual(11.23);
  }));

  it('should return message after operation if executed successfully', inject(function ($http) {
    var $scope = {};
    $controller('HomePageCtrl', {$scope: $scope, $http: $http});

    $httpBackend
            .expect('GET', '/v1/useraccount')
            .respond(200, {foo: 'bar'});

    $httpBackend
            .expect('POST', '/v1/operation', {type: 'deposit'})
            .respond(200, {balance: 2.4});

    $scope.executeOperation({type: "deposit"});

    $httpBackend.flush();

    expect($scope.message.success).toEqual("Operation completed successfully: ");
  }));

  it('should return message after ', inject(function ($http) {
    var $scope = {};
    $controller('HomePageCtrl', {$scope: $scope, $http: $http});

    $httpBackend
            .expect('GET', '/v1/useraccount')
            .respond(200, {foo: 'bar'});

    $httpBackend
            .expect('POST', '/v1/operation', {type: 'withdraw'})
            .respond(400);

    $scope.executeOperation({type: "withdraw"});

    $httpBackend.flush();

    expect($scope.message.error).toEqual("Insufficient amount in your account: ");
  }));
});

describe('LogoutCtrl', function () {
  beforeEach(angular.mock.module('bankApp'));
  var $controller;
  var $httpBackend;

  beforeEach(inject(function (_$controller_, _$httpBackend_) {
    $controller = _$controller_;
    $httpBackend = _$httpBackend_;
  }));

  it('Should logout user', inject(function ($http) {
    var $scope = {};
    $controller('LogoutCtrl', {$scope: $scope, $http: $http});

    $httpBackend
            .expect('GET', '/logout')
            .respond(200);

    $scope.logout();

    $httpBackend.flush();
  }));
});

describe('ChangePasswordCtrl', function () {
  beforeEach(angular.mock.module('bankApp'));
    var $controller;
    var $httpBackend;

  beforeEach(inject(function (_$controller_, _$httpBackend_) {
    $controller = _$controller_;
    $httpBackend = _$httpBackend_;
  }));

  it('Should change password', inject(function ($http) {
    var $scope = {};
    $controller('ChangePasswordCtrl', {$scope: $scope, $http: $http});

    $httpBackend
            .expect('POST', '/v1/useraccount/changePassword')
            .respond(200);

    $scope.changePassword('oldPassword', 'newPassword', 'newPassword');

    $httpBackend.flush();

    expect($scope.message.success).toEqual("Password changed successfully!!!");
    expect($scope.message.failure).toEqual("");
  }));

  it('Wrong old password', inject(function ($http) {
    var $scope = {};
    $controller('ChangePasswordCtrl', {$scope: $scope, $http: $http});

    $httpBackend
            .expect('POST', '/v1/useraccount/changePassword')
            .respond(400);

    $scope.changePassword('wrongOldPassword', 'newPassword', 'newPassword');

    $httpBackend.flush();

    expect($scope.message.success).toEqual("");
    expect($scope.message.failure).toEqual("Incorrect old password!!!");
  }));

  it('New password and its validation wont match', inject(function ($http) {
    var $scope = {};
    $controller('ChangePasswordCtrl', {$scope: $scope, $http: $http});

    $scope.changePassword('oldPassword', 'newPassword', 'wrongPassword');

    expect($scope.message.success).toEqual("");
    expect($scope.message.failure).toEqual("New password doesn't match the one in the validation field!!!");
  }));
});
