(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('survey', {
            parent: 'entity',
            url: '/survey?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Surveys'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/survey/surveys.html',
                    controller: 'SurveyController',
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
        .state('survey-detail', {
            parent: 'entity',
            url: '/survey/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Survey'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/survey/survey-detail.html',
                    controller: 'SurveyDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Survey', function($stateParams, Survey) {
                    return Survey.get({id : $stateParams.id});
                }]
            }
        })
        .state('survey.new', {
            parent: 'survey',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/survey/survey-dialog.html',
                    controller: 'SurveyDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                version: null,
                                description: null,
                                isPublic: false,
                                definition: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('survey', null, { reload: true });
                }, function() {
                    $state.go('survey');
                });
            }]
        })
        .state('survey.edit', {
            parent: 'survey',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/survey/survey-dialog.html',
                    controller: 'SurveyDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Survey', function(Survey) {
                            return Survey.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('survey', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('survey.delete', {
            parent: 'survey',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/survey/survey-delete-dialog.html',
                    controller: 'SurveyDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Survey', function(Survey) {
                            return Survey.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('survey', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
