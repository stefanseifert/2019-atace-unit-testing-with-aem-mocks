package aemmocksample.servlets.impl;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.day.cq.dam.api.Asset;
import com.day.image.Layer;
import com.google.common.collect.ImmutableMap;

import io.wcm.testing.mock.aem.junit.AemContext;

public class CropSquareServletTest {

  @Rule
  public AemContext context = new AemContext();

  private CropSquareServlet underTest;

  @Before
  public void setUp() {
    // register servlet as OSGI component
    underTest = context.registerInjectActivateService(new CropSquareServlet());
  }

  @Test
  public void testCropSquare() throws Exception {

    // create a JPEG asset on the fly
    Asset asset = context.create().asset("/content/dam/myimage.jpg", 100, 80, "image/jpeg");

    // create a current resource referencing our asset
    Resource resource = context.create().resource("/content/test/myresource",
        "fileReference", asset.getPath());
    context.currentResource(resource);

    // simulate a request parameter
    context.request().setParameterMap(ImmutableMap.of("width", 50));

    // call the servlet
    underTest.service(context.request(), context.response());

    // check response code
    assertEquals(HttpServletResponse.SC_OK, context.response().getStatus());

    // check the returned image binary
    try (InputStream is = new ByteArrayInputStream(context.response().getOutput())) {
      Layer layer = new Layer(is);
      assertEquals(50, layer.getWidth());
      assertEquals(50, layer.getHeight());
    }

  }

}
