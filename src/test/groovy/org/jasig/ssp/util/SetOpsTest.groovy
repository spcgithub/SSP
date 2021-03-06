/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.ssp.util

import static org.junit.Assert.*

import org.jasig.ssp.model.AbstractAuditable
import org.jasig.ssp.model.ObjectStatus
import org.junit.Before
import org.junit.Test

import com.google.common.collect.Sets


class SetOpsTest implements Serializable {

	private static final long serialVersionUID = 1L;

	class AuditableSubClass extends AbstractAuditable{

		private static final long serialVersionUID = -112482021549127611L;

		public AuditableSubClass(){
			setObjectStatus(ObjectStatus.ACTIVE)
		}

		private String other;

		protected int hashPrime(){
			return 11
		}

		public void setOther(String other){
			this.other = other;
		}

		@Override
		public int hashCode() {
			int result = hashPrime();

			// AbstractAuditable properties
			result *= getId() == null ? "id".hashCode() : getId().hashCode();
			result *= getObjectStatus() == null ? hashPrime() : getObjectStatus()
					.hashCode();
			result *= other == null ? "other".hashCode() : other.hashCode();
			return result
		};
	}

	private Set<AuditableSubClass> set1
	private Set<AuditableSubClass> set2

	private AuditableSubClass a1 = new AuditableSubClass(id:UUID.randomUUID())
	private AuditableSubClass a2 = new AuditableSubClass(id:UUID.randomUUID())
	private AuditableSubClass a3 = new AuditableSubClass(id:UUID.randomUUID())
	private AuditableSubClass a4 = new AuditableSubClass(id:UUID.randomUUID())
	private AuditableSubClass a2_b

	@Before
	void setup(){
		set1 = Sets.newHashSet()
		set2 = Sets.newHashSet()
	}

	@Test
	void updateSet_addRemoveAndUpdate(){
		set1.addAll([a1, a2, a3, a4])

		a2_b = a2
		a2_b.other="testString"
		set2.addAll([a2_b])

		SetOps.updateSet(set1, set2);

		final List active = ObjectStatus.filterForStatus(set1, ObjectStatus.ACTIVE)
		final List softDeleted = ObjectStatus.filterForStatus(set1, ObjectStatus.INACTIVE)

		assertEquals("only one element should be active", 1, active.size())
		assertTrue("a2 should be the active element", active.contains(a2))

		active.each{
			assertEquals("Should overwrite a2 with a2_b", "testString", it.other)
		}

		assertEquals("3 elements should be softDeleted", 3, softDeleted.size())

		assertEquals("size of set1 should be 4", 4, set1.size())
		assertEquals("size of set2 should still be 1", 1, set2.size())
	}

	@Test
	void updateSet_addAndRemove(){
		set1.addAll([a1, a3, a4])
		set2.addAll([a2])

		SetOps.updateSet(set1, set2);

		final List active = ObjectStatus.filterForStatus(set1, ObjectStatus.ACTIVE)
		final List softDeleted = ObjectStatus.filterForStatus(set1, ObjectStatus.INACTIVE)

		assertEquals("only one element should be active", 1, active.size())
		assertTrue("a2 should be the active element", active.contains(a2))

		assertEquals("3 elements should be softDeleted", 3, softDeleted.size())

		assertEquals("size of set1 should be 4", 4, set1.size())
		assertEquals("size of set2 should still be 1", 1, set2.size())
	}
}