package gov.usgs.cida.ncetl.service;

import gov.usgs.cida.ncetl.spec.ServiceTypeSpec;
import gov.usgs.webservices.jdbc.routing.InvalidServiceException;
import gov.usgs.webservices.jdbc.routing.UriRouter;
import gov.usgs.webservices.jdbc.service.WebService;
import gov.usgs.webservices.jdbc.spec.Spec;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Ivan Suftin <isuftin@usgs.gov>
 */
public class ServiceTypeService extends WebService {
    private static final long serialVersionUID = 1L;

    public ServiceTypeService() {
        this.enableCaching = false;
        this.specMapping.put("default", ServiceTypeSpec.class);
    }

    @Override
    protected void checkForValidParams(Spec spec) {
    } 

    @Override
    protected Map<String, String[]> defineParameters(HttpServletRequest req,
                                                     UriRouter router,
                                                     Map<String, String[]> params)
            throws InvalidServiceException {
       Map<String, String[]> tmpParams = new HashMap<String, String[]>();
       tmpParams.putAll(super.defineParameters(req, router, params));
       
       return tmpParams;
    }

}
