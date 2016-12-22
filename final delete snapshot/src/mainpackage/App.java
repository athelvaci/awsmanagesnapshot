package mainpackage;

import model.EbsAutoSnapshotConfig;

/**
 * The main application.
 *@author Athelvaci
 *
 */
public class App {

	//main application
	public static void main(String[] args) {
		try {

			// credential object will create in here
			EbsAutoSnapshotConfig config = new EbsAutoSnapshotConfig("AKIAJNTGOR3TTDKHXFSA",
					"NBITOINqO+2ebuBMao4/j0LqcLGYODkhXtz8GCMs"); //change here with your credentials

			// Run the application
			EbsSnapshotManager ebsSnapshotManager = new EbsSnapshotManager(config);
			ebsSnapshotManager.run();
		} catch (Exception e) {
			System.out.println("cant logged in");
		}
	}
}
