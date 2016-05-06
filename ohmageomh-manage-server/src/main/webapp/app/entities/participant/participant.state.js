(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('participant', {
            parent: 'entity',
            url: '/participant?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Participants'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/participant/participants.html',
                    controller: 'ParticipantController',
                    controllerAs: 'vm'
                }
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
        .state('participant-detail', {
            parent: 'entity',
            url: '/participant/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Participant'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/participant/participant-detail.html',
                    controller: 'ParticipantDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Participant', function($stateParams, Participant) {
                    return Participant.get({id : $stateParams.id});
                }]
            }
        })
        .state('participant.new', {
            parent: 'participant',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/participant/participant-dialog.html',
                    controller: 'ParticipantDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                firstName: null,
                                lastName: null,
                                gmail: null,
                                username: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('participant', null, { reload: true });
                }, function() {
                    $state.go('participant');
                });
            }]
        })
        .state('participant.edit', {
            parent: 'participant',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/participant/participant-dialog.html',
                    controller: 'ParticipantDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Participant', function(Participant) {
                            return Participant.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('participant', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('participant.delete', {
            parent: 'participant',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/participant/participant-delete-dialog.html',
                    controller: 'ParticipantDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Participant', function(Participant) {
                            return Participant.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('participant', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
