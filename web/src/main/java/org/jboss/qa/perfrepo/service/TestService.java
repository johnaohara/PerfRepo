/* 
 * Copyright 2013 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.qa.perfrepo.service;

import java.util.Collection;
import java.util.List;

import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionAttachment;
import org.jboss.qa.perfrepo.model.TestExecutionParameter;
import org.jboss.qa.perfrepo.model.TestExecutionTag;
import org.jboss.qa.perfrepo.model.TestMetric;
import org.jboss.qa.perfrepo.model.Value;
import org.jboss.qa.perfrepo.model.ValueParameter;
import org.jboss.qa.perfrepo.model.to.MetricReportTO;
import org.jboss.qa.perfrepo.model.to.TestExecutionSearchTO;
import org.jboss.qa.perfrepo.model.to.TestSearchTO;
import org.jboss.qa.perfrepo.service.ServiceException.Codes;

/**
 * 
 * Main facade to the test execution services.
 * 
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 */
public interface TestService extends Codes {

   /**
    * Stores a new test execution.
    * 
    * The new test execution needs to contain test UID and the referred test has to exist.
    * 
    * @param testExecution New test execution.
    * @return Created test execution. Contains database IDs.
    * @throws ServiceException
    */
   TestExecution createTestExecution(TestExecution testExecution) throws ServiceException;

   /**
    * Returns list of TestExecutionss according to criteria defined by TestExecutionSearchTO
    * 
    * @param search
    * @return
    */
   List<TestExecution> searchTestExecutions(TestExecutionSearchTO search);

   /**
    * Returns list of Tests according to criteria defined by TestSearchTO
    * 
    * @param search
    * @return
    */
   List<Test> searchTest(TestSearchTO search);

   /**
    * TODO: comment
    * 
    * @param ids
    * @return
    */
   List<TestExecution> getTestExecutions(Collection<Long> ids);

   /**
    * Add attachment to the test execution. The {@link TestExecution} object referred by attachment
    * needs to be an empty object with only id set.
    * 
    * @param attachment
    * @return id of newly created attachment
    */
   Long addAttachment(TestExecutionAttachment attachment) throws ServiceException;

   /**
    * Get test execution attachment by id.
    * 
    * @param id
    * @return
    */
   TestExecutionAttachment getAttachment(Long id);

   /**
    * Create a new test with collection of metrics. Eech metric will be created with
    * {@link TestService#storeMetric(Test, Metric)} method. Group id of the new test needs to be one
    * of the current user's roles.
    * 
    * @param test
    * @return
    * @throws ServiceException
    */
   Test createTest(Test test) throws ServiceException;

   /**
    * Delete a test execution with all it's subobjects.
    * 
    * @param testExecution
    * @throws ServiceException
    */
   void deleteTestExecution(TestExecution testExecution) throws ServiceException;

   /**
    * Get {@link TestExecution} with all details.
    * 
    * @param id
    * @return
    */
   TestExecution getFullTestExecution(Long id);

   List<TestExecution> getAllFullTestExecutions();

   List<TestExecution> findExecutionsByTest(Long testId);

   /**
    * Add metric to given test.
    * 
    * @param test
    * @param metric
    * @return
    * @throws ServiceException
    */
   TestMetric addMetric(Test test, Metric metric) throws ServiceException;

   /**
    * Update metric.
    * 
    * @param test
    * @param metric
    * @return Updated metric
    * @throws ServiceException
    */
   Metric updateMetric(Test test, Metric metric) throws ServiceException;

   /**
    * Get test with all metrics but without executions.
    * 
    * @param testId Test id
    * @return Test
    */
   Test getFullTest(Long testId);

   /**
    * Delete a test with all it's sub-objects. Use with caution!
    * 
    * @param test
    * @throws ServiceException
    */
   void deleteTest(Test test) throws ServiceException;

   Test updateTest(Test test);

   List<Test> getAllFullTests();

   Test getOrCreateTest(Test test) throws ServiceException;

   /**
    * Get metric with all associated tests (without details).
    * 
    * @param id
    * @return
    */
   Metric getFullMetric(Long id);

   List<Metric> getAllFullMetrics();

   /**
    * Returns all metric by name prefix, which belong tests with defined group id
    * 
    * @param name
    * @param test
    * @return
    */
   List<Metric> getMetrics(String name, Test test);

   /**
    * Returns metrics which belong tests with defined group id and are not defined on the defined
    * test
    * 
    * @param name
    * @param test
    * @return
    */
   List<Metric> getAvailableMetrics(Test test);

   /**
    * Returns all metrics, which are defined on the Test
    * 
    * @return
    */
   List<Metric> getTestMetrics(Test test);

   /**
    * Delete metric from the test.
    * 
    * @param test
    * @param metric
    * @throws ServiceException
    */
   void deleteMetric(Test test, Metric metric) throws ServiceException;

   /**
    * Removes metric from defined test. The method removes only relation between test and metric.
    * The metric itself is not deleted.
    * 
    * @param test
    * @param metric
    */
   void deleteTestMetric(TestMetric tm);

   TestExecution updateTestExecution(TestExecution testExecution) throws ServiceException;

   /**
    * Adds TestExecutionParamter to existing TestExecution
    * 
    * @param te TestExecution
    * @param tep TestExecutionParameter to add
    * @throws ServiceException
    */
   TestExecutionParameter addTestExecutionParameter(TestExecution te, TestExecutionParameter tep) throws ServiceException;

   /**
    * Returns TestExecutionParameter by id
    * 
    * @param id TestExecutionParameter
    * @return
    */
   TestExecutionParameter getTestExecutionParameter(Long id);

   /**
    * Updates TestExecutionParameter
    * 
    * @param tep TestExecutionParameter to update
    * @return
    */
   TestExecutionParameter updateTestExecutionParameter(TestExecutionParameter tep);

   /**
    * Removes TestExecutionParameter
    * 
    * @param tep
    * @return
    */
   void deleteTestExecutionParameter(TestExecutionParameter tep);

   /**
    * Adds TestExecutionTag to existing TestExecution
    * 
    * @param te TestExecution
    * @param tep TestExecutionTag to add
    * @throws ServiceException
    */
   TestExecutionTag addTestExecutionTag(TestExecution te, TestExecutionTag teg) throws ServiceException;

   /**
    * Removes TestExecutionParameter
    * 
    * @param tep
    * @return
    */
   void deleteTestExecutionTag(TestExecutionTag teg);

   /**
    * Creates Value Parameter to Value
    * 
    * @param value
    * @param vp
    * @return
    */
   ValueParameter addValueParameter(Value value, ValueParameter vp);

   /**
    * Updates Value Parameter
    * 
    * @param vp
    * @return
    */
   ValueParameter updateValueParameter(ValueParameter vp);

   /**
    * Removes Value Parameter
    * 
    * @param vp
    */
   void deleteValueParameter(ValueParameter vp);

   /**
    * Return value according to id
    * 
    * @param id
    * @return
    */
   Value getValue(Long id);

   /**
    * Adds Value to Test Execution
    * 
    * @param te
    * @param value
    * @return
    */
   Value addValue(TestExecution te, Value value);

   /**
    * Updates Test Execution Value
    * 
    * @param value
    * @return
    */
   Value updateValue(Value value);

   /**
    * Removes value from TestExecution
    * 
    * @param value
    */
   void deleteValue(Value value);

   /**
    * Computes metric report.
    * 
    * @param request
    * @return response TO
    */
   MetricReportTO.Response computeMetricReport(MetricReportTO.Request request);
}