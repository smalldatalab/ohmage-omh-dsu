(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('DataController', DataController);

    DataController.$inject = ['$scope', '$state']

    function DataController($scope, $state) {
        var vm = this;

        $scope.gridOptions = {
            enableGridMenu: true,
            exporterCsvFilename: 'OhmageDataExport.csv',
            exporterMenuPdf: false,
        };

        $scope.gridOptions.data = [
                {
                    "_id" : "cornell.mobility-daily-summary_v2.0_HSS_LC_1_moves-app_2015-09-09",
                    "user_id" : "HSS_LC_1",
                    "date" : "2015-09-09",
                    "active_time_in_seconds" : 17.0,
                    "walking_distance_in_km" : 0.012,
                    "steps" : 23,
                    "geodiameter_in_km" : 0,
                    "max_gait_speed_in_meter_per_second" : null,
                    "leave_home_time" : null,
                    "return_home_time" : null,
                    "time_not_at_home_in_seconds" : 0,
                    "coverage" : 0.3000613433025845
                },
                {
                    "_id" : "cornell.mobility-daily-summary_v2.0_HSS_LC_1_moves-app_2015-09-10",
                    "user_id" : "HSS_LC_1",
                    "date" : "2015-09-10",
                    "active_time_in_seconds" : 246.0,
                    "walking_distance_in_km" : 0.15499999999999997,
                    "steps" : 309,
                    "geodiameter_in_km" : 12.540653756900873,
                    "max_gait_speed_in_meter_per_second" : null,
                    "leave_home_time" : "2015-09-10T14:22:14.000-04:00",
                    "return_home_time" : "2015-09-10T21:18:51.000-04:00",
                    "time_not_at_home_in_seconds" : 20817,
                    "coverage" : 1.002858829384599
                }
        ];
    }
})();
