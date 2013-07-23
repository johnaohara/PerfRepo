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
package org.jboss.qa.perfrepo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.to.TestExecutionSearchTO;
import org.jboss.qa.perfrepo.service.ServiceException;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;

@Named
@ViewScoped
public class SearchController extends ControllerBase {

   private static final long serialVersionUID = 1L;

   //private static final Logger log = Logger.getLogger(SearchController.class);

   @Inject
   private TestService testService;

   private String tag;

   private TestExecutionSearchTO bean = null;

   private List<TestExecution> result;

   @PostConstruct
   public void init() {
      if (bean == null) {
         bean = new TestExecutionSearchTO();
      }
      if (result == null) {
         result = new ArrayList<TestExecution>();
      }
   }

   public TestExecutionSearchTO getBean() {
      return bean;
   }

   public String getTag() {
      return tag;
   }

   public void setTag(String tag) {
      this.tag = tag;
   }

   public String search() {
      result = testService.searchTestExecutions(bean);
      return null;
   }

   public String delete() {
      Long idToDelete = Long.valueOf(getRequestParam("idToDelete"));
      if (idToDelete == null) {
         throw new IllegalStateException("Bad request, missing idToDelete");
      } else {
         TestExecution execToRemove = removeById(idToDelete);
         if (execToRemove == null) {
            throw new IllegalStateException("Bad request, missing idToDelete");
         } else {
            try {
               testService.deleteTestExecution(execToRemove);
               addMessage(INFO, "page.execSearch.execSucessfullyDeleted", execToRemove.getName());
            } catch (ServiceException e) {
               addMessageFor(e);
            }
         }
      }
      return null;
   }

   private TestExecution removeById(Long id) {
      for (TestExecution exec : result) {
         if (exec.getId().equals(id)) {
            result.remove(exec);
            return exec;
         }
      }
      return null;
   }

   public List<TestExecution> getResult() {
      return result;
   }

   public void setResult(List<TestExecution> result) {
      this.result = result;
   }

}