package aemmocksample.models;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.day.cq.wcm.api.Page;

import io.wcm.testing.mock.aem.junit.AemContext;

public class NavigationListTest {

  @Rule
  public AemContext context = new AemContext();

  @Before
  public void setUp() {
    // it may be required to register the models of this project manually depending on your project/IDE setup
    context.addModelsForClasses(NavigationList.class);
  }

  @Test
  public void testGetChildren() {

    // create a root page with some child pages
    Page rootPage = context.create().page("/content/mysite");

    context.create().page("/content/mysite/page1");
    context.create().page("/content/mysite/page2", null,
        "hideInNav", true);
    context.create().page("/content/mysite/page3");

    // root page is current page
    context.currentPage(rootPage);

    // simulate a parameter passed in from HTL template to the model
    context.request().setAttribute("limit", 2);

    // instantiate the model
    NavigationList underTest = context.request().adaptTo(NavigationList.class);

    // assert the result
    List<Page> result = underTest.getChildren();
    assertEquals(2, result.size());
    assertEquals("/content/mysite/page1", result.get(0).getPath());
    assertEquals("/content/mysite/page3", result.get(1).getPath());

  }

}
