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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.jboss.qa.perfrepo.controller.JFreechartBean.XYLineChartSpec;
import org.jboss.qa.perfrepo.model.Metric;
import org.jboss.qa.perfrepo.model.Test;
import org.jboss.qa.perfrepo.model.TestExecution;
import org.jboss.qa.perfrepo.model.TestExecutionAttachment;
import org.jboss.qa.perfrepo.model.TestExecutionParameter;
import org.jboss.qa.perfrepo.model.TestExecutionTag;
import org.jboss.qa.perfrepo.model.Value;
import org.jboss.qa.perfrepo.model.ValueParameter;
import org.jboss.qa.perfrepo.model.util.EntityUtil;
import org.jboss.qa.perfrepo.rest.TestExecutionREST;
import org.jboss.qa.perfrepo.service.ServiceException;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.util.MultiValue;
import org.jboss.qa.perfrepo.util.MultiValue.ParamInfo;
import org.jboss.qa.perfrepo.util.MultiValue.ValueInfo;
import org.jboss.qa.perfrepo.util.Util;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Details of {@link TestExecution}
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named
@ViewScoped
public class TestExecutionController extends ControllerBase {

   private static final long serialVersionUID = 3012075520261954430L;
   private static final Logger log = Logger.getLogger(TestExecutionController.class);

   @Inject
   private TestService testService;

   private TestExecution testExecution = null;
   private Test test = null;

   private TestExecutionParameter editedParameter = null;
   private Value editedValue = null;
   private Long editedValueMetricSelectionId = null;

   private List<ValueInfo> values = null;

   private List<ParamInfo> selectedMultiValueList = null;
   private List<String> selectedMultiValueParamSelectionList = null;
   private String selectedMultiValueParamSelection = null;
   private ValueInfo selectedMultiValue = null;
   private XYLineChartSpec chartData = null;

   private boolean editMode;
   private boolean createMode;
   private Long testExecutionId;

   public Long getTestExecutionId() {
      return testExecutionId;
   }

   public void setTestExecutionId(Long testExecutionId) {
      this.testExecutionId = testExecutionId;
   }

   public boolean isEditMode() {
      return editMode;
   }

   public void setEditMode(boolean editMode) {
      this.editMode = editMode;
   }

   public boolean isCreateMode() {
      return createMode;
   }

   public void setCreateMode(boolean createMode) {
      this.createMode = createMode;
   }

   public void preRender() {
      reloadSessionMessages();
      if (testExecutionId == null) {
         if (!createMode) {
            log.error("No execution ID supplied");
            redirectWithMessage("/", ERROR, "page.exec.errorNoExecId");
         } else {
            if (testExecution == null) {
               testExecution = new TestExecution();
            }
         }
      } else {
         if (testExecution == null) {
            testExecution = testService.getFullTestExecution(testExecutionId);
            if (testExecution == null) {
               log.error("Can't find execution with id " + testExecutionId);
               redirectWithMessage("/", ERROR, "page.exec.errorExecNotFound", testExecutionId);
            } else {
               test = testService.getFullTest(testExecution.getTest().getId());
               if (test == null) {
                  log.error("Can't find test with id " + testExecution.getTest().getId());
                  redirectWithMessage("/", ERROR, "page.test.errorTestNotFound", testExecution.getTest().getId());
               } else {
                  values = MultiValue.createFrom(testExecution);
               }
            }
         }
      }
   }

   public TestExecution getTestExecution() {
      return testExecution;
   }

   public Test getTest() {
      return test;
   }

   public TestExecutionParameter getEditedParameter() {
      return editedParameter;
   }

   public void setEditedParameter(TestExecutionParameter param) {
      this.editedParameter = param;
   }

   public void unsetEditedParameter() {
      this.editedParameter = null;
   }

   public void createEditedParameter() {
      this.editedParameter = new TestExecutionParameter();
   }

   public Value getEditedValue() {
      return editedValue;
   }

   public String update() {
      if (testExecution != null) {
         try {
            testService.updateTestExecution(testExecution);
         } catch (ServiceException e) {
            //TODO: how to handle web-layer exceptions ?
            throw new RuntimeException(e);
         }
      }
      return "/testExecution/detail.xhtml?testExecutionId=";
   }

   public List<TestExecutionParameter> getTestExecutionParameters() {
      return testExecution.getSortedParameters();
   }

