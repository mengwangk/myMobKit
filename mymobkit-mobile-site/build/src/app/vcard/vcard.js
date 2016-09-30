angular.module( 'mymobkit.vcard', [
  'ui.router',
  'placeholders',
  'ui.bootstrap',
  'resources.parameter',
  'resources.mms',
  'directives.fileModel'
])

.config(["$stateProvider", function config( $stateProvider ) {
  $stateProvider.state( 'vcard', {
    url: '/vcard',
    views: {
      "main": {
        controller: 'vCardCtrl',
        templateUrl: 'vcard/vcard.tpl.html'
      }
    },
    data:{ pageTitle: 'vCard' }
  });
}])

.controller( 'vCardCtrl', ["$rootScope", "$scope", "Messages", "$window", "$timeout", "$http", "$location", "$anchorScroll", "$log", function vCardCtrl($rootScope, $scope, Messages, $window, $timeout, $http, $location, $anchorScroll, $log) {

  var configureVCardApi = function(){
    $scope.vCardLink = $rootScope.host + '/services/api/vcard/';
  };
  

  $scope.mmsMessage =  {
      to: '',
      body: '',
      subject: 'A sample vCard',
      vcard: 'BEGIN:VCARD\r\nVERSION:2.1\r\nN:;John Doe;;;\r\nFN:John Doe\r\nTEL;CELL:65-123686868\r\nEND:VCARD'
  };

  $scope.mmsMessageStatus = false;

  $scope.sendvCardMessage =  function(item, event) {
    $log.info('Sending MMS message');
    $scope.mmsMessageStatus = true;
    var params = angular.copy($scope.mmsMessage);
    var fd = new FormData();
    fd.append("to", $scope.mmsMessage.to);
    fd.append("subject", $scope.mmsMessage.subject);
    fd.append("body", $scope.mmsMessage.body);

    fd.append('vcard', $scope.mmsMessage.vcard);
    
    // Debugging
    console.dir($scope.mmsMessage.vcard);

    $http.post($scope.vCardLink, fd, {
      transformRequest: angular.identity,
      headers: {'Content-Type': undefined}
    })
    .success(function(data, status, headers, config){
      $scope.mmsMessageStatus = false;
      $scope.mmsSendStatus = "vCard is sent to server";
    })
    .error(function(data, status, headers, config){
      $scope.mmsMessageStatus = false;
      $scope.mmsSendStatus = "Failed to send vCard";
    });
  };

  configureVCardApi();

}]
)
;
