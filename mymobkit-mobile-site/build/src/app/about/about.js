angular.module( 'mymobkit.about', [
  'ui.router',
  'placeholders',
  'ui.bootstrap'
])

.config(["$stateProvider", function config( $stateProvider ) {
  $stateProvider.state( 'about', {
    url: '/about',
    views: {
      "main": {
        controller: 'AboutCtrl',
        templateUrl: 'about/about.tpl.html'
      }
    },
    data:{ pageTitle: 'About' }
  });
}])

.controller( 'AboutCtrl', ["$scope", function AboutCtrl( $scope ) {
  // This is simple a demo for UI Boostrap.
  $scope.dropdownDemoItems = [
   "About myMobKit"
  ];
}]);