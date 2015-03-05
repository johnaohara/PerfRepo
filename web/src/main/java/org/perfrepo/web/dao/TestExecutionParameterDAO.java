/**
 *
 * PerfRepo
 *
 * Copyright (C) 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.perfrepo.web.dao;

import org.perfrepo.model.TestExecution;
import org.perfrepo.model.TestExecutionParameter;

import javax.inject.Named;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.List;

/**
 * DAO for {@link TestExecutionParameter}
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Named
public class TestExecutionParameterDAO extends DAO<TestExecutionParameter, Long> {

	/**
	 * Discovers if test execution already has the parameter, in other word
	 * if it breaks the unique (testExecutionId, name) constraint
	 *
	 * @param testId
	 * @param paramName
	 * @return true if there's already test execution parameter with same pair (testExecutionId, param_name)
	 */
	public boolean hasTestParam(Long testExecutionId, TestExecutionParameter param) {
		CriteriaBuilder cb = criteriaBuilder();
		CriteriaQuery<TestExecutionParameter> criteria = cb.createQuery(TestExecutionParameter.class);

		Root<TestExecutionParameter> rParam = criteria.from(TestExecutionParameter.class);
		Join<TestExecutionParameter, TestExecution> rTestExecution = rParam.join("testExecution");

		Predicate pFixedTest = cb.equal(rTestExecution.get("id"), cb.parameter(Long.class, "testExecutionId"));
		Predicate pFixedName = cb.equal(rParam.get("name"), cb.parameter(String.class, "paramName"));

		criteria.where(cb.and(pFixedTest, pFixedName));
		criteria.select(rParam);

		TypedQuery<TestExecutionParameter> query = query(criteria);
		query.setParameter("testExecutionId", testExecutionId);
		query.setParameter("paramName", param.getName());
		List<TestExecutionParameter> tparams = query.getResultList();
		return tparams.size() > 0 && !tparams.get(0).getId().equals(param.getId());
	}

	public List<TestExecutionParameter> find(List<Long> execIdList, List<String> paramNameList) {
		CriteriaBuilder cb = criteriaBuilder();
		CriteriaQuery<TestExecutionParameter> criteria = cb.createQuery(TestExecutionParameter.class);

		Root<TestExecutionParameter> rParam = criteria.from(TestExecutionParameter.class);
		Predicate pParamNameInList = rParam.get("name").in(cb.parameter(List.class, "paramNameList"));
		Predicate pExecIdInList = rParam.get("testExecution").get("id").in(cb.parameter(List.class, "execIdList"));
		rParam.fetch("testExecution");
		criteria.where(cb.and(pParamNameInList, pExecIdInList));
		criteria.select(rParam);
		TypedQuery<TestExecutionParameter> query = query(criteria);
		query.setParameter("paramNameList", paramNameList);
		query.setParameter("execIdList", execIdList);
		return query.getResultList();
	}
}