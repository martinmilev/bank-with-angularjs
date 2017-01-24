describe('Routing', function() {
  beforeEach(angular.mock.module('bankApp'));

  it('Should map routes to controllers', function() {
    inject(function($route) {
      expect($route.routes['/'].templateUrl).toEqual('view/index_page.view.html');
      expect($route.routes[null].redirectTo).toEqual('/');
    });
  });
});

describe('TransactionsHistoryCtrl', function() {
  beforeEach(angular.mock.module('bankApp'));
  var $controller;
  var $httpBackend;

  beforeEach(inject(function(_$controller_, _$httpBackend_){
    $controller = _$controller_;
    $httpBackend = _$httpBackend_;
  }));

  it ('Should load transaction history on init', inject(function($http) {
    var $scope = {};
    $controller('TransactionsHistoryCtrl', { $scope: $scope, $http: $http });

    $httpBackend
      .expect('GET', '/v1/transactions')
      .respond(200, { foo: 'bar' });
    $httpBackend.flush();

    expect($scope.transactions).toEqual({ foo: 'bar' });
  }));

  it ('Should load another transaction history', inject(function($http) {
    var $scope = {};
    $controller('TransactionsHistoryCtrl', { $scope: $scope, $http: $http });

    $httpBackend
      .expect('GET', '/v1/transactions')
      .respond(200, [1,2,3]);
    $httpBackend.flush();

    expect($scope.transactions).toEqual([1,2,3]);
  }));

});

describe('HomePageCtrl', function() {
  beforeEach(angular.mock.module('bankApp'));
  var $controller;
  var $httpBackend;

  beforeEach(inject(function(_$controller_, _$httpBackend_){
    $controller = _$controller_;
    $httpBackend = _$httpBackend_;
  }));

  it ('should load transaction account data on init', inject(function($http) {
    var $scope = {};
    $controller('HomePageCtrl', { $scope: $scope, $http: $http });

    $httpBackend
            .expect('GET', '/v1/useraccount')
            .respond(200, { foo: 'bar' });
    $httpBackend.flush();

    expect($scope.account).toEqual({ foo: 'bar' });
  }));

  it ('sets deposit operation as default on init', inject(function($http) {
    var $scope = {};
    $controller('HomePageCtrl', { $scope: $scope, $http: $http });
    expect($scope.operation).toEqual({type: "deposit"});
  }));


  it ('should update balance after operation is executed successfully', inject(function($http) {
    var $scope = {};
    $controller('HomePageCtrl', { $scope: $scope, $http: $http });

    $httpBackend
            .expect('GET', '/v1/useraccount')
            .respond(200, { foo: 'bar' });

    $httpBackend
            .expect('POST', '/v1/operation', {type: 'deposit'})
            .respond(200, { balance: 23.44 });

    $scope.executeOperation({type: "deposit"});

    $httpBackend.flush();

    expect($scope.account.balance).toEqual(23.44);
  }));

  it ('should update balance after another operation is executed successfully', inject(function($http) {
    var $scope = {};
    $controller('HomePageCtrl', { $scope: $scope, $http: $http });

    $httpBackend
            .expect('GET', '/v1/useraccount')
            .respond(200, { foo: 'bar' });

    $httpBackend
            .expect('POST', '/v1/operation', {type: 'withdraw'})
            .respond(200, { balance: 11.23 });

    $scope.executeOperation({type: "withdraw"});

    $httpBackend.flush();

    expect($scope.account.balance).toEqual(11.23);
  }));

  it ('should return message after operation if executed successfully', inject(function($http) {
    var $scope = {};
    $controller('HomePageCtrl', { $scope: $scope, $http: $http });

    $httpBackend
            .expect('GET', '/v1/useraccount')
            .respond(200, { foo: 'bar' });

    $httpBackend
            .expect('POST', '/v1/operation', {type: 'deposit'})
            .respond(200, { balance: 2.4 });

    $scope.executeOperation({type: "deposit"});

    $httpBackend.flush();

    expect($scope.message.success).toEqual("Operation completed successfully: ");
  }));

  it ('should return message after ', inject(function($http) {
    var $scope = {};
    $controller('HomePageCtrl', { $scope: $scope, $http: $http });

    $httpBackend
            .expect('GET', '/v1/useraccount')
            .respond(200, { foo: 'bar' });

    $httpBackend
            .expect('POST', '/v1/operation', {type: 'withdraw'})
            .respond(400);

    $scope.executeOperation({type: "withdraw"});

    $httpBackend.flush();

    expect($scope.message.error).toEqual("Insufficient amount in your account: ");
  }));
});