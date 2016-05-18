(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('DashboardDetailController', DashboardDetailController);

    DashboardDetailController.$inject = ['$scope', '$state', '$stateParams', 'Study', 'DataType', 'Participant', 'Data']

    function DashboardDetailController($scope, $state, $stateParams, Study, DataType, Participant, Data) {
        // Make sure a study is specified
        if(typeof $stateParams.study == 'undefined' || typeof $stateParams.participant == 'undefined'){
            $state.go('home');
        }

        var vm = this;
        vm.study = Study.get({id: $stateParams.study});
        vm.participant = Study.getParticipant({id: $stateParams.study, participant: $stateParams.participant});
    }
})();
