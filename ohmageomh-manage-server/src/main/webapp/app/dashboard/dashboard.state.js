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
                },
                params: {
                    page: {
                        value: '1',
                        squash: true
                    },
                    sort: {
                        value: 'id,asc',
                        squash: true
                    },
                    search: null
                },
                resolve: {
                    pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                        return {
                            page: PaginationUtil.parsePage($stateParams.page),
                            sort: $stateParams.sort,
                            predicate: PaginationUtil.parsePredicate($stateParams.sort),
                            ascending: PaginationUtil.parseAscending($stateParams.sort),
                            search: $stateParams.search
                        };
                    }],
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
