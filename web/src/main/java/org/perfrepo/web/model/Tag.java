/**
 * PerfRepo
 * <p>
 * Copyright (C) 2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.perfrepo.web.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@javax.persistence.Entity
@Table(name = "tag")
@NamedQueries({
        @NamedQuery(name = Tag.FIND_BY_TEST_EXECUTION, query = "SELECT tag FROM TestExecution testExecution, Tag tag WHERE testExecution.id = :executionId AND tag MEMBER OF testExecution.tags")
})
public class Tag implements Entity<Tag>, Comparable<Tag> {

   private static final long serialVersionUID = -5239043908577304531L;

   public static final String FIND_BY_PREFIX = "Tag.findByPrefix";
   public static final String FIND_BY_TEST_EXECUTION = "Tag.findByTestExecution";

   @Id
   @SequenceGenerator(name = "TAG_ID_GENERATOR", sequenceName = "TAG_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TAG_ID_GENERATOR")
   private Long id;

   @Column(name = "name")
   private String name;

   @ManyToMany(mappedBy = "tags")
   private Set<TestExecution> testExecutions = new HashSet<>();

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public Set<TestExecution> getTestExecutions() {
      return testExecutions;
   }

   public void setTestExecutions(Set<TestExecution> testExecutions) {
      this.testExecutions = testExecutions;
   }

   @Override
   public int compareTo(Tag o) {
      if (o == null) {
         return -1;
      }

      if (this.getName() == null) {
         return 1;
      }

      return this.getName().compareTo(o.getName());
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Tag)) return false;

      Tag tag = (Tag) o;

      return getName() != null ? getName().equals(tag.getName()) : tag.getName() == null;
   }

   @Override
   public int hashCode() {
      return getName() != null ? getName().hashCode() : 0;
   }

   @Override
   public String toString() {
      return "Tag{" +
              "id=" + id +
              ", name='" + name + '\'' +
              '}';
   }
}