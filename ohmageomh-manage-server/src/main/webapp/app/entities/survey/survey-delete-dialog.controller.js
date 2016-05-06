(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('SurveyDeleteController',SurveyDeleteController);

    SurveyDeleteController.$inject = ['$uibModalInstance', 'entity', 'Survey'];

    function SurveyDeleteController($uibModalInstance, entity, Survey) {
        var vm = this;
        vm.survey = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Survey.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
