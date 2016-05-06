(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('NoteDeleteController',NoteDeleteController);

    NoteDeleteController.$inject = ['$uibModalInstance', 'entity', 'Note'];

    function NoteDeleteController($uibModalInstance, entity, Note) {
        var vm = this;
        vm.note = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Note.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
