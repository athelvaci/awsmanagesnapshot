package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.ec2.model.Snapshot;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.Volume;

/**
 * The EBS volume config information.
 * 
 * @author Athelvaci
 *
 */
public class EbsVolume {

	private String volumeId;

	private Map<String, String> tags;

	private List<EbsSnapshot> snapshots;

	public EbsVolume(Volume volume, List<Snapshot> snapshots) {
		this.volumeId = volume.getVolumeId();

		this.tags = new HashMap<String, String>();
		for (Tag tag : volume.getTags()) {
			this.tags.put(tag.getKey(), tag.getValue());
		}

		this.snapshots = new ArrayList<EbsSnapshot>();
		if (snapshots != null) {
			for (Snapshot snapshot : snapshots) {
				this.snapshots.add(new EbsSnapshot(snapshot));
			}
		}
	}

	/**
	 * @return the volumeId
	 */
	public String getVolumeId() {
		return this.volumeId;
	}

	/**
	 * @return the tags
	 */
	public Map<String, String> getTags() {
		return Collections.unmodifiableMap(this.tags);
	}

	/**
	 * @return the snapshots
	 */
	public List<EbsSnapshot> getSnapshots() {
		return Collections.unmodifiableList(this.snapshots);
	}

	public String getName() {
		String name = this.tags.get("Name");
		if (name == null) {
			name = "";
		}

		return name;
	}

	public boolean isAutoSnapshot() {
		return this.tags.containsKey("ebsautosnapshot");
	}

	public int getDays() {
		String val = this.tags.get("ebsautosnapshot");
		if (val == null) {
			return 0;
		}

		String[] params = val.split(";");
		for (String param : params) {
			String data[] = param.split("=");
			if (data.length != 2) {
				continue;
			}

			if (data[0].equalsIgnoreCase("days")) {
				try {
					return Integer.parseInt(data[1]);
				} catch (Exception e) {
					return 0;
				}
			}
		}

		return 0;
	}

	public boolean wasSnapshotTakenToday() {
		for (EbsSnapshot snapshot : this.snapshots) {
			if (snapshot.isToday()) {
				return true;
			}
		}

		return false;
	}

}

