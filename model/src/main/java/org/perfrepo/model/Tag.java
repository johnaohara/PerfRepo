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
package org.perfrepo.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Collection;

@javax.persistence.Entity
@Table(name = "tag")
@XmlRootElement(name = "tag")
public class Tag implements Entity<Tag>, Comparable<Tag> {

   private static final long serialVersionUID = -5239043908577304531L;

   public static final String FIND_BY_PREFIX = "Tag.findByPrefix";

   @Id
   @SequenceGenerator(name = "TAG_ID_GENERATOR", sequenceName = "TAG_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TAG_ID_GENERATOR")
   private Long id;

   @Column(name = "name")
   private String name;

   @ManyToMany(mappedBy = "tags")
   private Collection<TestExecution> testExecutions;

   @XmlTransient
   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   @XmlID
   @XmlAttribute(name = "id")
   public String getStringId() {
      return id == null ? null : String.valueOf(id);
   }

   public void setStringId(String id) {
      this.id = Long.valueOf(id);
   }

   public void setName(String name) {
      this.name = name;
   }

   @XmlAttribute(name = "name")
   public String getName() {
      return this.name;
   }

   public void setTestExecutions(Collection<TestExecution> testExecutions) {
      this.testExecutions = testExecutions;
   }

   @XmlTransient
   public Collection<TestExecution> getTestExecutions() {
      return testExecutions;
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
   public boolean equals(Object obj) {
      if (obj == null || !(obj instanceof Tag) || ((Tag) obj).getName() == null || ((Tag) obj).getId() == null)
         return false;

      return ((Tag) obj).getId().equals(this.id) && ((Tag) obj).getName().equals(this.name);

   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

   @Override
   public Tag clone() {
      try {
         return (Tag) super.clone();
      } catch (CloneNotSupportedException e) {
         throw new RuntimeException(e);
      }
   }
}