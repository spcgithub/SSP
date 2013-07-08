package org.jasig.ssp.service.external.impl

import org.jasig.ssp.service.reference.ConfigService
import org.jasig.ssp.util.collections.Pair
import org.jasig.ssp.util.sort.SortingAndPaging
import org.jasig.ssp.util.transaction.WithTransactionImpl;
import spock.lang.Specification;

public class ExternalPersonSyncTaskImplTest extends Specification {

	StubbedExternalPersonSyncTaskImpl syncTask
	ConfigService configService = Mock(ConfigService)

	def setup() {
		this.configService = Mock(ConfigService)
		this.syncTask = new StubbedExternalPersonSyncTaskImpl(
			withTransaction: new WithTransactionImpl(),
			configService: this.configService
		)
	}

	def "defaults to processing all available records once, possibly starting in the middle"() {

		given: "a sync task starting in the middle of all persons"
		syncTask.lastRecord = 4

		and: "a sync implementation that reads records endlessly"
		def batchCnt = 0
		syncTask.syncWithPersonImpl = { sAndP ->
			batchCnt++
			// always read the next 2 of 10 records
			new Pair(2L,10L)
		}

		when: "the task is executed"
		syncTask.exec()

		then: "chaos ensues"
		batchCnt == 5
		syncTask.lastRecord == 4

	}

	def "requests configured number of batches"() {
		given: "a configuration allowing only 2 of a possible 3 batches"
		configService.getByNameNullOrDefaultValue("task_external_person_sync_max_batches_per_exec") >> 2

		and: "a sync implementation that reads records endlessly"
		def batchCnt = 0
		syncTask.syncWithPersonImpl = { sAndP ->
			batchCnt++
			// always read the next 2 of 10 records
			new Pair(2L,10L)
		}

		when: "the task is executed"
		syncTask.exec()

		then:
		batchCnt == 2
	}

	def "requests batches of the configured size"() {
		given: "a configuration that requests two different batch sizes"
		configService.getByNameNullOrDefaultValue("task_external_person_sync_batch_size") >>> [6, 13]

		and: "a sync job that will read two batches and record the request size of each"
		def batchSizes = []
		syncTask.syncWithPersonImpl = { sAndP ->
			batchSizes << sAndP.maxResults
			batchSizes.empty ? new Pair(6L,19L) : new Pair(13L,19L)
		}

		when: "the task is executed"
		syncTask.exec()

		then:
		batchSizes == [6,13]
	}

	def "requests batches in a default size if misconfigured"() {
		given: "misconfigured batch sizes"
		configService.getByNameNullOrDefaultValue("task_external_person_sync_batch_size") >>> ["nonsense", "foo"]

		and: "a sync job that will read two batches and record the request size of each"
		def batchSizes = []
		syncTask.syncWithPersonImpl = { sAndP ->
			batchSizes << sAndP.maxResults
			new Pair(100L,200L)
		}

		when: "the task is executed"
		syncTask.exec()

		then:
		batchSizes == [100,100]
	}

	def "requests batches in a default size if config missing"() {
		given: "misconfigured batch sizes"
		configService.getByNameNullOrDefaultValue("task_external_person_sync_batch_size") >> null

		and: "a sync job that will read two batches and record the request size of each"
		def batchSizes = []
		syncTask.syncWithPersonImpl = { sAndP ->
			batchSizes << sAndP.maxResults
			new Pair(100L,200L)
		}

		when: "the task is executed"
		syncTask.exec()

		then:
		batchSizes == [100,100]
	}

}

class StubbedExternalPersonSyncTaskImpl extends ExternalPersonSyncTaskImpl {

	Closure syncWithPersonImpl

	@Override
	def Pair<Long, Long> syncWithPerson(final SortingAndPaging sAndP) {
		if (!(syncWithPersonImpl)) {
			new Pair(0L,0L)
		} else {
			syncWithPersonImpl.call(sAndP)
		}
	}

	// Some weird stuff here b/c of private field visibility rules for super
	// classes.
	// http://groovy.329449.n5.nabble.com/Sub-classes-and-private-fields-td349921.html

	void setConfigService(ConfigService cs) {
		metaClass.setAttribute(ExternalPersonSyncTaskImpl, this, "configService", cs, false, true)
	}

	void setWithTransaction(wt) {
		metaClass.setAttribute(ExternalPersonSyncTaskImpl, this, "withTransaction", wt, false, true)
	}

	void setLastRecord(lr) {
		metaClass.setAttribute(ExternalPersonSyncTaskImpl, this, "lastRecord", lr, false, true)
	}

	void getLastRecord() {
		metaClass.getAttribute(ExternalPersonSyncTaskImpl, this, "lastRecord", false, true)
	}
}
