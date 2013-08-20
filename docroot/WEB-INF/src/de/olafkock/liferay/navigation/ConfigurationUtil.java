package de.olafkock.liferay.navigation;

import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Note: This code is copied into the navigation portlet jsp, as that jsp
 * will run in portal classloader and can't access this implementation.
 * 
 * Any changes here should be made in 
 * custom_jsps/html/portlet/navigation/view.jsp as well.
 * 
 * @author Olaf Kock
 *
 */

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
		return null;
	}
}
