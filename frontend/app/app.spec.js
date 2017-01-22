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

  it ('Should load transaction account data on init', inject(function($http) {
    var $scope = {};
    $controller('HomePageCtrl', { $scope: $scope, $http: $http });

    $httpBackend
            .expect('GET', '/v1/')
            .respond(200, { foo: 'bar' });
    $httpBackend.flush();

    expect($scope.account).toEqual({ foo: 'bar' });
  }));

});
