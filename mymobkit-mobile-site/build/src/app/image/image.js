angular.module( 'mymobkit.image', [
  'ui.router',
  'placeholders',
  'ui.bootstrap',
  'resources.media',
  'directives.ngEnter'
])
.config(["$stateProvider", "$sceProvider", function config($stateProvider, $sceProvider) {
  $sceProvider.enabled(false);
  $stateProvider.state( 'image', {
    url: '/image',
    views: {
      "main": {
        controller: 'ImageCtrl',
        templateUrl: 'image/image.tpl.html'
      }
    },
    data:{ pageTitle: 'Image Gallery' }
  });
}])
.controller( 'ImageCtrl', ["$rootScope", "$scope", "Media", "$window", "$timeout", "$uibModal", "$http", "$log", function ImageCtrl($rootScope, $scope, Media, $window, $timeout, $uibModal, $http, $log) {
		
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

				//console.log(item);

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
			$rootScope.streamImageLink = $rootScope.host + '/services/stream/image';
		};
		
		var sortImage = function(v1, v2){
			if (v1.dateTaken > v2.dateTaken) {
				return -1;
			} 
			if (v1.dateTaken < v2.dateTaken) {
				return 1;
			}
			return 0;
		};
		
		var getAllImages = function(){
			Media.getAllMedia('image').get().$promise.then(
					function success(resp, headers) {						
						$scope.allImages = resp;
						$scope.images = $scope.allImages.images;	
						$scope.images.sort(sortImage);
						groupItems($scope.images, 'dateTaken');
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
		
		$scope.checkAll = function () {
			angular.forEach($scope.images, function (image) {
				image.dateTaken_SELECTED = $scope.selectedAll;
			});
		};
		
		$scope.deleteImages = function() {
			
			var modalInstance = $uibModal.open({
				templateUrl: 'assets/view/confirm_delete.html',
				controller: 'ConfirmDeleteModalCtrl'
			});

			modalInstance.result.then(function () {
				$scope.deleteCount = 0;
				// Proceed to delete the selected images
				angular.forEach($scope.images, function (image) {
					if (image.dateTaken_SELECTED) {
						$scope.deleteCount = $scope.deleteCount + 1;
						Media.deleteMedia('image', image.id).get().$promise.then(
							function success(resp, headers) {	
							
							}, function err(httpResponse) {
								$scope.errorMsg = httpResponse.status;
							});
					} 
				});
				
				for (var i = $scope.images.length - 1; i >= 0; i--) {
					if ($scope.images[i].dateTaken_SELECTED) {
						$scope.images.splice(i, 1);
					}
				}
				
			}, function () {
				$log.info('Dismissed at: ' + new Date());
			});
		};
	
		configureMediaApi();
		getAllImages();
}])
.controller( 'ImageModalCtrl', ["$rootScope", "$scope", "$uibModalInstance", "image", function ImageModalCtrl($rootScope, $scope, $uibModalInstance, image) {
		$scope.image = image;
		$scope.imageLink = $rootScope.streamImageLink + '?uri=' + image.contentUri + '&id=' + image.id + '&kind=0';
		$scope.close = function () {
			$uibModalInstance.close($scope.image);
		};
}])
.controller( 'ConfirmDeleteModalCtrl', ["$rootScope", "$scope", "$uibModalInstance", function ConfirmDeleteModalCtrl($rootScope, $scope, $uibModalInstance) {
		$scope.yes = function () {
			$uibModalInstance.close();
		};
		
		$scope.no = function () {
			$uibModalInstance.dismiss();
		};
}])
;