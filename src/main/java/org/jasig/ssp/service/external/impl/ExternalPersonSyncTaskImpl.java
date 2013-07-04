package org.jasig.ssp.service.external.impl;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.jasig.ssp.dao.external.ExternalPersonDao;
import org.jasig.ssp.model.ObjectStatus;
import org.jasig.ssp.model.Person;
import org.jasig.ssp.model.external.ExternalPerson;
import org.jasig.ssp.service.PersonService;
import org.jasig.ssp.service.external.ExternalPersonService;
import org.jasig.ssp.service.external.ExternalPersonSyncTask;
import org.jasig.ssp.service.reference.ConfigService;
import org.jasig.ssp.util.collections.Pair;
import org.jasig.ssp.util.sort.PagingWrapper;
import org.jasig.ssp.util.sort.SortDirection;
import org.jasig.ssp.util.sort.SortingAndPaging;
import org.jasig.ssp.util.transaction.WithTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;

@Service
public class ExternalPersonSyncTaskImpl implements ExternalPersonSyncTask {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ExternalPersonSyncTaskImpl.class);


	private static final String BATCH_SIZE_CONFIG_NAME = "task_external_person_sync_batch_size";
	private static final int DEFAULT_BATCH_SIZE = 100;
	private static final String MAX_BATCHES_PER_EXECUTION_CONFIG_NAME = "task_external_person_sync_max_batches_per_exec";
	private static final int DEFAULT_MAX_BATCHES_PER_EXECUTION = -1; // unlimited


	@Autowired
	private ExternalPersonService externalPersonService;

	@Autowired
	private PersonService personService;

	@Autowired
	private ConfigService configService;

	@Autowired
	private transient ExternalPersonDao dao;

	@Autowired
	private WithTransaction withTransaction;

	private transient int lastRecord = 0;

	// intentionally not transactional... this is the main loop, each iteration
	// of which should be its own transaction.
	@Override
	public void exec() {

		if ( Thread.currentThread().isInterrupted() ) {
			LOGGER.info("Abandoning external person sync because of thread interruption");
			return;
		}

		LOGGER.info("BEGIN : External person sync.");

		int batch = 0;
		Exception error = null;
		int maxBatchesAllowed = DEFAULT_MAX_BATCHES_PER_EXECUTION;
		while ( true ) {
			// Check this config every time in case someone wants to abort a
			// long-running execution.
			maxBatchesAllowed = getMaxBatchesAllowed();
			if ( maxBatchesAllowed == 0 ) {
				LOGGER.info("Abandoning external person sync at position [{}]"
						+ " and batch [{}] because the  batch limit has been"
						+ " set to zero.", lastRecord, batch);
				break;
			}

			error = null;
			batch++;

			// again, look up config every time to allow for relatively immediate
			// control over runnaway executions
			int batchSize = getBatchSize();
			final SortingAndPaging sAndP = SortingAndPaging.createForSingleSortWithPaging(
					ObjectStatus.ACTIVE, lastRecord, batchSize,
					"username",
					SortDirection.ASC.toString(), null);

			Pair<Long,Long> processedOfTotal = null;
			try {
				processedOfTotal = syncWithPersonInTransaction(sAndP);
			} catch ( InterruptedException e ) {
				LOGGER.info("Abandoning syncWithPerson because of thread interruption");
				Thread.currentThread().interrupt(); // reassert
			} catch (final Exception e) {
				LOGGER.error("Failed to sync Person table with ExternalPerson", e);
				error = e;
			} finally {

				lastRecord += processedOfTotal.getFirst();
				LOGGER.debug("Processed {} of {} candidate person records"
						+ " as of batch [{}].",
						new Object[] { lastRecord, processedOfTotal.getSecond(), batch });

				if ( lastRecord >= processedOfTotal.getSecond() ) {
					LOGGER.debug("No more records to process. Exiting person sync task.");
					lastRecord = 0;
					break;
				}

				if ( processedOfTotal.getFirst() == 0 ) {
					// guard against endless loops
					LOGGER.debug("Appear to be more records to process but last batch processed zero records. Exiting person sync task.");
					lastRecord = 0;
					break;
				}

				if ( maxBatchesAllowed > 0 && batch >= getMaxBatchesAllowed() ) {
					LOGGER.debug("No more batches allowed for this execution."
							+ " Exiting person sync task. Will resume at"
							+ " position [{}] on next execution.",
							lastRecord);
					// Will pick up at lastRecord next time the task is
					// triggered. This probably *is* what you want if you're
					// running the task frequently. It may or may not be
					// what you want if you're running infrequently, the sync
					// is slow, and you've set a batch limit b/c you don't
					// want the task running during peak hours. Thought about
					// adding a "always reset to first record" config option,
					// but that doesn't necessarily help either and might
					// actually make things worse b/c there'd be batches of
					// persons you'd never get to. Probably what you'd want is a
					// revolving behavior where we start back over at "person
					// zero" if you've processed the "last" person but you still
					// have batches that you're allowed to run. That's not
					// necessarily straightforward to implement, though (don't
					// want to process same persons over and over again), and
					// we're hoping to fix the perf issues shortly so hopefully
					// that implementation isn't worth pursuing.
					break;
				}

				if ( Thread.currentThread().isInterrupted() ) {
					// probably already logged above
					break;
				}

			}
		}


		LOGGER.info("END : External person sync.");
	}

	private Pair<Long, Long> syncWithPersonInTransaction(final SortingAndPaging sAndP) throws Exception {
		return withTransaction.withTransaction(new Callable<Pair<Long, Long>>() {
			@Override
			public Pair<Long, Long> call() throws Exception {
				return syncWithPerson(sAndP);
			}
		});
	}

	private Pair<Long,Long> syncWithPerson(final SortingAndPaging sAndP) throws InterruptedException {

		// Use InterruptedExceptions instead of manipulating return value b/c
		// the return value actually means "total number of possible processable"
		// rows, so if it is returned gracefully, the caller will likely
		// incorrectly update its internal state, as is the case with the
		// scheduled job call site.

		if ( Thread.currentThread().isInterrupted() ) {
			LOGGER.info("Abandoning external person sync because of thread interruption");
			throw new InterruptedException();
		}

		LOGGER.info("External person sync Selecting [{}] records starting at [{}]",
			sAndP.getMaxResults(), lastRecord);

		final PagingWrapper<Person> people = personService.getAll(sAndP);

		if ( Thread.currentThread().isInterrupted() ) {
			LOGGER.info("Abandoning external person sync because of thread interruption");
			throw new InterruptedException();
		}

		// allow access to people by schoolId
		final Map<String, Person> peopleBySchoolId = Maps.newHashMap();
		for (final Person person : people) {
			peopleBySchoolId.put(person.getSchoolId(), person);
		}

		Set<String> internalPeopleSchoolIds = peopleBySchoolId.keySet();
		if ( LOGGER.isDebugEnabled() ) {
			LOGGER.debug(
					"Candidate internal person schoolIds for sync with external persons {}",
					internalPeopleSchoolIds);
		}

		if ( Thread.currentThread().isInterrupted() ) {
			LOGGER.info("Abandoning external person sync because of thread interruption");
			throw new InterruptedException();
		}

		// fetch external people by schoolId
		final PagingWrapper<ExternalPerson> externalPeople =
				dao.getBySchoolIds(internalPeopleSchoolIds,SortingAndPaging.createForSingleSortWithPaging(
						ObjectStatus.ACTIVE, 0, sAndP.getMaxResults(),
						"username",
						SortDirection.ASC.toString(), null));

		int cnt = 0;
		for (final ExternalPerson externalPerson : externalPeople) {

			if ( Thread.currentThread().isInterrupted() ) {
				LOGGER.info("Abandoning external person sync because of thread interruption on person {}", externalPerson.getUsername());
				throw new InterruptedException();
			}

			LOGGER.debug(
					"Looking for internal person by external person schoolId {}",
					externalPerson.getSchoolId());
			// get the previously fetched person
			final Person person = peopleBySchoolId.get(externalPerson
					.getSchoolId());
			// upate person from external person
			externalPersonService.updatePersonFromExternalPerson(person, externalPerson);
			cnt++;

		}

		return new Pair(cnt, people.getResults());
	}

	private int getMaxBatchesAllowed() {
		String maxBatchesStr =
				configService.getByNameNullOrDefaultValue(MAX_BATCHES_PER_EXECUTION_CONFIG_NAME);
		int maxBatches = DEFAULT_MAX_BATCHES_PER_EXECUTION;
		try {
			maxBatches = Integer.parseInt(maxBatchesStr);
		} catch ( NumberFormatException e ) {
			LOGGER.warn("Failed to parse [{}] config [{}] to an integer. Falling"
					+ " back to [{}]. (Negative values treated as unlimited.)",
					new Object[] { MAX_BATCHES_PER_EXECUTION_CONFIG_NAME,
							maxBatchesStr, maxBatches });
		}
		return maxBatches;
	}

	private int getBatchSize() {
		String batchSizeStr =
				configService.getByNameNullOrDefaultValue(BATCH_SIZE_CONFIG_NAME);
		int batchSize = DEFAULT_BATCH_SIZE;
		try {
			batchSize = Integer.parseInt(batchSizeStr);
		} catch ( NumberFormatException e ) {
			LOGGER.warn("Failed to parse [{}] config [{}] to an integer. Falling"
					+ " back to [{}].",
					new Object[] { BATCH_SIZE_CONFIG_NAME,
							batchSizeStr, batchSize });
		}
		return batchSize;
	}

}
