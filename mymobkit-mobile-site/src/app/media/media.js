angular.module( 'mymobkit.media', [
  'ui.router',
  'placeholders',
  'ui.bootstrap',
  'resources.media',
  'directives.ngEnter'
])

.config(function config( $stateProvider, $sceProvider) {
  $sceProvider.enabled(false);
  $stateProvider.state( 'media', {
    url: '/media',
    views: {
      "main": {
        controller: 'MediaCtrl',
        templateUrl: 'media/media.tpl.html'
      }
    },
    data:{ pageTitle: 'Media' }
  });
})

.controller( 'MediaCtrl', function MediaCtrl($rootScope, $scope, Media, $window, $timeout, $uibModal, $http, $log) {

		var isNotDefined = function(val) { 
			return (angular.isUndefined(val) || val === null);
		};	
		
		var configureMediaApi = function(){
			$scope.allMediaLink = $rootScope.host + '/services/api/media/';
			$scope.streamMediaLink = $rootScope.host + '/services/stream/';
			$rootScope.streamImageLink = $rootScope.host + '/services/stream/image';
			$rootScope.streamVideoLink = $rootScope.host + '/services/stream/video';
		};
		var getAllImages = function(){
			Media.getAllMedia('image').get().$promise.then(
					function success(resp, headers) {						
						$scope.allImages = resp;
						$scope.images = $scope.allImages.images;	
					}, function err(httpResponse) {
						$scope.errorMsg = httpResponse.status;
					});
		};	
		
		var getAllVideos = function(){
			Media.getAllMedia('video').get().$promise.then(
					function success(resp, headers) {						
						$scope.allVideos = resp;
						$scope.videos = $scope.allVideos.videos;	
					}, function err(httpResponse) {
						$scope.errorMsg = httpResponse.status;
					});
		};	
		
		
		$scope.showImage = function (index) {
			
			var modalInstance = $uibModal.open({
				templateUrl: 'assets/view/image.html',
				controller: 'ImageModalCtrl',
				resolve: {
					image: function () {
						return $scope.images[index];
					}
				}
			});

			modalInstance.result.then(function (selected) {
				// Do nothing
			}, function () {
				$log.info('Dismissed at: ' + new Date());
			});
		};
	
	
		$scope.showVideo = function (index) {
			
			var modalInstance = $uibModal.open({
				templateUrl: 'assets/view/video.html',
				controller: 'VideoModalCtrl',
				resolve: {
					video: function () {
						return $scope.videos[index];
					}
				}
			});

			modalInstance.result.then(function (selected) {
				// Do nothing
			}, function () {
				$log.info('Dismissed at: ' + new Date());
			});
		};
		
		configureMediaApi();
		getAllImages();
		getAllVideos();
})
.controller( 'ImageModalCtrl', function ImageModalCtrl($scope, $uibModalInstance, image) {
		$scope.image = image;
		$scope.close = function () {
			$uibModalInstance.close($scope.image);
		};
})
.controller( 'VideoModalCtrl', function VideoModalCtrl($rootScope, $scope, $uibModalInstance, video) {
		$scope.video = video;
		$scope.videoLink = $rootScope.streamVideoLink + '?uri=' + video.contentUri + '&id=' + video.id + '&kind=0';
		$scope.close = function () {
			$uibModalInstance.close($scope.video);
		};
});


		