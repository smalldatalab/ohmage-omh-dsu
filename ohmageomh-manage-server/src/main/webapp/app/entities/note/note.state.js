(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('note', {
            parent: 'entity',
            url: '/note?page&sort&search',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'Notes'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/note/notes.html',
                    controller: 'NoteController',
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
        .state('note-detail', {
            parent: 'entity',
            url: '/note/{id}',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'Note'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/note/note-detail.html',
                    controller: 'NoteDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Note', function($stateParams, Note) {
                    return Note.get({id : $stateParams.id});
                }]
            }
        })
        .state('note.new', {
            parent: 'note',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/note/note-dialog.html',
                    controller: 'NoteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                body: null,
                                date: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('note', null, { reload: true });
                }, function() {
                    $state.go('note');
                });
            }]
        })
        .state('note.edit', {
            parent: 'note',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/note/note-dialog.html',
                    controller: 'NoteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Note', function(Note) {
                            return Note.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('note', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('note.delete', {
            parent: 'note',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/note/note-delete-dialog.html',
                    controller: 'NoteDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Note', function(Note) {
                            return Note.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('note', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
