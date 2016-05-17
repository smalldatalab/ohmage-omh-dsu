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
    }
})();
