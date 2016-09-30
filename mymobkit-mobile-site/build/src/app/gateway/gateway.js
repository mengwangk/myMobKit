angular.module( 'mymobkit.gateway', [
  'ui.state',
  'placeholders',
  'ui.bootstrap'
])

.config(["$stateProvider", function config( $stateProvider ) {
  $stateProvider.state( 'gateway', {
    url: '/gateway',
    views: {
      "main": {
        controller: 'GatewayCtrl',
        templateUrl: 'gateway/gateway.tpl.html'
      }
    },
    data:{ pageTitle: 'Gateway' }
  });
}])

.controller( 'GatewayCtrl', ["$scope", function GatewayCtrl( $scope ) {
  $scope.dropdownDemoItems = [
    "The first choice!",
    "And another choice for you.",
    "but wait! A third!"
  ];
}])

;
