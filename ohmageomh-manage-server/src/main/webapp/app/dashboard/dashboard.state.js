(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('dashboard', {
                parent: 'app',
                url: '/dashboard?study',
                data: {
                    authorities: ['ROLE_ADMIN', 'ROLE_USER'],
                    pageTitle: 'Dashboard'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/dashboard/dashboard.html',
                        controller: 'DashboardController',
                        controllerAs: 'vm'
                    },
                }
            })
            .state('dashboard-detail', {
                parent: 'dashboard',
                url: '/dashboard/detail?participant',
                data: {
                    authorities: ['ROLE_ADMIN', 'ROLE_USER'],
                    pageTitle: 'Dashboard'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/dashboard/dashboard-detail.html',
                        controller: 'DashboardDetailController',
                        controllerAs: 'vm'
                    },
                }
            })
    }
})();
