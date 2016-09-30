angular.module( 'mymobkit.video', [
  'ui.router',
  'placeholders',
  'ui.bootstrap',
  'resources.media',
  'directives.ngEnter'
])

.config(function config( $stateProvider, $sceProvider) {
  $sceProvider.enabled(false);
  $stateProvider.state( 'video', {
    url: '/video',
    views: {
      "main": {
        controller: 'VideoCtrl',
        templateUrl: 'video/video.tpl.html'
      }
    },
    data:{ pageTitle: 'Video Gallery' }
  });
})
.controller( 'VideoCtrl', function VideoCtrl($rootScope, $scope, Media, $window, $timeout, $uibModal, $http, $log) {

		function formatDate(date) {
			var d = new Date(date),
			month = '' + (d.getMonth() + 1),
			day = '' + d.getDate(),
			year = '' + d.getFullYear();
			
			if (month.length < 2) {
				month = '0' + month;
			}
			if (day.length < 2) {
				day = '0' + day;
			}
			return [year, month, day].join('-');
		}

		var groupItems = function(list, group_by){
			var prev_item = null;
			var group_changed = false;
			// this is a new field which is added to each item where we append "_CHANGED" to indicate a field change in the list
			var new_field = group_by + '_CHANGED';
			var new_field_display = group_by + '_DISPLAYED';
			var new_field_selected = group_by + '_SELECTED';

			// loop through each item in the list
			angular.forEach(list, function(item) {

				group_changed = false;

				// if not the first item
				if (prev_item !== null) {

					var prevDt = formatDate(new Date(prev_item[group_by]));
					var currDt= formatDate(new Date(item[group_by]));
					// check if the group by field changed
					if (prevDt !== currDt) {
						group_changed = true;
					}
				// otherwise we have the first item in the list which is new
				} else {
					group_changed = true;
					
				}
				item[new_field_display] = formatDate(new Date(item[group_by]));
				item[new_field_selected] = false;
				
				// if the group changed, then add a new field to the item to indicate this
				if (group_changed) {
					item[new_field] = true;
				} else {
					item[new_field] = false;
				}

				prev_item = item;
			});
		};

		var isNotDefined = function(val) { 
			return (angular.isUndefined(val) || val === null);
		};	
		
		var configureMediaApi = function(){
			$scope.allMediaLink = $rootScope.host + '/services/api/media/';
			$scope.streamMediaLink = $rootScope.host + '/services/stream/';
			$rootScope.streamVideoLink = $rootScope.host + '/services/stream/video';
		};
		
		var sortVideo = function(v1, v2){
			if (v1.dateTaken > v2.dateTaken) {
				return -1;
			} 
			if (v1.dateTaken < v2.dateTaken) {
				return 1;
			}
			return 0;
		};
		
		var getAllVideos = function(){
			Media.getAllMedia('video').get().$promise.then(
					function success(resp, headers) {						
						$scope.allVideos = resp;
						$scope.videos = $scope.allVideos.videos;	
						$scope.videos.sort(sortVideo);
						groupItems($scope.videos, 'dateTaken');
					}, function err(httpResponse) {
						$scope.errorMsg = httpResponse.status;
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
		
		$scope.checkAll = function () {
			/*
			if ($scope.selectedAll) {
				$scope.selectedAll = true;
			} else {
				$scope.selectedAll = false;
			}
			*/
			angular.forEach($scope.videos, function (video) {
				video.dateTaken_SELECTED = $scope.selectedAll;
			});
		};
		
		$scope.deleteVideos = function() {
			
			var modalInstance = $uibModal.open({
				templateUrl: 'assets/view/confirm_delete.html',
				controller: 'ConfirmDeleteModalCtrl'
			});

			modalInstance.result.then(function () {
				$scope.deleteCount = 0;
				// Proceed to delete the selected videos
				angular.forEach($scope.videos, function (video) {
					if (video.dateTaken_SELECTED) {
						$scope.deleteCount = $scope.deleteCount + 1;
						Media.deleteMedia('video', video.id).get().$promise.then(
							function success(resp, headers) {	
							
							}, function err(httpResponse) {
								$scope.errorMsg = httpResponse.status;
							});
					} 
				});
				
				for (var i = $scope.videos.length - 1; i >= 0; i--) {
					if ($scope.videos[i].dateTaken_SELECTED) {
						$scope.videos.splice(i, 1);
					}
				}
				
			}, function () {
				$log.info('Dismissed at: ' + new Date());
			});
		};
	
		configureMediaApi();
		getAllVideos();
})
.controller( 'VideoModalCtrl', function VideoModalCtrl($rootScope, $scope, $uibModalInstance, video) {
		$scope.video = video;
		$scope.videoLink = $rootScope.streamVideoLink + '?uri=' + video.contentUri + '&id=' + video.id + '&kind=0';
		$scope.close = function () {
			$uibModalInstance.close($scope.video);
		};
})
.controller( 'ConfirmDeleteModalCtrl', function ConfirmDeleteModalCtrl($rootScope, $scope, $uibModalInstance) {
		$scope.yes = function () {
			$uibModalInstance.close();
		};
		
		$scope.no = function () {
			$uibModalInstance.dismiss();
		};
});



		