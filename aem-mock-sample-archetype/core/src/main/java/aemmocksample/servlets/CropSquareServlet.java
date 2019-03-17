package aemmocksample.servlets;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.image.Layer;

/**
 * Example servlet that reads an asset reference from the current resource
 * and crops the referenced image to a square aspect ratio (1:1).
 * Optionally it is possible to pass in a width in pixel as request parameter.
 */
@Component(service = Servlet.class,property = {
   "sling.servlet.resourceTypes=aemmocksample/components/cropping",
   "sling.servlet.selectors=cropsquare",
   "sling.servlet.extensions=jpg",
   "sling.servlet.methods=" + HttpConstants.METHOD_GET
})
public class CropSquareServlet extends SlingSafeMethodsServlet {
  private static final long serialVersionUID = 1L;

  /**
   * Request parameter for specifying the width.
   */
  static final String RP_WIDTH = "width";

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

    // get referenced asset
    Asset asset = getAssetFromReference(request.getResource());
    if (asset == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // crop to square
    Layer layer = toLayer(asset);
    int squareWidth = Math.min(layer.getWidth(), layer.getHeight());
    layer.crop(new Rectangle(squareWidth, squareWidth));

    // optionally resize to given with
    int requestedWith = request.getParameter(RP_WIDTH) != null ? Integer.valueOf(request.getParameter(RP_WIDTH)): 0;
    if (requestedWith > 0) {
      layer.resize(requestedWith, requestedWith);
    }

    // return new image
    response.setContentType("image/jpeg");
    layer.write("image/jpeg", 0.98d, response.getOutputStream());
  }

  private Asset getAssetFromReference(Resource resource) {
    String assetReference = resource.getValueMap().get("fileReference", String.class);
    if (assetReference != null) {
      Resource assetResource = resource.getResourceResolver().getResource(assetReference);
      if (assetResource != null) {
        return assetResource.adaptTo(Asset.class);
      }
    }
    return null;
  }

  private Layer toLayer(Asset asset) throws IOException {
    Rendition rendition = asset.getOriginal();
    try (InputStream is = rendition.getStream()) {
      return new Layer(is);
    }
  }

}
