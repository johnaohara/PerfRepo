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

import org.perfrepo.model.report.ReportProperty;
import org.perfrepo.model.user.User;

import javax.inject.Named;

/**
 * DAO for {@link ReportProperty}
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Named
public class ReportPropertyDAO extends DAO<ReportProperty, Long> {

}