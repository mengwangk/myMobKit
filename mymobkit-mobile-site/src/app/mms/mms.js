angular.module( 'mymobkit.mms', [
 'ui.router',
 'placeholders',
 'ui.bootstrap',
 'resources.parameter',
 'resources.mms',
 'directives.fileModel'
])

.config(function config( $stateProvider ) {
  $stateProvider.state( 'mms', {
    url: '/mms',
    views: {
      "main": {
        controller: 'MmsCtrl',
        templateUrl: 'mms/mms.tpl.html'
      }
    },
    data:{ pageTitle: 'MMS' }
  });
})

.controller( 'MmsCtrl', function ContactCtrl( $rootScope, $scope, $window, MMS, Parameter, $timeout, $http, $location, $anchorScroll, $log) {
	var isNotDefined = function(val) { 
		return (angular.isUndefined(val) || val === null);
	};	
	
	var configureMmsApi = function(){
		$scope.mmsLink = $rootScope.host + '/services/api/mms/';
		$scope.parameterLink = $rootScope.host + '/services/api/parameter/';
		
		getParameterValue('preferences_apn_mmsc', false);
		getParameterValue('preferences_apn_mms_proxy', false);
		getParameterValue('preferences_apn_mms_port', true);
		getParameterValue('preferences_apn_mms_user', false);
		getParameterValue('preferences_apn_mms_password', false);
		getParameterValue('preferences_apn_mms_user_agent', false);
	};
	
	var getParameterValue = function(key, isNumber) {
		Parameter.getParameterValue(key).get().$promise.then(
				function success(resp, headers) {
					if (resp.isSuccessful) {
						if (!isNumber) {
							$scope.mmsForm[key] = resp.parameters[key];
						} else {
							$scope.mmsForm[key] = parseInt(resp.parameters[key], 10);
						}
					}
				}, function err(httpResponse) {
					$scope.errorMsg = httpResponse.status;
			});
	};
	
	var getSupportedContentTypes = function() {
		MMS.getSupportedContentTypes().get().$promise.then(
				function success(resp, headers) {
					if (resp.isSuccessful) {
						$scope.mimeTypes = resp.supportedContentTypes;
					}
				}, function err(httpResponse) {
					$scope.errorMsg = httpResponse.status;
			});
	};
	
	$scope.submitMmsForm =  function(item, event) {
		$scope.mmsFormStatus = true;
		var params = angular.copy($scope.mmsForm);
		$http(
				{
				method: 'POST', 
				url: $scope.parameterLink, 
				params:  params
				}
			).
			success(function(data, status, headers, config) {
				$scope.mmsFormStatus = false;
				$scope.saveStatus = "Successfully saved the parameters";
			}).
			error(function(data, status, headers, config) {
				$scope.mmsFormStatus = false;
				$scope.saveStatus = "Failed to save parameters";
			});
	};
	
	$scope.sendMmsMessage =  function(item, event) {
		$log.info('Sending MMS message');
		$scope.mmsMessageStatus = true;
		var params = angular.copy($scope.mmsMessage);
		var fd = new FormData();
		fd.append("to", $scope.mmsMessage.to);
		fd.append("subject", $scope.mmsMessage.subject);
		fd.append("body", $scope.mmsMessage.body);

		fd.append('partData_1', $scope.mmsMessage.partdata_1);
		fd.append("partContentType_1", $scope.mmsMessage.partcontenttype_1);

		fd.append('partData_2', $scope.mmsMessage.partdata_2);
		fd.append("partContentType_2", $scope.mmsMessage.partcontenttype_2);

		fd.append('partData_3', $scope.mmsMessage.partdata_3);
		fd.append("partContentType_3", $scope.mmsMessage.partcontenttype_3);
		
		// Debugging
		//$log.info($scope.mmsMessage.media_mime_type_1);
		console.dir($scope.mmsMessage.partdata_2);

		$http.post($scope.mmsLink, fd, {
			transformRequest: angular.identity,
			headers: {'Content-Type': undefined}
		})
		.success(function(data, status, headers, config){
			$scope.mmsMessageStatus = false;
			$scope.mmsSendStatus = "MMS is sent to server";
		})
		.error(function(data, status, headers, config){
			$scope.mmsMessageStatus = false;
			$scope.mmsSendStatus = "Failed to send MMS";
		});
	};
	
	
	$scope.mmsForm =  {
			preferences_apn_mmsc: '',
			preferences_apn_mms_proxy: '',
			preferences_apn_mms_port: 80,
			preferences_apn_mms_user: '',
			preferences_apn_mms_password: '',
			preferences_apn_mms_user_agent: ''
	};
	
	$scope.mmsMessage =  {
			to: '',
			body: '',
			subject: 'A sample MMS',
			partdata_1: '',
			partcontenttype_1: '',
			partdata_2: '',
			partcontenttype_2: '',
			partdata_3: '',
			partcontenttype_3: ''
	};
	
	$scope.mmsFormStatus = false;
	$scope.mmsMessageStatus = false;
	
	// Configure MMS APIs
	configureMmsApi();
	// Get supported MMS content types
	getSupportedContentTypes();
}
)
;
