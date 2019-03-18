package aemmocksample.services.impl;

import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.day.cq.replication.ReplicationStatus;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;

import aemmocksample.services.ReplicationService;

/**
 * Implements {@link ReplicationService}.
 */
@Component(service = ReplicationService.class)
public class ReplicationServiceImpl implements ReplicationService {

  @Reference
  private Replicator replicator;

  @Override
  public boolean isActivated(Page page) {
    ResourceResolver resourceResolver = page.adaptTo(Resource.class).getResourceResolver();
    Session session = resourceResolver.adaptTo(Session.class);
    if (session == null) {
      throw new RuntimeException("Unable to get JCR session from resource resolver.");
    }
    ReplicationStatus status = replicator.getReplicationStatus(session, page.getPath());
    return status.isActivated();
  }

}
