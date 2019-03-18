package aemmocksample.services.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import javax.jcr.Session;

import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.replication.ReplicationStatus;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;

import aemmocksample.services.ReplicationService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
public class ReplicationServiceImplTest {

  private AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

  @Mock
  private Replicator replicator;
  @Mock
  private ReplicationStatus status;

  private ReplicationService underTest;
  private Page page;

  @BeforeEach
  protected void setUp() {

    // register a mockito-mock of AEM service "Replicator"
    context.registerService(Replicator.class, replicator);

    // initialize and register the service we want to test
    underTest = context.registerInjectActivateService(new ReplicationServiceImpl());

    // create a test page
    page = context.create().page("/content/mypage");

    // make sure the mocked "Replicator" returns a status object for our test page
    when(replicator.getReplicationStatus(any(Session.class), eq(page.getPath()))).thenReturn(status);

  }

  @Test
  public void testIsNotActivated() {
    // simulate: last replication action was "activated"
    when(status.isActivated()).thenReturn(false);
    // assert result
    assertFalse(underTest.isActivated(page));
  }

  @Test
  public void testIsActivated() {
    // simulate: last replication action was "not activated"
    when(status.isActivated()).thenReturn(true);
    // assert result
    assertTrue(underTest.isActivated(page));
  }

}
