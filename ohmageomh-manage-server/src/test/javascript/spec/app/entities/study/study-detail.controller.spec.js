'use strict';

describe('Controller Tests', function() {

    describe('Study Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockStudy, MockUser, MockSurvey, MockIntegration, MockParticipant, MockOrganization;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockStudy = jasmine.createSpy('MockStudy');
            MockUser = jasmine.createSpy('MockUser');
            MockSurvey = jasmine.createSpy('MockSurvey');
            MockIntegration = jasmine.createSpy('MockIntegration');
            MockParticipant = jasmine.createSpy('MockParticipant');
            MockOrganization = jasmine.createSpy('MockOrganization');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Study': MockStudy,
                'User': MockUser,
                'Survey': MockSurvey,
                'Integration': MockIntegration,
                'Participant': MockParticipant,
                'Organization': MockOrganization
            };
            createController = function() {
                $injector.get('$controller')("StudyDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'ohmageApp:studyUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
