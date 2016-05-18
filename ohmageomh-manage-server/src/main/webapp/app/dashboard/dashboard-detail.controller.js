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

        //These variables MUST be set as a minimum for the calendar to work
        vm.calendarView = 'month';
        vm.viewDate = new Date();
        vm.events = [
            {
                title: 'Step Count',
                type: 'warning',
                startsAt: moment().startOf('week').subtract(2, 'days').toDate(),
                draggable: false,
                resizable: false,
                editable: false,
                deletable: false,
                allDay: true
            },
            {
                title: 'Step Count',
                type: 'warning',
                startsAt: moment().startOf('week').subtract(5, 'days').add(8, 'hours').toDate(),
                draggable: false,
                resizable: false,
                editable: false,
                deletable: false
            },
            {
                title: '<i class="glyphicon glyphicon-asterisk"></i> <span class="text-primary">Physical Activity</span>, with styling.',
                type: 'info',
                startsAt: moment().subtract(1, 'day').toDate(),
                draggable: false,
                resizable: false,
                editable: false,
                deletable: false
            }, {
                title: 'Survey Response',
                type: 'important',
                startsAt: moment().startOf('day').add(7, 'hours').toDate(),
                draggable: false,
                resizable: false,
                editable: false,
                deletable: false
            }
        ];

        vm.isCellOpen = true;

        vm.eventClicked = function(event) {
            alert.show('Clicked', event);
        };

        vm.eventEdited = function(event) {
            alert.show('Edited', event);
        };

        vm.eventDeleted = function(event) {
            alert.show('Deleted', event);
        };

        vm.eventTimesChanged = function(event) {
            alert.show('Dropped or resized', event);
        };

        vm.toggle = function($event, field, event) {
            $event.preventDefault();
            $event.stopPropagation();
            event[field] = !event[field];
        };
    }
})();
