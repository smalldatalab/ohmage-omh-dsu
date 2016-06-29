(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('IntegrationDialogController', IntegrationDialogController);

    IntegrationDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Integration', 'DataType', 'Study'];

    function IntegrationDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Integration, DataType, Study) {
        var vm = this;
        vm.integration = entity;
        vm.datatypes = DataType.query({size: 5000});
        vm.studies = Study.query({size: 5000});

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('ohmageApp:integrationUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.integration.id !== null) {
                Integration.update(vm.integration, onSaveSuccess, onSaveError);
            } else {
                Integration.save(vm.integration, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