   public List<TestExecutionTag> getTestExecutionTags() {
      List<TestExecutionTag> tegs = new ArrayList<TestExecutionTag>();
      if (testExecution != null && testExecution.getTestExecutionTags() != null) {
         tegs.addAll(testExecution.getTestExecutionTags());
      }
      return tegs;
   }

   public Collection<TestExecutionAttachment> getAttachments() {
      return testExecution == null ? Collections.<TestExecutionAttachment> emptyList() : testExecution.getAttachments();
   }

   public String delete() {
      TestExecution objectToDelete = testExecution;
      if (testExecution == null) {
         objectToDelete = new TestExecution();
         objectToDelete.setId(new Long(getRequestParam("testExecutionId")));
      }
      try {
         testService.deleteTestExecution(objectToDelete);
      } catch (Exception e) {
         // TODO: how to handle web-layer exceptions ?
         throw new RuntimeException(e);
      }
      return "Search";
   }

   public void deleteParameter(TestExecutionParameter param) {
      if (param != null) {
         try {
            testService.deleteTestExecutionParameter(param);
            testExecution.getParameters().remove(param);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }
   }

   public void updateEditedParameter() {
      if (editedParameter != null) {
         TestExecution idHolder = new TestExecution();
         idHolder.setId(testExecutionId);
         editedParameter.setTestExecution(idHolder);
         try {
            TestExecutionParameter freshParam = testService.updateTestExecutionParameter(editedParameter);
            EntityUtil.removeById(testExecution.getParameters(), freshParam.getId());
            testExecution.getParameters().add(freshParam);
            editedParameter = null;
         } catch (ServiceException e) {
            addMessageFor(e);
         }
      }
   }

   public void setEditedValue(Value value) {
      this.editedValue = value == null ? null : value.cloneWithParameters();
      if (editedValue != null || editedValue.getParameters() != null) {
         if (editedValue.getParameters() instanceof List) {
            Collections.sort((List<ValueParameter>) editedValue.getParameters());
         }
      }
   }

   public Long getEditedValueMetricSelectionId() {
      return editedValueMetricSelectionId;
   }

   public void setEditedValueMetricSelectionId(Long editedValueMetricSelectionId) {
      this.editedValueMetricSelectionId = editedValueMetricSelectionId;
   }

   public void createEditedValue() {
      editedValue = new Value();
   }

   public void unsetEditedValue() {
      editedValue = null;
   }

   public void addEditedValueParameter() {
      if (editedValue == null) {
         log.error("can't add parameter, editedValue not set");
         return;
      }
      ValueParameter vp = new ValueParameter();
      if (editedValue.getParameters() == null) {
         editedValue.setParameters(new ArrayList<ValueParameter>(1));
      }
      vp.setName("param" + (editedValue.getParameters().size() + 1));
      editedValue.getParameters().add(vp);
   }

   public void removeEditedValueParameter(ValueParameter vp) {
      if (editedValue == null) {
         log.error("can't remove parameter, editedValue not set");
         return;
      }
      if (editedValue.getParameters() == null) {
         return;
      }
      editedValue.getParameters().remove(vp);
   }

   // this is also create method for value
   public void updateEditedValue() {
      if (editedValue != null) {
         TestExecution idHolder = new TestExecution();
         idHolder.setId(testExecutionId);
         editedValue.setTestExecution(idHolder);
         try {
            Value freshValue = null;
            if (editedValue.getId() == null) {
               Metric selectedMetric = EntityUtil.findById(test.getMetrics(), editedValueMetricSelectionId);
               if (selectedMetric == null) {
                  addMessage(ERROR, "page.exec.errorMetricMandatory");
                  return;
               }
               editedValue.setMetric(selectedMetric.clone());
               freshValue = testService.createValue(editedValue);
            } else {
               freshValue = testService.updateValue(editedValue);
               EntityUtil.removeById(testExecution.getValues(), freshValue.getId());
            }
            testExecution.getValues().add(freshValue);
            editedValue = null;
            ValueInfo prevValueInfo = MultiValue.find(values, freshValue);
            values = MultiValue.createFrom(testExecution);
            showMultiValue(prevValueInfo == null ? null : prevValueInfo.getMetricName());
         } catch (ServiceException e) {
            addMessageFor(e);
         }
      }
   }

   public List<Metric> getTestMetric() {
      if (testExecution != null) {
         return testService.getTestMetrics(testExecution.getTest());
      }
      return null;
   }

   public void deleteValue(Value value) {
      if (value != null) {
         TestExecution idHolder = new TestExecution();
         idHolder.setId(testExecutionId);
         value.setTestExecution(idHolder);
         try {
            testService.deleteValue(value);
            EntityUtil.removeById(testExecution.getValues(), value.getId());
            ValueInfo prevValueInfo = MultiValue.find(values, value);
            values = MultiValue.createFrom(testExecution);
            showMultiValue(prevValueInfo.getMetricName());
         } catch (ServiceException e) {
            addMessageFor(e);
         }
      }
   }

   /**
    * Produce download link for an attachment. It will be an URL for the
    * {@link TestExecutionREST#getAttachment(Long)} method.
    * 
    * @param attachment
    * @return The download link.
    */
   public String getDownloadLink(TestExecutionAttachment attachment) {
      HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
      return request.getContextPath() + "/rest/testExecution/attachment/" + attachment.getId();
   }

   public List<ValueInfo> getValues() {
      return values;
   }

   public void updateParamSelection() {
      if (selectedMultiValue == null || !selectedMultiValue.isMultiValue()) {
         addMessage(ERROR, "page.exec.notMultiValue");
         return;
      }
      selectedMultiValueList = selectedMultiValue.getComplexValueByParamName(selectedMultiValueParamSelection);
      chartData = createChart(selectedMultiValueList, selectedMultiValue);
   }

   private void clearSelectedMultiValue() {
      selectedMultiValueParamSelectionList = null;
      selectedMultiValueParamSelection = null;
      selectedMultiValue = null;
      selectedMultiValueList = null;
      chartData = null;
   }

   public void showMultiValue(String metricName) {
      if (metricName == null) {
         clearSelectedMultiValue();
         return;
      }
      ValueInfo value = MultiValue.find(values, metricName);
      if (value == null || !value.isMultiValue()) {
         clearSelectedMultiValue();
         return;
      }
      selectedMultiValueParamSelectionList = value.getComplexValueParams();
      if (selectedMultiValueParamSelectionList.isEmpty()) {
         clearSelectedMultiValue();
         return;
      }
      Collections.sort(selectedMultiValueParamSelectionList);
      if (selectedMultiValueParamSelection == null) {
         selectedMultiValueParamSelection = selectedMultiValueParamSelectionList.get(0);
      }
      selectedMultiValueList = value.getComplexValueByParamName(selectedMultiValueParamSelection);
      selectedMultiValue = value;
      chartData = createChart(selectedMultiValueList, selectedMultiValue);
   }

   public XYLineChartSpec getChartData() {
      return chartData;
   }

   private XYLineChartSpec createChart(List<ParamInfo> values, ValueInfo mainValue) {
      if (selectedMultiValueList == null) {
         return null;
      }
      XYSeriesCollection dataset = new XYSeriesCollection();
      XYSeries series = new XYSeries(selectedMultiValueParamSelection);
      dataset.addSeries(series);
      try {
         for (ParamInfo pinfo : selectedMultiValueList) {
            Double paramValue = Double.valueOf(pinfo.getParamValue());
            if (paramValue != null) {
               series.add(paramValue, pinfo.getValue());
            }
         }
         XYLineChartSpec chartSpec = new XYLineChartSpec();
         chartSpec.title = "Multi-value for " + mainValue.getMetricName();
         chartSpec.xAxisLabel = selectedMultiValueParamSelection;
         chartSpec.yAxisLabel = "Metric value";
         chartSpec.dataset = dataset;
         return chartSpec;
      } catch (NumberFormatException e) {
         log.error("Can't chart non-numeric values");
         return null;
      } catch (Exception e) {
         log.error("Error while creating chart", e);
         return null;
      }
   }

   public List<ParamInfo> getSelectedMultiValueList() {
      return selectedMultiValueList;
   }

   public String getSelectedMultiValueParamSelection() {
      return selectedMultiValueParamSelection;
   }

   public void setSelectedMultiValueParamSelection(String selectedMultiValueParamSelection) {
      this.selectedMultiValueParamSelection = selectedMultiValueParamSelection;
   }

   public List<String> getSelectedMultiValueParamSelectionList() {
      return selectedMultiValueParamSelectionList;
   }

   public String displayValue(TestExecutionParameter param) {
      return Util.displayValue(param);
   }

}