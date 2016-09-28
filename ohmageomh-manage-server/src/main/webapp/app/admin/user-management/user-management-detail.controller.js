(function() {
    'use strict';

    angular
        .module('ohmageApp')
        .controller('UserManagementDetailController', UserManagementDetailController);

    UserManagementDetailController.$inject = ['$stateParams', '$location', 'User'];

    function UserManagementDetailController ($stateParams, $location, User) {
        var vm = this;

        vm.rootUrl = $location.$$protocol + "://" + $location.$$host;
        if($location.$$port != 80){
            vm.rootUrl = vm.rootUrl + ":" + $location.$$port;
        }

        vm.load = load;
        vm.user = {};

        vm.load($stateParams.login);

        function load (login) {
            User.get({login: login}, function(result) {
                vm.user = result;
            });
        }
    }
})();
