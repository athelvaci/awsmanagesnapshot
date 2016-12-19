package mainpackage;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateSnapshotRequest;
import com.amazonaws.services.ec2.model.CreateSnapshotResult;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DeleteSnapshotRequest;
import com.amazonaws.services.ec2.model.DescribeSnapshotsRequest;
import com.amazonaws.services.ec2.model.DescribeSnapshotsResult;
import com.amazonaws.services.ec2.model.DescribeVolumesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Volume;

import model.EbsAutoSnapshotConfig;
import model.EbsSnapshot;
import model.EbsVolume;


/**
 * The EBS snapshot manager logic.
 * 
 *@author Athelvaci
 */
public class EbsSnapshotManager {

	private AmazonEC2Client ec2Client;

	public EbsSnapshotManager(EbsAutoSnapshotConfig config) {

		// Build the client
		AWSCredentials credentials = new BasicAWSCredentials(
				config.getAwsPublicKey(), config.getAwsPrivateKey());
		this.ec2Client = new AmazonEC2Client(credentials);
	}

	public void run() {
		// Get all of the volumes
		DescribeVolumesResult describeVolumes = this.ec2Client.describeVolumes();
		//show volumes list size
		System.out.println(describeVolumes.getVolumes().size());

		// Process all of the volumes
		for (Volume volume : describeVolumes.getVolumes()) {

			// Look up all of the snapshots for the volume
			DescribeSnapshotsRequest describeSnapshotsRequest = new DescribeSnapshotsRequest();
			describeSnapshotsRequest = describeSnapshotsRequest
					.withFilters(new Filter().withName("volume-id").withValues(
							volume.getVolumeId()));
			DescribeSnapshotsResult describeSnapshots = this.ec2Client
					.describeSnapshots(describeSnapshotsRequest);

			// The volume and snapshot object to process
			EbsVolume ebsVolume = new EbsVolume(volume,
					describeSnapshots.getSnapshots());

			// Decide if the volume is ebsautosnapshot managed
			System.out.println(ebsVolume.getVolumeId() + "\t"
					+ ebsVolume.getName());
			if (!ebsVolume.isAutoSnapshot()) {
				System.out.println("Skipping volume, not ebsautosnapshot.\n");
				continue;
			}

			// Check if a snapshot was already taken today
			if (ebsVolume.wasSnapshotTakenToday()) {
				System.out
						.println("Skipping volume, snapshot already taken today.\n");
				continue;
			}

			// Take the daily snapshot
			CreateSnapshotRequest createSnapshotRequest = new CreateSnapshotRequest()
					.withVolumeId(ebsVolume.getVolumeId()).withDescription(
							"ebsautosnapshot");
			CreateSnapshotResult snapshotResult = this.ec2Client
					.createSnapshot(createSnapshotRequest);
			System.out.println("Created daily snapshot "
					+ snapshotResult.getSnapshot().getSnapshotId());

			// Set the tags on the snapshot to match the volume
			CreateTagsRequest createTagsRequest = new CreateTagsRequest()
					.withResources(snapshotResult.getSnapshot().getSnapshotId())
					.withTags(volume.getTags());
			this.ec2Client.createTags(createTagsRequest);

			// Delete snapshots that are too old
			int days = ebsVolume.getDays();
			System.out.println("Retaining " + days + " days.");
			for (EbsSnapshot snapshot : ebsVolume.getSnapshots()) {
				// Skip all snapshot not created as an auto snapshot
				if (!snapshot.isAutoSnapshot()) {
					continue;
				}

				if (snapshot.isBeforeDaysAgo(days)) {
					System.out.println("Deleting " + snapshot.getSnapshotId());
					DeleteSnapshotRequest deleteSnapshotRequest = new DeleteSnapshotRequest()
							.withSnapshotId(snapshot.getSnapshotId());
					this.ec2Client.deleteSnapshot(deleteSnapshotRequest);
				}
			}

			System.out.println("");
			System.out.println("");
		}
	}
}
