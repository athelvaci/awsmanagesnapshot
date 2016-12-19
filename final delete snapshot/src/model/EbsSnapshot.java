package model;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

import com.amazonaws.services.ec2.model.Snapshot;
import com.amazonaws.services.ec2.model.Tag;

/**
 * The EBS snapshot config information.
 * @author Athelvaci
 *
 */
public class EbsSnapshot {

	private String snapshotId;

	private Map<String, String> tags;

	private Date startTime;

	private String description;

	public EbsSnapshot(Snapshot snapshot) {
		this.snapshotId = snapshot.getSnapshotId();

		this.tags = new HashMap<String, String>();
		for (Tag tag : snapshot.getTags()) {
			this.tags.put(tag.getKey(), tag.getValue());
		}

		this.startTime = snapshot.getStartTime();
		this.description = snapshot.getDescription();
	}

	/**
	 * @return the snapshotId
	 */
	public String getSnapshotId() {
		return this.snapshotId;
	}

	/**
	 * @return the tags
	 */
	public Map<String, String> getTags() {
		return Collections.unmodifiableMap(this.tags);
	}

	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return new Date(startTime.getTime());
	}

	public boolean isAutoSnapshot() {
		return this.tags.containsKey("ebsautosnapshot")
				&& this.description.equals("ebsautosnapshot");
	}

	public boolean isToday() {
		return DateUtils.isSameDay(this.startTime, new Date());
	}

	public boolean isBeforeDaysAgo(int daysAgo) {
		return !DateUtils.addDays(this.startTime, daysAgo).after(new Date());
	}
}
