package de.olafkock.liferay.navigation;

import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.SimpleAction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Company;
import com.liferay.portal.service.CompanyLocalServiceUtil;

import java.util.List;

public class CustomNavigationStartupAction extends SimpleAction {
	/* (non-Java-doc)
	 * @see com.liferay.portal.kernel.events.SimpleAction#SimpleAction()
	 */
	public CustomNavigationStartupAction() {
		super();
	}

	/* (non-Java-doc)
	 * @see com.liferay.portal.kernel.events.SimpleAction#run(String[] ids)
	 */
	public void run(String[] ids) throws ActionException {
		try {
			List<Company> companies = CompanyLocalServiceUtil.getCompanies();
			for(Company company: companies) {
				ExpandoNavigationUtil.getNavigationColumn(true,  company.getCompanyId());
				ExpandoNavigationUtil.getNavigationColumn(false, company.getCompanyId());
			}
		} catch (SystemException e) {
			throw new ActionException(e);
		} catch (PortalException e) {
			throw new ActionException(e);
		}
	}
}