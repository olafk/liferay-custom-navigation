liferay-custom-navigation-hook
==============================

provides configurable and transparent multi-site navigation in Liferay

Liferay's Navigation only displays the pages (either public or private) 
of a single site. In some circumstances you want to include other sites
in your main navigation. An easy way is already integrated in Liferay: 
You can choose to "merge ", but this will only merge the default site's 
navigation into any of the non-default sites.

This plugin enables you to configure what exactly you want to show in
your navigation for any particular site. The screenshots show a 
scenario with four sites: You can have different editorial teams (and
permissions) on each one. In order to combine their navigation, you 
just have to find out the sites ids and enter them, together with the 
notion "private" or "public" in the "Custom Navigation Sites" 
configuration for each of the sites and they will all show up uniformly
in your main navigation
