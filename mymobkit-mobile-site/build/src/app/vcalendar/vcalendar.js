angular.module( 'mymobkit.vcalendar', [
  'ui.router',
  'placeholders',
  'ui.bootstrap',
  'resources.parameter',
  'resources.mms',
  'directives.fileModel'
])

.config(["$stateProvider", function config( $stateProvider ) {
  $stateProvider.state( 'vcalendar', {
    url: '/vcalendar',
    views: {
      "main": {
        controller: 'vCalendarCtrl',
        templateUrl: 'vcalendar/vcalendar.tpl.html'
      }
    },
    data:{ pageTitle: 'vCalendar' }
  });
}])

.controller( 'vCalendarCtrl', ["$rootScope", "$scope", "Messages", "$window", "$timeout", "$http", "$location", "$anchorScroll", "$log", function vCalendarCtrl($rootScope, $scope, Messages, $window, $timeout, $http, $location, $anchorScroll, $log) {

  var configureVCalendarApi = function(){
    $scope.vcalendarLink = $rootScope.host + '/services/api/vcalendar/';
  };
  
   $scope.mmsMessage =  {
      to: '',
      body: '',
      subject: 'A sample vCalendar',
      vcalendar: 'BEGIN:VCALENDAR\r\nVERSION:2.0\r\nBEGIN:VEVENT\r\nUID:uid1@example.com\r\nDTSTAMP:20270714T170000Z\r\nORGANIZER;CN=John Doe:MAILTO:john.doe@example.com\r\nDTSTART:20270714T170000Z\r\nDTEND:20270715T035959Z\r\nSUMMARY:Bastille Day Party\r\nEND:VEVENT\r\nEND:VCALENDAR'
  };

  $scope.mmsMessageStatus = false;

  $scope.sendvCalendarMessage =  function(item, event) {
    $log.info('Sending MMS message');
    $scope.mmsMessageStatus = true;
    var params = angular.copy($scope.mmsMessage);
    var fd = new FormData();
    fd.append("to", $scope.mmsMessage.to);
    fd.append("subject", $scope.mmsMessage.subject);
    fd.append("body", $scope.mmsMessage.body);

    fd.append('vcalendar', $scope.mmsMessage.vcalendar);
    
    // Debugging
    console.dir($scope.mmsMessage.vcalendar);

    $http.post($scope.vcalendarLink, fd, {
      transformRequest: angular.identity,
      headers: {'Content-Type': undefined}
    })
    .success(function(data, status, headers, config){
      $scope.mmsMessageStatus = false;
      $scope.mmsSendStatus = "vCalendar is sent to server";
    })
    .error(function(data, status, headers, config){
      $scope.mmsMessageStatus = false;
      $scope.mmsSendStatus = "Failed to send vCalendar";
    });
  };

  configureVCalendarApi();
}]
)
;
