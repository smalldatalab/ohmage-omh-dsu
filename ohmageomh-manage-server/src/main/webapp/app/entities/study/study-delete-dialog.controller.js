(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('StudyDeleteController',StudyDeleteController);

    StudyDeleteController.$inject = ['$uibModalInstance', 'entity', 'Study'];

    function StudyDeleteController($uibModalInstance, entity, Study) {
        var vm = this;
        vm.study = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Study.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
