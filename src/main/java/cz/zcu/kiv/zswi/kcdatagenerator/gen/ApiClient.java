package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import com.kerio.lib.json.api.client.KApiClient;
import com.kerio.lib.json.api.connect.admin.iface.Session;
import com.kerio.lib.json.api.connect.admin.struct.common.ApiApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mosladil
 */
public class ApiClient extends KApiClient {

	private final Logger logger = LoggerFactory.getLogger(ApiClient.class);

	@Override
	public void login(String hostUrl, String username, String password) {
		getSession().setHostname(hostUrl); // updating host name
		getApi(Session.class).login(username, password, new ApiApplication() {
			{
				setName("ComponentAdmin");
				setVendor("AT");
				setVersion("1.0");
			}
		});
		this.logger.debug("logged in");
	}

	@Override
	public void logout() {
		getApi(Session.class).logout();
		this.logger.debug("logged out");
	}

}
