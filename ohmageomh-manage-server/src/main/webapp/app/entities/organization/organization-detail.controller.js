(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('OrganizationDetailController', OrganizationDetailController);

    OrganizationDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Organization', 'Study', 'User'];

    function OrganizationDetailController($scope, $rootScope, $stateParams, entity, Organization, Study, User) {
        var vm = this;
        vm.organization = entity;
        
        var unsubscribe = $rootScope.$on('ohmageApp:organizationUpdate', function(event, result) {
            vm.organization = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
