package model;

public class EbsAutoSnapshotConfig {

	private String awsPublicKey;

	private String awsPrivateKey;

	public EbsAutoSnapshotConfig(String awsPublicKey, String awsPrivateKey) {
		this.awsPublicKey = awsPublicKey;
		this.awsPrivateKey = awsPrivateKey;
	}

	/**
	 * @return the awsPublicKey
	 */
	public String getAwsPublicKey() {
		return awsPublicKey;
	}

	/**
	 * @return the awsPrivateKey
	 */
	public String getAwsPrivateKey() {
		return awsPrivateKey;
	}
}

