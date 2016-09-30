angular.module( 'mymobkit.location', [
  'ui.router',
  'placeholders',
  'ui.bootstrap',
  'resources.messages',
  'ngGrid', 
  'directives.ngEnter',
  'directives.scrollglue'
])

.config(function config( $stateProvider ) {
  $stateProvider.state( 'location', {
    url: '/location',
    views: {
      "main": {
        controller: 'LocationCtrl',
        templateUrl: 'location/location.tpl.html'
      }
    },
    data:{ pageTitle: 'Location' }
  });
})

.controller( 'LocationCtrl', function LocationCtrl($rootScope, $scope, Messages, $window, $timeout, $http, $location, $anchorScroll, $log) {

  var isNotDefined = function(val) { 
    return (angular.isUndefined(val) || val === null);
  };  

  var cities = [];

  $scope.markers = [];
   
  var createMarker = function (info){
      
      var marker = new google.maps.Marker({
          map: $scope.map,
          position: new google.maps.LatLng(info.lat, info.long),
          title: info.city
      });
      marker.content = '<div class="infoWindowContent">' + info.desc + '</div>';
      
      google.maps.event.addListener(marker, 'click', function(){
          $scope.infoWindow.setContent('<h2>' + marker.title + '</h2>' + marker.content);
          $scope.infoWindow.open($scope.map, marker);
      });
      
      $scope.markers.push(marker);
      
  };

  $scope.openInfoWindow = function(e, selectedMarker){
      e.preventDefault();
      google.maps.event.trigger(selectedMarker, 'click');
  };


  var configureLocationApi = function(){
    $scope.locationLink = $rootScope.host + '/services/api/location/';

    $http({method: 'GET', url: $scope.locationLink}).
          success(function(data, status, headers, config) {
            //$log.info(data.longitude);
            if (!isNotDefined(data)) {
              cities.push({
                    city : 'My Place',
                    desc : 'This is my current location!',
                    lat : data.latitude,
                    long : data.longitude
              });

              $log.info(cities);

              var mapOptions = {
                  zoom: 8,
                  center: new google.maps.LatLng(data.latitude, data.longitude),
                  mapTypeId: google.maps.MapTypeId.TERRAIN
              };

              $scope.map = new google.maps.Map(document.getElementById('map'), mapOptions);
              $scope.infoWindow = new google.maps.InfoWindow();
              
              for (i = 0; i < cities.length; i++){
                  createMarker(cities[i]);
              }
            }
          }).
          error(function(data, status, headers, config) {
            $log.info('Unable to retrieve location');
    }); 
  };

	configureLocationApi();
}
)
;
