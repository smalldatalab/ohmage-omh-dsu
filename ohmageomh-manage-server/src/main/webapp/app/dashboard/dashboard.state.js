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
                url: '/detail?participant',
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
            .state('dashboard.newParticipant', {
                parent: 'dashboard',
                url: '/newParticipant',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/dashboard/dashboard-participant-dialog.html',
                        controller: 'DashboardParticipantDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    dsuId: null,
                                    label: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function() {
                            $state.go('dashboard', null, { reload: true });
                        }, function() {
                            $state.go('dashboard');
                        });
                }]
            })
            .state('dashboard.editParticipant', {
                parent: 'dashboard',
                url: '/editParticipant?participant',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/dashboard/dashboard-participant-dialog.html',
                        controller: 'DashboardParticipantDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: ['Participant', function(Participant) {
                                return Participant.get({id : $stateParams.participant});
                            }]
                        }
                    }).result.then(function() {
                            $state.go('dashboard', null, { reload: true });
                        }, function() {
                            $state.go('dashboard');
                        });
                }]
            })
    }
})();
