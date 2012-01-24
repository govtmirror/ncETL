/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.cida.ncetl.service;

import gov.usgs.cida.ncetl.spec.AccessSpec;
import gov.usgs.cida.ncetl.spec.CatalogSpec;
import gov.usgs.cida.ncetl.spec.ContributorSpec;
import gov.usgs.cida.ncetl.spec.ControlledVocabularySpec;
import gov.usgs.cida.ncetl.spec.CreatorSpec;
import gov.usgs.cida.ncetl.spec.DatasetSpec;
import gov.usgs.cida.ncetl.spec.DateTypeFormattedSpec;
import gov.usgs.cida.ncetl.spec.DocumentationSpec;
import gov.usgs.cida.ncetl.spec.GeospatialCoverageSpec;
import gov.usgs.cida.ncetl.spec.KeywordSpec;
import gov.usgs.cida.ncetl.spec.ProjectSpec;
import gov.usgs.cida.ncetl.spec.PropertySpec;
import gov.usgs.cida.ncetl.spec.PublisherSpec;
import gov.usgs.cida.ncetl.spec.ServiceSpec;
import gov.usgs.cida.ncetl.spec.SpatialRangeSpec;
import gov.usgs.cida.ncetl.spec.TimeCoverageSpec;
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
public class CatalogService extends WebService {
    private static final long serialVersionUID = 1L;

    public CatalogService() {
        this.enableCaching = false;
        this.specMapping.put("access", AccessSpec.class);
        this.specMapping.put("catalog", CatalogSpec.class);
        this.specMapping.put("contrib", ContributorSpec.class);
        this.specMapping.put("creator", CreatorSpec.class);
        this.specMapping.put("dataset", DatasetSpec.class);
        this.specMapping.put("datetype", DateTypeFormattedSpec.class);
        this.specMapping.put("doc", DocumentationSpec.class);
        this.specMapping.put("geo", GeospatialCoverageSpec.class);
        this.specMapping.put("keyword", KeywordSpec.class);
        this.specMapping.put("project", ProjectSpec.class);
        this.specMapping.put("prop", PropertySpec.class);
        this.specMapping.put("publisher", PublisherSpec.class);
        this.specMapping.put("srvc", ServiceSpec.class);
        this.specMapping.put("spatialrange", SpatialRangeSpec.class);
        this.specMapping.put("time", TimeCoverageSpec.class);
        this.specMapping.put("vocab", ControlledVocabularySpec.class);
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
       
       if (ActionType.create == action) {
           tmpParams.put("inserted", new String[] {"true"});
       } else if (ActionType.update == action) {
           tmpParams.put("updated", new String[] {"true"});
       }
       
       return tmpParams;
    }

}
