(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('organization', {
            parent: 'entity',
            url: '/organization?page&sort&search',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'Organizations'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/organization/organizations.html',
                    controller: 'OrganizationController',
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
        .state('organization-detail', {
            parent: 'entity',
            url: '/organization/{id}',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'Organization'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/organization/organization-detail.html',
                    controller: 'OrganizationDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Organization', function($stateParams, Organization) {
                    return Organization.get({id : $stateParams.id});
                }]
            }
        })
        .state('organization.new', {
            parent: 'organization',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/organization/organization-dialog.html',
                    controller: 'OrganizationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('organization', null, { reload: true });
                }, function() {
                    $state.go('organization');
                });
            }]
        })
        .state('organization.edit', {
            parent: 'organization',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/organization/organization-dialog.html',
                    controller: 'OrganizationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Organization', function(Organization) {
                            return Organization.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('organization', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('organization.delete', {
            parent: 'organization',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/organization/organization-delete-dialog.html',
                    controller: 'OrganizationDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Organization', function(Organization) {
                            return Organization.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('organization', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
