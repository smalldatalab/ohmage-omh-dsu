(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('DataTypeDeleteController',DataTypeDeleteController);

    DataTypeDeleteController.$inject = ['$uibModalInstance', 'entity', 'DataType'];

    function DataTypeDeleteController($uibModalInstance, entity, DataType) {
        var vm = this;
        vm.dataType = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            DataType.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
