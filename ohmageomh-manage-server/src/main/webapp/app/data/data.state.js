(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('data', {
                parent: 'app',
                url: '/data?study',
                data: {
                    authorities: ['ROLE_ADMIN', 'ROLE_USER'],
                    pageTitle: 'Data'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/data/data.html',
                        controller: 'DataController',
                        controllerAs: 'vm'
                    },
                }
            })
            .state('data.media', {
                parent: 'data',
                url: '/media',
                params: {
                    mediaArray: []
                },
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/data/data-media.html',
                        controller: 'DataMediaController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                    }).result.then(function() {
                            $state.go('data', null, { reload: true });
                        }, function() {
                            $state.go('^');
                        });
                }]
            })
    }
})();
