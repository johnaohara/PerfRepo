/**
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.detail', [

        ])
        .controller('DetailTestController', DetailTestController);

    function DetailTestController(_test, _isUserAlertsSubscriber, testService, testModalService,
                                  metricModalService, alertModalService, testExecutionModalService, $state) {
        var vm = this;
        vm.test = _test;
        vm.isUserAlertsSubscriber = _isUserAlertsSubscriber;
        vm.alertButtonEnabled = true;
        vm.editTest = editTest;
        vm.removeTest = removeTest;
        vm.createTestExecution = createTestExecution;
        vm.showTestExecutions = showTestExecutions;
        vm.subscribeAlerts = subscribeAlerts;
        vm.unsubscribeAlerts = unsubscribeAlerts;
        vm.addMetric = addMetric;
        vm.addAlert = addAlert;
        vm.editAlert = editAlert;
        vm.removeAlert = removeAlert;
        vm.updateDetail = updateDetail;

        function createTestExecution() {
            var modalInstance = testExecutionModalService.createTestExecution(vm.test.id);

            modalInstance.result.then(function (id) {
                $state.go('app.testExecutionDetail', {id: id});
            });
        }

        function showTestExecutions() {
            $state.go('app.testExecutionOverview', {initialFilters: [{id: 'uid', value: vm.test.uid, title: 'Test UID'}]});
        }

        function subscribeAlerts() {
            vm.alertButtonEnabled = false;
            testService.subscribeAlerts(vm.test.id).then(function() {
                vm.isUserAlertsSubscriber = true;
                vm.alertButtonEnabled = true;
            });
        }

        function unsubscribeAlerts() {
            vm.alertButtonEnabled = false;
            testService.unsubscribeAlerts(vm.test.id).then(function() {
                vm.isUserAlertsSubscriber = false;
                vm.alertButtonEnabled = true;
            });
        }

        function addAlert() {
            var modalInstance = alertModalService.createAlert(vm.test.metrics, vm.test.id);

            modalInstance.result.then(function () {
                updateDetail();
            });
        }

        function editAlert(alert) {
            var modalInstance = alertModalService.editAlert(vm.test.metrics, alert.id);

            modalInstance.result.then(function () {
                updateDetail();
            });
        }

        function removeAlert(alert) {
            var modalInstance = alertModalService.removeAlert(alert);

            modalInstance.result.then(function () {
                updateDetail();
            });
        }

        function addMetric() {
            var modalInstance = metricModalService.addOrCreateMetric(vm.test.id);

            modalInstance.result.then(function () {
                updateDetail();
            });
        }

        function editTest() {
            var modalInstance = testModalService.editTest(vm.test.id);

            modalInstance.result.then(function () {
                updateDetail();
            });
        }

        function removeTest() {
            var modalInstance = testModalService.removeTest(vm.test);

            modalInstance.result.then(function () {
                $state.go('app.testOverview');
            });
        }

        function updateDetail() {
            testService.getById(vm.test.id).then(function(response) {
                vm.test = response;
            });
        }
    }
})();