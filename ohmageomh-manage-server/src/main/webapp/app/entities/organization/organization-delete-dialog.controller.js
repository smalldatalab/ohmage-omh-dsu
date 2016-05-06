(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('OrganizationDeleteController',OrganizationDeleteController);

    OrganizationDeleteController.$inject = ['$uibModalInstance', 'entity', 'Organization'];

    function OrganizationDeleteController($uibModalInstance, entity, Organization) {
        var vm = this;
        vm.organization = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Organization.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
