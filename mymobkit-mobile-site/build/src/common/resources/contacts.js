angular.module('resources.contacts', [ 'ngResource' ]);
angular.module('resources.contacts').factory(
		'Contacts',
		[
				'$rootScope',
				'$resource',
				'$location',
				'$http',
				function($rootScope, $resource, $location, $http) {
					var contactsService = {};									
					contactsService.getAllContacts = function() {							
							var path = $rootScope.host + '/services/api/contact/';
							return $resource(path, {},
									{
										get : {
											method : 'GET',
											isArray : false,
											timeout: 180000
										}
									});
					};
					return contactsService;

		} ]);