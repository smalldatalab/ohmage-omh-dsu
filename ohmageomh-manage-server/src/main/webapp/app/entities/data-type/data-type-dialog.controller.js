(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('DataTypeDialogController', DataTypeDialogController);

    DataTypeDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'DataType', 'Integration'];

    function DataTypeDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, DataType, Integration) {
        var vm = this;
        vm.dataType = entity;
        vm.integrations = Integration.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('ohmageApp:dataTypeUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.dataType.id !== null) {
                DataType.update(vm.dataType, onSaveSuccess, onSaveError);
            } else {
                DataType.save(vm.dataType, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
