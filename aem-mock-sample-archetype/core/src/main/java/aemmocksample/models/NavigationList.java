package aemmocksample.models;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.commons.WCMUtils;

/**
 * Sample model that builds a "navigation list" of the current page's child pages.
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class NavigationList {

  /*
   * Model can be used like this in HTL:
   *
   * <sly data-sly-use.navigationList="{'aemmocksample.models.NavigationList' @ limit = 99]"/>
   * <ul data-sly-list.page="${navigationList.children}">
   *   <li><a href="${page.path @ extension='html'}">page.title</a></li>
   * </ul>
   */

  /**
   * Allows to limit the number of returned child pages to this value.
   */
  @RequestAttribute(injectionStrategy = InjectionStrategy.OPTIONAL)
  private int limit;

  @SlingObject
  private SlingHttpServletRequest request;

  private List<Page> children;

  @PostConstruct
  private void activate() {

    // get current page
    Page currentPage = WCMUtils.getComponentContext(request).getPage();

    // collect child pages that are not hidden and do not exceed the limit
    Iterable<Page> childIterable = () -> currentPage.listChildren(new PageFilter(false, false));
    Stream<Page> childStream = StreamSupport.stream(childIterable.spliterator(), false);
    if (limit > 0) {
      childStream = childStream.limit(limit);
    }
    children = childStream.collect(Collectors.toList());
  }

  public List<Page> getChildren() {
    return this.children;
  }

}
