(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService', '$state', 'Study'];

    function HomeController ($scope, Principal, LoginService, $state, Study) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.studies = null;
        vm.login = LoginService.open;
        vm.register = register;
        $scope.$on('authenticationSuccess', function() {
            getAccount();
            getStudies();
        });

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
                getStudies();
            });
        }
        function register () {
            $state.go('register');
        }
        function getStudies() {
            if(vm.isAuthenticated()){
                vm.studies = Study.query();
            }
        }
    }
})();
