package aemmocksample.services;

import com.day.cq.wcm.api.Page;

/**
 * Convenience service to check the replication status of a page.
 */
public interface ReplicationService {

  /**
   * Check if the last replication action of a page was "activate".
   * @param page Page
   * @return true if last action was "activate"
   */
  boolean isActivated(Page page);

}
