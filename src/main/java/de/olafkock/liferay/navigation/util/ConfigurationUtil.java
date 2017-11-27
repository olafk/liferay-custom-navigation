package de.olafkock.liferay.navigation.util;

import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationUtil {

	public static List<GroupConfig> decode(String[] strings) {
		int elements = strings == null? 0 : strings.length;
		ArrayList<GroupConfig> result = new ArrayList<GroupConfig>(elements);
		for(int i = 0; i < elements; i++) {
			GroupConfig element = decode(strings[i]);
			if(element != null) {
				result.add(element);
			}
		}
		return result;
	}

	private static GroupConfig decode(String string) {
		try {
			String[] split = StringUtil.split(string);
			
			if(split == null || split.length!=2) {
				return null;
			}
			long groupId = Long.valueOf(split[0]);
			if(groupId == 0) {
				return null;
			}
			if(split[1].equals("public")) {
				return new GroupConfig(groupId, false);
			} else if(split[1].equals("private")) {
				return new GroupConfig(groupId, true);
			}
		} catch (Exception ignore) {
			// ignore
		}
		return null;
	}
}