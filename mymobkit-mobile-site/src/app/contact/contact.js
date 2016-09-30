angular.module( 'mymobkit.contact', [
  'ui.router',
  'placeholders',
  'ui.bootstrap',
  'resources.contacts',
  'ngGrid'
])

.config(function config( $stateProvider ) {
  $stateProvider.state( 'contact', {
    url: '/contact',
    views: {
      "main": {
        controller: 'ContactCtrl',
        templateUrl: 'contact/contact.tpl.html'
      }
    },
    data:{ pageTitle: 'Contact' }
  });
})

.controller( 'ContactCtrl', function ContactCtrl( $rootScope, $scope, Contacts, $window, $timeout, $http, $location, $anchorScroll) {
	var isNotDefined = function(val) { 
		return (angular.isUndefined(val) || val === null);
	};	
	
	var configureContactApi = function(){
		$scope.contactLink = $rootScope.host + '/services/api/contact/';
	};
	
	var getAllContacts = function(){
		// Use get to return an object, query for array
		Contacts.getAllContacts().get().$promise.then(
				function success(resp, headers) {						
					$scope.allContacts = resp;
					$scope.contacts = $scope.allContacts.contacts;	
				}, function err(httpResponse) {
					$scope.errorMsg = httpResponse.status;
				});
	};	
	
	$scope.contacts = [];	
	$scope.selectedContact = [];	
	$scope.contactGrid = { 
		data: 'contacts',
		enableRowSelection: true,
        enableCellEditOnFocus: false,
		showGroupPanel: true,
        multiSelect: false, 
		showColumnMenu:true,
		showFilter:false,
		enableColumnResize:true,
		columnDefs: [
		{field: 'name', displayName: 'Name'},
		{field: 'phones', displayName: 'Phones'},
		{field: 'emails', displayName: 'Emails'},
		{field: 'vCard', displayName: 'vCard', visible:false}
		],
		selectedItems : $scope.selectedContact,
		afterSelectionChange: function(rowItem) {
			if (rowItem.selected) {
			}
		}
	};
	
	configureContactApi();
	getAllContacts();
}
)
;
