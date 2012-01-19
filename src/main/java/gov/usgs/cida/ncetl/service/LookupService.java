package gov.usgs.cida.ncetl.service;

import gov.usgs.cida.ncetl.spec.CollectionTypeSpec;
import gov.usgs.cida.ncetl.spec.DataFormatSpec;
import gov.usgs.cida.ncetl.spec.DatatypeSpec;
import gov.usgs.cida.ncetl.spec.DateTypeEnumSpec;
import gov.usgs.cida.ncetl.spec.DocumentationTypeSpec;
import gov.usgs.cida.ncetl.spec.ServiceTypeSpec;
import gov.usgs.cida.ncetl.spec.SpatialRangeTypeSpec;
import gov.usgs.cida.ncetl.spec.UpDownTypeSpec;
import gov.usgs.webservices.jdbc.routing.ActionType;
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
public class LookupService extends WebService {
    private static final long serialVersionUID = 1L;

    public LookupService() {
        this.enableCaching = false;
        this.specMapping.put("collection", CollectionTypeSpec.class);
        this.specMapping.put("dataformat", DataFormatSpec.class);
        this.specMapping.put("datatype", DatatypeSpec.class);
        this.specMapping.put("doctype", DocumentationTypeSpec.class);
        this.specMapping.put("dtenum", DateTypeEnumSpec.class);
        this.specMapping.put("srvctype", ServiceTypeSpec.class);
        this.specMapping.put("spatialrange", SpatialRangeTypeSpec.class);
        this.specMapping.put("updown", UpDownTypeSpec.class);
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
       
       ActionType action = router.getActionTypeFromUri();
       
       return tmpParams;
    }

}
