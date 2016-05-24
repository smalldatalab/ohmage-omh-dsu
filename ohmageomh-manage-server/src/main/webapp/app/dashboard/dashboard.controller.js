(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('DashboardController', DashboardController);

    DashboardController.$inject = ['$scope', '$state', '$stateParams', 'Study', 'DataType', 'Participant', 'Data', 'ParseLinks', 'AlertService', 'pagingParams', 'paginationConstants']

    function DashboardController($scope, $state, $stateParams, Study, DataType, Participant, Data, ParseLinks, AlertService, pagingParams, paginationConstants) {
        // Make sure a study is specified
        if(typeof $stateParams.study == 'undefined'){
            $state.go('home');
        }

        var vm = this;
        vm.study = Study.get({id: $stateParams.study});

        vm.loadAll = loadAll;
        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.clear = clear;
        vm.search = search;
        vm.searchQuery = pagingParams.search;
        vm.currentSearch = pagingParams.search;
        vm.loadAll();
        //vm.participants = Study.getParticipants({id: $stateParams.study});

        function loadAll () {
            if (pagingParams.search) {
                ParticipantSearch.query({
                    query: pagingParams.search,
                    page: pagingParams.page - 1,
                    size: paginationConstants.itemsPerPage,
                    sort: sort()
                }, onSuccess, onError);
            } else {
                Study.getParticipants({
                    id: $stateParams.study,
                    page: pagingParams.page - 1,
                    size: paginationConstants.itemsPerPage,
                    sort: sort()
                }, onSuccess, onError);
            }
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.participants = data;
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function loadPage (page) {
            vm.page = page;
            vm.transition();
        }

        function transition () {
            $state.transitionTo($state.$current, {
                study: vm.study.id,
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }


        function search (searchQuery) {
            if (!searchQuery){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = '_score';
            vm.reverse = false;
            vm.currentSearch = searchQuery;
            vm.transition();
        }

        function clear () {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'id';
            vm.reverse = true;
            vm.currentSearch = null;
            vm.transition();
        }
    }
})();
