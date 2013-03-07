package gov.usgs.cida.ncetl.service;

import gov.usgs.cida.ncetl.spec.task.ArchiveSpec;
import gov.usgs.cida.ncetl.spec.task.ExcludeSpec;
import gov.usgs.cida.ncetl.spec.task.ExcludeTypeSpec;
import gov.usgs.cida.ncetl.spec.task.RenameSpec;
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
 * @author Jordan Walker <jiwalker@usgs.gov>
 */
public class TaskService extends WebService {
    private static final long serialVersionUID = 1L;

    public TaskService() {
        this.enableCaching = false;
        this.specMapping.put("archive", ArchiveSpec.class);
        this.specMapping.put("exclude", ExcludeSpec.class);
        this.specMapping.put("excludetype", ExcludeTypeSpec.class);
        this.specMapping.put("rename", RenameSpec.class);
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
