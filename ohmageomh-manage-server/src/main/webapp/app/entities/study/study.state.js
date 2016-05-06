(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('study', {
            parent: 'entity',
            url: '/study?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Studies'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/study/studies.html',
                    controller: 'StudyController',
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
        .state('study-detail', {
            parent: 'entity',
            url: '/study/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Study'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/study/study-detail.html',
                    controller: 'StudyDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Study', function($stateParams, Study) {
                    return Study.get({id : $stateParams.id});
                }]
            }
        })
        .state('study.new', {
            parent: 'study',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/study/study-dialog.html',
                    controller: 'StudyDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                removeGps: false,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('study', null, { reload: true });
                }, function() {
                    $state.go('study');
                });
            }]
        })
        .state('study.edit', {
            parent: 'study',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/study/study-dialog.html',
                    controller: 'StudyDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Study', function(Study) {
                            return Study.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('study', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('study.delete', {
            parent: 'study',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/study/study-delete-dialog.html',
                    controller: 'StudyDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Study', function(Study) {
                            return Study.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('study', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
