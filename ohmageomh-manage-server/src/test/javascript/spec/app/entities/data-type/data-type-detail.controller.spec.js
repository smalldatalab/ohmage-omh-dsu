'use strict';

describe('Controller Tests', function() {

    describe('DataType Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockDataType, MockIntegration;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockDataType = jasmine.createSpy('MockDataType');
            MockIntegration = jasmine.createSpy('MockIntegration');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'DataType': MockDataType,
                'Integration': MockIntegration
            };
            createController = function() {
                $injector.get('$controller')("DataTypeDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'ohmageApp:dataTypeUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
