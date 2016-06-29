(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('IntegrationDeleteController',IntegrationDeleteController);

    IntegrationDeleteController.$inject = ['$uibModalInstance', 'entity', 'Integration'];

    function IntegrationDeleteController($uibModalInstance, entity, Integration) {
        var vm = this;
        vm.integration = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Integration.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
