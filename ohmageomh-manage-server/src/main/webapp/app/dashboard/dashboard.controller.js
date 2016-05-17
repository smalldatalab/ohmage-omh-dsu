(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('DashboardController', DashboardController);

    DashboardController.$inject = ['$scope', '$state', '$stateParams', 'Study', 'DataType', 'Participant', 'Data']

    function DashboardController($scope, $state, $stateParams, Study, DataType, Participant, Data) {
        // Make sure a study is specified
        if(typeof $stateParams.study == 'undefined'){
            $state.go('home');
        }

        var vm = this;
        vm.study = Study.get({id: $stateParams.study});
        vm.participants = Study.getParticipants({id: $stateParams.study});
    }
})();
