package de.olafkock.liferay.navigation.util;

public class GroupConfig {

	private final long groupId;
	private final boolean isPrivate;

	public GroupConfig(long groupId, boolean isPrivate) {
		this.groupId = groupId;
		this.isPrivate = isPrivate;
	}

	public long getGroupId() {
		return groupId;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

}