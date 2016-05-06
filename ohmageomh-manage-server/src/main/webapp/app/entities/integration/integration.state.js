(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('integration', {
            parent: 'entity',
            url: '/integration?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Integrations'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/integration/integrations.html',
                    controller: 'IntegrationController',
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
        .state('integration-detail', {
            parent: 'entity',
            url: '/integration/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Integration'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/integration/integration-detail.html',
                    controller: 'IntegrationDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Integration', function($stateParams, Integration) {
                    return Integration.get({id : $stateParams.id});
                }]
            }
        })
        .state('integration.new', {
            parent: 'integration',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/integration/integration-dialog.html',
                    controller: 'IntegrationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                description: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('integration', null, { reload: true });
                }, function() {
                    $state.go('integration');
                });
            }]
        })
        .state('integration.edit', {
            parent: 'integration',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/integration/integration-dialog.html',
                    controller: 'IntegrationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Integration', function(Integration) {
                            return Integration.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('integration', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('integration.delete', {
            parent: 'integration',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/integration/integration-delete-dialog.html',
                    controller: 'IntegrationDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Integration', function(Integration) {
                            return Integration.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('integration', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
