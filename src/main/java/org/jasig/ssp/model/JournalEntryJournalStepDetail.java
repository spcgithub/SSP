package org.jasig.ssp.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.jasig.ssp.model.reference.JournalStepDetail;

/**
 * Associative class between {@link JournalEntry} and {@link JournalStepDetail}.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class JournalEntryJournalStepDetail extends Auditable implements
		Serializable {

	private static final long serialVersionUID = -1482715931640054820L;

	@ManyToOne()
	@JoinColumn(name = "journal_entry_id", nullable = false, insertable = false)
	private JournalEntry journalEntry;

	@ManyToOne()
	@JoinColumn(name = "journal_step_detail_id", nullable = false, insertable = true)
	private JournalStepDetail journalStepDetail;

	/**
	 * Empty constructor. Do not use.
	 */
	public JournalEntryJournalStepDetail() {
		super();
	}

	/**
	 * Construct an associative instance to the specified entities.
	 * 
	 * @param journalEntry
	 *            Associated journal entry
	 * @param journalStepDetail
	 *            Associated journal step detail
	 */
	public JournalEntryJournalStepDetail(
			@NotNull final JournalEntry journalEntry,
			@NotNull final JournalStepDetail journalStepDetail) {
		super();
		this.journalEntry = journalEntry;
		this.journalStepDetail = journalStepDetail;
	}

	public JournalEntry getJournalEntry() {
		return journalEntry;
	}

	public void setJournalEntry(@NotNull final JournalEntry journalEntry) {
		this.journalEntry = journalEntry;
	}

	public JournalStepDetail getJournalStepDetail() {
		return journalStepDetail;
	}

	public void setJournalStepDetail(
			@NotNull final JournalStepDetail journalStepDetail) {
		this.journalStepDetail = journalStepDetail;
	}

	@Override
	protected int hashPrime() {
		return 257;
	}

	@Override
	public int hashCode() { // NOPMD by jon.adams on 5/18/12 12:56 PM
		int result = hashPrime();

		// Auditable properties
		result *= getId() == null ? "id".hashCode() : getId().hashCode();
		result *= getObjectStatus() == null ? hashPrime() : getObjectStatus()
				.hashCode();

		result *= journalEntry == null ? "journalEntry".hashCode()
				: journalEntry.hashCode();
		result *= journalStepDetail == null ? "journalStepDetail".hashCode()
				: journalStepDetail.hashCode();

		return result;
	}
}